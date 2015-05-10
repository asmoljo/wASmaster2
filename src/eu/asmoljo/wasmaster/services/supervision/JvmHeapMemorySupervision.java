package eu.asmoljo.wasmaster.services.supervision;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Set;

import javax.management.JMException;
import javax.management.ObjectName;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.configservice.ConfigService;
import com.ibm.websphere.management.configservice.ConfigServiceProxy;
import com.ibm.websphere.management.exception.ConfigServiceException;
import com.ibm.websphere.management.exception.ConnectorException;
import com.ibm.websphere.pmi.stat.WSJVMStats;

import eu.asmoljo.wasmaster.helper.MBeanSet;
import eu.asmoljo.wasmaster.helper.PMI;
import eu.asmoljo.wasmaster.helper.SendMail;
import eu.asmoljo.wasmaster.services.WasCluster;
import eu.asmoljo.wasmaster.services.properties.ServiceProperties;

public class JvmHeapMemorySupervision {

	
	AdminClient ac;
	ConfigService configService;
	Session session;
	ServiceProperties serviceProperties;
	SendMail sendMail;
	

	
	
	public JvmHeapMemorySupervision(AdminClient adminClient, String servicePropertyFile) throws Throwable{
		ac = adminClient;
		configService = new ConfigServiceProxy(ac);
		session = new Session("wasmaster", false);
		serviceProperties = new ServiceProperties(servicePropertyFile);
		
	}

	

	
	
	
	public void monitorJvmHeapSize(long sleepSeconds, long iterationsNumber, String output) throws Throwable{
		int i=0;
		while (iterationsNumber>i){
			printJvmHeapSize(output);
			i++;
			Thread.sleep(sleepSeconds * 1000); //*1000 jer se zadaje u milisekundama
		}
		System.exit(0);
		
	}
	
	
	
	
    
    public void printJvmHeapSize(String output) throws Throwable{
    	
    		
    	
    	ObjectName perfMBean;
    	String scopeType = serviceProperties.getServiceProperty("ScopeType");
    	String scopeName = serviceProperties.getServiceProperty("ScopeName");
    	Set jvmSet = null;
    	
   			//Ovdje kreiram MBeanove od JVM-a 
    		if (scopeType.equalsIgnoreCase("Cell")){
    			jvmSet = MBeanSet.getMBeanSet("WebSphere:*,type=JVM", ac);
    		}
    		else if (scopeType.equalsIgnoreCase("Server")){
    			jvmSet = MBeanSet.getMBeanSet("WebSphere:*,type=JVM,process="+scopeName+"", ac);
    		}
    		else if (scopeType.equalsIgnoreCase("Cluster")){
    			WasCluster wascluster = new WasCluster(ac, "wascluster");	//ovdje pozivam drugi servis za pomoc
    			ArrayList<String> clusterMembers = wascluster.getClusterMembers(scopeName);
    			
    			jvmSet = MBeanSet.getMBeanSet("WebSphere:*,type=JVM,process="+clusterMembers.get(0)+"", ac);
    			Set jvmSetTemp = MBeanSet.getMBeanSet("WebSphere:*,type=JVM,process="+clusterMembers.get(1)+"", ac);
    			jvmSet.add(jvmSetTemp.iterator().next());
    		}
    		
    		
    		
    		if (!jvmSet.isEmpty()) {
    			Iterator iter = jvmSet.iterator();
    			
    			while (iter.hasNext()) {
    				
    				//Ovdje kreiram MBean za JVM
    				ObjectName mBean = (ObjectName)iter.next();
    				String process = mBean.getKeyProperty("process");
    				String node = mBean.getKeyProperty("node");
    				String maxHeapSize =(String)ac.getAttribute(mBean, "maxMemory");
    				if (process.equalsIgnoreCase("nodeagent") || process.equalsIgnoreCase("dmgr")){ //Ovako ignoriram JVM od nodeagent-a i dmgr-a 
    					continue;//ovako preskacem i idem na iduci element u listi
    				}
    				
    				//Ovdje kreiram Perf MBean za JVM 
    	    		Set perfSet = MBeanSet.getMBeanSet("WebSphere:*,type=Perf,process="+process+"", ac);
    	    		if (perfSet.isEmpty()){
    	    			System.out.println("Ne postoji Perf MBean za JVM "+process+"!");
    	    			continue;	//ovako preskacem i idem na iduci element u listi
    	    		}else{
    	    			perfMBean = (ObjectName)perfSet.iterator().next();
    	    		}
    	    		
    	    		long currentHeapSize = PMI.getRangedStats(ac, perfMBean, mBean, WSJVMStats.HeapSize).getCurrent();
    	    		long highWaterMarkHeapSize = PMI.getRangedStats(ac, perfMBean, mBean, WSJVMStats.HeapSize).getHighWaterMark();
    	    		long lowWaterMarkHeapSize = PMI.getRangedStats(ac, perfMBean, mBean, WSJVMStats.HeapSize).getLowWaterMark();
    	    		long currentUsedHeapSize = PMI.getCountStats(ac, perfMBean, mBean, WSJVMStats.UsedMemory).getCount();
    	    		long freeHeapSize = PMI.getCountStats(ac, perfMBean, mBean, WSJVMStats.FreeMemory).getCount();
    	    		long spaceForExtend = Long.parseLong(maxHeapSize)/1000000-currentHeapSize/1000;
    	    		
    	    		String jvmNameFormated = "/node:"+node+"/server:"+process+"/";
    	    		String maximumHeapSizeFormated = "maximumHeapSize: "+Long.parseLong(maxHeapSize)/1000000+" MB";
    	    		String currentHeapSizeFormated = "currentHeapSize: "+currentHeapSize/1000+" MB";
    	    		String currentUsedHeapSizeFormated = "currentUsedHeapSize: "+currentUsedHeapSize/1000+" MB";
    	    		String freeHeapSizeFormated = "freeHeapSize: "+freeHeapSize/1000+" MB";
    	    		String spaceForExtendFormated = "spaceForExtend: "+spaceForExtend+" MB";
    	    		String highWaterMarkHeapSizeFormated = "highWaterMarkHeapSize: "+highWaterMarkHeapSize/1000+" MB";
    	    		String lowWaterMarkHeapSizeFormated = "lowWaterMarkHeapSize: "+lowWaterMarkHeapSize/1000+" MB";
    	    		String newLine = "\n";
    	    		
    	    		
    	    		String allParametersFormated = jvmNameFormated+newLine+maximumHeapSizeFormated+newLine+currentHeapSizeFormated+newLine+currentUsedHeapSizeFormated+newLine+freeHeapSizeFormated+newLine+spaceForExtendFormated+newLine+highWaterMarkHeapSizeFormated+newLine+lowWaterMarkHeapSizeFormated;
    	    		
    	    		
    	    		//Printanje svih JVM parametara na konzolu
    	    		System.out.println(newLine+newLine+allParametersFormated);
    	    		
    	    		
    	    		
    	    		
    	    		
    	    		
    	    		//Ako je output parametar 'mail', onda salje mail na adrese definirane u property datoteci
    	    		if (output.equalsIgnoreCase("mail")){
    	    		
    	    		
				    	    		//Povlacenje i konverzija trigera za slanje maila
				    	    		Integer mailTriger = Integer.parseInt(serviceProperties.getServiceProperty("SpaceForExtendTriger"));
				    	    		
				    	    		
				    	    		//salje mail ako je 'spaceForExtend' kriticno
				    	    		if (spaceForExtend < mailTriger){
				    	    			
				    	    			
				        	    		
				        	    		//Povlacenje propertya za slanje mail-a
				        	    		String mailServer = serviceProperties.getServiceProperty("MailServer");
				        	    		String from = serviceProperties.getServiceProperty("From");
				        	    		ArrayList<String> mailList = new ArrayList<>();
				        	    		mailList.add(serviceProperties.getServiceProperty("To"));
				        	    		mailList.add(serviceProperties.getServiceProperty("To1"));
				        	    		mailList.add(serviceProperties.getServiceProperty("To2"));
				        	    		System.out.println("Output se salje na ove mail adrese: "+mailList);
				        	    		
				        	    		int i = 0;
				        	    		while (i<mailList.size()){
				        	    			if(!mailList.get(i).equalsIgnoreCase("skip")) //ako je mail adresa razlicita od 'skip' salji mail na tu adresu
				        	    			SendMail.send("wASmaster2 nadzor "+jvmNameFormated,allParametersFormated,from,mailList.get(i),mailServer);
				        	    			i++;
				        	    		}
				
				    	    		}
    	    		}
    	    		
    	    		
    	    		
    	    		
    	    		
    	    		
    	    		//AKO JE OUTPUT PARAMETAR 'LOG' ONDA SE OUTPUT ZAPISUJE U LOG NA TRENUTNOJ LOKACIJI GDJE JE APLIKACIJA POKRENUTA
    	    		if (output.equalsIgnoreCase("log")){
    	    							String timeLog = new SimpleDateFormat("_dd.MM.yyyy").format(Calendar.getInstance().getTime());
    	    							String currentDirectory = System.getProperty("user.dir");
				    	    			File logFile = new File(currentDirectory+"/"+serviceProperties.getServiceProperty("LogName")+timeLog);
				    	    			if (!logFile.exists()) {
				    	    				logFile.createNewFile();
				    	    			}
				    	    			System.out.println("Output se zapisuje u log: "+logFile.getAbsolutePath());
				    	    			FileWriter fw = new FileWriter(logFile.getAbsoluteFile(), true); //ovaj true znaci da se u log apenda,tj. da ne gazi stari zapis
				    	    			BufferedWriter bw = new BufferedWriter(fw);
				    	    			bw.write(newLine+newLine+allParametersFormated);
				    	    			bw.close();
				    	    			
    	    		}
    	    		
    	    		
    	    		
    	    		
    	    		
    	    		
    	    		
    			
    			
    		}
	

    	}
    	

    	
    	
    }
    
	
	
	
}

package eu.asmoljo.wasmaster.services.supervision;

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
import com.ibm.websphere.pmi.stat.WSSessionManagementStats;

import eu.asmoljo.wasmaster.helper.MBeanSet;
import eu.asmoljo.wasmaster.helper.PMI;
import eu.asmoljo.wasmaster.helper.SendMail;
import eu.asmoljo.wasmaster.services.properties.ServiceProperties;

public class SessionSupervision {
	
	
	
	
	AdminClient ac;
	ConfigService configService;
	Session session;
	ServiceProperties serviceProperties;
	SendMail sendMail;
	

	
	
	public SessionSupervision(AdminClient adminClient, String servicePropertyFile) throws Throwable{
		ac = adminClient;
		configService = new ConfigServiceProxy(ac);
		session = new Session("wasmaster", false);
		serviceProperties = new ServiceProperties(servicePropertyFile);
		
	}

	

	
	
    
    public void printSessionPerformance() throws ConfigServiceException, ConnectorException, NullPointerException, JMException{
    	
    		
    	
    	ObjectName perfMBean;
    	
   			//Ovdje kreiram MBean od JVM-a
    		//Set jvmSet = MBeanSet.getMBeanSet("WebSphere:*,type=SessionManager,process=PnopServerA1", ac);
    		Set jvmSet = MBeanSet.getMBeanSet("WebSphere:*,type=SessionManager", ac);
    		if (!jvmSet.isEmpty()) {
    			Iterator iter = jvmSet.iterator();
    			
    			while (iter.hasNext()) {
    				
    				//Ovdje kreiram MBean za JVM
    				ObjectName mBean = (ObjectName)iter.next();
    				String process = mBean.getKeyProperty("process");
    				String node = mBean.getKeyProperty("node");
    				String name = mBean.getKeyProperty("name");
    				
    				if (process.equalsIgnoreCase("nodeagent") || process.equalsIgnoreCase("dmgr")){ //Ovako ignoriram JVM od nodeagent-a i dmgr-a 
    					continue;//ovako preskacem i idem na iduci element u listi
    				}
    				
    				//Ovdje kreiram Perf MBean za SessionManager
    	    		Set perfSet = MBeanSet.getMBeanSet("WebSphere:*,type=Perf,process="+process+"", ac);
    	    		if (perfSet.isEmpty()){
    	    			System.out.println("Ne postoji Perf MBean za JVM "+process+"!");
    	    			continue;	//ovako preskacem i idem na iduci element u listi
    	    		}else{
    	    			perfMBean = (ObjectName)perfSet.iterator().next();
    	    		}
    	    		
    	    		long liveCount = PMI.getRangedStats(ac, perfMBean, mBean, WSSessionManagementStats.LiveCount).getCurrent();
    	    		long activeCount =  PMI.getRangedStats(ac, perfMBean, mBean, WSSessionManagementStats.ActiveCount).getCurrent();
    	    		
    	    		
    	    		
    	    		
    	    		String jvmNameFormated = "/node:"+node+"/server:"+process+"/module:"+name+"/";
    	    		String liveCountFormated = "liveCount:"+liveCount;
    	    		String activeCountFormated = "activeCount:"+activeCount;
    	    		
    	    		
    	    		String newLine = "\n";
    	    		
    	    		
    	    		String allParametersFormated = jvmNameFormated+newLine+newLine+liveCountFormated+newLine+newLine+activeCountFormated;
    	    		
    	    		//Printanje svih JVM parametara na konzolu
    	    		System.out.println(newLine+newLine+allParametersFormated);
    	    		
    	    		
    	    		//Povlacenje i konverzija trigera za slanje maila
    	    		Integer mailTriger = Integer.parseInt(serviceProperties.getServiceProperty("SessionTriger"));
    	    		
    	    		
    	    		
    	    		
    	    		//salje mail ako je 'spaceForExtend' kriticno
    	    		if (liveCount > mailTriger){
    	    			
        	    		
        	    		//Povlacenje propertya za slanje mail-a
        	    		String mailServer = serviceProperties.getServiceProperty("MailServer");
        	    		String from = serviceProperties.getServiceProperty("From");
        	    		String to = serviceProperties.getServiceProperty("To");
    	    			
    	    			//Slanje mail-a
        	    		SendMail.send("wASmaster2 nadzor "+jvmNameFormated,allParametersFormated,from,to,mailServer);
    	    			
    	    		}
    	    		
    	    		
    	    		
    	    		
    			
    			
    		}
	

    	}
    	

    	System.exit(0);
    	
	
	}
}

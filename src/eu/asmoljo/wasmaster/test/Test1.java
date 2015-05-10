package eu.asmoljo.wasmaster.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.JMException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.configservice.ConfigService;
import com.ibm.websphere.management.configservice.ConfigServiceProxy;
import com.ibm.websphere.management.exception.ConfigServiceException;
import com.ibm.websphere.management.exception.ConnectorException;
import com.ibm.websphere.pmi.stat.WSJVMStats;
import com.ibm.websphere.pmi.stat.WSThreadPoolStats;

import eu.asmoljo.wasmaster.helper.MBeanSet;
import eu.asmoljo.wasmaster.helper.PMI;
import eu.asmoljo.wasmaster.services.properties.ServiceProperties;

public class Test1 {

	
	AdminClient ac;
	ConfigService configService;
	Session session;
	ServiceProperties serviceProperties;
	

	
	
	public Test1(AdminClient adminClient, String servicePropertyFile) throws Throwable{
		ac = adminClient;
		configService = new ConfigServiceProxy(ac);
		session = new Session("wasmaster", false);
		serviceProperties = new ServiceProperties(servicePropertyFile);
		
	}

	

	
	
    
    public void test() throws ConfigServiceException, ConnectorException, NullPointerException, JMException{
    	
    		
    	
    	ObjectName perfMBean;
    	
   			//Ovdje kreiram MBean od JVM-a
    		Set jvmSet = MBeanSet.getMBeanSet("WebSphere:*,type=ThreadPool,name=WebContainer", ac);
    		if (!jvmSet.isEmpty()) {
    			Iterator iter = jvmSet.iterator();
    			
    			while (iter.hasNext()) {
    				
    				//Ovdje kreiram MBean za JVM
    				ObjectName mBean = (ObjectName)iter.next();
    				String process = mBean.getKeyProperty("process");
    				String node = mBean.getKeyProperty("node");
    				
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
    	    		
    	    		long concurrentlyActiveThreads = PMI.getBoundedStats(ac, perfMBean, mBean, WSThreadPoolStats.ActiveCount).getCurrent();
    	    		long averageNumberThreadsInPool = PMI.getBoundedStats(ac, perfMBean, mBean, WSThreadPoolStats.PoolSize).getCurrent();
    	    		
    	    		
    	    		System.out.println("/node:"+node+"/server:"+process+"/");
    	    		
    	    		System.out.println("concurrentlyActiveThreads: "+concurrentlyActiveThreads);
    	    		System.out.println("averageNumberThreadsInPool: "+averageNumberThreadsInPool);
    	    		

    	    		System.out.println("\n");
 
    	    			
    			
    			
    		}
	

    	}
    	

    	System.exit(0);
    	
    }
    
	
	
	
}

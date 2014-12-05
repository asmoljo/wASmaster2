package eu.asmoljo.wasmaster.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;

import eu.asmoljo.wasmaster.helper.MBeanList;
import eu.asmoljo.wasmaster.helper.MBeanSet;
import eu.asmoljo.wasmaster.services.properties.ServiceProperties;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.application.AppConstants;
import com.ibm.websphere.management.configservice.ConfigService;
import com.ibm.websphere.management.configservice.ConfigServiceHelper;
import com.ibm.websphere.management.configservice.ConfigServiceProxy;
import com.ibm.websphere.management.exception.ConfigServiceException;
import com.ibm.websphere.management.exception.ConnectorException;
import com.ibm.websphere.management.wlm.ClusterMemberData;

public class Test1 {

	
	AdminClient ac;
	ConfigService configService;
	Session session;
	ServiceProperties serviceProperties;
	ArrayList al = new ArrayList();
	
	
	public Test1(AdminClient adminClient, String servicePropertyFile) throws Throwable{
		ac = adminClient;
		configService = new ConfigServiceProxy(ac);
		session = new Session("wasmaster", false);
		serviceProperties = new ServiceProperties(servicePropertyFile);
		
	}

	
    
    public void test(String CLUSTERNAME) throws ConfigServiceException, ConnectorException, AttributeNotFoundException, MalformedObjectNameException, NullPointerException, InstanceNotFoundException, MBeanException, ReflectionException{
    	
    	
    	String clusterName = CLUSTERNAME;

    	Set set1 = MBeanSet.getMBeanSet("WebSphere:type=Cluster,name="+ clusterName +",*",ac);
    	
    	Iterator iter = set1.iterator();

    	ObjectName mBean = null;
    	
		
		
		
		while (iter.hasNext()) {
			mBean = (ObjectName) iter.next();
			al.add(mBean);
			}
		 
		
		
		
		
		
		ClusterMemberData[] clusterMembersList = (ClusterMemberData[]) ac.invoke(mBean, "getClusterMembers", null, null);
		
		
		int i=0;
		while(i<clusterMembersList.length){
			System.out.println(clusterMembersList[i].memberName);
			i++;
		}
		
		
        
		
		System.out.println("\nZatvaram program");
		System.exit(0);
     
    	
    }
    
	
	
	
}

package eu.asmoljo.wasmaster.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.swing.JOptionPane;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.exception.ConnectorException;

public class ApplicationEnvironment  {

	AdminClient ac;
	


	public ApplicationEnvironment(AdminClient adminClient) {

		ac = adminClient;

	}



	public Set<ObjectName> getJ2EEApplicationTypeMbeans() throws ConnectorException, MalformedObjectNameException, NullPointerException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException {

		
		ObjectName querry = new ObjectName("WebSphere:*,type=J2EEApplication,j2eeType=J2EEApplication,name=MCSA");
		Set<ObjectName> mBeanSet = ac.queryNames(querry, null);

		if (!mBeanSet.isEmpty()) {

			Iterator<ObjectName> iter = mBeanSet.iterator();

		
			while (iter.hasNext()) {
				ObjectName mBean = (ObjectName) iter.next();
				
				//System.out.println("Pronaðeno:"+mBean.getKeyPropertyListString());
		
				
				System.out.println("Deployment Descriptor:"+ac.getAttribute(mBean, "deploymentDescriptor"));
				System.out.println("Server:"+ac.getAttribute(mBean, "server"));
				System.out.println("Java Version:"+ac.getAttribute(mBean, "javaVersion"));
				System.out.println("Java Vendor:"+ac.getAttribute(mBean, "javaVendor"));
				System.out.println("Implementation Version:"+ac.getAttribute(mBean, "implementationVersion"));
				//System.out.println("Edition:"+ac.getAttribute(mBean, "edition"));
				System.out.println("State:"+ac.getAttribute(mBean, "state"));
				System.out.println("Start Time:"+ac.getAttribute(mBean, "startTime"));
				System.out.println("Modules:"+ac.getAttribute(mBean, "modules"));
				
				
			}
		}
		
		


		return mBeanSet;
		
		
		
		
	}
	

	

	public void makeHeapDump() throws ConnectorException, MalformedObjectNameException, NullPointerException, InstanceNotFoundException, MBeanException, ReflectionException {


		ObjectName querry = new ObjectName("WebSphere:*,type=AppManagement");
		Set<ObjectName> mBeanSet = ac.queryNames(querry, null);

		if (!mBeanSet.isEmpty()) {

			Iterator<ObjectName> iter = mBeanSet.iterator();

		
			while (iter.hasNext()) {
				ObjectName mBean = (ObjectName) iter.next();
				
				System.out.println("Pronaðeno:"+mBean.getKeyPropertyListString());
				//System.out.println("Pronaðeno:"+mBean.getKeyProperty("name"));
				//ac.invoke(arg0, arg1, arg2, arg3);
				//Vector v = (Vector) ac.invoke(mBean, "getApplicationInfo", p3, s3);
				
				
			}
		}
			}
			

				
		
	}
	
	
	
	



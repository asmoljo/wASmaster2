package eu.asmoljo.wasmaster.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.swing.JOptionPane;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.exception.ConnectorException;

public class JVMHeapDump  {

	AdminClient ac;
	


	public JVMHeapDump(AdminClient adminClient) {

		ac = adminClient;

	}



	public Set<ObjectName> getJ2EEServerMbeans() throws ConnectorException, MalformedObjectNameException, NullPointerException {

		
		ObjectName querry = new ObjectName("WebSphere:*,type=Server,j2eeType=J2EEServer");
		Set<ObjectName> mBeanSet = ac.queryNames(querry, null);

		if (!mBeanSet.isEmpty()) {

			Iterator<ObjectName> iter = mBeanSet.iterator();

		
			while (iter.hasNext()) {
				ObjectName mBean = (ObjectName) iter.next();
				
				System.out.println("Pronaðeno:"+mBean.getKeyPropertyListString());
				
				
			}
		}

		return mBeanSet;
	}
	

	

	public void makeHeapDump(String J2EEServer) throws ConnectorException, MalformedObjectNameException, NullPointerException, InstanceNotFoundException, MBeanException, ReflectionException {


			ObjectName querry = new ObjectName("WebSphere:*,type=JVM,j2eeType=JVM,J2EEServer="+J2EEServer+"");
			Set<ObjectName> mBeanSet = ac.queryNames(querry, null);
			
			
			if (!mBeanSet.isEmpty()) {

				Iterator<ObjectName> iter = mBeanSet.iterator();

				ObjectName mBean = (ObjectName) iter.next();
				
				String result = (String)ac.invoke(mBean, "generateHeapDump", null, null);
				
				
				
				System.out.println("Za proces "+J2EEServer+" HeapDump je kreiran na lokaciji :"+result);
				JOptionPane.showMessageDialog(null, result,"HeapDump je kreiran na lokaciji",JOptionPane.INFORMATION_MESSAGE);
					
					
				}
			}
			

				
		
	}
	
	
	
	



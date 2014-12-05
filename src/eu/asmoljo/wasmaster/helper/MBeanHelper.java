package eu.asmoljo.wasmaster.helper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.management.MBeanInfo;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.exception.ConnectorException;

public class MBeanHelper {
	
	
	
	
	
	
	public static ArrayList getProcessList(AdminClient ac) throws Throwable{
		
		AdminClient adminClient = ac;
		ArrayList al = new ArrayList();

		Set set = MBeanSet.getMBeanSet("WebSphere:*,type=Server",adminClient);
		Set set1 = MBeanSet.getMBeanSet("WebSphere:*,type=Application",adminClient);
		Set set2 = MBeanSet.getMBeanSet("WebSphere:*,type=WebServer",adminClient);
		
		
		
		
		if (!set1.isEmpty()) {
			
			Iterator iter = set1.iterator();

			int i = 0;
			while (iter.hasNext()) {
				ObjectName mBean = (ObjectName) iter.next();
				//MBeanInfo mbinfo = adminClient.getMBeanInfo(mBean);
				
				String attList;
					
				/*if(mbinfo.getAttributes().toString() !=null){
					System.out.println(mbinfo.getAttributes());
				}*/

				
				
				
				al.add(adminClient.getAttribute(mBean, "name"));
				al.add(mBean);
				
				
				
				
				System.out.println(i +"----"+(String)adminClient.getAttribute(mBean, "name")); //ovo odmah i ispisuje imena procesa
				i++;
			}
		}
		
		
		return al;
		
	}
	

}

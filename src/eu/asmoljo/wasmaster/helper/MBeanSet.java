package eu.asmoljo.wasmaster.helper;

import java.util.Set;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.exception.ConnectorException;

public class MBeanSet {
	
	static ObjectName queryName;
	
	
	public static Set getMBeanSet(String querry, AdminClient ac) throws MalformedObjectNameException, NullPointerException, ConnectorException {

		Set s = null;
		String q = querry;
		AdminClient adminClient = ac;

		queryName = new ObjectName(q);
		s = adminClient.queryNames(queryName, null);

		return s;
	}

}

package eu.asmoljo.wasmaster.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.configservice.ConfigService;
import com.ibm.websphere.management.exception.ConnectorException;
import com.ibm.websphere.management.wlm.ClusterData;
import com.ibm.websphere.management.wlm.ClusterMemberData;

import eu.asmoljo.wasmaster.helper.MBeanSet;
import eu.asmoljo.wasmaster.services.properties.ServiceProperties;

public class WasCluster {

	AdminClient ac;

	

	public WasCluster(AdminClient adminClient, String servicePropertyFile)
			throws Throwable {

		ac = adminClient;

	}

	/**
	 * Ispisuje imena servera u clusteru na osnovu imena clustera
	 * @param CLUSTERNAME
	 * @throws MalformedObjectNameException
	 * @throws NullPointerException
	 * @throws ConnectorException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 */
	public void printClusterMembers(String CLUSTERNAME) throws MalformedObjectNameException, NullPointerException, ConnectorException, InstanceNotFoundException, MBeanException, ReflectionException {

		String clusterName = CLUSTERNAME;

		Set set1 = MBeanSet.getMBeanSet("WebSphere:type=Cluster,name="+ clusterName + ",*", ac);

		Iterator iter = set1.iterator();

		ObjectName mBean = null;

		while (iter.hasNext()) {
			mBean = (ObjectName) iter.next();
			}

		ClusterMemberData[] clusterMembersList = (ClusterMemberData[]) ac
				.invoke(mBean, "getClusterMembers", null, null);

		int i = 0;
		while (i < clusterMembersList.length) {
			System.out.println(clusterMembersList[i].memberName);
			i++;
		}

		
		

	}
	
	
	
	/**
	 * Vraca listu sa imenima servera u clusteru na osnovu imena clustera
	 * @param CLUSTERNAME
	 * @return
	 * @throws MalformedObjectNameException
	 * @throws NullPointerException
	 * @throws ConnectorException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 */
	public ArrayList<String> getClusterMembers(String CLUSTERNAME) throws MalformedObjectNameException, NullPointerException, ConnectorException, InstanceNotFoundException, MBeanException, ReflectionException {

		ArrayList<String> clusterMembers = new ArrayList<String>();
		String clusterName = CLUSTERNAME;

		Set set1 = MBeanSet.getMBeanSet("WebSphere:type=Cluster,name="+ clusterName + ",*", ac);

		Iterator iter = set1.iterator();

		ObjectName mBean = null;

		while (iter.hasNext()) {
			mBean = (ObjectName) iter.next();
			}

		ClusterMemberData[] clusterMembersList = (ClusterMemberData[]) ac.invoke(mBean, "getClusterMembers", null, null);

		int i = 0;
		while (i < clusterMembersList.length) {
			clusterMembers.add(clusterMembersList[i].memberName);
			i++;
		}

		
		return clusterMembers;

	}
	
	
	/**
	 * Vraca ime clustera na osnovu imena servera i imena noda
	 * @param SERVER
	 * @param NODE
	 * @return
	 * @throws MalformedObjectNameException
	 * @throws NullPointerException
	 * @throws ConnectorException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 */
	public String getClusterNameByMember(String SERVER,String NODE) throws MalformedObjectNameException, NullPointerException, ConnectorException, InstanceNotFoundException, MBeanException, ReflectionException {

		
		String serverName = SERVER;
		String nodeName = NODE;
		

		Set set1 = MBeanSet.getMBeanSet("WebSphere:type=ClusterMgr,*", ac);

		Iterator iter = set1.iterator();

		ObjectName mBean = null;
		
		
		while (iter.hasNext()) {
			mBean = (ObjectName) iter.next();
		}

		String[] signature = {"java.lang.String","java.lang.String"};
		String[] parameters = {serverName,nodeName};
		
		ClusterData clusterData = (ClusterData) ac.invoke(mBean, "retrieveClusterByMember", parameters, signature);
		
		
		return clusterData.clusterName;

	}
	
	
	
	
	/**
	 * Vraca listu sa imenima svih clustera u celiji
	 * @return
	 * @throws MalformedObjectNameException
	 * @throws NullPointerException
	 * @throws ConnectorException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 */
	public ArrayList<String> getAllClusterNames() throws MalformedObjectNameException, NullPointerException, ConnectorException, InstanceNotFoundException, MBeanException, ReflectionException {

		ArrayList<String> clusterNames = new ArrayList<String>();
	

		Set set1 = MBeanSet.getMBeanSet("WebSphere:type=ClusterMgr,*", ac);

		Iterator iter = set1.iterator();

		ObjectName mBean = null;
		
		
		while (iter.hasNext()) {
			mBean = (ObjectName) iter.next();
		}

		String[] signature = {"java.lang.String","java.lang.String"};
		//String[] parameters = {serverName,nodeName};
		
		ClusterData[]  clustersList = (ClusterData[]) ac.invoke(mBean, "retrieveClusters", null, null);
		
		int i = 0;
		while (i < clustersList.length) {
			clusterNames.add(clustersList[i].clusterName);
			i++;
		}
		
		
		return clusterNames;

	}
	
	
	
	

}

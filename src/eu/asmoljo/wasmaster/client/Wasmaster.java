package eu.asmoljo.wasmaster.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.AdminClientFactory;
import com.ibm.websphere.management.exception.ConnectorException;

import eu.asmoljo.wasmaster.client.properties.ClientConnectionProperties;
import eu.asmoljo.wasmaster.helper.ConfigObjectHelper;
import eu.asmoljo.wasmaster.services.ApplicationEnvironment;
import eu.asmoljo.wasmaster.services.DataSourceBuilder;
import eu.asmoljo.wasmaster.services.DataSourceConnectionPool;
import eu.asmoljo.wasmaster.services.ThreadPoolManager;
import eu.asmoljo.wasmaster.services.WasCluster;
import eu.asmoljo.wasmaster.test.Test1;


public class Wasmaster {

	private static AdminClient adminClient;
	private static ArrayList<String> ndList = new ArrayList<String>();
	private static ArrayList<String> clientConnectionPropertiesList;
	
	static String mode = null;
	static String soapServer = null;
	static String actionName = null;
	static String actionType = null;
	static String param4 = null;
	static String param5 = null;
	static String param6 = null;
	static String param7 = null;
	static String param8 = null;
	static String param9 = null;
	//ovo ces morat napravit za svaku klasu-akciju posebne poruke jer ce i primati razlicite parametre
	static String SCRIPT_ERROR_MSG="[For SCRIPT MODE use: mode{interact/script}, server, action{pae/pdse/tpm},tpname{WebContainer/ORB.thread.pool/TCPChannel.DCS/...}, maximumSize, minimumSize, inactivityTimeout,restart{restart=yes/restart=no}]";
	static String INTERACT_ERROR_MSG="[For INTERACTIVE MODE use: mode{interact/script}, server, action{pae/pdse/tpm}]";
	

	
	
	
	public static void main(String[] args) throws Throwable {
		
		
		
		try {
			
			mode = args[0];
			soapServer = args[1];
			actionName = args[2];
			actionType = args[3];
			
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Niste zadali sve parametre za aplikaciju!");
			e.printStackTrace();
			System.exit(0);
		}
		

		
		
		
		if (mode.equalsIgnoreCase("interact")){
			Wasmaster.createAdminClient(soapServer);
			Wasmaster.action(actionName);
		}
		else if (mode.equalsIgnoreCase("script")) {
			Wasmaster.createAdminClient(soapServer);
			param4 = args[4];
			param5 = args[5];
			param6 = args[6];
			param7 = args[7];
			param8 = args[8];
			param9 = args[9];
			Wasmaster.action(actionName);
		}
		else {
			System.out.println("Unknown model!");
			System.out.println(SCRIPT_ERROR_MSG);
			System.out.println(INTERACT_ERROR_MSG);
			System.out.println("without commas!");
			System.exit(0);
		}
		
		
		
	}
	
	
	
	
	//OVO CE POSLUZIT AKO SE BUDE KORISTIO GUI
	private  void getNDList() throws IOException {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("resources\\NDList.properties");
		ResourceBundle ndBundle = new PropertyResourceBundle(is);
		Set<String> ndKeySet = ndBundle.keySet();
	}
	
	     
	
	
	
	
	
	
	//KREIRANJE ADMIN CLIENTA
	public static void createAdminClient(String soapServer) throws Throwable {

			ClientConnectionProperties ccp = new ClientConnectionProperties();
			clientConnectionPropertiesList = ccp.getConnectionPropertiesList(soapServer);
			
			String currentDirectory = System.getProperty("user.dir");
			String trustStore = currentDirectory + "\\trust.jks";
		

			
			// Setiranje Properties objekta za JMX connector attributes
			Properties connectionProps = new Properties();
			connectionProps.setProperty(AdminClient.CONNECTOR_TYPE, AdminClient.CONNECTOR_TYPE_SOAP);
			connectionProps.setProperty(AdminClient.CONNECTOR_SECURITY_ENABLED, "true");
			connectionProps.setProperty(AdminClient.USERNAME, (String) clientConnectionPropertiesList.get(2));
			connectionProps.setProperty(AdminClient.PASSWORD, (String) clientConnectionPropertiesList.get(3));
			connectionProps.setProperty(AdminClient.CONNECTOR_HOST, (String) clientConnectionPropertiesList.get(0));
			connectionProps.setProperty(AdminClient.CONNECTOR_PORT, (String) clientConnectionPropertiesList.get(1));
			connectionProps.setProperty("javax.net.ssl.trustStore", trustStore);
			connectionProps.setProperty("javax.net.ssl.trustStorePassword", (String) clientConnectionPropertiesList.get(4));
			connectionProps.setProperty("java.home", (String) clientConnectionPropertiesList.get(5));
			System.setProperty("com.ibm.ssl.performURLHostNameVerification", "true");
			
		// Kreiranje adminClienta na osnovu connector propertya
			try {
				adminClient = AdminClientFactory.createAdminClient(connectionProps);
				System.out.println("Tip konekcije na WAS :"+adminClient.getType());
			    System.out.println("Uspjesno spajanje na server "+ clientConnectionPropertiesList.get(0) + ":"+clientConnectionPropertiesList.get(1));
			} 
			catch (ConnectorException err) {
				System.out.println(err + "\n"+"Greska kod spajanja klijenta na WAS. Provjerite parametre u properties datoteci i certifikat u truststoreu. ");
				System.exit(0);
			}
		

			
	}
	
	
	
	
	
	
	
	
		//POKRETANJE AKCIJE
		public static void action(String actionName) throws Throwable  {
			
			
			
			if (actionName.equalsIgnoreCase("pae")){
				System.out.println("Selected action is: " +actionName);	
				ApplicationEnvironment ae = new ApplicationEnvironment(adminClient);
				ae.getJ2EEApplicationTypeMbeans();
			}
			
			
			
			else if (actionName.equalsIgnoreCase("pdse")) {
				System.out.println("Selected action is: " +actionName);
			}
			
			
			
			
			else if (actionName.equalsIgnoreCase("dsb")) {
				System.out.println("Selected action is: " +actionName);
				DataSourceBuilder dsb = new DataSourceBuilder(adminClient);
				dsb.createDataSourceScript("ServerCluster","test1_clus", "DB2 Universal JDBC Driver Provider (XA)","Testni2 JDBC Provider ASMOLJO","Description for Testni2 JDBC Provider ASMOLJO");
			}
			
			
			
			//THREAD POOL MANAGER
			else if (actionName.equalsIgnoreCase("tpm")) {
				System.out.println("Selected action is: " +actionName+"\n");
				ThreadPoolManager tpm = new ThreadPoolManager(adminClient);
					if(mode.equalsIgnoreCase("script")){
						tpm.changeAttributesScript(param4,param5,param6,param7,param8,param9);
					}
					else{
						tpm.changeAttributesInteractive();
					}
			}
			
			
			
			//DATASOURCE CONNECTION POOL
			else if(actionName.equalsIgnoreCase("dscp")){
				System.out.println("Selected action is: " + actionName + "-" + actionType + "\n");
				DataSourceConnectionPool dscp = new DataSourceConnectionPool(adminClient, actionName);
				
				//AKO MIENJAMO VRIJEDNOSTI CONNECTION POOLA
				if(actionType.equalsIgnoreCase("set")){
					dscp.setNewAtributeValues();
				}
				//AKO SAMO CITAMO TRENUTNE VRIJEDNOSTI CONNECTION POOLA
				else if (actionType.equalsIgnoreCase("get")){
					dscp.getAtributeValues();
				}
				else System.out.println("missing 'set' or 'get' actionType");
				System.exit(0);
			}
			
			
			//WAS CLUSTER
			else if(actionName.equalsIgnoreCase("wasc")){
				System.out.println("Selected action is: " +actionName+"\n");
				WasCluster wasc = new WasCluster(adminClient,actionName);
				//wasc.getClusterNameByMember("HitroSrvA1", "was85t1Node01");
				
				ArrayList<String> clusters = wasc.getAllClusterNames();
				int i = 0;
				while(i<clusters.size()){
				System.out.println(clusters.get(i));
				i++;
				}
			}
			
			
			
			//CONFIG OBJECT HELPER
			else if(actionName.equalsIgnoreCase("com")){
				System.out.println("Selected action is: " +actionName+"\n");
				ConfigObjectHelper com = new ConfigObjectHelper(adminClient);
				//com.printAllConfObjectTypes();
				com.printAllAttributeNamesForObjectType("DataSource");
			}
			
			
			
			
			
			else if(actionName.equalsIgnoreCase("t1")){
				System.out.println("Selected action is: " +actionName+"\n");
				Test1 t = new Test1(adminClient, actionName);
				t.test("HitroClus");
			}
			
			
			
			
			
			else {
				System.out.println("Action name: "+actionName+" is incorrect! {pae/pdse/mtp}");
				System.out.println("...closing program...");
				System.out.println("Stop!");
				System.exit(0);
			}
			
			
			
			
			
		}
	
	
	
	
	
	
	
}


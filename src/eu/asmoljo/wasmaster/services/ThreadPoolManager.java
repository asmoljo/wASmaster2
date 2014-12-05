package eu.asmoljo.wasmaster.services;

import eu.asmoljo.wasmaster.utils.UserInputReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.configservice.ConfigService;
import com.ibm.websphere.management.configservice.ConfigServiceHelper;
import com.ibm.websphere.management.configservice.ConfigServiceProxy;
import com.ibm.websphere.management.exception.ConfigServiceException;
import com.ibm.websphere.management.exception.ConnectorException;
import com.sun.tracing.dtrace.ArgsAttributes;

public class ThreadPoolManager implements NotificationListener {

	AdminClient ac;
	ConfigService configService;
	Session session;
	long ntfyCount;
	private String[] scopes;


	public ThreadPoolManager(AdminClient adminClient) throws InstanceNotFoundException, ConnectorException {

		ac = adminClient;
		configService = new ConfigServiceProxy(ac);
		session = new Session("wasmaster", false);
		

	}



	
	
	
	//CMD Interaktivna metoda rada
	public void changeAttributesInteractive() throws ConnectorException, MalformedObjectNameException, NullPointerException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, InvalidAttributeValueException, ConfigServiceException, IOException {



				
				
				//VAZNO!! , VAZNO!!, VAZNO!!***********************************************************************************************
				//*************************************************************************************************************************************
				// Locate the application object.KAKO ?? OVAKO :
				//resolve(Session session, javax.management.ObjectName scope, java.lang.String containmentPath) 
				//Resolve the config object based on the specified containment path. Sto je containmentPath ? -->
				//Containment path:
					//Before we move on the object name, let's discuss the containment path first. Because a configuration ID contains a seemingly random XML fragment ID, you cannot know the configuration ID of 
					//something if you only know its name. To solve that problem. IBM introduced the concept of a containment path. A containment path specifies the hierarchical path of how to reach the object you 
					//are looking for. The general syntax, which may remind some people of XPath, is as follows:
					//type:name/type:name/type:name/.../
				//*************************************************************************************************************************************
				//**************************************************************************************************************************************
				
				
		
		
				//SCOPE ISPIS i ODABIR
				ObjectName[] scopeObjectIDs = configService.resolve(session,"Server");
				
				int i=0;
				while (scopeObjectIDs.length>i){
					
					String object = (String) configService.getAttribute(session, scopeObjectIDs[i],"name"); 
					System.out.println(object);
					i++;
				}
				System.out.println("\n"+"Upisati ime Scope-a");
				String scopeName = UserInputReader.getUserInputReadString();
				ObjectName scopeObjectID = configService.resolve(session, "Server="+scopeName)[0];
				String scope = (String) configService.getAttribute(session, scopeObjectID,"name"); 
				System.out.println ("Scope akcije je: " + scope +"\n");
				

				
				
				
				//THREAD POOL IME i ODABIR
				ObjectName[] threadPoolObjectIDs = configService.resolve(session,scopeObjectID,"ThreadPool");
				
				int j=0;
				while (threadPoolObjectIDs.length>j){
					
					String object = (String) configService.getAttribute(session, threadPoolObjectIDs[j],"name"); 
					System.out.println(object);
					j++;
				}
				System.out.println("\n"+"Upisati ime ThreadPoola-a");
				String threadPoolName = UserInputReader.getUserInputReadString();
				//Ovdje je ime pod 'ThreadPool' tj. ovdje 'WebContainer' najbolje pogledati na WAS konzoli, npr 'Application servers > test_srv_1_0 > Thread pools' i vidi pod kolonu 'Names'
				ObjectName threadPoolObjectID = configService.resolve(session,scopeObjectID,"ThreadPool="+threadPoolName)[0];//ovaj broj u ugl. zagradi automatski bira element u listi, jer ova metoda ustvari vraca listu,a nama treba samo jedan element tipa 'ObjectName'
				String threadPool = (String) configService.getAttribute(session, threadPoolObjectID,"name"); 
				System.out.println ("\n"+"Thread Pool je: " + threadPool+"\n");
				
				
				
				
				//ISPIS ATRIBUTA
				AttributeList list1 = configService.getAttributes(session, threadPoolObjectID, new String[]{"maximumSize","minimumSize","inactivityTimeout"}, false);
				Integer currentMaximumSize = (Integer)ConfigServiceHelper.getAttributeValue(list1, "maximumSize");
				Integer currentMinimumSize = (Integer)ConfigServiceHelper.getAttributeValue(list1, "minimumSize");
				Integer currentInactivityTimeout = (Integer)ConfigServiceHelper.getAttributeValue(list1, "inactivityTimeout");
				System.out.println ("currentMaximumSize : " + currentMaximumSize);
				System.out.println ("Upisite novu vrijednost ili 'Enter' za trenutnu vrijednost");
				Integer maxSize = UserInputReader.getUserInputReadInteger();
				System.out.println ("currentMinimumSize : " + currentMinimumSize);
				System.out.println ("Upisite novu vrijednost ili 'Enter' za trenutnu vrijednost");
				Integer minSize = UserInputReader.getUserInputReadInteger();
				System.out.println ("currentInactivityTimeout : " + currentInactivityTimeout);
				System.out.println ("Upisite novu vrijednost ili 'Enter' za trenutnu vrijednost");
				Integer inacTimeout = UserInputReader.getUserInputReadInteger();
				
				
				//KREIRANJE LISTE SA NOVIM ATRIBUTIMA
				AttributeList attrList = new AttributeList();
				attrList.add(new Attribute("maximumSize", maxSize));
				attrList.add(new Attribute("minimumSize", minSize));
				attrList.add(new Attribute("inactivityTimeout", inacTimeout));
				
			
				System.out.println("Mjenjam atribute");
				configService.setAttributes(session,  threadPoolObjectID, attrList);

				System.out.println("Spremam sesiju");
				configService.save(session, false);
				
				System.out.println("Zatvaram sesiju");
				configService.discard(session);
				
				System.out.println("Zatvaram program");
				System.exit(0);
				
				
				
		}
	
	
	
	
	
	
	
	
	
	
	
	
	//***********************************************************************************************************************************************
	//CMD skriptni naèin rada koji zahtjeva sve parametre u CMD-u
	public void changeAttributesScript(String conftarget, String tpname, String maxsize, String minsize, String inatimeout, String restart) throws ConnectorException, MalformedObjectNameException, NullPointerException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, InvalidAttributeValueException, ConfigServiceException, IOException, InterruptedException {



		
		
		//VAZNO!! , VAZNO!!, VAZNO!!***********************************************************************************************
		//*************************************************************************************************************************************
		// Locate the application object.KAKO ?? OVAKO :
		//resolve(Session session, javax.management.ObjectName scope, java.lang.String containmentPath) 
		//Resolve the config object based on the specified containment path. Sto je containmentPath ? -->
		//Containment path:
			//Before we move on the object name, let's discuss the containment path first. Because a configuration ID contains a seemingly random XML fragment ID, you cannot know the configuration ID of 
			//something if you only know its name. To solve that problem. IBM introduced the concept of a containment path. A containment path specifies the hierarchical path of how to reach the object you 
			//are looking for. The general syntax, which may remind some people of XPath, is as follows:
			//type:name/type:name/type:name/.../
		//*************************************************************************************************************************************
		//**************************************************************************************************************************************
		
		
		
		
		//Aktiviranje Notification Listenera za slusanje poruka od node-a
		System.out.println("Process for Notification Listener configuration is started...");
		try {
			ArrayList<String> scopeElements = scopeListBuild(conftarget);
			String query = "WebSphere:type=NodeAgent,node=" + scopeElements.get(1) + ",*";
			ObjectName queryName = new ObjectName(query);
			ObjectName nodeMbean = getMbean(queryName);
			System.out.println("Notification Listener successfully configured for parent MBean: " + ac.getAttribute(nodeMbean, "diagnosticProviderName"));
			registerNotificationListener(nodeMbean);
		} catch (IndexOutOfBoundsException e) {
			System.out.println("Scope list is empty! Parent MBean object can't be find and registered for receiving notifications.");
			System.out.println("Program continues without parent MBean notifications...");
		}
		
		
		
		
		//Kreiranje objekta za konfiguriranje. Ime servera + Vrsta Thread poola
		System.out.println("\n");
		System.out.println("Process for createing configuration object is started...");
		//Kreiranje objekta kojem treba mijenjati atribute
		ObjectName configObjectID = null;
		try {
			configObjectID = configService.resolve(session, "Server="+conftarget)[0];
			String configObjectName = (String) configService.getAttribute(session, configObjectID,"name"); 
			System.out.println ("configObjectName: /"+ configObjectName);
			} 
		catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Can't find configObjectID: " +conftarget);
			close();
		}
		
		
		//THREAD POOL IME i ODABIR
		//Ovdje je ime pod 'ThreadPool' tj. ovdje 'WebContainer' najbolje pogledati na WAS konzoli, npr 'Application servers > test_srv_1_0 > Thread pools' i vidi pod kolonu 'Names'
		ObjectName threadPoolObjectID = null;
		try {
			threadPoolObjectID = configService.resolve(session,configObjectID,"ThreadPool="+tpname)[0];//ovaj broj u ugl. zagradi automatski bira element u listi, jer ova metoda ustvari vraca listu,a nama treba samo jedan element tipa 'ObjectName'
			String threadPoolObjectName = (String) configService.getAttribute(session, threadPoolObjectID,"name"); 
			System.out.println ("threadPoolObjectName: /" + threadPoolObjectName);
		} catch (Exception e) {
			System.out.println("Can't find threadPoolObjectName: "+tpname);
			e.printStackTrace();
			close();
		}
		
		
		
		
		
		System.out.println("\n");
		System.out.println("Process for changing atributes of configuration object has started...");
		//ISPIS ATRIBUTA
		AttributeList list1 = configService.getAttributes(session, threadPoolObjectID, new String[]{"maximumSize","minimumSize","inactivityTimeout"}, false);
		System.out.println ("currentMaximumSize= " + (Integer)ConfigServiceHelper.getAttributeValue(list1, "maximumSize") + "--> new :" + maxsize);
		System.out.println ("currentMinimumSize= " + (Integer)ConfigServiceHelper.getAttributeValue(list1, "minimumSize") + "--> new :" + minsize);
		System.out.println ("currentInactivityTimeout= " + (Integer)ConfigServiceHelper.getAttributeValue(list1, "inactivityTimeout")+ "--> new :" + inatimeout);

		
		//KREIRANJE LISTE SA NOVIM ATRIBUTIMA
		AttributeList attrList = new AttributeList();
		attrList.add(new Attribute("maximumSize", Integer.decode(maxsize)));
		attrList.add(new Attribute("minimumSize", Integer.decode(minsize)));
		attrList.add(new Attribute("inactivityTimeout", Integer.decode(inatimeout)));
	
		
		//PROMJENA ,RESTART,SPREMANJE i ZATVARANJE 
		configService.setAttributes(session,  threadPoolObjectID, attrList);
		if (restart.equalsIgnoreCase("restart=yes")) {
			restart(conftarget);
		}else if (restart.equalsIgnoreCase("restart=no")) {
			System.out.println("Server "+conftarget+" nece biti restartan");
		}else {
			System.out.println("Nije odabrana 'restart' opcija! {restart=yes/restart=no}");
			System.exit(0);
		}
		System.out.println("saving WAS session");
		configService.save(session, false);
		//Thread se ostavlja ziv jos minutu da se stignu notifikacije ispisati na konzolu
		Thread.currentThread().sleep(60000);
		close();
		
		
		
}
		
	
	
	
	
	
	
	
	
	
	
		//METODA ZA ZATVARANJE SESIJE I PROGRAMA
		private void close() throws ConfigServiceException, ConnectorException{
			System.out.println("...closing WAS session...");
			configService.discard(session);
			System.out.println("...closing program...");
			System.out.println("Stop!");
			System.exit(0);
		}
		
		
		
		//Uzima ime servera i vraca listu sa scopeom tipa {"cell","node","server"}
		private ArrayList<String> scopeListBuild(String conftarget) throws MalformedObjectNameException, ConnectorException, InstanceNotFoundException, MBeanException, ReflectionException, AttributeNotFoundException{
			
			ArrayList<String> scope = new ArrayList<String>();
			try {
				ObjectName querry = new ObjectName("WebSphere:*,type=Server,name="+conftarget);
				ObjectName on = getMbean(querry);
				scope.add((String)ac.getAttribute(on, "cellName"));
				scope.add((String)ac.getAttribute(on, "nodeName"));
				scope.add(conftarget);
				} 
			catch (NoSuchElementException e) {
				System.out.println("configObjectID: " + conftarget + " is stopped, or input configObjectID name is incorrect, so scope list can't be created.");
			}
			return scope;
		}
		
		
		private ObjectName getMbean(ObjectName querry) throws ConnectorException{
			Set<ObjectName> set = ac.queryNames(querry, null);
			Iterator<ObjectName> iter = set.iterator();
			ObjectName on = iter.next();
			return on;
		}
		
		
		//RESTART Servera-a
		private void restart(String conftarget) throws MalformedObjectNameException, ConnectorException, InstanceNotFoundException, MBeanException, ReflectionException{
			try {
				System.out.println("Begin restarting "+conftarget);
				ObjectName querry = new ObjectName("WebSphere:*,type=Server,name="+conftarget);
				ac.invoke(getMbean(querry), "restart", null, null);
			} catch (NoSuchElementException e) {
				System.out.println("configObjectID: " + conftarget + " is stopped. Start will not be initiated!");
			}
			
		}
		
		
	
	    private void registerNotificationListener(ObjectName on){
	    	try
	        {
	            ac.addNotificationListener(on, this, null, null);
	            System.out.println("Registered for event notifications");
	        }
	        catch (InstanceNotFoundException e)
	        {
	            System.out.println(e);
	        }
	        catch (ConnectorException e)
	        {
	            System.out.println(e);
	        }
	    }





	    //Svaka notifikacija koju registrirani MBean preko metode iznad 'registerNotificationListener(ObjectName on)' generira automatski poziva ovu metodu
		public void handleNotification(Notification notification, Object handback) {
	        ntfyCount++;
	        System.out.println("***************************************************");
	        System.out.println("* Notification:"+ntfyCount);
	        System.out.println("* Notification received at " + new Date().toString());
	        System.out.println("* type      = " + notification.getType());
	        System.out.println("* message   = " + notification.getMessage());
	        System.out.println("* source    = " + notification.getSource());
	        System.out.println("* seqNum    = " + Long.toString(notification.getSequenceNumber()));
	        System.out.println("* timeStamp = " + new Date(notification.getTimeStamp()));
	        System.out.println("* userData  = " + notification.getUserData());
	        System.out.println("***************************************************");
	       
			
		}
			

				
		
	}
	
	
	
	



package eu.asmoljo.wasmaster.client.properties;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;



public class ClientConnectionProperties {

	ResourceBundle configBundle = null;
	InputStream is;
	URLConnection con;
	URL url;
	String jndi;
	String[] propertiesList;
	ArrayList<String> al = new ArrayList<String>();
	File propertieFile;

	public ArrayList<String> getConnectionPropertiesList(String server) throws Throwable {

		

		try {
	
				
			is = this.getClass().getClassLoader().getResourceAsStream(server + ".properties");	

			configBundle = new PropertyResourceBundle(is);

			al.add(configBundle.getString("Connector_Host").trim());
			al.add(configBundle.getString("Connector_Port").trim());
			al.add(configBundle.getString("Connector_Username").trim());
			al.add(configBundle.getString("Connector_Password").trim());
			al.add(configBundle.getString("Trust_store_pwd").trim());
			al.add(configBundle.getString("Java_Home").trim());
			
			
			
			
		} catch (NullPointerException err) {
			System.out.println("Can't find '" + server+ ".properties' file");
		}

		return al;
	}

}

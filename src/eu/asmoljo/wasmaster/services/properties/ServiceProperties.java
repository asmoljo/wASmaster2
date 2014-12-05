package eu.asmoljo.wasmaster.services.properties;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;



public class ServiceProperties {

	ResourceBundle configBundle = null;
	InputStream is;
	URLConnection con;
	URL url;
	String jndi;
	String[] propertiesList;
	ArrayList<String> al = new ArrayList<String>();
	File propertieFile;
	String propertieFileName;

	
	
	public ServiceProperties(String properties) throws Throwable{
		propertieFileName = properties;
		makePropertiesList();
	}
	
	
	
	/**
	 * Interna (privatna) metoda koju koristi konstruktor kako bi kreirao ResourceBundle sa propertyma
	 * 
	 * @throws Throwable
	 */
	private void makePropertiesList() throws Throwable {
		
		try {
			is = this.getClass().getClassLoader().getResourceAsStream(propertieFileName+".properties");	
			configBundle = new PropertyResourceBundle(is);
		} 
		catch (NullPointerException err) {
			System.out.println("Can't find '" +propertieFileName+ ".properte' file");
		}
		
	}
	
	
	/**
	 * 
	 * Metoda koja na osnovu imena propertya u datoteci vraca vrijednost tog  propertya
	 * 
	 * @param servicePropertyName
	 * @return vrijednost propertya (String)
	 */
	public String getServiceProperty(String servicePropertyName){
		String servicePropertyValue = "skip";
		try {
			servicePropertyValue = configBundle.getString(servicePropertyName).trim();
		} catch (Exception e) {
			System.out.println("Can't find '" +servicePropertyName+ "' in propertie file: '"+propertieFileName+".properties'."+servicePropertyName+" is commented, or name is wrong");
		}
		return servicePropertyValue;
		
	}
	

}

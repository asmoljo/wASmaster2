package eu.asmoljo.wasmaster.helper;

import javax.management.InstanceNotFoundException;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.configservice.ConfigService;
import com.ibm.websphere.management.configservice.ConfigServiceProxy;
import com.ibm.websphere.management.exception.ConfigServiceException;
import com.ibm.websphere.management.exception.ConnectorException;

public class ConfigObjectHelper {

	
	AdminClient ac;
	ConfigService configService;
	Session session;
	
	
	public ConfigObjectHelper(AdminClient adminClient) throws InstanceNotFoundException, ConnectorException{
		ac = adminClient;
		configService = new ConfigServiceProxy(ac);
		session = new Session("wasmaster", false);
	}
	
	
	
	
	/**
	 * Metoda koja ispisuje sve tipove konfiguracijskih objekata 
	 * 
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 */
	public void printAllConfObjectTypes() throws ConfigServiceException, ConnectorException{
		for(String confObj: configService.getSupportedConfigObjectTypes()){
		 System.out.println(confObj);
		}
	}
	
	
	/**
	 * Metoda koja ispisuje sve atribute koje ima konfiguracijski objekt
	 * 
	 * @param objTypeName
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 */
	public void printAllAttributeNamesForObjectType(String objTypeName) throws ConfigServiceException, ConnectorException{
		System.out.println(configService.getAttributesMetaInfo(objTypeName));
	}
}

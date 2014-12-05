package eu.asmoljo.wasmaster.helper;

import javax.management.ObjectName;

import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.configservice.ConfigService;
import com.ibm.websphere.management.exception.ConfigServiceException;
import com.ibm.websphere.management.exception.ConnectorException;

public class AttributesList {
	
	
	/**
	 * 
	 * Metoda koja iz liste MBeanova vraca onaj MBean cija je vrijednost atributa 'attributeName' jednaka vrijednosti  'attributeValue'
	 * 
	 * @author Antonio Smoljo 
	 * @
	 * @param mbeanList (ObjectName[])
	 * @param mbeanName (String)
	 * @return ObjectName koji ima atribut 'attributeName' jednak parametru 'attributeValue'
	 * @throws ConnectorException 
	 * @throws ConfigServiceException 
	 * 
	 * 
	 */
	public void printAttributesCurrentValues(ObjectName[] mbeanList, String attributeName, String attributeValue, ConfigService configService) throws ConfigServiceException, ConnectorException{
		
		ObjectName targetObjectName = null;
		
        if (mbeanList != null) {
        	
            for (ObjectName mbean: mbeanList) {
            	if (attributeValue.equals(configService.getAttribute(null, mbean, attributeName))){
            		targetObjectName = mbean;
                	System.out.println("MBean with attribute '"+attributeName+"'='"+ attributeValue +"' is found");
                }
            }
            if(targetObjectName==null){
            	System.out.println("Can't find MBean with attribute '"+attributeName+"'='"+ attributeValue +"'");
             }
            
        }
		
		
		
		
	}
	

}

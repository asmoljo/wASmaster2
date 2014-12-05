package eu.asmoljo.wasmaster.services;

import eu.asmoljo.wasmaster.helper.MBeanList;
import eu.asmoljo.wasmaster.services.properties.ServiceProperties;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.ObjectName;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.configservice.ConfigService;
import com.ibm.websphere.management.configservice.ConfigServiceHelper;
import com.ibm.websphere.management.configservice.ConfigServiceProxy;
import com.ibm.websphere.management.exception.ConfigServiceException;
import com.ibm.websphere.management.exception.ConnectorException;



public class DataSourceConnectionPool {

	
	AdminClient ac;
	ConfigService configService;
	Session session;
	ServiceProperties serviceProperties;
	
	
	public DataSourceConnectionPool(AdminClient adminClient, String servicePropertyFile) throws Throwable{
		ac = adminClient;
		configService = new ConfigServiceProxy(ac);
		session = new Session("wasmaster", false);
		serviceProperties = new ServiceProperties(servicePropertyFile);
		
		
	}

	
    
	
	//METODA KOJA POSTAVLJA NOVE VRIJEDNOSTI ATRIBUTA NA TEMELJU ZADANIH VRIJEDNOSTI IZ dscp.properties
    public void setNewAtributeValues() throws ConfigServiceException, ConnectorException, AttributeNotFoundException{
    	

    	

    	
    	
        //Direktan poziv kompletnog scopea
    	ObjectName scope = configService.resolve(session, serviceProperties.getServiceProperty("ScopeType")+"="+serviceProperties.getServiceProperty("ScopeName"))[0] ;
    	
        
        ObjectName[] jdbcProviderList = configService.resolve(session, scope,"JDBCProvider");
        ObjectName jdbcProvider = MBeanList.getMBeanFromListBasedOnAttributeName(jdbcProviderList, "name",serviceProperties.getServiceProperty("JDBCProviderName"),configService);
        
        ObjectName[]  dataSourceList = configService.resolve(session, jdbcProvider,"DataSource");
        ObjectName dataSource = MBeanList.getMBeanFromListBasedOnAttributeName(dataSourceList, "name",serviceProperties.getServiceProperty("DataSourceName"),configService);
        
        //Direktan poziv connection poolu jer postoji samo jedan za svaki datasource
        ObjectName connectionPool = configService.resolve(session, dataSource,"ConnectionPool")[0];
       
        

        

        System.out.println("--------------------SCOPE-------------------------------");
        System.out.println(ConfigServiceHelper.getConfigDataId(scope)+"----"+ConfigServiceHelper.getConfigDataType(scope)+"----"+ConfigServiceHelper.getDisplayName(scope));
        System.out.println("---------------------------------------------------------");
        
        
        System.out.println("--------------------JDBCProvider-------------------------------");
        System.out.println(ConfigServiceHelper.getConfigDataId(jdbcProvider)+"----"+ConfigServiceHelper.getConfigDataType(jdbcProvider)+"----"+ConfigServiceHelper.getDisplayName(jdbcProvider));
        System.out.println("---------------------------------------------------------");
        
        
        System.out.println("--------------------DataSource-------------------------------");
        System.out.println(ConfigServiceHelper.getConfigDataId(dataSource)+"----"+ConfigServiceHelper.getConfigDataType(dataSource)+"----"+ConfigServiceHelper.getDisplayName(dataSource));
        System.out.println("---------------------------------------------------------");
        
        System.out.println("--------------------ConnectionPool-------------------------------");
        System.out.println(ConfigServiceHelper.getConfigDataId(connectionPool)+"----"+ConfigServiceHelper.getConfigDataType(connectionPool)+"----"+ConfigServiceHelper.getDisplayName(connectionPool));
        System.out.println("---------------------------------------------------------");
        
        
        
        //Kreiranje liste sa atributima od ConnectionPoola
        System.out.println("Attribute list:");
        String[] attributeNameList = new String[]{"connectionTimeout","maxConnections","minConnections","reapTime","unusedTimeout","agedTimeout","purgePolicy"};
        AttributeList connectionPoolAttributeList = configService.getAttributes(session, connectionPool, attributeNameList, false);
        
        
        
        //Ispis trenutnih i buducih vrijednosti atributa
        System.out.println ("connectionTimeout= " + (long)ConfigServiceHelper.getAttributeValue(connectionPoolAttributeList, "connectionTimeout") + "--> new :" + serviceProperties.getServiceProperty("connectionTimeout"));
        System.out.println ("maxConnections= " + (Integer)ConfigServiceHelper.getAttributeValue(connectionPoolAttributeList, "maxConnections") + "--> new :" + serviceProperties.getServiceProperty("maxConnections"));
        System.out.println ("minConnections= " + (Integer)ConfigServiceHelper.getAttributeValue(connectionPoolAttributeList, "minConnections") + "--> new :" + serviceProperties.getServiceProperty("minConnections"));
        System.out.println ("reapTime= " + (long)ConfigServiceHelper.getAttributeValue(connectionPoolAttributeList, "reapTime") + "--> new :" + serviceProperties.getServiceProperty("reapTime"));
        System.out.println ("unusedTimeout= " + (long)ConfigServiceHelper.getAttributeValue(connectionPoolAttributeList, "unusedTimeout") + "--> new :" + serviceProperties.getServiceProperty("unusedTimeout"));
        System.out.println ("agedTimeout= " + (long)ConfigServiceHelper.getAttributeValue(connectionPoolAttributeList, "agedTimeout") + "--> new :" + serviceProperties.getServiceProperty("agedTimeout"));
        System.out.println ("purgePolicy= " + (String)ConfigServiceHelper.getAttributeValue(connectionPoolAttributeList, "purgePolicy") + "--> new :" + serviceProperties.getServiceProperty("purgePolicy")+"\n");
        
        
        
        //Kreiranje liste sa novim atributima i dodavanje novih vrijednosti atributima iz property datoteke
        AttributeList newAttributeList = new AttributeList();
        newAttributeList.add(new Attribute("connectionTimeout", Long.parseLong(serviceProperties.getServiceProperty("connectionTimeout"))));
        newAttributeList.add(new Attribute("maxConnections", Integer.parseInt(serviceProperties.getServiceProperty("maxConnections"))));
        newAttributeList.add(new Attribute("minConnections", Integer.parseInt(serviceProperties.getServiceProperty("minConnections"))));
        newAttributeList.add(new Attribute("reapTime", Long.parseLong(serviceProperties.getServiceProperty("reapTime"))));
        newAttributeList.add(new Attribute("unusedTimeout", Long.parseLong(serviceProperties.getServiceProperty("unusedTimeout"))));
        newAttributeList.add(new Attribute("agedTimeout", Long.parseLong(serviceProperties.getServiceProperty("agedTimeout"))));
        newAttributeList.add(new Attribute("purgePolicy", serviceProperties.getServiceProperty("purgePolicy")));
        
        
             
		
      //setiranje novih vrijednosti atributa i spremanje i zatvaranej WAS sesije
		configService.setAttributes(session,  connectionPool, newAttributeList);
		System.out.println("Postavljeni novi atributi");

		
		configService.save(session, false);
		System.out.println("WAS sesija spremljena");
		
		
		configService.discard(session);
		System.out.println("WAS sesija zatvorena");
		
		System.out.println("Zatvaram program");
		System.exit(0);
    	

    	
    }
    
    
    
    
    
    
    
    
    
    //METODA KOJA ISPISUJE TRENUTNE VRIJEDNOSTI ATRIBUTA NA TEMELJU ZADANIH VRIJEDNOSTI IZ dscp.properties
    public void getAtributeValues() throws ConfigServiceException, ConnectorException, AttributeNotFoundException {
    	
    	
    	
    	//Direktan poziv kompletnog scopea
    	ObjectName scope = configService.resolve(session, serviceProperties.getServiceProperty("ScopeType")+"="+serviceProperties.getServiceProperty("ScopeName"))[0] ;
    	
        //OVDJE SE NAVODI I TIP JDBC PROVIDERA PA ONDA IME DATASOURCEA,ALI SE MOZE POZIVATII DIREKTNO IME DATASOURCEA
        ObjectName[] jdbcProviderList = configService.resolve(session, scope,"JDBCProvider");
        ObjectName jdbcProvider = MBeanList.getMBeanFromListBasedOnAttributeName(jdbcProviderList, "name",serviceProperties.getServiceProperty("JDBCProviderName"),configService);
        
        ObjectName[]  dataSourceList = configService.resolve(session, jdbcProvider,"DataSource");
        ObjectName dataSource = MBeanList.getMBeanFromListBasedOnAttributeName(dataSourceList, "name",serviceProperties.getServiceProperty("DataSourceName"),configService);
        
        //Direktan poziv connection poolu jer postoji samo jedan za svaki datasource
        ObjectName connectionPool = configService.resolve(session, dataSource,"ConnectionPool")[0];
       
        

        

        System.out.println("\n--------------------SCOPE-------------------------------");
        System.out.println(ConfigServiceHelper.getConfigDataType(scope)+":"+ConfigServiceHelper.getDisplayName(scope));
        System.out.println("---------------------------------------------------------");
        
        
       
        System.out.println(ConfigServiceHelper.getConfigDataType(jdbcProvider)+":"+ConfigServiceHelper.getDisplayName(jdbcProvider));
        System.out.println("---------------------------------------------------------");
        
        
        
        System.out.println(ConfigServiceHelper.getConfigDataType(dataSource)+":"+ConfigServiceHelper.getDisplayName(dataSource));
        System.out.println("---------------------------------------------------------");
        
        System.out.println("--------------------ConnectionPool-------------------------------");
    
    	
        
        
        //Kreiranje liste sa atributima od ConnectionPoola
        System.out.println("Attribute list:");
        String[] attributeNameList = new String[]{"connectionTimeout","maxConnections","minConnections","reapTime","unusedTimeout","agedTimeout","purgePolicy"};
        AttributeList connectionPoolAttributeList = configService.getAttributes(session, connectionPool, attributeNameList, false);
        
        
        
        //Ispis trenutnih vrijednosti atributa 
        System.out.println ("connectionTimeout= " + (long)ConfigServiceHelper.getAttributeValue(connectionPoolAttributeList, "connectionTimeout"));
        System.out.println ("maxConnections= " + (Integer)ConfigServiceHelper.getAttributeValue(connectionPoolAttributeList, "maxConnections"));
        System.out.println ("minConnections= " + (Integer)ConfigServiceHelper.getAttributeValue(connectionPoolAttributeList, "minConnections"));
        System.out.println ("reapTime= " + (long)ConfigServiceHelper.getAttributeValue(connectionPoolAttributeList, "reapTime"));
        System.out.println ("unusedTimeout= " + (long)ConfigServiceHelper.getAttributeValue(connectionPoolAttributeList, "unusedTimeout"));
        System.out.println ("agedTimeout= " + (long)ConfigServiceHelper.getAttributeValue(connectionPoolAttributeList, "agedTimeout"));
        System.out.println ("purgePolicy= " + (String)ConfigServiceHelper.getAttributeValue(connectionPoolAttributeList, "purgePolicy")+"\n");
        
        
        
        
        //spremanje i zatvaranej WAS sesije
  		configService.save(session, false);
  		System.out.println("WAS sesija spremljena");
  		
  		
  		configService.discard(session);
  		System.out.println("WAS sesija zatvorena");
  		
  		System.out.println("Zatvaram program");
  		System.exit(0);
        
        
    	
    	
    }
    
	
	
	
}

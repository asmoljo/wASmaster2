package eu.asmoljo.wasmaster.services;


import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.AdminClientFactory;
import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.configservice.ConfigService;
import com.ibm.websphere.management.configservice.ConfigServiceHelper;
import com.ibm.websphere.management.configservice.ConfigServiceProxy;
import com.ibm.websphere.management.configservice.SystemAttributes;
import com.ibm.websphere.management.exception.ConfigServiceException;
import com.ibm.websphere.management.exception.ConnectorException;
import com.ibm.websphere.management.exception.InvalidConfigDataTypeException;
import com.ibm.websphere.management.exception.RequiredAttributeNotFoundException;



public class DataSourceBuilder {

    // program configuration settings
    // if true, an XA capable jdbc provider is created, otherwise non-XA
    private static final boolean createXAJDBCProvider = true;
    
    // if true, a CMP connection factory will be created for use by CMP beans,
    // otherwise the data source created can be used with BMP beans, session
    // beans, or servlets
    private static final boolean createCMPConnectionFactory = true;
    
    // if true, create node-scoped objects, otherwise create server
    private static final boolean createNodeScopedCfgObjs = true;

    
    
    private static final String jdbcProviderName = "Sample DB2 JDBC Provider";
    private static final String jdbcProviderTemplateNameXA = "DB2 Using IBM JCC Driver Provider Only (XA)";
        
    private static final String jdbcProviderTemplateName =   "DB2 Using IBM JCC Driver Provider Only";
      
    // an alias for authentication data
    private static final String authDataAlias = "db2admin";
    private static final String authAliasDescription =  "Sample authentication alias";
       
    private static final String authAliasUserID = "db2admin"; // user ID
    private static final String authAliasPassword = "db2admin"; // password
    private static final String dataSourceProviderTemplateNameXA =  "DB2 Using IBM JCC Driver - XA DataSource";
       
    private static final String dataSourceHelperClassName = "com.ibm.websphere.rsadapter.DB2DataStoreHelper";
        
    private static final String cmpConnFactTemplateName =    "CMPConnectorFactory";
     
    // display name for data source(DS)and connection factory(CF)
    private static final String dataSourceName = "SampleDataSource";
    private static final String dataSourceTemplateName =  "DB2 Using IBM JCC Driver - DataSource";
       

    @SuppressWarnings("unused")
    private static final String dbName = "SamplesDB"; // the database name
    
    
    

    
	AdminClient ac;
	ConfigService configService;
	Session session;
	long ntfyCount;
	
	String scopeType;
	String scopeName;
	String completeScope;
	String providerTemplate;
	String providerName;
	String providerDescription;
	
	
	ObjectName scopeObject = null;
	ObjectName jdbcProvider;
	
	
	public DataSourceBuilder(AdminClient adminClient) throws InstanceNotFoundException, ConnectorException{
		ac = adminClient;
		configService = new ConfigServiceProxy(ac);
		session = new Session("wasmaster", false);
	}
    
    

    public void createDataSourceScript(String scptype, String scpname, String prvtmp, String provname, String provdesc) throws Exception {


    	scopeType = scptype;
    	scopeName = scpname;
    	providerTemplate = prvtmp;
    	providerName = provname;
    	providerDescription = provdesc;
    	
        
        
    	
        
        getScope();
        
        //checkExistance()  NAPRAVI PRVO METODU KOJA PROVJERAVA DALI VEC POSTOJI TO STO SE POKUSAVA NAPRAVITI
        createJDBCProvider(ac, session, scopeObject, providerTemplate);
        //setDriverPath() NAPRAVI METODU KOJA PODEŠAVA VARIJABLU ZA PUTANJU DO DRIVERA NA FILESYSTEMU NA ODREÐENOM SCOPEU 
        
        
        
        
        
        System.out.println("\n...saving WAS session...");
		configService.save(session, false);
		close();
		
        
        }
    
        
    
    
        
        
    
    private void getScope() throws ConfigServiceException, ConnectorException{
    	
    	try {
    		// retrieve the scopeObject MBean
    		scopeObject = configService.resolve(session, scopeType+"="+scopeName)[0];
            
            //Create and print complete scope of operation
            completeScope = scopeType+":/" + ConfigServiceHelper.getDisplayName(scopeObject);
            System.out.println("Selected scope is: " + completeScope);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Can't find scope: "+scopeType+"/"+scopeName);
			close();
		} catch (InvalidConfigDataTypeException e) {
			System.out.println("Unknown scope type: "+scopeType);
			close();
		}
    	
    	
    }
    
    
    
    
    
    
    
    
    
    
    

    
    
    
    
    
    
    
    
    
    
    
    
    
        
        
        
        
 

        
        
       
    
    	//Creating JDBC provider
        private void createJDBCProvider(AdminClient adminClient, Session session, ObjectName scopeObject, String prvtmp) throws Exception {
        	
        	ObjectName selectedTemplate = null;
        	
            try {

            	//Uzmi sve template
            	ObjectName[] templates = configService.queryTemplates(session, "JDBCProvider");
                            
                // Odaberi zadani template iz ponudjenih
                
                
                if (templates != null) {
                	
                    for (ObjectName template: templates) {
                    	if (prvtmp.equals(template.getKeyProperty(SystemAttributes._WEBSPHERE_CONFIG_DATA_DISPLAY_NAME))){
                        	selectedTemplate = template;
                        	System.out.println("'"+ prvtmp +"' template is found");
                        }
                    }
                    if(selectedTemplate==null){
                    	System.out.println("Can't find template: '"+ prvtmp);
                    	close();
                    }
                    
                }
				
			} catch (RequiredAttributeNotFoundException e) {
				System.out.println("Can't get template list!");
				close();
			}
        	
            
            
			

          
                    
                   
            //Kreiraj listu sa atributima i kreiraj JDBC provider na osnovu templatea
            AttributeList providerAttributes = new AttributeList();
            providerAttributes.add(new Attribute("name", providerName));
            providerAttributes.add(new Attribute("description", providerDescription));
            jdbcProvider = configService.createConfigDataByTemplate(session, scopeObject, "JDBCProvider", providerAttributes, selectedTemplate);
            System.out.println("Created JDBC provider: '" + providerName  + "' at (" + completeScope + ") scope");
            
        }
        
        
        
        
        
        
		private void close() throws ConfigServiceException, ConnectorException{
			System.out.println("\n...closing WAS session...");
			configService.discard(session);
			System.out.println("\n...closing program...");
			System.out.println("\n...Stop!");
			System.exit(0);
		}
        
        
        
        
        
        /**
        
        
        
        
        
        
        
        
        
        
        
        
        
        // create an authentication alias for the data source
        createAuthAlias(adminClient, session, configServiceMBean);

        // Retrieve built-in WebSphere Relational Resource Adapter (RRA) at
        // same scope as above
        ObjectName rra =
            ConfigServiceHelper.createObjectName(null, "J2CResourceAdapter",
                null);

        // invoke mbean queryConfigObjects operation
        matches = (ObjectName[])adminClient.invoke(
            configServiceMBean,
            "queryConfigObjects",
            // parameters for operation
            new Object[] {session, scope, rra, null},
            // signature of bean method to invoke
            new String[] {"com.ibm.websphere.management.Session",
                "javax.management.ObjectName",
                "javax.management.ObjectName",
                "javax.management.QueryExp"});
        rra = matches[0]; // use the first object found at this scope

        // create a data source using the jdbc provider
        ObjectName dataSource =
            createDataSource(adminClient, session, scope, jdbcProv, rra,
                configServiceMBean);

        // if configured to do so, create a Connection Factory on the
        // built-in WebSphere RRA for use by CMP beans
        if (createCMPConnectionFactory) {
            createConnectionFactory(adminClient, session, rra, dataSource,
                configServiceMBean);
        }

        // invoke mbean save operation to persist the changes
        // to the configuration repository, if platform is ND
        // will also need to perform a node sync
        adminClient.invoke(
            configServiceMBean,
            "save",
            // parameters for operation
            new Object[] {session, false},
            // signature of bean method to invoke
            new String[] {"com.ibm.websphere.management.Session",
                "boolean"});
        
        System.out.println("Configuration changes saved");

        // reload data source MBean
        reload(adminClient, session);
    }


    private void createAuthAlias(AdminClient adminClient, Session session,
        ObjectName configServiceMBean) throws Exception {

        // Find the parent security object
        ObjectName security =
            ConfigServiceHelper.createObjectName(null, "Security", null);
        // invoke mbean queryConfigObjects operation
        Object result = adminClient.invoke(
            configServiceMBean,
            "queryConfigObjects",
            // parameters for operation
            new Object[] {session, null, security, null},
            // signature of bean method to invoke
            new String[] {"com.ibm.websphere.management.Session",
                "javax.management.ObjectName",
                "javax.management.ObjectName",
                "javax.management.QueryExp"});
        security = ((ObjectName[])result)[0];
        
        // Prepare the attribute list
        AttributeList authAliasAttrs = new AttributeList();
        authAliasAttrs.add(new Attribute("alias", authDataAlias));
        authAliasAttrs.add(new Attribute("userId", authAliasUserID));
        authAliasAttrs.add(new Attribute("password", authAliasPassword));
        authAliasAttrs.add(
            new Attribute("description", authAliasDescription));

        // invoke jmx createConfigData operation
        result = adminClient.invoke(configServiceMBean, "createConfigData",
            // parameters for operation
            new Object[] {session, security, "authDataEntries",
            "JAASAuthData", authAliasAttrs},
            // signature of bean method to invoke
            new String[] {"com.ibm.websphere.management.Session",
                "javax.management.ObjectName",
                "java.lang.String",
                "java.lang.String",
                "javax.management.AttributeList"});
        
        System.out.println("Created authorization alias: " + authDataAlias);
    }

  
  
  
  
    private void createConnectionFactory(AdminClient adminClient,
        Session session, ObjectName rra, ObjectName dataSource,
        ObjectName configServiceMBean)
        throws Exception {
        
        // Prepare the attribute list
        AttributeList cfAttrs = new AttributeList();
        cfAttrs.add(new Attribute("name", dataSourceName + "_CF"));
        cfAttrs .add(new Attribute("authMechanismPreference",
            "BASIC_PASSWORD"));
        cfAttrs.add(new Attribute("authDataAlias", authDataAlias));
        cfAttrs.add(new Attribute("cmpDatasource", dataSource));
        
        // invoke jmx queryTemplates operation
        Object result = adminClient.invoke(
            configServiceMBean,
            "queryTemplates",
            // parameters for operation
            new Object[] {session, "CMPConnectorFactory",},
            // signature of bean method to invoke
            new String[] {"com.ibm.websphere.management.Session",
                "java.lang.String"});

        // find the template with the desired display name attribute
        ObjectName connFactTemplate = null;
        if (result != null) {
            ObjectName[] templates = (ObjectName[])result;
            for (ObjectName template: templates) {
                if (cmpConnFactTemplateName.equals(template.getKeyProperty(
                    SystemAttributes._WEBSPHERE_CONFIG_DATA_DISPLAY_NAME))) {
                    connFactTemplate = template;
               }
            }
        }

        // use the template found above to create the CMP connection factory
        // invoke jmx createConfigDataByTemplate operation
        adminClient.invoke(
            configServiceMBean,
            "createConfigDataByTemplate",
            // parameters for operation
            new Object[] {session, rra, "CMPConnectorFactory",
                cfAttrs, connFactTemplate},
            // signature of bean method to invoke
            new String[] {"com.ibm.websphere.management.Session",
                "javax.management.ObjectName",
                "java.lang.String",
                "javax.management.AttributeList",
                "javax.management.ObjectName"
                });

        System.out.println("Created CMP Connection factory: " +
            dataSourceName + "_CF");
    }


    private ObjectName createDataSource(AdminClient adminClient,
        Session session, ObjectName scope, ObjectName jdbcProv,
        ObjectName rra, ObjectName configServiceMBean)
        throws Exception {

        // the template name to use based on whether this example is
        // configured to use an XA data source or not
        String templateName = dataSourceProviderTemplateNameXA;
        if (!createXAJDBCProvider) {
            templateName = dataSourceTemplateName;
        }
        
        // the attribute DataSource.relationResourceAdapter is required
        // in addition to the attributes in the template
        AttributeList dsAttrs = new AttributeList();
        dsAttrs.add(new Attribute("name", dataSourceName));
        // override some other props in the template
        dsAttrs.add(new Attribute("description", dataSourceName));
        dsAttrs.add(new Attribute("jndiName", "jdbc/" + dataSourceName));
        dsAttrs.add(new Attribute("datasourceHelperClassname",
            dataSourceHelperClassName));
        // link to the built-in WebSphere RRA
        dsAttrs.add(new Attribute("relationalResourceAdapter", rra));
        dsAttrs.add(new Attribute("authDataAlias", authDataAlias));

        // invoke jmx queryTemplates operation
        Object result = adminClient.invoke(
            configServiceMBean,
            "queryTemplates",
            // parameters for operation
            new Object[] {session, "DataSource"},
            // signature of bean method to invoke
            new String[] {"com.ibm.websphere.management.Session",
                "java.lang.String"});

        // find the template with the desired display name attribute
        ObjectName db2Template = null;
        if (result != null) {
            ObjectName[] templates = (ObjectName[])result;
            for (ObjectName template: templates) {
                if (templateName.equals(template.getKeyProperty(
                    SystemAttributes._WEBSPHERE_CONFIG_DATA_DISPLAY_NAME))) {
                    db2Template = template;
               }
            }
        }

        // use the template found above to create the data source
        // invoke jmx createConfigDataByTemplate operation
        ObjectName dataSource = (ObjectName)adminClient.invoke(
            configServiceMBean,
            "createConfigDataByTemplate",
            // parameters for operation
            new Object[] {session, jdbcProv, "DataSource",
                dsAttrs, db2Template},
            // signature of bean method to invoke
            new String[] {"com.ibm.websphere.management.Session",
                "javax.management.ObjectName",
                "java.lang.String",
                "javax.management.AttributeList",
                "javax.management.ObjectName"
                });

        System.out.println("Created data source: " + dataSourceName +
            " at " + (createNodeScopedCfgObjs ? "node" : "server") +
            " scope");
        return dataSource;
    }




    private void reload(AdminClient adminClient, Session session)
        throws Exception {

        // retrieve the DataSourceCfgHelper MBean
        ObjectName mBean =
            retrieveJMXMBean(adminClient, "DataSourceCfgHelper");

        // call the reload operation
        Object result =
            adminClient.invoke(
                mBean,
                "reload",
                new Object[] {},
                new String[] {});

        if (result != null) {
            System.err.println(
                "DataSourceCfgHelper MBean reload operation failed: "
                + result);
        }
        else {
            System.out.println("Reloaded DataSourceCfgHelper MBean");
        }
    }
    
   

    // find the specified MBean
    @SuppressWarnings("unchecked")
    private ObjectName retrieveJMXMBean(AdminClient adminClient,
        String beanName)
        throws MalformedObjectNameException, ConnectorException {
        // retrieve the ConfigService MBean
        ObjectName mBean = null;
        ObjectName queryName =
            new ObjectName("WebSphere:type=" + beanName + ",*");
        Set names = adminClient.queryNames(queryName, null);
        if (!names.isEmpty()) {
            mBean = (ObjectName) names.iterator().next();
        }
        return mBean;
        
        
        
        
        */
        
    }
        





package sample.tomcat.jndi.config;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.env.Environment;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import sample.tomcat.jndi.utils.DataSourceUtil;

@Configuration
@EnableScheduling
@ImportResource({"classpath:cxf-servlet.xml"})
public class AppConfig {
	
	private static final Logger log = LoggerFactory.getLogger(AppConfig.class); 

	@Autowired
	private Environment env;
	
	@Autowired
	private DataSourceUtil dataSourceUtil;
	
	public static int port;

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	
	@Scheduled(
			initialDelay = 1000,
			fixedRate = 60000
			)
    public void run() {
        log.info("Running scheduled task at {}", dateFormat.format(new Date()));

        try {
			String db = env.getProperty("jdbc.abc.datasource.name");
			dataSourceUtil.getDataSourcefromJNDI(db);
			dataSourceUtil.getDataSourcefromFactoryBean(db);
        } catch (NamingException e) {
			e.printStackTrace();
		}
    }
	
	// various @Bean definitions

	@Bean
	public ServletRegistrationBean cxfServletRegistrationBean() {
		ServletRegistrationBean registrationBean = new ServletRegistrationBean(new CXFServlet(), "/services/*");
		registrationBean.setLoadOnStartup(1);
		registrationBean.setName("CXFServlet");
		registrationBean.setEnabled(true);
		return registrationBean;
	}
	
	@Bean
	public TomcatEmbeddedServletContainerFactory tomcatFactory() {
		return new TomcatEmbeddedServletContainerFactory() {

			@Override
			protected TomcatEmbeddedServletContainer getTomcatEmbeddedServletContainer(Tomcat tomcat) {
				tomcat.enableNaming();
				
				// results in a JNDI lookup failure when the lookup is performed
				// by the Scheduled task
				// return super.getTomcatEmbeddedServletContainer(tomcat);

				/**
				 * resolves the JNDI lookup issue noted above.
				 * 
				 * see to the following link for more info:
				 * https://stackoverflow.com/a/27825078/1384297
				 */
				TomcatEmbeddedServletContainer container = super.getTomcatEmbeddedServletContainer(tomcat);

				port = container.getPort();
				
				for (Container child : container.getTomcat().getHost().findChildren()) {
					if (child instanceof Context) {
						ClassLoader contextClassLoader = ((Context) child).getLoader().getClassLoader();
						Thread.currentThread().setContextClassLoader(contextClassLoader);
						break;
					}
				}

				return container;
			}

			@Override
			protected void postProcessContext(Context context) {
				
				// Abc datasource
				ContextResource resource = new ContextResource();
				resource.setName(env.getProperty("jdbc.abc.datasource.name"));
				resource.setType(DataSource.class.getName());
				resource.setProperty("driverClassName", env.getProperty("jdbc.abc.datasource.driver"));
				resource.setProperty("url", env.getProperty("jdbc.abc.datasource.url"));
				resource.setProperty("username", env.getProperty("jdbc.abc.datasource.username"));
				resource.setProperty("password", env.getProperty("jdbc.abc.datasource.password"));
				context.getNamingResources().addResource(resource);

				// other datasources ...
			}
			
		};
	}

    @Bean
    public DataSources getDataSources() {
    	return new DataSources() {
    		
			@Override
			public DataSource getDataSource(String db) throws IllegalArgumentException, NamingException {
				return jndiDataSources().get(db);
			}
    		
    	};
    }
	
	@Bean(destroyMethod = "")
	public HashMap<String, DataSource>jndiDataSources() throws IllegalArgumentException, NamingException {
		HashMap<String, DataSource> dataSources = new HashMap<String, DataSource>();
		
		// NOTE: these datasources must be defined in the TomcatEmbeddedServletContainerFactory bean's postProcessContext() method above.
		
		// Abc datasource
		JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
		bean.setJndiName(String.format("java:comp/env/%s", env.getProperty("jdbc.abc.datasource.name")));
		bean.setProxyInterface(DataSource.class);
		bean.setLookupOnStartup(true);
		bean.afterPropertiesSet();
		dataSources.put(env.getProperty("jdbc.abc.datasource.name"), (DataSource) bean.getObject());
		
		// other datasources ...
		
		return dataSources;		
	}
	
}

package sample.tomcat.jndi.config;

import java.text.SimpleDateFormat;
import java.util.Date;

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
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import sample.tomcat.jndi.utils.SampleUtil;

@Configuration
@EnableScheduling
@ImportResource({"classpath:cxf-servlet.xml"})
public class AppConfig {
	
	private static final Logger log = LoggerFactory.getLogger(AppConfig.class); 
	
	@Autowired
	private SampleUtil sampleUtil;
	
	public static int port;

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	
	@Scheduled(
			initialDelay = 1000,
			fixedRate = 60000
			)
    public void run() {
        log.info("Running scheduled task at {}", dateFormat.format(new Date()));

        try {
			sampleUtil.getDataSourcefromJNDI();
			sampleUtil.getDataSourcefromFactoryBean();
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
				ContextResource resource = new ContextResource();
				resource.setName("jdbc/myDataSource");
				resource.setType(DataSource.class.getName());
				resource.setProperty("driverClassName", "your.db.Driver");
				resource.setProperty("url", "jdbc:yourDb");
				context.getNamingResources().addResource(resource);
			}
			
		};
	}

	@Bean(destroyMethod="")
	public DataSource jndiDataSource() throws IllegalArgumentException, NamingException {
		JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
		bean.setJndiName("java:comp/env/jdbc/myDataSource");
		bean.setProxyInterface(DataSource.class);
		bean.setLookupOnStartup(false);
		bean.afterPropertiesSet();
		return (DataSource)bean.getObject();
	}

}

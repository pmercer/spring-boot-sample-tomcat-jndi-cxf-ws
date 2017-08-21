package sample.tomcat.jndi.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Register this SpringContextBridge as a Spring Component.
 * 
 * @see https://blog.jdriven.com/2015/03/using-spring-managed-bean-in-non-managed-object/
 */
@Component
public class SpringContextBridge implements SpringContextBridgedServices, ApplicationContextAware {

	private static ApplicationContext applicationContext;

	@Autowired
	private DataSourceService dataSourceService; // Autowire the DataSourceService
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	/**
	 * A static method to lookup the SpringContextBridgedServices Bean in the
	 * applicationContext. It is basically an instance of itself, which was
	 * registered by the @Component annotation.
	 *
	 * @return the SpringContextBridgedServices, which exposes all the Spring
	 *         services that are bridged from the Spring context.
	 */
	public static SpringContextBridgedServices services() {
		return applicationContext.getBean(SpringContextBridgedServices.class);
	}

	@Override
	public DataSourceService getDataSourceService() {
		return dataSourceService; // Return the Autowired DataSourceService
	}
	
}
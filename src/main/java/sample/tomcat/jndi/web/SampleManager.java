package sample.tomcat.jndi.web;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.ws.Service.Mode;
import javax.xml.ws.ServiceMode;

import org.apache.cxf.annotations.DataBinding;
import org.apache.cxf.feature.Features;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import sample.tomcat.jndi.config.DataSourceService;
import sample.tomcat.jndi.config.DataSources;
import sample.tomcat.jndi.config.SpringContextBridge;

@WebService(
		serviceName="SampleManager", 
		targetNamespace="http://jndi.tomcat.sample",
		portName="SampleManagerSOAPPort"
	)
	@Features(features = "org.apache.cxf.feature.LoggingFeature")        
	@DataBinding(org.apache.cxf.xmlbeans.XmlBeansDataBinding.class)
	@ServiceMode(value=Mode.MESSAGE)
public class SampleManager {

	@Autowired
	private Environment env;
	
	@Autowired
	private DataSources dataSources;
	
	@WebMethod(operationName="echo")
	public String echo(
			@XmlElement(required=true) @WebParam(name="input") String input)
	{
        return String.format("Hello, %s!", input);
	}

	@WebMethod(operationName="factoryBean")
	public String factoryBean() throws IllegalArgumentException, NamingException {
		return "DataSource retrieved from JNDI using JndiObjectFactoryBean: " + dataSources.getDataSource(env.getProperty("jdbc.abc.datasource.name"));
	}

	@WebMethod(operationName="direct")
	public String direct() throws NamingException {
		return "DataSource retrieved directly from JNDI: " +
				new InitialContext().lookup(String.format("java:comp/env/%s", env.getProperty("jdbc.abc.datasource.name")));
	}
	
	@WebMethod(operationName="factoryBeanFromBridge")
	public String factoryBeanFromBridge() {
		// Get the DataSourceService from the Bridge
		DataSourceService dataSourceService = SpringContextBridge.services().getDataSourceService();
		
		try {
			return dataSourceService.getDataSourcefromFactoryBean(env.getProperty("jdbc.abc.datasource.name")).toString();
		} catch (NamingException e) {
			return "NamingException: " + e;
		}
	}

	@WebMethod(operationName="directFromBridge")
	public String directFromBridge() throws NamingException {
		// Get the DataSourceService from the Bridge
		DataSourceService dataSourceService = SpringContextBridge.services().getDataSourceService();
		
		try {
			return dataSourceService.getDataSourcefromJNDI(env.getProperty("jdbc.abc.datasource.name")).toString();
		} catch (NamingException e) {
			return "NamingException: " + e;
		}
	}
	
}

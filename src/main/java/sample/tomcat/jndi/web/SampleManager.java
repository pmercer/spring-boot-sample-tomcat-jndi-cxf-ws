package sample.tomcat.jndi.web;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.ws.Service.Mode;
import javax.xml.ws.ServiceMode;

import org.apache.cxf.annotations.DataBinding;
import org.apache.cxf.feature.Features;
import org.springframework.beans.factory.annotation.Autowired;

import sample.tomcat.jndi.config.SampleUtilService;
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
	private DataSource dataSource;
    
	@WebMethod(operationName="echo")
	public String echo(
			@XmlElement(required=true) @WebParam(name="input") String input)
	{
        return String.format("Hello, %s!", input);
	}

	@WebMethod(operationName="factoryBean")
	public String factoryBean() {
		return "DataSource retrieved from JNDI using JndiObjectFactoryBean: " + dataSource;
	}

	@WebMethod(operationName="direct")
	public String direct() throws NamingException {
		return "DataSource retrieved directly from JNDI: " +
				new InitialContext().lookup("java:comp/env/jdbc/myDataSource");
	}
	
	@WebMethod(operationName="factoryBeanFromBridge")
	public String factoryBeanFromBridge() {
		// Get the SampleUtilService from the Bridge
		SampleUtilService sampleUtilService = SpringContextBridge.services().getSampleUtilService();
		
		try {
			return sampleUtilService.getDataSourcefromFactoryBean();
		} catch (NamingException e) {
			return "NamingException: " + e;
		}
	}

	@WebMethod(operationName="directFromBridge")
	public String directFromBridge() throws NamingException {
		// Get the SampleUtilService from the Bridge
		SampleUtilService sampleUtilService = SpringContextBridge.services().getSampleUtilService();
		
		try {
			return sampleUtilService.getDataSourcefromJNDI();
		} catch (NamingException e) {
			return "NamingException: " + e;
		}
	}
	
}

package sample.tomcat.jndi.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.apache.commons.lang.StringUtils;
import org.apache.xmlbeans.XmlObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.oxm.xmlbeans.XmlBeansMarshaller;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.ws.client.core.WebServiceTemplate;

import sample.tomcat.jndi.SampleApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SampleApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class SampleManagerTests {

    private static final Logger logger = LoggerFactory.getLogger(SampleManagerTests.class);

    protected WebServiceTemplate webServiceTemplate;
    
    @Autowired
    Environment env;
    
	@Before
	public void init() throws Exception {
		this.setupWebServiceTemplate("SampleManager");
	}

	@Test
	public void echo() throws Exception {
		String input = "testing ...";
		logger.debug(String.format("calling echo() with input [%s]", input));

		String soapBody =
				"    <jndi:echo xmlns:jndi=\"http://jndi.tomcat.sample\">\r\n" + 
				"         \r\n" + 
				"      <input>%s</input>\r\n" + 
				"      \r\n" + 
				"    </jndi:echo>";
		
	    XmlObject requestPayload = XmlObject.Factory.parse(String.format(soapBody, input));
	    XmlObject response = (XmlObject) webServiceTemplate.marshalSendAndReceive(requestPayload);
		assertThat(response).isNotNull();
	}
	
	@Test
	public void factoryBean() throws Exception {
		String soapBody = "<jndi:factoryBean xmlns:jndi=\"http://jndi.tomcat.sample\"/>";
	    XmlObject requestPayload = XmlObject.Factory.parse(soapBody);
	    XmlObject response = (XmlObject) webServiceTemplate.marshalSendAndReceive(requestPayload);
		assertThat(response).isNotNull();
	}

	@Test
	public void direct() throws Exception {
		String soapBody = "<jndi:direct xmlns:jndi=\"http://jndi.tomcat.sample\"/>";
	    XmlObject requestPayload = XmlObject.Factory.parse(soapBody);
	    XmlObject response = (XmlObject) webServiceTemplate.marshalSendAndReceive(requestPayload);
	    assertThat(response).isNotNull();
	}

	@Test
	public void factoryBeanFromBridge() throws Exception {
		String soapBody = "<jndi:factoryBeanFromBridge xmlns:jndi=\"http://jndi.tomcat.sample\"/>";
	    XmlObject requestPayload = XmlObject.Factory.parse(soapBody);
	    XmlObject response = (XmlObject) webServiceTemplate.marshalSendAndReceive(requestPayload);
		assertThat(response).isNotNull();
	}

	@Test
	public void directFromBridge() throws Exception {
		String soapBody = "<jndi:directFromBridge xmlns:jndi=\"http://jndi.tomcat.sample\"/>";
	    XmlObject requestPayload = XmlObject.Factory.parse(soapBody);
	    XmlObject response = (XmlObject) webServiceTemplate.marshalSendAndReceive(requestPayload);
	    assertThat(response.toString(), not(containsString("NamingException")));	    
	}
	
	private void setupWebServiceTemplate(String serviceName) throws Exception {
        try {
        	logger.debug("configuring WebServiceTemplate for {} ...", serviceName);
        	
            webServiceTemplate = new WebServiceTemplate();

            XmlBeansMarshaller marshaller = new XmlBeansMarshaller();
            webServiceTemplate.setMarshaller(marshaller);
            webServiceTemplate.setUnmarshaller(marshaller);
            
    		String path = null;
        	if (!StringUtils.isEmpty(env.getProperty("server.context-path"))) {
        		path = String.format("%s/services/%s", env.getProperty("server.context-path"), serviceName);
        	} else {
        		path = String.format("services/%s", serviceName);
        	}

    	    UriComponents uriComponents =
    	    		UriComponentsBuilder.newInstance()
    	    	      .scheme("http")
    	    	      .host("localhost")
    	    	      .port(env.getProperty("local.server.port"))
    	    	      .path(path)
    	    	      .build();
            
            webServiceTemplate.setDefaultUri(uriComponents.toString());

        	logger.debug("successfully configured WebServiceTemplate.");
        	
        } catch (final Exception e) {
            logger.error("Exception occured configuring WebServiceTemplate: {}", e);
            throw e;
        }
    }
	
}

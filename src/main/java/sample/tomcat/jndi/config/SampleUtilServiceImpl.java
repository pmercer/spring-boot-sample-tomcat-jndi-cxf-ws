package sample.tomcat.jndi.config;

import javax.naming.NamingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sample.tomcat.jndi.utils.SampleUtil;

@Service
public class SampleUtilServiceImpl implements SampleUtilService {

	@Autowired
	private SampleUtil sampleUtil;

	@Override
	public String getDataSourcefromJNDI() throws NamingException {
		return sampleUtil.getDataSourcefromJNDI();
	}

	@Override
	public String getDataSourcefromFactoryBean() throws NamingException {
		return sampleUtil.getDataSourcefromFactoryBean();
	}
	
}

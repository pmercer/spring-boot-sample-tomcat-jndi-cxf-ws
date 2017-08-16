package sample.tomcat.jndi.config;

import javax.naming.NamingException;

public interface SampleUtilService {
	public String getDataSourcefromJNDI() throws NamingException;
	public String getDataSourcefromFactoryBean() throws NamingException;
}

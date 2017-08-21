package sample.tomcat.jndi.config;

import javax.naming.NamingException;
import javax.sql.DataSource;

public interface DataSourceService {
	public DataSource getDataSourcefromJNDI(String db) throws NamingException;
	public DataSource getDataSourcefromFactoryBean(String db) throws NamingException;
}

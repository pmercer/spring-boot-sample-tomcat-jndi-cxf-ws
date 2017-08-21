package sample.tomcat.jndi.config;

import javax.naming.NamingException;
import javax.sql.DataSource;

public interface DataSources {
	 public DataSource getDataSource(String db) throws IllegalArgumentException, NamingException;
}

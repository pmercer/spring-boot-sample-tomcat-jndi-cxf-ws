package sample.tomcat.jndi.utils;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sample.tomcat.jndi.config.DataSources;

@Component
public class DataSourceUtil {

	private static final Logger log = LoggerFactory.getLogger(DataSourceUtil.class);	
	
	@Autowired
	private DataSources dataSources;
	
	public DataSource getDataSourcefromFactoryBean(String db) throws NamingException {
		log.debug("getting DataSource from FactoryBean for db {} ...", db);
		
		DataSource dataSource = dataSources.getDataSource(db);
		
		String response =
				String.format("DataSource retrieved from JNDI using JndiObjectFactoryBean: {%s}", dataSource);
		
		log.debug(response);
		
		return dataSource;
	}
	
	/**
	 * NOTE: this currently throws an exception when it is executed in the context of a CXF web service.
	 * @param db
	 * @return
	 * @throws NamingException
	 */
	public DataSource getDataSourcefromJNDI(String db) throws NamingException {
		log.debug("getting DataSource directly from JNDI for db {} ...", db);
		
		DataSource dataSource =
				(DataSource) new InitialContext().lookup("java:comp/env/" + db);

		String response =
				String.format("DataSource retrieved directly from JNDI: {%s}", dataSource);
		
		log.debug(response);
		
		return dataSource;
	}
	
}

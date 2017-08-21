package sample.tomcat.jndi.config;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sample.tomcat.jndi.utils.DataSourceUtil;

@Service
public class DataSourceServiceImpl implements DataSourceService {

	@Autowired
	private DataSourceUtil dataSourceUtil;

	@Override
	public DataSource getDataSourcefromJNDI(String db) throws NamingException {
		return dataSourceUtil.getDataSourcefromJNDI(db);
	}

	@Override
	public DataSource getDataSourcefromFactoryBean(String db) throws NamingException {
		return dataSourceUtil.getDataSourcefromFactoryBean(db);
	}
	
}

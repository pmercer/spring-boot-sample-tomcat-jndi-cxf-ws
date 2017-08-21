/*
 * Copyright 2012-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sample.tomcat.jndi.web;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import sample.tomcat.jndi.config.DataSources;
import sample.tomcat.jndi.utils.DataSourceUtil;

@Controller
public class SampleController {

	@Autowired
	private Environment env;
	
	@Autowired
	private DataSources dataSources;

	@Autowired
	private DataSourceUtil dataSourceUtil;
	
	@RequestMapping("/factoryBean")
	@ResponseBody
	public String factoryBean() throws NamingException {
		return "DataSource retrieved from JNDI using JndiObjectFactoryBean: " + dataSources.getDataSource(env.getProperty("jdbc.abc.datasource.name"));
	}

	@RequestMapping("/direct")
	@ResponseBody
	public String direct() throws NamingException {
		return "DataSource retrieved directly from JNDI: " +
				new InitialContext().lookup(String.format("java:comp/env/%s", env.getProperty("jdbc.abc.datasource.name")));
	}
	
	@RequestMapping("/factoryBeanFromUtil")
	@ResponseBody
	public String factoryBeanFromUtil() throws NamingException {
		return dataSourceUtil.getDataSourcefromFactoryBean(env.getProperty("jdbc.abc.datasource.name")).toString();
	}

	@RequestMapping("/directFromUtil")
	@ResponseBody
	public String directFromUtil() throws NamingException {
		return dataSourceUtil.getDataSourcefromJNDI(env.getProperty("jdbc.abc.datasource.name")).toString();
	}

}

package sample.tomcat.jndi.config;

/**
 * This interface represents a list of Spring Beans (services) which need to be
 * referenced from a non Spring class.
 * 
 * @see https://blog.jdriven.com/2015/03/using-spring-managed-bean-in-non-managed-object/
 */
public interface SpringContextBridgedServices {
	SampleUtilService getSampleUtilService();
}
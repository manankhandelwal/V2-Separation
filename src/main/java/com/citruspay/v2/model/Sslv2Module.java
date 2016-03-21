package com.citruspay.v2.model;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

import java.util.Properties;
import java.util.Set;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.configuration.api.ConfigurationUtilities;
import org.glassfish.hk2.configuration.persistence.properties.PropertyFileBean;
import org.glassfish.hk2.configuration.persistence.properties.PropertyFileHandle;
import org.glassfish.hk2.configuration.persistence.properties.PropertyFileService;
import org.glassfish.hk2.configuration.persistence.properties.PropertyFileUtilities;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.jvnet.hk2.annotations.Service;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sslv2Module extends AbstractBinder{

	/**
	 * The Config.
	 */
	private Properties applicationProperties;
	/**
	 * The Vertx.
	 */
	private Vertx vertx;

	private ServiceLocator locator;

	private static Logger log = LoggerFactory
			.getLogger(Sslv2Module.class);

	/**
	 * Instantiates a new Guice module.
	 *
	 * @param config
	 *            the config
	 * @param vertx
	 *            the vertx
	 */
	public Sslv2Module(Properties applicationProperties, Vertx vertx,
			ServiceLocator locator) {
		this.applicationProperties = applicationProperties;
		this.vertx = vertx;
		this.locator = locator;
	}

	/**
	 * Guice module
	 */
	@Override
	protected void configure() {
		// Populate the Vertx instance for application
		bind(vertx).to(Vertx.class);
		bind(vertx.eventBus()).to(EventBus.class);

		// Enable HK2 service integration
		ConfigurationUtilities.enableConfigurationSystem(locator);

		// Enable Properties service, to get service properties from a
		// Properties object
		PropertyFileUtilities.enablePropertyFileService(locator);

		// The propertyFileBean contains the mapping from type names to Java
		// Beans
		PropertyFileBean propertyFileBean = new PropertyFileBean();
		propertyFileBean.addTypeMapping("AerospikeConfigBean",
				AerospikeConfigBean.class);
		// Add in the mapping from type name to bean classes
		PropertyFileService propertyFileService = locator
				.getService(PropertyFileService.class);
		propertyFileService.addPropertyFileBean(propertyFileBean);

		// Add services to locator
		Reflections reflections = new Reflections("com.citruspay.v2.service");
		Set<Class<?>> annotated = reflections
				.getTypesAnnotatedWith(Service.class);
		for (Class<?> configuredBy : annotated) {
			log.info("ConfiguredBy class : {}", configuredBy.getSimpleName());
			ServiceLocatorUtilities.addClasses(locator, configuredBy);
		}

		PropertyFileHandle propertyFileHandle = propertyFileService
				.createPropertyHandleOfAnyType();
		propertyFileHandle.readProperties(applicationProperties);
	}

}

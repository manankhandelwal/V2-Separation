package com.citruspay.v2.model;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.glassfish.hk2.api.ServiceLocator;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The interface Vertx route.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface VertxRoute {
	/**
	 * Root path.
	 *
	 * @return the string
	 */
	String rootPath() default "/";

	/**
	 * The LOG.
	 */
	Logger LOG = LoggerFactory.getLogger(VertxRoute.class.getName());

	/**
	 * The interface Route.
	 */
	interface Route {
		/**
		 * Init void.
		 *
		 * @param vertx
		 *            the vertx
		 *
		 * @return the router
		 */
		Router init(Vertx vertx);
	}

	/**
	 * The type Loader.
	 */
	class Loader {
		/**
		 * Add package.
		 *
		 * @param packageName
		 *            the package name
		 *
		 * @return the routes in package
		 */
		public static Map<String, Route> getRoutesInPackage(String packageName,
				ServiceLocator locator) {
			Reflections reflections = new Reflections(packageName);
			Map<String, Route> routers = new HashMap<String, Route>();
			Set<Class<?>> annotated = reflections
					.getTypesAnnotatedWith(VertxRoute.class);
			for (Class<?> rit : annotated) {
				try {
					if (Route.class.isAssignableFrom(rit)) {
						// Route r = (Route) rit.getConstructor().newInstance();
						LOG.info("Startig route : {}", rit.getCanonicalName());
						Route r = (Route) locator.getService(rit);
						LOG.info("Route instance : {}", r);
						routers.put(rit.getAnnotation(VertxRoute.class)
								.rootPath(), r);
					}
				} catch (Exception ex) {
					LOG.error("Error starting routes", ex);
				}
			}
			return routers;
		}
	}
}

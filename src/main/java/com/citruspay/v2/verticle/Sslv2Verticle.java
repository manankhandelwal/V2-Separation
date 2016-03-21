/*
 * @author: Tausif
 * This is the main verticle that will be deployed.
 */

package com.citruspay.v2.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.file.FileSystem;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.citruspay.v2.model.Sslv2Module;
import com.citruspay.v2.model.VertxRoute;

public class Sslv2Verticle extends AbstractVerticle {

	private static Logger log = LoggerFactory.getLogger(Sslv2Verticle.class);

	/*@Override
	public void start() throws Exception {
		vertx.createHttpServer().requestHandler(r -> {
			r.response().end("<h1>SSLv2 on " + "Vert.x 3 </h1>");
		}).listen(8080, result -> {
			if (result.succeeded()) {
				System.out.println("Called Successfully");
				log.info("SSLv2 verticle deployed successfully.");
			} else {
				System.out.println("Failed");
			}
		});
	}*/
	
	@Override
	public void start() throws Exception {
		HttpServer server = vertx.createHttpServer();
		Router mainRouter = Router.router(vertx);
		Properties applicationProperties = loadApplicationProperties();
		ServiceLocator locator = ServiceLocatorUtilities
				.createAndPopulateServiceLocator();

		ServiceLocatorUtilities.bind(locator, new Sslv2Module(
				applicationProperties, vertx, locator));
		/*CitrusCacheProviderMigrationService citrusCacheProviderMigrationService = locator
				.getService(CitrusCacheProviderMigrationService.class);
		log.info("CitrusCacheProviderMigrationService instance {}",
				citrusCacheProviderMigrationService);*/
		VertxRoute.Loader
				.getRoutesInPackage("com.citruspay.v2.api", locator)
				.forEach((key, route) -> {
					log.info("Service route instance {}", route);
					mainRouter.mountSubRouter(key, route.init(vertx));
				});
		server.requestHandler(mainRouter::accept)
				.listen(Integer.parseInt(applicationProperties
						.getProperty("app.port")));
	}

	private Properties loadApplicationProperties() {
		Properties applicationProperties = new Properties();
		FileSystem fs = vertx.fileSystem();
		InputStream in = new ByteArrayInputStream(fs.readFileBlocking(
				"local/app.properties").getBytes());
		try {
			applicationProperties.load(in);
		} catch (IOException e) {
			log.error("Loading of application properties failed", e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				log.error(
						"Closing of input stream  for application properties failed",
						e);
			}
		}
		return applicationProperties;
	}

}

/*
 * @author: Tausif
 * This is the main verticle that will be deployed.
 */

package com.citruspay.v2.verticle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;

public class SslV2Verticle extends AbstractVerticle {

	private static Logger log = LoggerFactory.getLogger(SslV2Verticle.class);

	@Override
	public void start() throws Exception {
		vertx.createHttpServer().requestHandler(r -> {
			r.response().end("<h1>SSLv2 on " + "Vert.x 3 </h1>");
		}).listen(8080, result -> {
			if (result.succeeded()) {
				System.out.println("Called Successfully");
			} else {
				System.out.println("Failed");
			}
		});
	}
}

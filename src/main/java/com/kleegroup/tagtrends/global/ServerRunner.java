package com.kleegroup.tagtrends.global;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;

import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.StaticHttpHandler;

import javax.ws.rs.core.UriBuilder;

import java.io.IOException;
import java.net.URI;

/**
 * Lance le serveur Grizzly pour gérer les WebServices de TagTrends.
 * Utilise port définit dans la variable d'environnement "jersey.test.port" (par défaut 9998)
 * Ecoute les services sur http://localhost:<<port>>/
 * @author cguinard
 */
public class ServerRunner {
	public static final URI BASE_URI = getBaseURI();

	/**
	 * Lancement du serveur Grizzly pour les WebServices.
	 * @param args paramètres (non utilisés)
	 * @throws IOException Si erreur
	 */
	public static void main(String[] args) throws IOException {
		HttpServer httpServer = startServer();
		System.out
				.println(String
						.format("Jersey app started with WADL available at "
								+ "%sapplication.wadl\nTry out %sdata\nHit enter to stop it...",
								BASE_URI, BASE_URI));
		System.in.read();
		httpServer.stop();
	}

	protected static HttpServer startServer() throws IOException {

		System.out.println("Starting grizzly...");
		ResourceConfig rc = new PackagesResourceConfig("");
		HttpServer httpServer = GrizzlyServerFactory.createHttpServer(BASE_URI,
				rc);
//		httpServer.getServerConfiguration().addHttpHandler(
//				new CLStaticHttpHandler(ServerRunner.class.getClassLoader()));
		// TODO : faire marcher !!
		
		httpServer.getServerConfiguration().addHttpHandler(
				new StaticHttpHandler(
						"src/main/resources/static"),
				"/static");
		return httpServer;
	}

	private static int getPort(int defaultPort) {
		String port = System.getProperty("jersey.test.port");
		if (null != port) {
			try {
				return Integer.parseInt(port);
			} catch (NumberFormatException e) {
			}
		}
		return defaultPort;
	}

	private static URI getBaseURI() {
		return UriBuilder.fromUri("http://localhost/").port(getPort(9998))
				.build();
	}
}

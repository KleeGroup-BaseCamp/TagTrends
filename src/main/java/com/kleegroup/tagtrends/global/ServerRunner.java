package com.kleegroup.tagtrends.global;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;

/**
 * Lance le serveur Grizzly pour gérer les WebServices de TagTrends.
 * Utilise port définit dans la variable d'environnement "jersey.test.port" (par défaut 9998)
 * Ecoute les services sur http://localhost:<<port>>/
 * @author cguinard
 */
public class ServerRunner {
	public static final URI BASE_URI = getBaseURI();
	public static final String PROTECTED_COLLECTION = "--rien--"; //"sampleData"
	public static final String DATABASE_IP = "192.168.247.11"; //"mongostd.dev.klee.lan.net"

	/**
	 * Lancement du serveur Grizzly pour les WebServices.
	 * @param args paramètres (non utilisés)
	 * @throws IOException Si erreur
	 */
	public static void main(final String[] args) throws IOException {
		final HttpServer httpServer = startServer();
		System.out.println(String.format("Jersey app started with WADL available at " + "%sapplication.wadl\nTry out %sdata", BASE_URI, BASE_URI));
		try {

			final Object lock = new Object();
			synchronized (lock) {
				lock.wait(0); //on attend le temps demandé et 0 => illimité
			}
		} catch (final InterruptedException e) {
			//rien arret normal
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			httpServer.shutdownNow();
		}
	}

	protected static HttpServer startServer() throws IOException {
		System.out.println("Starting grizzly...");
		final HttpServer httpServer = GrizzlyServerFactory.createHttpServer(BASE_URI/*, rc*/);
		httpServer.getServerConfiguration().addHttpHandler(new CLStaticHttpHandler(ServerRunner.class.getClassLoader(), "/static/"), "/static");
		return httpServer;
	}

	private static int getPort(final int defaultPort) {
		final String port = System.getProperty("jersey.test.port");
		if (null != port) {
			return Integer.parseInt(port);
		}
		return defaultPort;
	}

	private static URI getBaseURI() {
		return UriBuilder.fromUri("http://0.0.0.0/").port(getPort(9998)).build();
	}
}

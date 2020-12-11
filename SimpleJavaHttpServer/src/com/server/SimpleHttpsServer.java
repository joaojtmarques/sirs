package com.server;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.*;
import java.util.concurrent.*;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;

import com.sun.net.httpserver.*;
import org.json.JSONObject;

public class SimpleHttpsServer {
	private int port;
	private HttpsServer server;
	private static String protocol = "TLS";
	private DataStore _datastore = new DataStore();

	public void Start(int port) {
		try {
			this.port = port;

			// load certificate
			String keystoreFilename = "F:/Universidade/4th_Year/SIRS/sirs/Server/serverKeyStore.p12";
			char[] storepass = "mypassword".toCharArray();
			char[] keypass = "mypassword".toCharArray();
			String alias = "1";
			FileInputStream fIn = new FileInputStream(keystoreFilename);
			KeyStore keystore = KeyStore.getInstance("pkcs12");
			keystore.load(fIn, storepass);

			String trustStoreFilename = "F:/Universidade/4th_Year/SIRS/sirs/Server/serverTrustStore";
			FileInputStream fIn2 = new FileInputStream(trustStoreFilename);
			KeyStore truststore = KeyStore.getInstance("pkcs12");
			truststore.load(fIn2, storepass);

			// display certificate
			Certificate cert = keystore.getCertificate(alias);
			System.out.println(cert);

			// setup the key manager factory
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(keystore, keypass);

			// setup the trust manager factory
			TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
			tmf.init(truststore);

			// create https server
			server = HttpsServer.create(new InetSocketAddress(port), 0);
			// create ssl context
			SSLContext sslContext = SSLContext.getInstance(protocol);
			// setup the HTTPS context and parameters
			sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
			server.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
				public void configure(HttpsParameters params) {
					try {
						// initialise the SSL context
						SSLContext c = SSLContext.getDefault();
						SSLEngine engine = c.createSSLEngine();
						params.setNeedClientAuth(true);
						params.setCipherSuites(engine.getEnabledCipherSuites());
						params.setProtocols(engine.getEnabledProtocols());

						// get the default parameters
						SSLParameters defaultSSLParameters = c.getDefaultSSLParameters();
						params.setSSLParameters(defaultSSLParameters);


					} catch (Exception ex) {
						ex.printStackTrace();
						System.out.println("Failed to create HTTPS server");
					}
				}
			});

			System.out.println("server started at " + port);
			server.createContext("/test", new TestHandler());
			server.createContext("/bind-request", new SimpleHttpsServer.BindRequestHandler());
			server.createContext("/bind-confirmation", new SimpleHttpsServer.BindConfirmationHandler());
			server.createContext("/bind-check", new SimpleHttpsServer.BindCheckHandler());
			server.createContext("/post-location", new SimpleHttpsServer.PostLocationHandler());
			server.createContext("/get-location", new SimpleHttpsServer.GetLocationHandler());
			// start the server
			ThreadPoolExecutor tpe = new ThreadPoolExecutor(5, 10, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
			server.setExecutor(tpe);
			server.start();

		} catch (IOException | NoSuchAlgorithmException | KeyManagementException | KeyStoreException | UnrecoverableKeyException | CertificateException e) {
			e.printStackTrace();
		}
	}

	private String getPath() {
		return this.getClass().getClassLoader().getResource("").getPath() + "com/server/";
	}


	// TEST HANDLERS
	public class TestHandler implements HttpHandler {

		@Override
		public void handle(HttpExchange he) throws IOException {
			System.out.println("-> Received test request.");
			System.out.println("\t- Request IP:" + he.getRemoteAddress());

			// send response
			String response = "{\"msg\": \"Server up and running.\"}";
			he.getResponseHeaders().set("Content-Type", "application/json");
			he.sendResponseHeaders(200, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
	}

	// HANDLERS
	public class BindRequestHandler implements HttpHandler {
		// receives json with
		// 1. guardian's user key
		// returns uniqueID for the association

		@Override
		public void handle(HttpExchange he) throws IOException {
			System.out.println("-> Received bind-request request.");
			System.out.println("\t- Request IP:" + he.getRemoteAddress());

			// get request message
			InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String requestString = br.readLine();
			JSONObject request = new JSONObject(requestString);

			// add key to requestedAssociations
			String premiumKey = request.getString("premiumKey");
			String uniqueID = UUID.randomUUID().toString();
			Boolean success = _datastore.addRequestedAssociation(uniqueID, premiumKey);
			String ack = success ? "Request was made." : "No remaining requests available.";

			System.out.println("\t- Ack: " + ack);

			// create JSON object with response
			JSONObject jo = new JSONObject();
			jo.put("id", uniqueID);
			jo.put("ack", ack);

			// send response
			String response = jo.toString();
			he.getResponseHeaders().set("Content-Type", "application/json");
			he.sendResponseHeaders(200, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
	}


	public class BindConfirmationHandler implements HttpHandler {
		// receives JSON with
		// 1. uniqueID;
		// to complete association with guardian

		@Override
		public void handle(HttpExchange he) throws IOException {
			System.out.println("-> Received bind-confirmation request.");

			// get request message
			InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String requestString = br.readLine();
			System.out.println("\t- Request string: " + requestString);
			JSONObject request = new JSONObject(requestString);

			// check parameters and complete association
			String uniqueId = request.getString("associationId");
			System.out.println("\t- Association: " + uniqueId);
			String ack = "";
			if (_datastore.hasRequestedAssociation(uniqueId)) {
				_datastore.removeRequestedAssociation(uniqueId);
				_datastore.addAssociation(uniqueId);
				ack = "Bind successful.";
			}
			else {
				ack = "Bind unsuccessful.";
			}

			System.out.println("\t- ACK: " + ack);

			// create JSON object with ack response
			JSONObject jo = new JSONObject();
			jo.put("ack", ack);

			// send response
			String response = jo.toString();
			he.getResponseHeaders().set("Content-Type", "application/json");
			he.sendResponseHeaders(200, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
	}


	public class BindCheckHandler implements HttpHandler {
		// receives JSON with
		// 1. associationId
		// returns ack if association exists

		@Override
		public void handle(HttpExchange he) throws IOException {
			System.out.println("-> Received bind-check request.");

			// get request message
			InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String requestString = br.readLine();
			System.out.println("\t- Request string: " + requestString);
			JSONObject request = new JSONObject(requestString);

			// get associationId
			String associationId = request.getString("associationId");
			System.out.println("\t- Association id: " + associationId);

			// check if association exists
			String ack = _datastore.hasAssociation(associationId) ? "Child was successfully associated." : "Child was not associated.";

			System.out.println("\t- Ack: " + ack);

			// create JSON object with ack response
			JSONObject jo = new JSONObject();
			jo.put("ack", ack);
			String response = jo.toString();

			// send response
			he.getResponseHeaders().set("Content-Type", "application/json");
			he.sendResponseHeaders(200, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
	}


	public class PostLocationHandler implements HttpHandler {
		// receives JSON with
		// 1. associationId
		// 2. data
		// stores data

		@Override
		public void handle(HttpExchange he) throws IOException {
			System.out.println("-> Received post-location request.");

			// get request message
			InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String requestString = br.readLine();
			System.out.println("\t- Request string: " + requestString);
			JSONObject request = new JSONObject(requestString);

			// get associationId and data
			String associationId = request.getString("associationId");
			System.out.println("\t- Association id: " + associationId);
			String data = request.getString("data"); //64 base encoded ciphered
			System.out.println("\t- Data: " + data);

			//store data
			String ack = "";
			if (_datastore.hasAssociation(associationId)) {
				_datastore.addLocationData(associationId, data);
				ack = "Location stored successfully.";
			}
			else {
				ack = "Couldn't find association id, location was not stored.";
			}

			System.out.println("\t- Ack: " + ack);

			// create JSON object with ack response
			JSONObject jo = new JSONObject();
			jo.put("ack", ack);
			String response = jo.toString();

			// send response
			he.getResponseHeaders().set("Content-Type", "application/json");
			he.sendResponseHeaders(200, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
	}


	public class GetLocationHandler implements HttpHandler {
		// returns latest data from a certain association given through url

		@Override
		public void handle(HttpExchange he) throws IOException {
			System.out.println("-> Received get-location request.");

			// parse request
			Map<String, Object> parameters = new HashMap<>();
			URI requestedUri = he.getRequestURI();
			String query = requestedUri.getRawQuery();
			parseQuery(query, parameters);

			// get data
			String associationId = (String) parameters.get("id");
			String data = _datastore.getData(associationId);

			// create JSON object with data
			JSONObject jo = new JSONObject();
			jo.put("value", data);

			System.out.println("\t- Locations: " + jo.toString());

			// send response
			String response = jo.toString();
			he.getResponseHeaders().set("Content-Type", "application/json");
			he.sendResponseHeaders(200, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
	}

	@SuppressWarnings("unchecked")
	public void parseQuery(String query, Map<String, Object> parameters) throws UnsupportedEncodingException {

		if (query != null) {
			String pairs[] = query.split("[&]");

			for (String pair : pairs) {
				String param[] = pair.split("[=]");

				String key = null;
				String value = null;
				if (param.length > 0) {
					key = URLDecoder.decode(param[0], System.getProperty("file.encoding"));
				}

				if (param.length > 1) {
					value = URLDecoder.decode(param[1], System.getProperty("file.encoding"));
				}

				if (parameters.containsKey(key)) {
					Object obj = parameters.get(key);
					if (obj instanceof List<?>) {
						List<String> values = (List<String>) obj;
						values.add(value);
					} else if (obj instanceof String) {
						List<String> values = new ArrayList<String>();
						values.add((String) obj);
						values.add(value);
						parameters.put(key, values);
					}
				} else {
					parameters.put(key, value);
				}
			}
		}
	}


	public void Stop() {
		server.stop(0);
		System.out.println("server stopped");
	}
}

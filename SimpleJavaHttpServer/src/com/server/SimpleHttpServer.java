package com.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.util.*;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import org.json.JSONArray;
import org.json.JSONObject;

public class SimpleHttpServer {
	private int port;
	private HttpServer server;

	private DataStore _datastore = new DataStore();

	public void Start(int port) {
		try {
			this.port = port;
			server = HttpServer.create(new InetSocketAddress(this.port), 0);
			System.out.println("server started at " + port);
			server.createContext("/test", new TestHandler());
			server.createContext("/bind-request", new BindRequestHandler());
			server.createContext("/bind-confirmation", new BindConfirmationHandler());
			server.createContext("/bind-check", new BindCheckHandler());
			server.createContext("/post-location", new PostLocationHandler());
			server.createContext("/get-location", new GetLocationHandler());
			server.setExecutor(null);
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void Stop() {
		server.stop(0);
		System.out.println("server stopped");
	}


	public class TestHandler implements HttpHandler {

		@Override
		public void handle(HttpExchange he) throws IOException {
			System.out.println("-> Received test request.");
			System.out.println("\t- Request IP:" + he.getRemoteAddress());

			// send response
			String response = "Server up and running.";
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
		// 1. guardian's public key
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
			String pKey = request.getString("publicKey");
			String premiumKey = request.getString("premiumKey");
			System.out.println("\t- PublicKey: " + pKey);
			String uniqueID = UUID.randomUUID().toString();
			Boolean success = _datastore.addRequestedAssociation(pKey, uniqueID, premiumKey);
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
		// 1. guardian's public key
		// 2. uniqueID;
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
			String pKey = request.getString("publicKey");
			System.out.println("\t- PKey: " + pKey);
			String uniqueId = request.getString("associationId");
			System.out.println("\t- Association: " + uniqueId);
			String ack = "";
			if (_datastore.hasRequestedAssociation(pKey) && _datastore.getRequestedAssociation(pKey).equals(uniqueId)) {
				_datastore.removeRequestedAssociation(pKey);
				_datastore.addAssociation(uniqueId, pKey);
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
			/*JSONObject jo = new JSONObject();
			JSONArray jsArray = new JSONArray();
			for (String s : data) {
				JSONObject loc = new JSONObject();
				loc.put("value", s);
				jsArray.put(loc);
			}
			jo.put("locations", jsArray);*/

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
}

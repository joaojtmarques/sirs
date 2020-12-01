package com.happylife.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.util.*;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import org.json.JSONArray;
import org.json.JSONObject;

public class SimpleHttpServer {
	private int port;
	private HttpServer server;

	// maps guardian's public key to a requested association's id
	private HashMap<String, String> _requestedAssociations = new HashMap<>();

	// maps association id to public key of who has access
	private HashMap<String, String> _associations = new HashMap<>();

	// maps child id to association id
	private HashMap<String, String> _children = new HashMap<>();

	// maps association id to child's data
	private HashMap<String, ArrayList<Byte[]>> _data = new HashMap<>();

	public void Start(int port) {
		try {
			this.port = port;
			server = HttpServer.create(new InetSocketAddress(this.port), 0);
			System.out.println("server started at " + port);
			server.createContext("/bind-request", new BindRequestHandler());
			server.createContext("/bind-confirmation", new BindConfirmationHandler());
			server.createContext("/post", new PostHandler());
			server.createContext("/get", new GetHandler());
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





	// HANDLERS
	public class BindRequestHandler implements HttpHandler {
		// receives json with
		// 1. guardian's public key
		// returns uniqueID for the association

		@Override
		public void handle(HttpExchange he) throws IOException {
			System.out.println("Received bind-request request.");

			// get request message
			InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String request = br.readLine();

			// add key to requestedAssociations
			String pKey = request;
			System.out.println(pKey);
			String uniqueID = UUID.randomUUID().toString();
			_requestedAssociations.put(pKey, uniqueID);

			// create JSON object with data
			JSONObject jo = new JSONObject();
			jo.put("id", uniqueID);

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
		// 3. uniqueChildID
		// to complete association with guardian

		@Override
		public void handle(HttpExchange he) throws IOException {
			System.out.println("Received bind-confirmation request.");

			// parse request
			Map<String, Object> parameters = new HashMap<>();
			InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String query = br.readLine();
			parseQuery(query, parameters);

			// check parameters and complete association
			String pKey = (String) parameters.get("key");
			String uniqueID = (String) parameters.get("id");
			String childID = (String) parameters.get("childID");
			if (_requestedAssociations.containsKey(pKey) && _requestedAssociations.get(pKey).equals(uniqueID)) {
				_requestedAssociations.remove(pKey);
				_associations.put(uniqueID, pKey);
				_children.put(childID, uniqueID);
			}

			// send response
			he.sendResponseHeaders(200, -1);
		}
	}


	public class PostHandler implements HttpHandler {
		// receives JSON with
		// 1. childID
		// 2. data
		// stores data

		@Override
		public void handle(HttpExchange he) throws IOException {
			// parse request
			Map<String, Object> parameters = new HashMap<>();
			InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String query = br.readLine();
			parseQuery(query, parameters);

			// get data and store it
			String childID = (String) parameters.get("childID");
			Byte[] data = (Byte[]) parameters.get("data");
			String associationID = _children.get(childID);
			_data.get(associationID).add(data);

			// send response
			he.sendResponseHeaders(200, -1);
		}
	}


	public class GetHandler implements HttpHandler {
		// returns latest data from a certain association

		@Override
		public void handle(HttpExchange he) throws IOException {
			// parse request
			Map<String, Object> parameters = new HashMap<>();
			InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String query = br.readLine();
			parseQuery(query, parameters);

			// get data
			String associationID = (String) parameters.get("association");
			Byte[] data = _data.get(associationID).get(_data.get(associationID).size() - 1);

			// create JSON object with data
			JSONArray ja = new JSONArray();
			JSONObject jo = new JSONObject();
			jo.put("data", data);
			ja.put(jo);

			// send response
			String response = ja.toString();
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

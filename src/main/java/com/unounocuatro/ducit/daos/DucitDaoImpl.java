package com.unounocuatro.ducit.daos;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DucitDaoImpl implements DucitDAO{

	private static final String DB_URL = "jdbc:mysql://localhost:3306/ducit";

	//  Database credentials
	private static final String USER = "root";
	private static final String PASS = "weblogic1";

	public String getWordMeaning(String word) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		conn = DriverManager.getConnection(DB_URL,USER,PASS);
		stmt = conn.createStatement();
		String sql;
		sql = "SELECT meaning FROM word w WHERE w.word LIKE '" + word + "'";
		ResultSet rs = stmt.executeQuery(sql);
		return (rs.next())? rs.getString(1) : null;
	}

	public String getDefinition(String word) throws Exception {
		int i=0;
		URL url = new URL("https://es.wikipedia.org/w/api.php?action=query&list=search&srsearch=" + word.replace(" ", "%20") + "&format=json");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");

		if (conn.getResponseCode() != 200) throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());


		BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));

		StringBuilder sb = new StringBuilder();

		String line;
		while ((line = br.readLine()) != null) sb.append(line);
			
		JsonElement jelement = new JsonParser().parse(sb.toString());
		JsonObject  jobject = jelement.getAsJsonObject();
		sb.setLength(0);
		int size = jobject.getAsJsonObject("query").getAsJsonArray("search").size();
		while(i < size){
			JsonElement locObj = jobject.getAsJsonObject("query")
					.getAsJsonArray("search").get(i);
			sb.append(locObj.getAsJsonObject().get("snippet").getAsString());
			i++;
		}

		return sb.toString();
	}

	public String getSynonyms(String word) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		conn = DriverManager.getConnection(DB_URL,USER,PASS);
		stmt = conn.createStatement();
		String sql;
		sql = "SELECT synonym FROM word w WHERE w.word LIKE '" + word + "'";
		ResultSet rs = stmt.executeQuery(sql);
		return (rs.next())? rs.getString(1) : null;
	}
	
	public String getAntonyms(String word) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		conn = DriverManager.getConnection(DB_URL,USER,PASS);
		stmt = conn.createStatement();
		String sql;
		sql = "SELECT antonym FROM word w WHERE w.word LIKE '" + word + "'";
		ResultSet rs = stmt.executeQuery(sql);
		return (rs.next())? rs.getString(1) : null;
	}

}

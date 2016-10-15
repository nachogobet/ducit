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
import com.unounocuatro.ducit.utils.DucitUtils;

public class DucitDaoImpl implements DucitDAO{

	private static final String DB_URL = "jdbc:mysql://localhost:3306/ducit";

	//  Database credentials
	private static final String USER = "root";
	private static final String PASS = "weblogic1";

	public String getWordMeaning(String word) throws SQLException {
		Connection conn = DriverManager.getConnection(DB_URL,USER,PASS);
		Statement stmt = conn.createStatement();
		String sql;
		sql = "SELECT meaning FROM word w WHERE w.word LIKE '" + word + "'";
		ResultSet rs = stmt.executeQuery(sql);
		String result = new String();

		while(rs.next()){
			result += rs.getString(1) + "%";
		}
		
		return result;
	}

	public String getDefinition(String word) throws Exception {
		int i=0;
		URL url = new URL("https://es.wikipedia.org/w/api.php?action=query&list=search&srsearch=" + word.replaceAll("(\\r|\\n)", "").replace(" ", "%20") + "&format=json");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");


		BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));

		StringBuilder sb = new StringBuilder();

		String line;
		while ((line = br.readLine()) != null) sb.append(line);
			
		JsonElement jelement = new JsonParser().parse(sb.toString());
		JsonObject  jobject = jelement.getAsJsonObject();
		sb.setLength(0);
		try{
			int size = jobject.getAsJsonObject("query").getAsJsonArray("search").size();
			while(i < size){
				JsonElement locObj = jobject.getAsJsonObject("query")
						.getAsJsonArray("search").get(i);
				sb.append(locObj.getAsJsonObject().get("snippet").getAsString());
				i++;
			}
		} catch(NullPointerException e){
			return null;
		}
		

		return sb.toString();
	}

	public String getSynonyms(String word) throws SQLException {
		Connection conn = DriverManager.getConnection(DB_URL,USER,PASS);
		Statement stmt = conn.createStatement();
		String sql;
		sql = "SELECT Synonym FROM word w WHERE w.word LIKE '" + word + "'";
		ResultSet rs = stmt.executeQuery(sql);

		if(rs.next() && rs.getString(1)!= null && !rs.getString(1).isEmpty())
			return rs.getString(1).replace("|", "-");
		else
			return "no hay sinonimos para esta palabra";
	}
	
	public String getAntonyms(String word) throws SQLException {
		Connection conn = DriverManager.getConnection(DB_URL,USER,PASS);
		Statement stmt = conn.createStatement();
		String sql;
		sql = "SELECT antonym FROM word w WHERE w.word LIKE '" + word + "'";
		ResultSet rs = stmt.executeQuery(sql);
		
		if(rs.next() && rs.getString(1)!= null && !rs.getString(1).isEmpty())
			return rs.getString(1).replace("|", "-");
		else
			return "no hay sinonimos para esta palabra";
	}

	public String fixWord(String word) throws SQLException {
		// ESTE CODIGO DUCIT FUE CREADO POR EL PROJECT MANAGER y EL FOUNDER
		int min = Integer.MAX_VALUE;
		String correct = new String(word);
		Connection conn = DriverManager.getConnection(DB_URL,USER,PASS);
		Statement stmt = conn.createStatement();
		String pattern = DucitUtils.getSQLPattern(word);
		String sql;
		
		if(word.charAt(word.length()-1) == 'm')
			sql = "SELECT word FROM word w WHERE w.word REGEXP '" + pattern + "' OR w.word REGEXP '" + pattern.substring(0, pattern.length()-15) + "'"+ " OR w.word REGEXP '" + pattern.substring(0, pattern.length()-22) + "'";
		else if(word.charAt(word.length()-2) == 'm')
			sql = "SELECT word FROM word w WHERE w.word REGEXP '" + pattern + "' OR w.word REGEXP '" + pattern.substring(0, pattern.length()-7) + "'"+ " OR w.word REGEXP '" + pattern.substring(0, pattern.length()-22) + "'";
		else	
			sql = "SELECT word FROM word w WHERE w.word REGEXP '" + pattern + "' OR w.word REGEXP '" + pattern.substring(0, pattern.length()-7) + "'"+ " OR w.word REGEXP '" + pattern.substring(0, pattern.length()-14) + "'";

		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next()){
			if(rs.getString(1)!=null){
				int distance = DucitUtils.getLevenshteinDistance(word, rs.getString(1));
				if(distance < min){
					min = distance;
					correct = rs.getString(1);
				}
			}
		}
		
		return correct;
	}

}

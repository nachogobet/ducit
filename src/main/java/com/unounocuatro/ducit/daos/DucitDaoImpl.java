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
		String correct = new String(word);
		String result = new String();
		int min = Integer.MAX_VALUE;
		String definiteWord = DucitUtils.getSQLPattern(word);
		Connection conn = null;
		Statement stmt = null;
		conn = DriverManager.getConnection(DB_URL,USER,PASS);
		stmt = conn.createStatement();
		String sql;
		sql = "SELECT meaning,word FROM word w WHERE w.word LIKE '" + definiteWord + "'";
		ResultSet rs = stmt.executeQuery(sql);
		
		while(rs.next()){
			if(rs.getString(1)!=null){
				int distance = DucitUtils.getLevenshteinDistance(word, rs.getString(2));
				if(distance < 3 && distance < min){
					min = distance;
					result = rs.getString(1);
					correct = rs.getString(2);
				} else if(distance == min){
					result += "%" + rs.getString(1);
				}
			}
		}
		
		return correct + "zzz" + result;
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
		String correct = new String();
		String result = new String();
		int min = Integer.MAX_VALUE;
		String definiteWord = DucitUtils.getSQLPattern(word);
		Connection conn = null;
		Statement stmt = null;
		conn = DriverManager.getConnection(DB_URL,USER,PASS);
		stmt = conn.createStatement();
		String sql;
		sql = "SELECT synonym, word FROM word w WHERE w.word LIKE '" + definiteWord + "'";
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next()){
			if(rs.getString(1)!=null){
				int distance = DucitUtils.getLevenshteinDistance(word, rs.getString(2));
				if(distance < min){
					min = distance;
					result = rs.getString(1);
					correct = rs.getString(2);
				}
			}
		}
		
		return correct + "zzz" + result.replace("|", "-");
	}
	
	public String getAntonyms(String word) throws SQLException {
		String result = new String();
		String correct = new String();
		int min = Integer.MAX_VALUE;
		String definiteWord = DucitUtils.getSQLPattern(word);
		Connection conn = null;
		Statement stmt = null;
		conn = DriverManager.getConnection(DB_URL,USER,PASS);
		stmt = conn.createStatement();
		String sql;
		sql = "SELECT antonym, word FROM word w WHERE w.word LIKE '" + definiteWord + "'";
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next()){
			if(rs.getString(1)!=null){
				int distance = DucitUtils.getLevenshteinDistance(word, rs.getString(2));
				if(distance < min){
					min = distance;
					result = rs.getString(1);
					correct = rs.getString(2);
				}
			}
		}
		
		return correct + "zzz" + result.replace("|", "-");
	}

}

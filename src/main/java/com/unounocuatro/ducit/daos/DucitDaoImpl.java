package com.unounocuatro.ducit.daos;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.unounocuatro.ducit.utils.DucitUtils;

public class DucitDaoImpl implements DucitDAO{

	public DucitDaoImpl(Connection conn){
		this.conn = conn;
	}

	private Connection conn;



	//  Database credentials


	public String getWordMeaning(String word) throws SQLException {
		Statement stmt = conn.createStatement();
		String sql;
		sql = "SELECT meaning FROM word w WHERE w.word LIKE '" + word + "'";
		ResultSet rs = stmt.executeQuery(sql);
		String result = new String("SIGNIFICADOS:");
		int length = result.length();
			
		while(rs.next()) result += "\n- " + rs.getString(1);
		if(result.length() == length)
			result += "\nNo se encontraron significados para esta palabra";

		return result + "\n";
	}

	public String getDefinition(String word) {
		int i=0;
		try{
			URL url = new URL("https://es.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro=&explaintext=&titles=" + word.replaceAll("(\\r|\\n)", "").replace(" ", "%20") + "&format=json");
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
			int size = jobject.getAsJsonObject("query").getAsJsonObject("pages").size();
			while(i < size){
				JsonElement locObj = jobject.getAsJsonObject("query")
						.getAsJsonObject("pages");
				sb.append(locObj.getAsJsonObject().toString());
				i++;
			}



			return DucitUtils.cleanWikiText(sb.toString());
		} catch(Exception e){
			return "No hay conexión a internet";
		}

	}

	public String getSynonyms(String word) throws SQLException {
		String result = new String("\nSINONIMOS:");
		boolean found = false;
		Statement stmt = conn.createStatement();
		String sql;
		sql = "SELECT Synonym FROM word w WHERE w.word LIKE '" + word + "'";
		ResultSet rs = stmt.executeQuery(sql);
		
		if(rs.next()){
			found = true;
			result += "\n" + rs.getString(1);
			if(result== null || result.contains("null"))
				return result.replace("null", "") + "\nNo se encontraron sinónimos para esta palabra";
		}
		
		if(!found)
			result += "\nNo se encontraron sinónimos para esta palabra";
		return result.replace("|", "-");
	}

	public String getAntonyms(String word) throws SQLException {
		String result = new String("\nANTONIMOS:");
		boolean found = false;
		Statement stmt = conn.createStatement();
		String sql;
		sql = "SELECT antonym FROM word w WHERE w.word LIKE '" + word + "'";
		ResultSet rs = stmt.executeQuery(sql);
		if(rs.next()){
			found = true;
			result += "\n" + rs.getString(1);
			if(result== null || result.contains("null"))
				return result.replace("null", "") + "\nNo se encontraron antónimos para esta palabra";
		}
		if(!found)
			result += "\nNo se encontraron antónimos para esta palabra";
		
		return result.replace("|", "-");
	}

	public String fixWord(String word) throws SQLException {
		if(word.length() < 2)
			return word;

		int min = Integer.MAX_VALUE;
		String correct = new String(word);
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

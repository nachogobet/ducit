package com.unounocuatro.ducit.daos;

import java.sql.SQLException;

public interface DucitDAO {
	String getWordMeaning(String word) throws SQLException;
	
	String getDefinition(String word) throws Exception;
	
	String getSynonyms(String word) throws SQLException;
}

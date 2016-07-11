package com.unounocuatro.ducit.daos;

import java.sql.SQLException;

public interface DucitDAO {
	String getWordMeaning(String word) throws SQLException;
}

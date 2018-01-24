package com.zextras.lib.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetParser<T>
{
  T readFromResultSet(ResultSet resultSet) throws SQLException;
}

package com.zextras.lib.sql;

import com.zextras.lib.Error.UnableToObtainDBConnectionError;

import java.io.Closeable;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface QueryExecutor extends Closeable
{
  ResultSet executeQuery(int start, int size) throws UnableToObtainDBConnectionError, SQLException;
}

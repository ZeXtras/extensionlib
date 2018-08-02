package com.zextras.lib;

import com.zextras.lib.Error.UnableToObtainDBConnectionError;
import com.zextras.lib.db.DbHandler;
import com.zextras.lib.log.ChatLog;
import com.zextras.lib.sql.DbPrefetchIterator;
import com.zextras.lib.sql.QueryExecutor;
import com.zextras.lib.sql.ResultSetParser;
import org.apache.commons.dbutils.DbUtils;
import org.openzal.zal.Utils;
import org.openzal.zal.log.ZimbraLog;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class DbHelper
{
  private final DbHandler mDbHandler;

  public DbHelper(DbHandler dbHandler)
  {
    mDbHandler = dbHandler;
  }

  public static class DbConnection implements Closeable
  {
    private java.sql.Connection mConnection;
    private Boolean mOldAutoCommitState;

    private DbConnection(Connection connection) throws SQLException
    {
      if (connection == null)
      {
        throw new SQLException("Error getting DB connection");
      }
      mConnection = connection;
      mOldAutoCommitState = null;
    }

    public void close()
    {
      DbUtils.closeQuietly(mConnection);
    }

    public void beginTransaction() throws SQLException
    {
      try
      {
        mOldAutoCommitState = mConnection.getAutoCommit();
        mConnection.setAutoCommit(false);
      }
      catch (SQLException e)
      {
        DbUtils.closeQuietly(mConnection);
        throw e;
      }
    }

    public void commitAndClose() throws SQLException
    {
      if (mOldAutoCommitState == null)
      {
        throw new SQLException("BeginTransaction not called");
      }
      try
      {
        mConnection.commit();
      }
      finally
      {
        mConnection.setAutoCommit(mOldAutoCommitState);
        DbUtils.closeQuietly(mConnection);
      }
    }

    public void rollbackAndClose() throws SQLException
    {
      if (mOldAutoCommitState == null)
      {
        throw new SQLException("BeginTransaction not called");
      }
      try
      {
        mConnection.rollback();
      }
      finally
      {
        mConnection.setAutoCommit(mOldAutoCommitState);
        DbUtils.closeQuietly(mConnection);
      }
    }

    private PreparedStatement prepareStatement(String query) throws SQLException
    {
      return mConnection.prepareStatement(query);
    }
  }

  static class NoParameters implements ParametersFactory
  {
    @Override
    public int init(PreparedStatement preparedStatement) throws SQLException
    {
      return 1;
    }
  }

  public interface ResultSetFactory<T>
  {
    T create(ResultSet rs) throws Exception;
  }

  public interface ParametersFactory
  {
    int init(PreparedStatement preparedStatement) throws SQLException;
  }

  public DbConnection beginTransaction() throws SQLException
  {
    DbConnection dbConnection = new DbConnection(mDbHandler.getConnection());
    dbConnection.beginTransaction();
    return dbConnection;
  }

  // No transaction is started
  public DbConnection beginConnection() throws SQLException
  {
    return new DbConnection(mDbHandler.getConnection());
  }

  public void rollbackAndClose(DbConnection connection)
  {
    if (connection != null)
    {
      try
      {
        connection.rollbackAndClose();
      }
      catch( SQLException e )
      {
        ChatLog.log.err(Utils.exceptionToString(e));
      }
    }
  }

  public void query(String sql, ResultSetFactory rsFactory) throws SQLException
  {
    query(sql, new NoParameters() ,rsFactory);
  }

  public void query(String sql, ParametersFactory parametersFactory, ResultSetFactory rsFactory) throws SQLException
  {
    DbConnection connection = new DbConnection(mDbHandler.getConnection());
    try
    {
      executeQuery(connection,sql,parametersFactory,rsFactory);
    }
    finally
    {
      connection.close();
    }
  }

  public void query(DbConnection connection, String query,ParametersFactory parametersFactory) throws SQLException
  {
    executeQuery(connection,query, parametersFactory);
  }

  public void query(DbConnection connection, String query,ResultSetFactory rsFactory) throws SQLException
  {
    executeQuery(connection,query, new NoParameters(), rsFactory);
  }

  public void executeQuery(DbConnection connection, String query,ParametersFactory parametersFactory,ResultSetFactory rsFactory) throws SQLException
  {
    PreparedStatement statement = null;
    ResultSet rs = null;
    try
    {
      statement = connection.prepareStatement(query);
      parametersFactory.init(statement);
      rs = statement.executeQuery();
      while (rs.next())
      {
        try
        {
          rsFactory.create(rs);
        }
        catch (Exception e)
        {
          ChatLog.log.err(Utils.exceptionToString(e));
        }
      }
    }
    finally
    {
      DbUtils.closeQuietly(rs);
      DbUtils.closeQuietly(statement);
    }
  }
  // begin and close transaction
  public int executeTransactionQuery(String query) throws SQLException
  {
    return executeTransactionQuery(query,new NoParameters());
  }

  // begin and close transaction
  public int executeTransactionQuery(String query, ParametersFactory parametersFactory) throws SQLException
  {
    DbConnection connection = beginTransaction();
    try
    {
      int res = executeQuery(connection,query,parametersFactory);
      connection.commitAndClose();
      return res;
    }
    catch (SQLException e)
    {
      connection.rollbackAndClose();
      throw e;
    }
  }

  // require connection.commit and connection.close or connection.rollBackAndClose
  public int executeQuery(DbConnection connection, String query) throws SQLException
  {
    return executeQuery(connection,query,new NoParameters());
  }

  // require connection.commit and connection.close or connection.rollBackAndClose
  public int executeQuery(DbConnection connection, String query, ParametersFactory parametersFactory) throws SQLException
  {
    PreparedStatement statement = null;
    ResultSet rs = null;
    try
    {
      statement = connection.prepareStatement(query);
      parametersFactory.init(statement);
      return statement.executeUpdate();
    }
    finally
    {
      DbUtils.closeQuietly(rs);
      DbUtils.closeQuietly(statement);
    }
  }

  public <T> Iterator<T> buildGlobalIterator(
    String query,
    ParametersFactory parametersFactory,
    final ResultSetFactory<T> resultSetFactory,
    int blockSize
  ) throws SQLException
  {
    final Connection connection = mDbHandler.getConnection();
    final PreparedStatement preparedStatement = connection.prepareStatement(query);
    final int i = parametersFactory.init(preparedStatement);
    return new DbPrefetchIterator<T>(
      new QueryExecutor()
      {
        @Override
        public ResultSet executeQuery(int start, int size) throws UnableToObtainDBConnectionError, SQLException
        {
          preparedStatement.setInt(i, start);
          preparedStatement.setInt(i+1, size);
          return preparedStatement.executeQuery();
        }

        @Override
        public void close() throws IOException
        {
          DbUtils.closeQuietly(connection);
        }
      },
      new ResultSetParser<T>()
      {
        @Override
        public T readFromResultSet(ResultSet resultSet) throws SQLException
        {
          try
          {
            return resultSetFactory.create(resultSet);
          }
          catch (Exception e)
          {
            throw new SQLException(e);
          }
        }
      },
      blockSize
    );
  }

  public <T> Iterator<T> buildIterator(
    String query,
    ParametersFactory parametersFactory,
    final ResultSetFactory<T> resultSetFactory
  ) throws SQLException
  {
    final Connection connection = mDbHandler.getConnection();
    final PreparedStatement preparedStatement = connection.prepareStatement(query);
    parametersFactory.init(preparedStatement);
    final ResultSet resultSet = preparedStatement.executeQuery();
    return new Iterator<T>()
    {
      T mNext = null;
      @Override
      public boolean hasNext()
      {
        try
        {
          while (mNext == null && resultSet.next())
          {
            mNext = resultSetFactory.create(resultSet);
          }

          return mNext != null;
        }
        catch (Exception e)
        {
          ZimbraLog.mailbox.error(Utils.exceptionToString(e));
        }
        return false;
      }

      @Override
      public T next()
      {
        if (mNext == null)
        {
          throw new NoSuchElementException();
        }
        T next = mNext;
        mNext = null;
        return next;
      }

      @Override
      public void remove()
      {
        throw new UnsupportedOperationException();
      }
    };
  }
}

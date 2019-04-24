package com.zextras.lib.db;

import com.zextras.lib.Error.UnableToObtainDBConnectionError;
import com.zextras.lib.log.ChatLog;
import com.zextras.lib.sql.DbPrefetchIterator;
import com.zextras.lib.sql.QueryExecutor;
import com.zextras.lib.sql.ResultSetParser;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.io.IOUtils;
import org.openzal.zal.Utils;
import org.openzal.zal.log.ZimbraLog;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DbHelper
{
  public static final String sDISABLE_FOREIGN_KEYS = "SET DATABASE REFERENTIAL INTEGRITY FALSE";
  public static final String sENABLE_FOREIGN_KEYS  = "SET DATABASE REFERENTIAL INTEGRITY TRUE";

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
      try
      {

        if(mConnection != null && !mConnection.isClosed() && mOldAutoCommitState != null)
        {
          mConnection.setAutoCommit(mOldAutoCommitState);
        }
      }
      catch(SQLException e)
      {
        throw new RuntimeException(e);
      }
      finally
      {
        DbUtils.closeQuietly(mConnection);
      }
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

    public void commit() throws SQLException
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
      }
    }

    public void commitAndClose() throws SQLException
    {
      try
      {
        commit();
      }
      finally
      {
        DbUtils.closeQuietly(mConnection);
      }
    }

    public void rollback() throws SQLException
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
      }
    }

    public void rollbackAndClose() throws SQLException
    {
      try
      {
        rollback();
      }
      finally
      {
        DbUtils.closeQuietly(mConnection);
      }
    }

    private PreparedStatement prepareStatement(String query) throws SQLException
    {
      return mConnection.prepareStatement(query);
    }
  }

  public static class NoParameters implements ParametersFactory
  {
    @Override
    public int init(PreparedStatement preparedStatement) throws SQLException
    {
      return 1;
    }
  }

  public interface ResultSetFactory<T>
  {
    T create(ResultSetHelper rs, DbConnection connection) throws Exception;
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
      IOUtils.closeQuietly(connection);
    }
  }

  public void query(DbConnection connection, String query, ParametersFactory parametersFactory, ResultSetFactory resultSetFactory) throws SQLException
  {
    executeQuery(connection, query, parametersFactory, resultSetFactory);
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
    ResultSetHelper rs = null;
    try
    {
      statement = connection.prepareStatement(mDbHandler.cleanSql(query));
      parametersFactory.init(statement);
      rs = new ResultSetHelper(statement.executeQuery());
      while (rs.next())
      {
        try
        {
          rsFactory.create(rs, connection);
        }
        catch (Exception e)
        {
          ChatLog.log.err(Utils.exceptionToString(e));
        }
      }
    }
    finally
    {
      IOUtils.closeQuietly(rs);
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
      statement = connection.prepareStatement(mDbHandler.cleanSql(query));
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
    final String query,
    final ParametersFactory parametersFactory,
    final ResultSetFactory<T> resultSetFactory,
    int blockSize
  ) throws SQLException
  {
    final DbConnection[] connection = {null};
    final PreparedStatement[] preparedStatement = {null};

    return new DbPrefetchIterator<T>(
      new QueryExecutor()
      {

        @Override
        public ResultSet executeQuery(int start, int size) throws UnableToObtainDBConnectionError, SQLException
        {
          if( connection[0] == null ) {
            connection[0] = beginConnection();
            preparedStatement[0] = connection[0].prepareStatement(mDbHandler.cleanSql(query));
          }
          final int i = parametersFactory.init(preparedStatement[0]);
          preparedStatement[0].setInt(i, start);
          preparedStatement[0].setInt(i+1, size);
          return preparedStatement[0].executeQuery();
        }

        @Override
        public void close() throws IOException
        {
          DbUtils.closeQuietly(preparedStatement[0]);
          IOUtils.closeQuietly(connection[0]);
          connection[0] = null;
          preparedStatement[0] = null;
        }
      },
      new ResultSetParser<T>()
      {
        @Override
        public T readFromResultSet(ResultSet resultSet) throws SQLException
        {
          try
          {
            return resultSetFactory.create(new ResultSetHelper(resultSet), connection[0]);
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
    DbConnection connection,
    String query,
    ParametersFactory parametersFactory,
    final ResultSetFactory<T> resultSetFactory
  )
    throws SQLException
  {
    List<T> list = new ArrayList<T>();
    PreparedStatement preparedStatement = connection.prepareStatement(mDbHandler.cleanSql(query));
    try
    {
      parametersFactory.init(preparedStatement);
      ResultSet resultSet = preparedStatement.executeQuery();
      setList(resultSetFactory, list, resultSet, connection);
    }
    finally
    {
      DbUtils.closeQuietly(preparedStatement);
    }

    return list.iterator();
  }

  public <T> Iterator<T> buildIterator(
    String query,
    ParametersFactory parametersFactory,
    final ResultSetFactory<T> resultSetFactory
  ) throws SQLException
  {
    final DbConnection connection = beginConnection();
    PreparedStatement preparedStatement = null;
    List<T> list = new ArrayList<T>();
    try
    {
      preparedStatement = connection.prepareStatement(mDbHandler.cleanSql(query));
      parametersFactory.init(preparedStatement);
      ResultSet resultSet = preparedStatement.executeQuery();
      setList(resultSetFactory, list, resultSet, connection);

      return list.iterator();
    }
    finally
    {
      DbUtils.closeQuietly(preparedStatement);
      IOUtils.closeQuietly(connection);
    }
  }

  private <T> void setList(
    ResultSetFactory<T> resultSetFactory,
    List<T> list,
    ResultSet resultSet,
    DbConnection connection
  )
    throws SQLException
  {
    while(resultSet.next())
    {
      try
      {
        T element = resultSetFactory.create(new ResultSetHelper(resultSet), connection);
        if(element != null)
        {
          list.add(element);
        }
      }
      catch(Exception e)
      {
        ZimbraLog.mailbox.error(Utils.exceptionToString(e));
      }
    }
  }
}

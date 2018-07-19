package com.zextras.lib.sql;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.io.IOUtils;

import java.sql.ResultSet;
import java.util.*;

public class DbPrefetchIterator<T> implements Iterator<T>
{
  void queryNextResultSet()
  {
    if( mNoMoreResults ){
      return;
    }

    mCurrentList.clear();
    ResultSet resultSet = null;
    try
    {
      resultSet = mExecutor.executeQuery(mStart, mQueryBlockSize);
      while( resultSet.next() )
      {
        T item = mResultSetParser.readFromResultSet(resultSet);
        if (item != null)
        {
          mCurrentList.add(item);
        }
      }
      mStart += mQueryBlockSize;
      mIterator = mCurrentList.iterator();
      mNoMoreResults = mCurrentList.size() < mQueryBlockSize;
    }
    catch ( Exception ex )
    {
      NoSuchElementException newEx = new NoSuchElementException("SQL Exception");
      newEx.initCause(ex);
      throw newEx;
    }
    finally
    {
      DbUtils.closeQuietly(resultSet);
      IOUtils.closeQuietly(mExecutor);
    }
  }

  private int mStart = 0;

  private final QueryExecutor mExecutor;
  private final ResultSetParser<T> mResultSetParser;
  private final int mQueryBlockSize;
  private List<T> mCurrentList;
  private Iterator<T> mIterator;
  private boolean mNoMoreResults;

  public DbPrefetchIterator(QueryExecutor executor, ResultSetParser<T> resultSetParser, int queryBlockSize)
  {
    mExecutor = executor;
    mResultSetParser = resultSetParser;
    mQueryBlockSize = queryBlockSize;
    mCurrentList = new ArrayList<T>(queryBlockSize);
    mIterator = Collections.<T>emptyList().iterator();
    mNoMoreResults = false;
  }

  @Override
  public boolean hasNext()
  {
    if( mIterator.hasNext() )
    {
      return true;
    }
    else
    {
      queryNextResultSet();
      return mIterator.hasNext();
    }
  }

  @Override
  public T next()
    throws NoSuchElementException
  {
    if( !hasNext() )
    {
      throw new NoSuchElementException("No more items");
    }
    return mIterator.next();
  }

  @Override
  public void remove()
  {
    throw new UnsupportedOperationException("Remove unsupported");
  }
}

/*
 * Copyright (C) 2017 ZeXtras S.r.l.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, version 2 of
 * the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package com.zextras.lib.log;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import com.zextras.lib.Error.ZxError;
import com.zextras.lib.UnableToFindLogger;
import com.zextras.lib.log.writers.ZELogWriter;
import org.openzal.zal.Account;


public class ZELogModule
{
  @Deprecated
  public void debug( ZELogger logger, String msg )
  {
    debug(logger, null, msg);
  }

  @Deprecated
  public void info( ZELogger logger, String msg )
  {
    info(logger, null, msg);
  }

  @Deprecated
  public void warn( ZELogger logger, String msg )
  {
    warn(logger, null, msg);
  }

  @Deprecated
  public void err( ZELogger logger, String msg )
  {
    err(logger, null, msg);
  }

  @Deprecated
  public void crit( ZELogger logger, String msg )
  {
    crit(logger, null, msg);
  }

  @Deprecated
  public void debug( ZELogger logger, Account account, String msg )
  {
    CurrentLogContext.begin()
        .setAccount(account)
        .setLoggerName((logger!=null?logger.getLoggerName():""))
        .freeze();
    try
    {
      debug(msg);
    }
    finally
    {
      CurrentLogContext.end();
    }
  }

  @Deprecated
  public void info( ZELogger logger, Account account, String msg )
  {
    CurrentLogContext.begin()
        .setAccount(account)
        .setLoggerName((logger!=null?logger.getLoggerName():""))
        .freeze();
    try
    {
      info(msg);
    }
    finally
    {
      CurrentLogContext.end();
    }
  }

  @Deprecated
  public void warn( ZELogger logger, Account account, String msg )
  {
    CurrentLogContext.begin()
        .setAccount(account)
        .setLoggerName((logger!=null?logger.getLoggerName():""))
        .freeze();
    try
    {
      warn(msg);
    }
    finally
    {
      CurrentLogContext.end();
    }
  }

  @Deprecated
  public void err( ZELogger logger, Account account, String msg )
  {
    CurrentLogContext.begin()
        .setAccount(account)
        .setLoggerName((logger!=null?logger.getLoggerName():""))
        .freeze();
    try
    {
      err(msg);
    }
    finally
    {
      CurrentLogContext.end();
    }
  }

  @Deprecated
  public void crit( ZELogger logger, Account account, String msg )
  {
    CurrentLogContext.begin()
        .setAccount(account)
        .setLoggerName((logger!=null?logger.getLoggerName():""))
        .freeze();
    try
    {
      crit(msg);
    }
    finally
    {
      CurrentLogContext.end();
    }
  }


  public void debug( String msg )
  {
    write(SeverityLevel.DEBUG, msg);
  }

  public void info( String msg )
  {
    write( SeverityLevel.INFORMATION, msg );
  }

  public void warn( String msg )
  {
    write( SeverityLevel.WARNING, msg );
  }

  public void err( String msg )
  {
    write( SeverityLevel.ERROR, msg );
  }

  public void crit( String msg )
  {
    write( SeverityLevel.CRITICAL, msg );
  }

  @Deprecated
  public void write( ZELogger logger, ZxError ex )
  {
    CurrentLogContext.begin()
        .setLoggerName((logger!=null?logger.getLoggerName():""))
        .freeze();
    try
    {
      write( ex.getSeverity(), ex.getMessage() );
    }
    finally
    {
      CurrentLogContext.end();
    }
  }

  @Deprecated
  public void write( ZELogger logger, SeverityLevel level, Exception ex )
  {
    CurrentLogContext.begin()
        .setLoggerName((logger!=null?logger.getLoggerName():""))
        .freeze();
    try
    {
      write( level, ex.getMessage() );
    }
    finally
    {
      CurrentLogContext.end();
    }
  }

  public void write( ZxError ex )
  {
    write(ex.getSeverity(), ex.getMessage());
  }

  public void write( SeverityLevel level, String msg )
  {
    LogContext logContext = CurrentLogContext.current();
    for( ZELogWriter writer : mLogWriterList.get() )
    {
      if( writer.write(level, logContext,  msg) ) {
        break;
      }
    }
  }

  public boolean isLogged( SeverityLevel level )
  {
    for( ZELogWriter writer : mLogWriterList.get() )
    {
      if( writer.isLogged(level) ) {
        return true;
      }
    }
    return false;
  }

  public void addLogWriter( ZELogWriter logWriter )
  {
    while (true)
    {
      List<ZELogWriter> current = mLogWriterList.get();
      ArrayList<ZELogWriter> newList = new ArrayList<>(current);
      newList.add(logWriter);
      Collections.sort(newList);
      if( mLogWriterList.compareAndSet(current, newList) ) break;
    }
  }

  public boolean removeLogWriter( int id, boolean onlyUserVisible )
  {
    while (true)
    {
      boolean found = false;

      List<ZELogWriter> current = mLogWriterList.get();
      ArrayList<ZELogWriter> newList = new ArrayList<>(current);

      for (ZELogWriter logWriter : newList)
      {
        if ((!onlyUserVisible || logWriter.isUserVisible()) &&
          logWriter.getId() == id)
        {
          newList.remove(logWriter);
          if( mLogWriterList.compareAndSet(current, newList) )
          {
            return true;
          }
          else
          {
            found = true;
            break;
          }
        }
      }

      if( !found ) {
        return false;
      }
    }
  }

  public boolean removeLogWriter(ZELogWriter logWriter)
  {
    boolean result;

    while (true)
    {
      List<ZELogWriter> current = mLogWriterList.get();
      ArrayList<ZELogWriter> newList = new ArrayList<>(current);
      result = newList.remove(logWriter);
      if( mLogWriterList.compareAndSet(current, newList) ) {
        break;
      }
    }

    return result;
  }

  public ZELogWriter getLogWriter(int id, boolean onlyUserVisible)
    throws UnableToFindLogger
  {
    for( ZELogWriter logWriter : mLogWriterList.get() )
    {
      if( (!onlyUserVisible || logWriter.isUserVisible()) &&
        logWriter.getId() == id )
      {
        return logWriter;
      }
    }
    throw new UnableToFindLogger(id);
  }

  public boolean removeAllLogWriters( boolean onlyUserVisible )
  {
    List<ZELogWriter> toBeDeleted;
    while( true )
    {
      toBeDeleted = new LinkedList<ZELogWriter>();
      List<ZELogWriter> current = mLogWriterList.get();
      ArrayList<ZELogWriter> newList = new ArrayList<>(current);

      for (ZELogWriter logWriter : newList)
      {
        if (!onlyUserVisible || logWriter.isUserVisible())
        {
          toBeDeleted.add(logWriter);
        }
      }

      for (ZELogWriter logWriter : toBeDeleted)
      {
        newList.remove(logWriter);
      }
      if( mLogWriterList.compareAndSet(current, newList) ) break;
    }

    return !toBeDeleted.isEmpty();
  }

  public List<ZELogWriter> listLogWriter( boolean onlyUserVisible )
  {
    List<ZELogWriter> list = new LinkedList<ZELogWriter>();

    for( ZELogWriter logWriter : mLogWriterList.get() )
    {
      if( (!onlyUserVisible || logWriter.isUserVisible()) )
      {
        list.add( logWriter );
      }
    }

    return list;
  }

  public void flush()
  {
    for(ZELogWriter logWriter : mLogWriterList.get())
    {
      logWriter.flush();
    }
  }


  private static final Map<SeverityLevel, String> sLevelNames;

  static
  {
    sLevelNames = new HashMap<SeverityLevel, String>();
    sLevelNames.put(SeverityLevel.DEBUG, "debug");
    sLevelNames.put(SeverityLevel.INFORMATION, "info");
    sLevelNames.put(SeverityLevel.WARNING, "warn");
    sLevelNames.put(SeverityLevel.ERROR, "err");
    sLevelNames.put(SeverityLevel.CRITICAL, "crit");
  }

  public static SeverityLevel getLevelForName(String name)
  {
    name = name.toLowerCase();
    for (SeverityLevel level : sLevelNames.keySet())
    {
      if (sLevelNames.get(level).equals(name))
      {
        return level;
      }
    }
    return null;
  }

  public static String getNameForLevel(SeverityLevel level)
  {
    return sLevelNames.get(level);
  }

  private final AtomicReference<List<ZELogWriter>> mLogWriterList;

  public ZELogModule()
  {
    mLogWriterList = new AtomicReference(Collections.emptyList());
  }

}

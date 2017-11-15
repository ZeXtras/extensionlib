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

package com.zextras.lib.log.writers;

import com.zextras.lib.log.LogContext;
import com.zextras.lib.log.SeverityLevel;
import org.openzal.zal.log.ZimbraLog;

import com.google.inject.Singleton;

@Singleton
public class ZELogWriterZimbraLog extends ZELogWriter
{
public int getPriority()
  {
    return 1000;
  }

  public boolean isUserVisible()
  {
    return true;
  }

  public String getName()
  {
    return "ZimbraLogger";
  }

  public String getDescription()
  {
    return "Standard zimbra logger";
  }

  @Override
  public boolean isLogged( SeverityLevel level )
  {
    return level.compareTo(getLevel()) >= 0;
  }

  @Override
  public boolean write( SeverityLevel level, LogContext ctxt, String msg )
  {
    if( level.compareTo(getLevel()) >= 0 )
    {
      if( !ctxt.getLoggerName().isEmpty() )
      {
        msg = ctxt.getLoggerName() + ": " + msg;
      }

      if( level == SeverityLevel.DEBUG)
      {
        ZimbraLog.extensions.debug( msg );
      }
      else if( level == SeverityLevel.INFORMATION)
      {
        ZimbraLog.extensions.info( msg );
      }
      else if( level == SeverityLevel.WARNING)
      {
        ZimbraLog.extensions.warn( msg );
      }
      else if(level == SeverityLevel.ERROR)
      {
        ZimbraLog.extensions.error( msg );
      }
      else
      {
        ZimbraLog.extensions.fatal( msg );
      }
      return true;
    }
    else
    {
      return false;
    }
  }

  public void flush()
  {
  }
}

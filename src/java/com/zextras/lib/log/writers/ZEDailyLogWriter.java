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

import com.zextras.lib.log.ChatLog;
import com.zextras.lib.log.LogContext;
import com.zextras.lib.log.SeverityLevel;
import org.openzal.zal.Provisioning;
import org.openzal.zal.log.PatternLayout;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class ZEDailyLogWriter extends ZELogWriter
{
  private final Provisioning mProvisioning;
  private       Logger       mLogger;
  private       String       mLogPath;
  private       FileAppender mAppender;
  private final Class        mLogClass;
  private final String mDailyPattern = "'.'yyyy-MM-dd";
  private boolean mEnabled;
  private boolean mAppenderInitialized  = false;
  private boolean mInitializedException = false;

  public ZEDailyLogWriter(String logPath, Class logClass, Provisioning provisioning)
  {
    mProvisioning = provisioning;
    mEnabled = false;
    mLogPath = logPath;
    mLogClass = logClass;
  }

  public void setEnabled(boolean enabled)
  {
    mEnabled = enabled;
  }

  private void initAppender()
    throws IOException
  {
    if (!mAppenderInitialized)
    {
      try
      {
        mAppender = new DailyRollingFileAppender(
          new PatternLayout(),
          mLogPath,
          mDailyPattern
        );
        mLogger = Logger.getLogger(mLogClass);
        mLogger.addAppender(mAppender);
        mLogger.setAdditivity(false);

        mAppenderInitialized = true;
      } catch (IOException ex) {
        mInitializedException = true;
        mAppenderInitialized = true;
        String errorMsg = "The log file on " + mLogPath + " cannot be initialize: " + ex.getMessage();
        ChatLog.log.err(errorMsg);
        throw ex;
      }
    }

    if (mInitializedException) {
      throw new IOException();
    }
  }

  public void unregister()
  {
    if( mAppenderInitialized )
    {
      mAppenderInitialized = false;
      mLogger.removeAppender(mAppender);
    }
  }

  @Override
  public boolean isUserVisible()
  {
    return false;
  }

  @Override
  public boolean isLogged( SeverityLevel level )
  {
    return (level.compareTo(getLevel()) >= 0 && mEnabled );
  }

  @Override
  public boolean write( SeverityLevel level, LogContext ctxt, String msg ) {
    if (isLogged(level))
    {
      try {
        initAppender();
      } catch (IOException e) {
        return false;
      }

      SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss,SSS" );
      String out = df.format(new Date()) + "\t";

      if( !ctxt.getLoggerName().isEmpty() )
      {
        msg = ctxt.getLoggerName() + ": " + msg;
      }


      if( !ctxt.getUserAddress().isEmpty() )
      {
        msg = "[user="+ctxt.getUserAddress().toString() +"] "+ msg;
      }

      String accountName = "";
      if( !ctxt.getAccountName().isEmpty() ) {
        accountName = ctxt.getAccountName() + " ";
      }

      if(level == SeverityLevel.DEBUG)
      {
        out += "DEBUG "+ accountName + msg;
        mLogger.info(out);
      }
      else if(level == SeverityLevel.INFORMATION)
      {
        out += "INFO "+ accountName + msg;
        mLogger.info(out);
      }
      else if(level == SeverityLevel.WARNING)
      {
        out += "WARN "+ accountName + msg;
        mLogger.warn(out);
      }
      else if(level == SeverityLevel.ERROR)
      {
        out += "ERR "+ accountName + msg;
        mLogger.error(out);
      }
      else
      {
        out += "CRIT "+ accountName + msg;
        mLogger.fatal(out);
      }
    }

    return false;
  }

  @Override
  public void flush()
  {
  }
}

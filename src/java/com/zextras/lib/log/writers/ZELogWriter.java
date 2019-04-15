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

import java.util.concurrent.locks.ReentrantLock;

import com.zextras.lib.log.LogContext;
import com.zextras.lib.log.SeverityLevel;
import javax.annotation.Nonnull;

abstract public class ZELogWriter implements Comparable<ZELogWriter>
{
  private SeverityLevel mLevel;
  private final int           mId;

  private static       int           sNextAvailableId = 0;
  private static final ReentrantLock sLock            = new ReentrantLock();

  private static int nextAvailableId()
  {
    sLock.lock();
    try
    {
      if (Integer.MAX_VALUE == sNextAvailableId)
      {
        sNextAvailableId = 0;
      }
      sNextAvailableId += 1;

      return sNextAvailableId;
    }
    finally
    {
      sLock.unlock();
    }
  }

  public void setLevel( SeverityLevel level )
  {
    mLevel = level;
  }

  public SeverityLevel getLevel()
  {
    return mLevel;
  }

  public int getId()
  {
    return mId;
  }

  public ZELogWriter()
  {
    mId = nextAvailableId();
  }

  public abstract boolean isUserVisible();
  public abstract String getName();
  public abstract String getDescription();

  public int compareTo(@Nonnull ZELogWriter writer) {
    return ((Integer)getPriority()).compareTo( writer.getPriority() );
  }

  abstract public boolean write( SeverityLevel level, LogContext logContext, String msg );
  abstract public boolean isLogged( SeverityLevel level );

  abstract public void flush();
  abstract public int getPriority();

  @Override
  public boolean equals(Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }

    ZELogWriter that = (ZELogWriter) o;

    if (mId != that.mId)
    {
      return false;
    }
    if (mLevel != that.mLevel)
    {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode()
  {
    int result = mLevel != null ? mLevel.hashCode() : 0;
    result = 31 * result + mId;
    return result;
  }
}

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

package com.zextras.lib.json;

public class JSONException extends Exception
{
  public String getEntryId()
  {
    return mEntryId;
  }

  public String getFilePath()
  {
    return mFilePath;
  }

  public boolean hasEntryId()
  {
    return mEntryId != null;
  }

  public boolean hasFilePath()
  {
    return mFilePath != null;
  }

  private String       mFilePath;
  private String       mEntryId;
  private Throwable    mCause;

  public JSONException(String message)
  {
    super(message);
  }

  public JSONException(String entryId, String filePath, Throwable cause)
  {
    super(cause);
    mEntryId = entryId;
    mFilePath = filePath;
  }

  public JSONException(String message, Throwable cause)
  {
    super (message);
    mCause = cause;
  }

  public JSONException(Throwable cause)
  {
    super(cause.getMessage());
    mCause = cause;
  }

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

    JSONException that = (JSONException) o;

    if (mCause != null ? !mCause.equals(that.mCause) : that.mCause != null)
    {
      return false;
    }
    if (mEntryId != null ? !mEntryId.equals(that.mEntryId) : that.mEntryId != null)
    {
      return false;
    }
    if (mFilePath != null ? !mFilePath.equals(that.mFilePath) : that.mFilePath != null)
    {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode()
  {
    int result = mFilePath != null ? mFilePath.hashCode() : 0;
    result = 31 * result + (mEntryId != null ? mEntryId.hashCode() : 0);
    result = 31 * result + (mCause != null ? mCause.hashCode() : 0);
    return result;
  }

  public Throwable getCause()
  {
    if (mCause == null) {
      return super.getCause();
    }
    return mCause;
  }
}

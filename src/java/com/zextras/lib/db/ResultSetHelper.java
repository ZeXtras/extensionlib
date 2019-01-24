package com.zextras.lib.db;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.InputStream;
import java.sql.Types;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

public class ResultSetHelper implements Closeable
{
  private final ResultSetMetaData mRsmd;
  private final ResultSet mRs;

  public ResultSetHelper(@NotNull
    ResultSet rs)
    throws SQLException
  {
    mRsmd = rs.getMetaData();
    mRs = rs;
  }

  public boolean next() throws SQLException
  {
    return mRs.next();
  }

  @Override
  public void close()
  {
    try
    {
      mRs.close();
    }
    catch( SQLException e )
    {
    }
  }

  public int getColumnCount() throws SQLException
  {
    return mRsmd.getColumnCount();
  }

  public String getColumnName(int i) throws SQLException
  {
    return mRsmd.getColumnName(i);
  }

  public InputStream getBinaryStream(int i) throws SQLException
  {
    return get(mRs.getBinaryStream(i));
  }

  public boolean hasColumn(String columnName) throws SQLException
  {
    for (int i = 1; i <= mRsmd.getColumnCount(); i++)
    {
      if (columnName.equalsIgnoreCase(mRsmd.getColumnName(i)))
      {
        return true;
      }
    }

    return false;
  }

  @Nullable
  public String getString(String s) throws SQLException
  {
    String value = get(mRs.getString(s));
    if(value != null && getColumnType(s) == Types.CHAR)
    {
      return value.trim();
    }
    return value;
  }

  @Nullable
  public Boolean getBoolean(String s) throws SQLException
  {
    return get(mRs.getBoolean(s));
  }

  @Nullable
  public Byte getByte(String s) throws SQLException
  {
    return get(mRs.getByte(s));
  }

  @Nullable
  public Short getShort(String s) throws SQLException
  {
    return get(mRs.getShort(s));
  }

  @Nullable
  public Integer getInt(String s) throws SQLException
  {
    return get(mRs.getInt(s));
  }

  @Nullable
  public Long getLong(String s) throws SQLException
  {
    return get(mRs.getLong(s));
  }

  @Nullable
  public Float getFloat(String s) throws SQLException
  {
    return get(mRs.getFloat(s));
  }

  @Nullable
  public Double getDouble(String s) throws SQLException
  {
    return get(mRs.getDouble(s));
  }

  @Nullable
  public byte[] getBytes(String s) throws SQLException
  {
    return get(mRs.getBytes(s));
  }

  @Nullable
  public Date getDate(String s) throws SQLException
  {
    return get(mRs.getDate(s));
  }

  @Nullable
  public Time getTime(String s) throws SQLException
  {
    return get(mRs.getTime(s));
  }

  @Nullable
  public Timestamp getTimestamp(String s) throws SQLException
  {
    return get(mRs.getTimestamp(s));
  }

  @Nullable
  public InputStream getAsciiStream(String s) throws SQLException
  {
    return get(mRs.getAsciiStream(s));
  }

  @Nullable
  public InputStream getBinaryStream(String s) throws SQLException
  {
    return get(get(mRs.getBinaryStream(s)));
  }

  @Nullable
  private <T> T get(T value) throws SQLException
  {
    if (mRs.wasNull())
    {
      return null;
    }
    return value;
  }

  public String getNotNullString(String s) throws SQLException
  {
    String value = getNotNull(s, mRs.getString(s));
    if (value == null)
    {
      return null;
    }
    return value.trim();
  }

  public Boolean getNotNullBoolean(String s) throws SQLException
  {
    return getNotNull(s, mRs.getBoolean(s));
  }

  public Byte getNotNullByte(String s) throws SQLException
  {
    return getNotNull(s, mRs.getByte(s));
  }

  public Short getNotNullShort(String s) throws SQLException
  {
    return getNotNull(s, mRs.getShort(s));
  }

  public Integer getNotNullInt(String s) throws SQLException
  {
    return getNotNull(s, mRs.getInt(s));
  }

  public Long getNotNullLong(String s) throws SQLException
  {
    return getNotNull(s, mRs.getLong(s));
  }

  public Float getNotNullFloat(String s) throws SQLException
  {
    return getNotNull(s, mRs.getFloat(s));
  }

  public Double getNotNullDouble(String s) throws SQLException
  {
    return getNotNull(s, mRs.getDouble(s));
  }

  public byte[] getNotNullBytes(String s) throws SQLException
  {
    return getNotNull(s, mRs.getBytes(s));
  }

  public Date getNotNullDate(String s) throws SQLException
  {
    return getNotNull(s, mRs.getDate(s));
  }

  public Time getNotNullTime(String s) throws SQLException
  {
    return getNotNull(s, mRs.getTime(s));
  }

  public Timestamp getNotNullTimestamp(String s) throws SQLException
  {
    return getNotNull(s, mRs.getTimestamp(s));
  }

  public InputStream getNotNullAsciiStream(String s) throws SQLException
  {
    return getNotNull(s, mRs.getAsciiStream(s));
  }

  public InputStream getNotNullBinaryStream(String s) throws SQLException
  {
    return getNotNull(s, get(mRs.getBinaryStream(s)));
  }

  public int getColumnType(String s) throws SQLException
  {
    return mRsmd.getColumnType(mRs.findColumn(s));
  }

  private <T> T getNotNull(String columnName, T value) throws SQLException
  {
    if (mRs.wasNull())
    {
      throw new SQLException("Column " + columnName + " is null");
    }
    return value;
  }

  public ResultSetMetaData getMetaData()
  {
    return mRsmd;
  }

  public ResultSet getResultSet()
  {
    return mRs;
  }

  private <T> T opt(T value,T defaultValue) throws SQLException
  {
    if (value == null || mRs.wasNull())
    {
      return defaultValue;
    }
    return value;
  }
  
  public String optString(String s,String defaultValue) throws SQLException
  {
    String value = opt(mRs.getString(s),defaultValue);
    if(value != null && getColumnType(s) == Types.CHAR)
    {
      return value.trim();
    }
    return value;
  }
  
  public boolean optBoolean(String s,boolean defaultValue) throws SQLException
  {
    return opt(mRs.getBoolean(s),defaultValue);
  }
  
  public byte optByte(String s,byte defaultValue) throws SQLException
  {
    return opt(mRs.getByte(s),defaultValue);
  }
  
  public short optShort(String s,short defaultValue) throws SQLException
  {
    return opt(mRs.getShort(s),defaultValue);
  }
  
  public int optInt(String s,int defaultValue) throws SQLException
  {
    return opt(mRs.getInt(s),defaultValue);
  }
  
  public long optLong(String s,long defaultValue) throws SQLException
  {
    return opt(mRs.getLong(s),defaultValue);
  }
  
  public float optFloat(String s,float defaultValue) throws SQLException
  {
    return opt(mRs.getFloat(s),defaultValue);
  }
  
  public double optDouble(String s,double defaultValue) throws SQLException
  {
    return opt(mRs.getDouble(s),defaultValue);
  }
  
  public byte[] optBytes(String s,byte[] defaultValue) throws SQLException
  {
    return opt(mRs.getBytes(s),defaultValue);
  }
  
  public Date optDate(String s,Date defaultValue) throws SQLException
  {
    return opt(mRs.getDate(s),defaultValue);
  }
  
  public Time optTime(String s,Time defaultValue) throws SQLException
  {
    return opt(mRs.getTime(s),defaultValue);
  }
  
  public Timestamp optTimestamp(String s,Timestamp defaultValue) throws SQLException
  {
    return opt(mRs.getTimestamp(s),defaultValue);
  }
  
  public InputStream optAsciiStream(String s,InputStream defaultValue) throws SQLException
  {
    return opt(mRs.getAsciiStream(s),defaultValue);
  }
  
  public InputStream optBinaryStream(String s,InputStream defaultValue) throws SQLException
  {
    return opt(mRs.getBinaryStream(s),defaultValue);
  }
}

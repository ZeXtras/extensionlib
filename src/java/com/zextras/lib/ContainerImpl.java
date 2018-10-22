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

package com.zextras.lib;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.zextras.lib.Error.MetadataKeyInvalidTypeFoundError;
import com.zextras.lib.Error.MetadataKeyNotFoundError;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ContainerImpl implements Container
{
  private static final ObjectWriter sJsonWriter = (new ObjectMapper()).writer();

  @JsonProperty("privateMap")
  protected final Map<String, Object> mContainerMap;

  public ContainerImpl()
  {
    mContainerMap = new HashMap<String, Object>();
  }

  public ContainerImpl(Map<String, Object> stringObjectMap)
  {
    mContainerMap = stringObjectMap;
  }

  private Object get(String key)
  {
    if (mContainerMap.containsKey(key))
    {
      Object value = mContainerMap.get(key);
      if (value == null)
      {
        throw new MetadataKeyNotFoundError(key);
      }
      return value;
    }
    throw new MetadataKeyNotFoundError(key);
  }

  private void put(String key, Object value)
  {
    if (value == null)
    {
      throw new NullPointerException();
    }
    mContainerMap.put(key, value);
  }

  @Override
  public Map<String, Object> getPrivateMap()
  {
    return mContainerMap;
  }

  @Override
  public Set<String> keySet()
  {
    return mContainerMap.keySet();
  }

  @Override
  public Iterator<Map.Entry<String, Object>> iterator()
  {
    return mContainerMap.entrySet().iterator();
  }

  @Override
  public String getString(String key)
  {
    Object value = get(key);
    if (value instanceof String)
    {
      return (String) value;
    }
    else
    {
      try
      {
        return value.toString();
      }
      catch (Exception e)
      {
        throw new MetadataKeyInvalidTypeFoundError(key, MetadataKeyInvalidTypeFoundError.ValueType.STRING);
      }
    }
  }

  @Override
  public String optString(String key, @NotNull String defaultValue)
  {
    if( mContainerMap.containsKey(key) )
    {
      return getString(key);
    }
    else
    {
      return defaultValue;
    }
  }

  @Override
  public void putString(String key, String value)
  {
    put(key, value);
  }

  @Override
  public boolean getBoolean(String key)
  {
    Object value = get(key);
    if (value instanceof Boolean )
    {
      return (Boolean) value;
    }
    else if (value instanceof String)
    {
      if (((String)value).equalsIgnoreCase("false"))
      {
        return false;
      }
      if (((String)value).equalsIgnoreCase("true"))
      {
        return true;
      }
    }
    throw new MetadataKeyInvalidTypeFoundError(key, MetadataKeyInvalidTypeFoundError.ValueType.BOOLEAN);
  }

  @Override
  public boolean optBoolean(String key, boolean defaultValue)
  {
    if( mContainerMap.containsKey(key) )
    {
      return getBoolean(key);
    }
    else
    {
      return defaultValue;
    }
  }

  @Override
  public void putBoolean(String key, boolean value)
  {
    put(key, value);
  }

  @Override
  public long getLong(String key)
  {
    Object value = get(key);
    if (value instanceof Long)
    {
      return (Long) value;
    }
    else if (value instanceof String)
    {
      try
      {
        return Long.valueOf((String) value);
      }
      catch (Exception e) {}
    }
    else if (value instanceof Integer)
    {
      return Long.valueOf(String.valueOf(value));
    }
    throw new MetadataKeyInvalidTypeFoundError(key, MetadataKeyInvalidTypeFoundError.ValueType.LONG);
  }

  @Override
  public long optLong(String key, long defaultValue)
  {
    if( mContainerMap.containsKey(key) )
    {
      return getLong(key);
    }
    else
    {
      return defaultValue;
    }
  }

  @Override
  public void putLong(String key, long value)
  {
    put(key, value);
  }

  @Override
  public int getInt(String key)
  {
    Object value = get(key);
    if (value instanceof Long)
    {
      long longValue = (Long) value;
      if ((longValue <= Integer.MAX_VALUE) && (longValue >= Integer.MIN_VALUE)) {
        return (int) longValue;
      }
    }
    else if (value instanceof String)
    {
      try
      {
        long longValue = Long.valueOf( (String) value );
        if ((longValue <= Integer.MAX_VALUE) && (longValue >= Integer.MIN_VALUE)) {
          return (int) longValue;
        }
      }
      catch (Exception e) {}
    }
    throw new MetadataKeyInvalidTypeFoundError(key, MetadataKeyInvalidTypeFoundError.ValueType.INT);
  }

  @Override
  public int optInt(String key, int defaultValue)
  {
    if( mContainerMap.containsKey(key) )
    {
      return getInt(key);
    }
    else
    {
      return defaultValue;
    }
  }

  @Override
  public Container getContainer(String key)
  {
    Object value = get(key);
    if (value instanceof Container)
    {
      return (Container) value;
    }
    throw new MetadataKeyInvalidTypeFoundError(key, MetadataKeyInvalidTypeFoundError.ValueType.CONTAINER);
  }

  @Override
  public Container optContainer(String key, @NotNull Container defalutValue)
  {
    if( mContainerMap.containsKey(key) )
    {
      return getContainer(key);
    }
    else
    {
      return defalutValue;
    }
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

    ContainerImpl container = (ContainerImpl) o;
    return mContainerMap.equals(container.mContainerMap);
  }

  @Override
  public int hashCode()
  {
    return mContainerMap != null ? mContainerMap.hashCode() : 0;
  }

  @Override
  public void putContainer(String key, Container value)
  {
    put(key, value);
  }

  @Override
  public Iterable<String> getListString(String key)
  {
    Object value = get(key);
    if (value instanceof ContainerListString)
    {
      return (ContainerListString) value;
    }
    if (value instanceof EmptyContainerList)
    {
      return new ContainerListString(Collections.<String>emptyList());
    }
    if (value instanceof ContainerList<?>)
    {
      return (ContainerList<String>) value;
    }
    throw new MetadataKeyInvalidTypeFoundError(key, MetadataKeyInvalidTypeFoundError.ValueType.STRING);
  }

  @Override
  public Iterable<String> optListString(String key, @NotNull Iterable<String> defaultValue)
  {
    if( mContainerMap.containsKey(key) )
    {
      return getListString(key);
    }
    else
    {
      return defaultValue;
    }
  }

  @Override
  public void putListString(String key, ContainerList<String> value)
  {
    put(key, value);
  }

  @Override
  public void putListString(String key, Iterable<String> value)
  {
    ContainerList<String> list = new ContainerListString(value);
    put(key, list);
  }

  @Override
  public Iterable<Container> getListContainer(String key)
  {
    Object value = get(key);
    if (value instanceof ContainerListContainer)
    {
      return (ContainerListContainer) value;
    }
    if (value instanceof ContainerList)
    {
      return (ContainerList<Container>) value;
    }
    if (value instanceof EmptyContainerList)
    {
      return new ContainerListContainer(Collections.<Container>emptyList());
    }
    throw new MetadataKeyInvalidTypeFoundError(key, MetadataKeyInvalidTypeFoundError.ValueType.CONTAINER);
  }

  @Override
  public Iterable<Container> optListContainer(String key, @NotNull Iterable<Container> defaultValue)
  {
    if( mContainerMap.containsKey(key) )
    {
      return getListContainer(key);
    }
    else
    {
      return defaultValue;
    }
  }

  @Override
  public void putListContainer(String key, ContainerList<Container> value)
  {
    put(key, value);
  }

  @Override
  public void putListContainer(String key, Iterable<Container> value)
  {
    ContainerList<Container> list = new ContainerListContainer(value);
    put(key, list);
  }

  @Override
  public Iterable<Long> getListLong(String key)
  {
    Object value = get(key);
    if (value instanceof ContainerListLong)
    {
      return (ContainerListLong) value;
    }
    if (value instanceof ContainerList)
    {
      return (ContainerList<Long>) value;
    }
    if (value instanceof EmptyContainerList)
    {
      return new ContainerListLong(Collections.<Long>emptyList());
    }
    throw new MetadataKeyInvalidTypeFoundError(key, MetadataKeyInvalidTypeFoundError.ValueType.LONG);
  }

  @Override
  public Iterable<Long> optListLong(String key, @NotNull Iterable<Long> defaultValue)
  {
    if( mContainerMap.containsKey(key) )
    {
      return getListLong(key);
    }
    else
    {
      return defaultValue;
    }
  }

  @Override
  public void putListLong(String key, ContainerList<Long> value)
  {
    put(key, value);
  }

  @Override
  public void putListLong(String key, Iterable<Long> value)
  {
    ContainerList<Long> list = new ContainerListLong(value);
    put(key, list);
  }

  @Override
  public void putListInt(String key, Iterable<Integer> value)
  {
    List<Long> tmpList = new ArrayList<Long>();
    for (Integer listValue : value)
    {
      tmpList.add(listValue.longValue());
    }

    ContainerList<Long> list = new ContainerListLong(tmpList);
    put(key, list);
  }

  @Override
  public Iterable<Boolean> getListBoolean(String key)
  {
    Object value = get(key);
    if (value instanceof ContainerListBoolean)
    {
      return (ContainerListBoolean) value;
    }
    if (value instanceof ContainerList)
    {
      return (ContainerList<Boolean>) value;
    }
    if (value instanceof EmptyContainerList)
    {
      return new ContainerListBoolean(Collections.<Boolean>emptyList());
    }
    throw new MetadataKeyInvalidTypeFoundError(key, MetadataKeyInvalidTypeFoundError.ValueType.BOOLEAN);
  }

  @Override
  public Iterable<Boolean> optListBoolean(String key, @NotNull Iterable<Boolean> defaultValue)
  {
    if( mContainerMap.containsKey(key) )
    {
      return getListBoolean(key);
    }
    else
    {
      return defaultValue;
    }
  }

  @Override
  public void putListBoolean(String key, ContainerList<Boolean> value)
  {
    put(key, value);
  }

  @Override
  public void putListBoolean(String key, Iterable<Boolean> value)
  {
    ContainerList<Boolean> list = new ContainerListBoolean(value);
    put(key, list);
  }

  @Override
  public boolean has(String key)
  {
    return mContainerMap.containsKey(key);
  }

  @Override
  public void putList(String key, ContainerList containerList)
  {
    put(key, containerList);
  }

  @Override
  public void remove(String key)
  {
    mContainerMap.remove(key);
  }

  @Override
  public boolean isEmpty()
  {
    return keySet().isEmpty();
  }

  public String toJsonString()
  {
    String s;
    try
    {
      s = sJsonWriter.writeValueAsString( mContainerMap );
    }
    catch( Exception ex )
    {
      return null;
    }
    return s;
  }

  @Override
  public String toString()
  {
    return "ContainerImpl{" +
      "mContainerMap=" + mContainerMap +
      '}';
  }
}

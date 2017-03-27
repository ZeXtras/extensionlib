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

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.container.ContainerSerializer;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import java.util.Set;

@JsonSerialize(using = ContainerSerializer.class)
public interface Container extends Iterable<Map.Entry<String, Object>>, Serializable
{
  Map<String, Object> getPrivateMap();

  public Set<String> keySet();
  public Iterator<Map.Entry<String, Object>> iterator();

  public String getString(String key);
  public String optString(String key, String defaultValue);
  public void putString(String key, String value);

  public boolean getBoolean(String key);
  public boolean optBoolean(String key, boolean defaultValue);
  public void putBoolean(String key, boolean value);

  public long getLong(String key);
  public long optLong(String key, long defaultValue);
  public void putLong(String key, long value);

  public int getInt(String key);
  public int optInt(String key, int defaultValue);

  public Container getContainer(String key);
  public Container optContainer(String key, Container defalutValue);
  public void putContainer(String key, Container value);

  public Iterable<String> getListString(String key);
  public Iterable<String> optListString(String key, Iterable<String> defaultValue);
  public void putListString(String key, ContainerList<String> value);
  public void putListString(String key, Iterable<String> value);

  public Iterable<Container> getListContainer(String key);
  public Iterable<Container> optListContainer(String key, Iterable<Container> defaultValue);
  public void putListContainer(String key, ContainerList<Container> value);
  public void putListContainer(String key, Iterable<Container> value);

  public Iterable<Long> getListLong(String key);
  public Iterable<Long> optListLong(String key, Iterable<Long> defaultValue);
  public void putListLong(String key, ContainerList<Long> value);
  public void putListLong(String key, Iterable<Long> value);

  public void putListInt(String key, Iterable<Integer> value);

  public Iterable<Boolean> getListBoolean(String key);
  public Iterable<Boolean> optListBoolean(String key, Iterable<Boolean> defaultValue);
  public void putListBoolean(String key, ContainerList<Boolean> value);
  public void putListBoolean(String key, Iterable<Boolean> value);

  public String toJsonString();
  public boolean has(String key);

  void putList(String key, ContainerList containerList);

  void remove(String key);

  boolean isEmpty();
}

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

import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsonorg.JSONObjectSerializer;
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule;
import com.zextras.lib.Container;

@JsonSerialize(using = JSONObjectSerializer.class)
public class JSONObject extends LinkedHashMap<String, Object>
{
  private static final ObjectReader sJsonReader;
  private static final ObjectWriter sJsonWriter;
  private static final ObjectWriter sJsonPrettyWriter;

  static
  {
    ObjectMapper om = new ObjectMapper();
    om.registerModule(new JsonOrgModule());
    sJsonReader = om.reader(JSONObject.class);
    sJsonWriter = om.writer();
    sJsonPrettyWriter = om.writerWithDefaultPrettyPrinter();
  }

  public JSONObject()
  {
    super();
  }

  @Override
  public Object clone()
  {
    JSONObject objectCopy = new JSONObject();

    for (Map.Entry<String, Object> entry : this.entrySet())
    {
      String entryKey = entry.getKey();
      Object entryClone = entry.getValue();

      if (entryClone instanceof JSONObject)
      {
        objectCopy.put(entryKey, ((JSONObject) entryClone).clone());
      }

      if (entryClone instanceof JSONArray ) {
        objectCopy.put(entryKey, ((JSONArray) entryClone).clone());
      }

      objectCopy.put(entryKey, entryClone);
    }

    return objectCopy;
  }

  public JSONObject(Map map)
  {
    this();
    if( map == null ) { return; }

    for( Object o : map.entrySet() )
    {
      Map.Entry e = (Map.Entry)o;
      String key = String.valueOf( e.getKey() );
      Object value = e.getValue();
      this.put(key, value);
    }
  }

  public JSONObject(Container container)
  {
    this();
    if (container == null) { return; }

    for (Map.Entry<String, Object> entry : container) {
      if (entry.getValue() instanceof Container)
      {
        super.put(entry.getKey(), new JSONObject((Container) entry.getValue()));
      }
      else if (entry.getValue() instanceof Iterable)
      {
        JSONArray tmpArr = new JSONArray();
        for (Object obj : ((Iterable) (entry.getValue()))) {
          if (obj instanceof Container)
          {
            tmpArr.add(new JSONObject((Container) obj));
          }
          else
          {
            tmpArr.add(obj);
          }
        }
        super.put(entry.getKey(), tmpArr);
      }
      else
      {
        this.put(entry.getKey(), entry.getValue());
      }
    }
  }

  public JSONObject(JSONObject jo, String[] names) throws JSONException
  {
    this();

    if( jo == null ) { return; }

    for( String key : names )
    {
      Object value = jo.get( key );
      this.put(key, value);
    }
  }

  public static JSONObject fromString( String json ) throws JSONException
  {
    if( json == null || json.isEmpty() )
    {
      return new JSONObject();
    }

    try
    {
      return sJsonReader.readValue(json);
    }
    catch(Exception ex)
    {
      throw new JSONException("[JSONObject] Error parsing string: " + ex.getMessage());
    }
  }

  public LinkedHashMap<String, Object> getPrivateMap()
  {
    return this;
  }

  public Object get(String key)
  {
    if( key == null ) { return null; }
    return super.get(key);
  }

  public String getString(String key)
  {
    Object value = this.get(key);
    return  value != null ? String.valueOf(value) : null;
  }

  public boolean getBoolean(String key) throws JSONException
  {
    Object o = get(key);

    if( o instanceof Boolean )
    {
      return o.equals(Boolean.TRUE) ? Boolean.TRUE : Boolean.FALSE;
    }

    if( o instanceof String )
    {
      return ((String) o).equalsIgnoreCase("true") ? Boolean.TRUE : Boolean.FALSE;
    }

    throw new JSONException( key + " is not a Boolean. [it's a " + (o!=null?o.getClass().toString():"null") + "]" );
  }

  public int getInt(String key) throws JSONException
  {
    Object o = get(key);
    try
    {
      if( o instanceof Number )
      {
        return ((Number) o).intValue();
      }
      else
      {
        return Integer.parseInt( (String) o );
      }
    }
    catch(Exception ex)
    {
      throw new JSONException(key + " is not a number. [it's a " + (o!=null?o.getClass().toString():"null") + "]");
    }
  }

  public long getLong(String key) throws JSONException
  {
    Object o = get(key);
    try
    {
      if( o instanceof Number )
      {
        return ((Number) o).longValue();
      }
      else
      {
        return Long.parseLong( (String) o );
      }
    }
    catch(Exception ex)
    {
      throw new JSONException(key + " is not a number. [it's a " + (o!=null?o.getClass().toString():"null") + "]");
    }
  }

  public JSONObject getJSONObject(String key) throws JSONException
  {
    Object object = super.get(key);

    if (object instanceof JSONObject)
    {
      return (JSONObject)object;
    }

    if ( object instanceof Map )
    {
      return new JSONObject( (Map)object );
    }

    if( object instanceof String )
    {
      return JSONObject.fromString( (String) object );
    }

    if( object == null )
    {
      return new JSONObject();
    }

    throw new JSONException(
      "JSONObject[" + key +
      "] is not a JSONObject. [it's a " +
      (object.getClass().toString()) + "]"
    );
  }

  public JSONArray castToJSONArray(String key) throws JSONException
  {
    Object object = get(key);

    if( object == null )
    {
      return new JSONArray();
    }

    if (object instanceof JSONArray)
    {
      return (JSONArray)object;
    }

    if (object instanceof List)
    {
      return new JSONArray( (List)object );
    }

    JSONArray array = new JSONArray();
    array.add((String)object);
    return array;
//    throw new JSONException("JSONObject[" + key + "] is not a JSONArray");
  }

  public JSONArray getJSONArray(String key) throws JSONException
  {
    Object object = get(key);
    if (object instanceof JSONArray)
    {
      return (JSONArray)object;
    }

    if (object instanceof List)
    {
      return new JSONArray( (List)object );
    }

    if (object instanceof Set)
    {
      return new JSONArray(new ArrayList((Set) object));
    }

    if( object instanceof String )
    {
      String str = (String) object;
      if(!str.startsWith("["))
        str = "[\"" + str;

      if(!str.endsWith("["))
        str += "\"]";

      return JSONArray.fromString(str);
    }

    if( object == null )
    {
      return new JSONArray();
    }

    throw new JSONException("JSONObject[" + key + "] is not a JSONArray.");
  }

  public Object opt(String key)
  {
    return get( key );
  }

  public String optString(String key)
  {
    return optString(key, "");
  }

  public String optString(String key, String defValue)
  {
    if( this.has(key) )
    {
      String value = getString(key);
      if( value != null )
      {
        return value;
      }
      else
      {
        return defValue;
      }
    }
    else
    {
      return defValue;
    }
  }

  public int optInt(String key)
  {
    return optInt(key, 0);
  }

  public int optInt(String key, int defValue)
  {
    if( this.has(key) )
    {
      try
      {
        return getInt(key);
      }
      catch(JSONException ex)
      {
        return defValue;
      }
    }
    else
    {
      return defValue;
    }
  }

  public long optLong(String key)
  {
    return optLong(key, 0L);
  }

  public long optLong(String key, long defValue)
  {
    if( this.has(key) )
    {
      try
      {
        return getLong(key);
      }
      catch(JSONException ex)
      {
        return defValue;
      }
    }
    else
    {
      return defValue;
    }
  }

  public boolean optBoolean(String key)
  {
    return optBoolean(key, false);
  }

  public boolean optBoolean(String key, boolean defValue)
  {
    if( this.has(key) )
    {
      try
      {
        return getBoolean(key);
      }
      catch(JSONException ex)
      {
        return defValue;
      }
    }
    else
    {
      return defValue;
    }
  }

  public JSONObject optJSONObject(String key)
  {
    Object o = get(key);
    return o instanceof JSONObject ? (JSONObject)o : null;
  }

  public String[] getNames()
  {
    return this.keySet().toArray(new String[this.keySet().size()]);
  }

  public boolean has(String key)
  {
    return this.get(key) != null;
  }

  public Iterator<String> keys()
  {
    return this.keySet().iterator();
  }

  public int length()
  {
    return this.size();
  }

  @Override
  public String toString()
  {
    return toStringWithWriter(sJsonWriter);
  }

  public String toPrettyString()
  {
    return toStringWithWriter(sJsonPrettyWriter);
  }

  private String toStringWithWriter(ObjectWriter writer)
  {
    String s;
    try
    {
      s = writer.writeValueAsString( this );
    }
    catch( Exception ex )
    {
      return null;
    }
    return s;
  }

  public JSONObject put(String key, Object value)
  {
    if( value == null )
    {
      return this;
    }

    if(value instanceof Map)
    {
      super.put(key, new JSONObject((Map) value));
    }
    else if(value instanceof List)
    {
      super.put(key, new JSONArray((List) value));
    }
    else if(value instanceof Object[])
    {
      List<Object> list = Arrays.asList((Object[]) value);
      super.put(key, new JSONArray( list ));
    }
    else
    {
      super.put(key, value);
    }
    return this;
  }

  public boolean equals(Object obj)
 {
   if(obj==null || !obj.getClass().equals(getClass()))
   {
     return false;
   }
   JSONObject compObj = (JSONObject)obj;

   Set <String> keySet1 = compObj.keySet();
   Set <String> keySet2 = this.keySet();

   if(!keySet1.equals(keySet2))
   {
     return false;
   }

   for(String key : keySet2)
   {
     Object value1 = this.get(key);
     Object value2 = compObj.get(key);
     if((value1 == null && value2 != null) ||
        (value1 != null && value2 == null))
     {
       return false;
     }
     if(value1 != null && value2 != null)
     {
       if(!value1.equals(value2))
       {
         return false;
       }
     }
   }

   return true;
 }
}

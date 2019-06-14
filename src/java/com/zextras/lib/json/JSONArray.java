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

import java.sql.Blob;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule;

public class JSONArray extends ArrayList<Object> implements List<Object>
{
  private static final ObjectReader sJsonReader;
  private static final ObjectWriter sJsonWriter;
  private static final ObjectWriter sJsonPrettyWriter;


  static
  {
    ObjectMapper om = new KindObjectMapper();
    om.registerModule(new JsonOrgModule());
    sJsonReader = om.reader(JSONArray.class);
    sJsonWriter = om.writer();
    sJsonPrettyWriter = om.writerWithDefaultPrettyPrinter();
  }

  public JSONArray()
  {
    super();
  }

  public JSONArray(List list)
  {
    super(list);
  }

  @Override
  public Object clone()
  {
    JSONArray objectCopy = new JSONArray();

    for (Object entryClone : this)
    {

      if (entryClone instanceof JSONObject)
      {
        objectCopy.put(((JSONObject) entryClone).clone());
      }

      if (entryClone instanceof JSONArray)
      {
        objectCopy.put(((JSONArray) entryClone).clone());
      }

      objectCopy.put(entryClone);
    }

    return objectCopy;
  }

  public boolean equals(Object obj)
  {
    if(obj==null || !obj.getClass().equals(getClass()))
    {
      return false;
    }

    JSONArray compObj = (JSONArray)obj;

    if(this.size()!=compObj.size())
    {
      return false;
    }

    int size = this.size();

    for(int i = 0 ; i < size ; i++)
    {
      Object value1 = this.get(i);
      Object value2 = compObj.get(i);

      if((value1 == null && value2 != null) ||
          (value1 != null && value2 == null))
      {
        return false;
      }
      if(value1 != null && value2 != null)
      {
        if(value1.getClass().equals(LinkedHashMap.class))
        {
          value1 = new JSONObject((LinkedHashMap)value1);
        }

        if(value2.getClass().equals(LinkedHashMap.class))
        {
          value2 = new JSONObject((LinkedHashMap)value2);
        }

        if(!value1.equals(value2))
        {
          return false;
        }
      }
    }

    return true;
  }

  public static JSONArray fromString( String json ) throws JSONException
  {
    if( json == null || json.isEmpty() )
    {
      return new JSONArray();
    }

    try
    {
      return sJsonReader.readValue( json );
    }
    catch(Exception ex)
    {
      throw new JSONException("[JSONArray] Error parsing string: " + ex.getMessage());
    }
  }

  public int length()
  {
    return this.size();
  }

  public Object get( int index )
  {
    try
    {
      Object o = super.get( index );
      return o;
    }
    catch( Exception ex )
    {
      return null;
    }
  }

  public JSONArray put( Object o )
  {
    add( o );
    return this;
  }

  public JSONArray set( int i ,Object o )
  {
    if (i < size())
    {
      super.set(i, o);
    }
    else
    {
      super.add(i, o);
    }
    return this;
  }

  public long getLong( int index ) throws JSONException
  {
    Object o = this.get( index );
    try
    {
      if( o instanceof Number )
      {
        return ((Number) o).longValue();
      }
      else
      {
        return Long.parseLong( (String)o );
      }
    }
    catch( Exception ex )
    {
      throw new JSONException("[JSONArray] Element "
                              + index
                              + " is not a number");
    }
  }

  public short getShort( int index ) throws JSONException
  {
    Object o = this.get( index );
    try
    {
      if( o instanceof Number )
      {
        return ((Number) o).shortValue();
      }
      else
      {
        return Short.parseShort( (String)o );
      }
    }
    catch( Exception ex )
    {
      throw new JSONException("[JSONArray] Element "
                                + index
                                + " is not a number");
    }
  }

  public int getInteger( int index ) throws JSONException
  {
    Object o = this.get( index );
    try
    {
      if( o instanceof Number )
      {
        return ((Number) o).intValue();
      }
      else
      {
        return Integer.parseInt( (String)o );
      }
    }
    catch( Exception ex )
    {
      throw new JSONException("[JSONArray] Element "
                                + index
                                + " is not a number");
    }
  }

  public double getDouble( int index ) throws JSONException
  {
    Object o = this.get( index );
    try
    {
      if( o instanceof Number )
      {
        return ((Number) o).doubleValue();
      }
      else
      {
        return Double.parseDouble( (String)o );
      }
    }
    catch( Exception ex )
    {
      throw new JSONException("[JSONArray] Element "
              + index
              + " is not a number");
    }
  }

  public boolean getBoolean( int index ) throws JSONException
  {
    Object o = this.get( index );
    try
    {
      if( o instanceof Boolean )
      {
        return ((Boolean) o).booleanValue();
      }
      else
      {
        return Boolean.parseBoolean( (String)o );
      }
    }
    catch( Exception ex )
    {
      throw new JSONException("[JSONArray] Element "
              + index
              + " is not a number");
    }
  }

  public String getString( int index )
  {
    Object o = this.get( index );
    if( o != null)
    {
      return o.toString();
    }
    return null;
  }

  public byte[] getBytes(int index) throws JSONException
  {
    Object o = this.get( index );
    try
    {
      if( o instanceof byte[] )
      {
        return (byte[]) o;
      }
      else
      {
        return ((String)o).getBytes();
      }
    }
    catch( Exception ex )
    {
      throw new JSONException("[JSONArray] Element "
        + index
        + " is not a byte array");
    }
  }

  public JSONArray getJSONArray( int index )
  {
    Object o = this.get( index );
    if( o instanceof JSONArray )
    {
      return (JSONArray)o;
    }

    if (o instanceof List)
    {
      return new JSONArray( (List)o );
    }

    if( o instanceof String )
    {
      return new JSONArray().put(o);
    }

    return null;
  }

  public JSONObject optJSONObject(int index)
  {
    Object o = opt(index);
    return o instanceof JSONObject ? (JSONObject)o : null;
  }

  public JSONObject getJSONObject( int index )
  {
    Object o = this.get( index );
    if( o instanceof JSONObject )
    {
      return (JSONObject)o;
    }

    if( o instanceof Map )
    {
      return new JSONObject( (Map)o );
    }

    return null;
  }

  public Object opt( int index )
  {
    return get( index );
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

  public boolean isNull(int index)
  {
    return this.opt(index) == null;
  }
}

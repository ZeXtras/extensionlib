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

package com.zextras.utils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.IOException;

public class ToJSONSerializer extends ToStringSerializer
{
  @Override
  public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider)
    throws IOException, JsonGenerationException
  {
    Object obj = ((Jsonable)value).toJSON();

    if( obj instanceof Integer ) {
      jgen.writeNumber((Integer)obj);
      return;
    }
    if( obj instanceof Byte ) {
      jgen.writeNumber(((Byte)obj).intValue());
      return;
    }

    jgen.writeString(obj.toString());
  }
}

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

package com.fasterxml.jackson.datatype.jsonorg;

import java.io.IOException;
import java.lang.reflect.Type;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.zextras.lib.json.JSONArray;
import com.zextras.lib.json.JSONObject;

import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

public class JSONArraySerializer extends JSONBaseSerializer<JSONArray>
{
    public final static JSONArraySerializer instance = new JSONArraySerializer();

    public JSONArraySerializer()
    {
        super(JSONArray.class);
    }

    @Override
    public void serialize(JSONArray value, JsonGenerator jgen, SerializerProvider provider)
        throws IOException, JsonGenerationException
    {
        jgen.writeStartArray();
        serializeContents(value, jgen, provider);
        jgen.writeEndArray();
    }

    @Override
    public void serializeWithType(JSONArray value, JsonGenerator jgen, SerializerProvider provider,
            TypeSerializer typeSer)
        throws IOException, JsonGenerationException
    {
        typeSer.writeTypePrefixForArray(value, jgen);
        serializeContents(value, jgen, provider);
        typeSer.writeTypeSuffixForArray(value, jgen);
    }

    @Override
    public JsonNode getSchema(SerializerProvider provider, Type typeHint)
        throws JsonMappingException
    {
        return createSchemaNode("array", true);
    }

    protected void serializeContents(JSONArray value, JsonGenerator jgen, SerializerProvider provider)
        throws IOException, JsonGenerationException
    {
        for (int i = 0, len = value.length(); i < len; ++i) {
            Object ob = value.opt(i);
            if (ob == null) {
                jgen.writeNull();
                continue;
            }
            Class<?> cls = ob.getClass();
            if (cls == JSONObject.class) {
                JSONObjectSerializer.instance.serialize((JSONObject) ob, jgen, provider);
            } else if (cls == JSONArray.class) {
                serialize((JSONArray) ob, jgen, provider);
            } else  if (cls == String.class) {
                jgen.writeString((String) ob);
            } else  if (cls == Integer.class) {
                jgen.writeNumber(((Integer) ob).intValue());
            } else  if (cls == Long.class) {
                jgen.writeNumber(((Long) ob).longValue());
            } else  if (cls == Boolean.class) {
                jgen.writeBoolean(((Boolean) ob).booleanValue());
            } else  if (cls == Double.class) {
                jgen.writeNumber(((Double) ob).doubleValue());
            } else if (JSONObject.class.isAssignableFrom(cls)) { // sub-class
                JSONObjectSerializer.instance.serialize((JSONObject) ob, jgen, provider);
            } else if (JSONArray.class.isAssignableFrom(cls)) { // sub-class
                serialize((JSONArray) ob, jgen, provider);
            } else if (JSONArray.class.isAssignableFrom(cls)) { // sub-class
                JSONArraySerializer.instance.serialize((JSONArray) ob, jgen, provider);
            } else {
                provider.defaultSerializeValue(ob, jgen);
            }
        }
    }
}

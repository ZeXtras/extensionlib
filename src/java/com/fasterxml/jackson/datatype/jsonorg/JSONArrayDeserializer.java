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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import com.zextras.lib.json.JSONArray;
import com.zextras.lib.json.JSONObject;

public class JSONArrayDeserializer extends StdDeserializer<JSONArray>
{
    public final static JSONArrayDeserializer instance = new JSONArrayDeserializer();

    public JSONArrayDeserializer()
    {
        super(JSONArray.class);
    }

    @Override
    public JSONArray deserialize(JsonParser jp, DeserializationContext ctxt)
        throws IOException, JsonProcessingException
    {
        JSONArray array = new JSONArray();
        JsonToken t;
        while ((t = jp.nextToken()) != JsonToken.END_ARRAY) {
            switch (t) {
            case START_ARRAY:
                array.put(deserialize(jp, ctxt));
                continue;
            case START_OBJECT:
                array.put(JSONObjectDeserializer.instance.deserialize(jp, ctxt));
                continue;
            case VALUE_STRING:
                array.put(jp.getText());
                continue;
            case VALUE_NULL:
                array.put(null);
                continue;
            case VALUE_TRUE:
                array.put(Boolean.TRUE);
                continue;
            case VALUE_FALSE:
                array.put(Boolean.FALSE);
                continue;
            case VALUE_NUMBER_INT:
                array.put(jp.getNumberValue());
                continue;
            case VALUE_NUMBER_FLOAT:
                array.put(jp.getNumberValue());
                continue;
            case VALUE_EMBEDDED_OBJECT:
                array.put(jp.getEmbeddedObject());
                continue;
            }
            throw ctxt.mappingException("Urecognized or unsupported JsonToken type: "+t);
        }
        return array;
    }
}

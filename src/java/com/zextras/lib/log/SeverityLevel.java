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

package com.zextras.lib.log;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

public enum SeverityLevel
{
  DEBUG,
  INFORMATION,
  WARNING,
  ERROR,
  CRITICAL;

  private final static Map<Byte, SeverityLevel>   mByteToSeverityLevelMap;
  private final static Map<SeverityLevel, Byte>   mSeverityLevelToByteMap;
  private final static Map<String, SeverityLevel> mStringToSeverityLevelMap;

  static
  {
    mByteToSeverityLevelMap = new HashMap<Byte, SeverityLevel>(5);
    mByteToSeverityLevelMap.put((byte) 0, DEBUG);
    mByteToSeverityLevelMap.put((byte) 1, INFORMATION);
    mByteToSeverityLevelMap.put((byte) 2, WARNING);
    mByteToSeverityLevelMap.put((byte) 3, ERROR);
    mByteToSeverityLevelMap.put((byte) 4, CRITICAL);

    mSeverityLevelToByteMap = new HashMap<SeverityLevel, Byte>(5);
    mSeverityLevelToByteMap.put(DEBUG, (byte) 0);
    mSeverityLevelToByteMap.put(INFORMATION, (byte) 1);
    mSeverityLevelToByteMap.put(WARNING, (byte) 2);
    mSeverityLevelToByteMap.put(ERROR, (byte) 3);
    mSeverityLevelToByteMap.put(CRITICAL, (byte) 4);

    mStringToSeverityLevelMap = new HashMap<String, SeverityLevel>(5);
    mStringToSeverityLevelMap.put("debug", DEBUG);
    mStringToSeverityLevelMap.put("information", INFORMATION);
    mStringToSeverityLevelMap.put("warning", WARNING);
    mStringToSeverityLevelMap.put("error", ERROR);
    mStringToSeverityLevelMap.put("critical", CRITICAL);
  }

  public static SeverityLevel fromByte(byte level)
    throws InvalidParameterException
  {
    if(mByteToSeverityLevelMap.containsKey(level))
    {
      return mByteToSeverityLevelMap.get(level);
    }

    throw new InvalidParameterException("Invalid severity level " + level + ". Valid levels: " + mByteToSeverityLevelMap.keySet());
  }

  public static SeverityLevel fromString(String level) throws InvalidParameterException
  {
    try
    {
      return fromByte(Byte.valueOf(level));
    }
    catch ( NumberFormatException ex ) {
    }

    if( mStringToSeverityLevelMap.containsKey(level) )
    {
      return mStringToSeverityLevelMap.get(level);
    }
    else
    {
      throw new InvalidParameterException("Invalid severity level " + level + ". Valid levels: " + mStringToSeverityLevelMap.keySet());
    }
  }

  public byte toByte()
    throws InvalidParameterException
  {
    return mSeverityLevelToByteMap.get(this);
  }

  public String toString()
  {
    String original = super.toString().toLowerCase();
    char [] chars   = original.toCharArray();
    chars[0]        = Character.toUpperCase(chars[0]);
    return new String(chars);
  }

  public static boolean checkSeverityLevel(String value)
  {
    try {
      return mByteToSeverityLevelMap.containsKey( Byte.valueOf(value) );
    } catch (NumberFormatException ex) {
      return false;
    }
  }
}

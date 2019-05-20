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

package com.zextras.modules.chat.server.db;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.zextras.lib.db.DbHandler;
import com.zextras.lib.switches.Service;
import org.openzal.zal.ZimbraConnectionProvider;

import java.sql.Connection;
import java.sql.SQLException;

@Singleton
public abstract class MariaDbHandler implements DbHandler
{
  private final ZimbraConnectionProvider mZimbraConnectionProvider;

  @Inject
  public MariaDbHandler(
    ZimbraConnectionProvider zimbraConnectionProvider
  )
  {
    mZimbraConnectionProvider = zimbraConnectionProvider;
  }

  public Connection getConnection() throws SQLException
  {
    return mZimbraConnectionProvider.getConnection().getConnection();
  }

  public String cleanSql(String sql)
  {
    return sql;
  }

  public void start() throws Service.ServiceStartException
  {}

  public void stop()
  {}
}

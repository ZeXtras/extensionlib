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

import org.openzal.zal.Account;
import org.openzal.zal.Provisioning;
import org.openzal.zal.exceptions.ZimbraException;
import javax.annotation.Nonnull;

public class AccountHelper
{
  private Account     mAccount;
  private String      mName;
  private AccountData mAccountData;

  public AccountHelper(
    @Nonnull String email,
    @Nonnull Provisioning provisioning
  )
  {
    try
    {
      mAccount = provisioning.getAccountByName(email);
    }
    catch (ZimbraException e)
    {
      RuntimeException ex = new RuntimeException();
      ex.initCause(ex);
      throw ex;
    }

    mName = emptyStringWhenNull(email);

    if (mAccount != null)
    {
      mAccountData = new AccountDataWithAccount(mAccount);
    }
    else
    {
      mAccountData = new AccountDataWithNoAccount(email, email);
    }
  }

  public String getName()
  {
    return mAccountData.getName();
  }

  public String getMainAddress()
  {
    return mAccountData.getMainAddress();
  }

  private String emptyStringWhenNull(String str)
  {
    return (str == null ? "" : str);
  }

  private interface AccountData {
    public String getName();
    public String getMainAddress();
  }

  private class AccountDataWithAccount implements AccountData
  {
    private final Account mAccount;

    public AccountDataWithAccount(Account account)
    {
      mAccount = account;
    }

    @Override
    public String getName()
    {
      mName = emptyStringWhenNull(mAccount.getDisplayName());
      if (mName.trim().isEmpty())
      {
        mName = mAccount.getUid();
      }
      return mName;
    }

    @Override
    public String getMainAddress()
    {
      return mAccount.getName();
    }
  }

  private class AccountDataWithNoAccount implements AccountData
  {
    private final String mName;
    private final String mEmail;

    public AccountDataWithNoAccount(String name, String email)
    {
      mName = name;
      mEmail = email;
    }

    @Override
    public String getName()
    {
      return mName;
    }

    @Override
    public String getMainAddress()
    {
      return mEmail;
    }
  }
}

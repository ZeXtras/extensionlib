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

import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.openzal.zal.LocalConfig;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;

@Singleton
public class ZimbraSSLContextProvider implements Provider<SSLContext>
{
  public SSLContext createTLSContext()
  {
    return createContext("TLS");
  }

  public SSLContext createSSLContext()
  {
    return createContext("SSL");
  }

  private SSLContext createContext(String type)
  {
    try
    {
      String keyStoreLocation = LocalConfig.get("mailboxd_keystore");
      String keyStorePassword = LocalConfig.get("mailboxd_keystore_password");

      KeyStore ks = KeyStore.getInstance("JKS");
      InputStream im = new FileInputStream(keyStoreLocation);
      try
      {
        ks.load(im, keyStorePassword.toCharArray());
      }
      finally
      {
        im.close();
      }
      KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
      kmf.init(ks, keyStorePassword.toCharArray());
      SSLContext sc = SSLContext.getInstance(type);
      sc.init(kmf.getKeyManagers(), null, new SecureRandom());

      return sc;
    }
    catch (Exception ex)
    {
      RuntimeException newEx = new RuntimeException("Unable to create SSL/TLS context");
      newEx.initCause(ex);
      throw newEx;
    }
  }

  @Override
  public SSLContext get()
  {
    return createSSLContext();
  }
}

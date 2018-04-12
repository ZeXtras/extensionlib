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
import org.openzal.zal.Provisioning;
import org.openzal.zal.ProvisioningImp;

import javax.inject.Inject;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@Singleton
public class ZimbraSSLContextProvider implements Provider<SSLContext>
{
  private final Provisioning mProvisioning;

  @Inject
  public ZimbraSSLContextProvider(
    Provisioning provisioning
  )
  {
    mProvisioning = provisioning;
  }

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

  public String[] getMailboxdSslProtocols() {
    Collection<String> protocols = mProvisioning.getLocalServer().getMultiAttr(ProvisioningImp.A_zimbraMailboxdSSLProtocols);
    return protocols.toArray(new String[0]);
  }

  public Collection<String> getSslExcludedCiphers() {
    return mProvisioning.getLocalServer().getMultiAttr(ProvisioningImp.A_zimbraSSLExcludeCipherSuites);
  }

  public Collection<String> getSslIncludedCiphers() {
    return mProvisioning.getLocalServer().getMultiAttr(ProvisioningImp.A_zimbraSSLIncludeCipherSuites);
  }

  public String[] getSslCiphers(SSLContext sslContext) {
    Collection<String> ciphers = getSslIncludedCiphers();
    if (ciphers.size() == 0)
    {
      ciphers = Arrays.asList(sslContext.getServerSocketFactory().getSupportedCipherSuites());
    }
    Collection<String> excludedCiphers = new ArrayList<String>(getSslExcludedCiphers());

    if (excludedCiphers.size() > 0 )
    {
      Collection<String> acceptedCiphers = new ArrayList<String>();
      for (String cipher : ciphers)
      {
        boolean matches = false;
        for (String exclude : excludedCiphers)
        {
          if (cipher.matches(exclude))
          {
            matches = true;
          }
        }
        if (!matches)
        {
          acceptedCiphers.add(cipher);
        }
      }
      return acceptedCiphers.toArray(new String[0]);
    }
    else
    {
      return ciphers.toArray(new String[0]);
    }
  }


}

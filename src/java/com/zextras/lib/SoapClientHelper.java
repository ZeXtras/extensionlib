package com.zextras.lib;

import org.openzal.zal.Account;
import org.openzal.zal.AdministrationConstants;
import org.openzal.zal.AuthProvider;
import org.openzal.zal.OperationContext;
import org.openzal.zal.Server;
import org.openzal.zal.ZAuthToken;
import org.openzal.zal.soap.SoapTransport;

public class SoapClientHelper
{
  public SoapTransport openUser(OperationContext operationContext, Server server)
  {
    return open(operationContext,server.getServiceURL(AdministrationConstants.USER_SERVICE_URI));
  }

  public SoapTransport openAdmin(OperationContext operationContext,Server server)
  {
    return open(operationContext,server.getAdminURL(AdministrationConstants.ADMIN_SERVICE_URI));
  }

  public SoapTransport open(OperationContext operationContext, String url)
  {
    SoapTransport soapTransport = new SoapTransport(url);
    Account contextAccount = operationContext.getAccount();
    ZAuthToken authToken = AuthProvider.getAuthToken(contextAccount);
    soapTransport.setAuthToken(authToken);
    soapTransport.setTargetAcctId(contextAccount.getId());

    return soapTransport;
  }

}
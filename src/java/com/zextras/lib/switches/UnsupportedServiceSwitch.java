package com.zextras.lib.switches;

public class UnsupportedServiceSwitch implements ServiceSwitch
{
  private final String mName;

  public UnsupportedServiceSwitch(String name)
  {
    mName = name;
  }

  @Override
  public void turnOn(SwitchConditionNotification conditionNotification) throws Service.ServiceStartException
  {
    throw new Service.ServiceStartException("Unsupported service");
  }

  @Override
  public void turnOff()
  {
  }

  @Override
  public boolean isOn()
  {
    return false;
  }

  @Override
  public boolean couldTurnOn(SwitchConditionNotification conditionNotification)
  {
    return false;
  }

  @Override
  public boolean couldTurnOff()
  {
    return false;
  }

  @Override
  public String name()
  {
    return mName;
  }
}

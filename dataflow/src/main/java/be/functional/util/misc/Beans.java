package be.functional.util.misc;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;

public class Beans {

  private Beans() {
  }

  public static Object getProperty(final Object pBean, final String pPropertyName) {
    try {
      return PropertyUtils.getProperty(pBean, pPropertyName);
    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public static void setProperty(final Object pBean, final String pPropertyName, final Object pValue) {
    try {
      PropertyUtils.setProperty(pBean, pPropertyName, pValue);
    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new IllegalArgumentException(e);
    }
  }
}

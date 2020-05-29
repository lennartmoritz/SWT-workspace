package org.iMage.plugins;

import java.util.Objects;

/**
 * Abstract parent class for plug-ins for JMJRST
 *
 * @author Dominik Fuchss
 */
public abstract class PluginForJmjrst implements Comparable<PluginForJmjrst> {

  /**
   * Returns the name of this plug-in
   *
   * @return the name of the plug-in
   */
  public abstract String getName();

  /**
   * Returns the number of parameters of this plug-in
   *
   * @return the number of parameters of this plug-in
   */
  public abstract int getNumberOfParameters();

  /**
   * JMJRST pushes the main application to every subclass - so plug-ins are allowed to look at Main
   * as well.
   *
   * @param main
   *     JMJRST main application
   */
  public abstract void init(org.jis.Main main);

  /**
   * Runs plug-in
   */
  public abstract void run();

  /**
   * Returns whether the plug-in can be configured or not
   *
   * @return true if the plug-in can be configured.
   */
  public abstract boolean isConfigurable();

  /**
   * Open a configuration dialogue.
   */
  public abstract void configure();

  @Override
  public int compareTo(PluginForJmjrst otherPlugin) {
    int res = this.getName().compareTo(otherPlugin.getName());
    if (res == 0) {
      res = this.getNumberOfParameters() - otherPlugin.getNumberOfParameters();
    }
    return res;
  }

  @Override
  public int hashCode() {
    int hashVal = Objects.hash(this.getName(), this.getNumberOfParameters());
    return hashVal;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj.getClass() != this.getClass()) {
      return false;
    } 
    return this.compareTo((PluginForJmjrst) obj) == 0;
  }
}

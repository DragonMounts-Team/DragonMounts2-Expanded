package net.dragonmounts.util.debugging;

import net.dragonmounts.DragonMountsConfig;

/**
 * User: The Grey Ghost
 * Date: 24/12/2014
 *
 * The Startup classes for this example are called during startup, in the following order:
 *  preInitCommon
 *  preInitClientOnly
 *  initCommon
 *  initClientOnly
 *  postInitCommon
 *  postInitClientOnly
 *  See MinecraftByExample class for more information
 */
public class StartupDebugClientOnly
{
  public static void preInitClientOnly()
  {
    if (!DragonMountsConfig.isDebug()) return;
  }

  public static void initClientOnly()
  {
    if (!DragonMountsConfig.isDebug()) return;
  }

  public static void postInitClientOnly()
  {
    if (!DragonMountsConfig.isDebug()) return;
  }
}

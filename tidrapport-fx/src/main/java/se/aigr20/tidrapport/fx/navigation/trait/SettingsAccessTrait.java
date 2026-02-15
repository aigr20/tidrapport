package se.aigr20.tidrapport.fx.navigation.trait;

import se.aigr20.tidrapport.fx.settings.SettingsService;

public non-sealed interface SettingsAccessTrait extends ControllerTrait {
  SettingsService getSettingsService();

  void setSettingsService(SettingsService settingsService);
}

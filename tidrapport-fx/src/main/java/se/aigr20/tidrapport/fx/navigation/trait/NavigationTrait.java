package se.aigr20.tidrapport.fx.navigation.trait;

import se.aigr20.tidrapport.fx.navigation.StageNavigator;
import se.aigr20.tidrapport.fx.navigation.View;

public non-sealed interface NavigationTrait extends ControllerTrait {
  StageNavigator getNavigator();

  void setNavigator(StageNavigator navigator);

  default void navigateTo(final View view) {
    getNavigator().navigateTo(view);
  }
}

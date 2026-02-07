package se.aigr20.tidrapport.fx;

import se.aigr20.tidrapport.fx.navigation.AbstractNavigator;
import se.aigr20.tidrapport.fx.navigation.StageNavigator;
import se.aigr20.tidrapport.fx.navigation.View;
import se.aigr20.tidrapport.fx.navigation.trait.NavigationTrait;

import javafx.application.Application;
import javafx.stage.Stage;

public class TidrapportFxApplication extends Application {
  @Override
  public void start(final Stage stage) {
    final StageNavigator navigator = new StageNavigator(stage);
    AbstractNavigator.registerInjectable(NavigationTrait.class, navigator);

    navigator.navigateTo(View.START);
    stage.setTitle("Tidrapport");
    stage.show();
  }
}

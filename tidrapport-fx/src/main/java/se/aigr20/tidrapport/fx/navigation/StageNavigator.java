package se.aigr20.tidrapport.fx.navigation;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

import se.aigr20.tidrapport.fx.navigation.trait.StageAccessTrait;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Provides navigation as well as dependency injection for JavaFX controllers.
 */
public class StageNavigator extends AbstractNavigator {
  private final Stage appStage;
  private final Map<View, Parent> cachedViews;

  public StageNavigator(final Stage stage) {
    appStage = stage;
    cachedViews = new EnumMap<>(View.class);
  }

  public void navigateTo(final View view) {
    final Parent target = cachedViews.computeIfAbsent(view, ignored -> {
      final FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/" +
                                                                      view.getFxmlFile()));
      loader.setControllerFactory(controllerClass -> {
        final Object controller = createController(controllerClass);
        if (controller instanceof StageAccessTrait stageAccess) {
          stageAccess.setStage(appStage);
        }
        return controller;
      });
      try {
        return loader.load();
      } catch (final IOException e) {
        throw new IllegalStateException("Failed to load FXML: " + loader.getLocation(), e);
      }
    });
    appStage.setScene(new Scene(target));
  }
}

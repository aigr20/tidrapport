package se.aigr20.tidrapport.fx;

import java.io.IOException;
import java.util.Set;

import se.aigr20.tidrapport.fx.navigation.AbstractNavigator;
import se.aigr20.tidrapport.fx.navigation.StageNavigator;
import se.aigr20.tidrapport.fx.navigation.View;
import se.aigr20.tidrapport.fx.navigation.trait.CreatesReports;
import se.aigr20.tidrapport.fx.navigation.trait.NavigationTrait;
import se.aigr20.tidrapport.fx.navigation.trait.SettingsAccessTrait;
import se.aigr20.tidrapport.fx.report.ReportService;
import se.aigr20.tidrapport.fx.settings.SettingsService;
import se.aigr20.tidrapport.fx.settings.TidrapportSettings;

import javafx.application.Application;
import javafx.stage.Stage;

public class TidrapportFxApplication extends Application {
  @Override
  public void start(final Stage stage) {
    final StageNavigator navigator = new StageNavigator(stage);
    final SettingsService settingsService = new SettingsService(new TidrapportSettings(null,
                                                                                       8d,
                                                                                       5,
                                                                                       Set.of()));
    final ReportService reportService = new ReportService(settingsService);
    AbstractNavigator.registerInjectable(NavigationTrait.class, navigator);
    AbstractNavigator.registerInjectable(SettingsAccessTrait.class, settingsService);
    AbstractNavigator.registerInjectable(CreatesReports.class, reportService);

    loadSavedSettings(settingsService);

    navigator.navigateTo(View.REPORT);
    stage.setTitle("Tidrapport");
    stage.show();
  }

  public static void main(final String[] args) {
    launch(args);
  }

  private void loadSavedSettings(final SettingsService settingsService) {
    try {
      final TidrapportSettings settings = settingsService.loadSettings();
      if (settings != null) {
        settingsService.setSettings(settings);
      } else {
        System.err.println("No settings.json found. Using default settings");
      }
    } catch (final IOException e) {
      throw new IllegalStateException(e);
    }
  }
}

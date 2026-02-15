package se.aigr20.tidrapport.fx.settings;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import se.aigr20.tidrapport.fx.navigation.trait.SettingsAccessTrait;
import se.aigr20.tidrapport.fx.navigation.trait.StageAccessTrait;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class SettingsController implements SettingsAccessTrait, StageAccessTrait {
  private Stage stage;
  private SettingsService settingsService;

  @FXML
  private Spinner<Double> hoursPerDaySpinner;
  @FXML
  private Spinner<Integer> daysPerWeekSpinner;
  @FXML
  private ListView<String> excludedActivitiesList;
  @FXML
  private Button removeExcludedBtn;
  private ObservableList<String> excludedActivities;

  @FXML
  public void initialize() {
    final List<String> initiallyExcluded = new ArrayList<>(settingsService
                                                                   .getCurrent()
                                                                   .excludedActivities());
    excludedActivities = FXCollections.observableList(initiallyExcluded);

    hoursPerDaySpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0d,
                                                                                         24d,
                                                                                         settingsService
                                                                                                 .getCurrent()
                                                                                                 .hoursPerDay(),
                                                                                         0.05d));
    daysPerWeekSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,
                                                                                          7,
                                                                                          settingsService
                                                                                                  .getCurrent()
                                                                                                  .daysPerWeek(),
                                                                                          1));

    excludedActivitiesList.setItems(excludedActivities);
    excludedActivitiesList.setEditable(true);
    excludedActivitiesList.setCellFactory(lv -> new AutoFocusedTextFieldListCell<>(new StringConverter<>() {
      @Override
      public String toString(final String s) {
        return s;
      }

      @Override
      public String fromString(final String s) {
        return s;
      }
    }));
    removeExcludedBtn
            .disableProperty()
            .bind(excludedActivitiesList.getSelectionModel().selectedItemProperty().isNull());

  }

  @FXML
  public void addExcludedActivity() {
    final int nextIndex = excludedActivities.size();
    excludedActivities.add("");
    excludedActivitiesList.scrollTo(nextIndex);
    excludedActivitiesList.edit(nextIndex);
  }

  @FXML
  public void removeSelectedExcludedActivity() {
    excludedActivities.remove(excludedActivitiesList.getSelectionModel().getSelectedIndex());
  }

  @FXML
  public void handleSave() {
    final double hoursPerDay = hoursPerDaySpinner.getValue();
    final int daysPerWeek = daysPerWeekSpinner.getValue();

    final TidrapportSettings newSettings = settingsService
            .getSettings()
            .get()
            .withHoursPerDay(hoursPerDay)
            .withDaysPerWeek(daysPerWeek)
            .withExcludedActivities(new HashSet<>(excludedActivities));

    final Task<Void> task = settingsService.saveSettings(newSettings);
    task.setOnSucceeded(event -> stage.close());
    Thread.ofVirtual().start(task);
  }

  @FXML
  public void handleCancel() {
    stage.close();
  }

  @Override
  public SettingsService getSettingsService() {
    return settingsService;
  }

  @Override
  public void setSettingsService(final SettingsService settingsService) {
    this.settingsService = settingsService;
  }

  @Override
  public Stage getStage() {
    return stage;
  }

  @Override
  public void setStage(final Stage stage) {
    this.stage = stage;
  }
}

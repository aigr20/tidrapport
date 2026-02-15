package se.aigr20.tidrapport.fx.report;

import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import se.aigr20.tidrapport.fx.navigation.StageNavigator;
import se.aigr20.tidrapport.fx.navigation.View;
import se.aigr20.tidrapport.fx.navigation.trait.CreatesReports;
import se.aigr20.tidrapport.fx.navigation.trait.NavigationTrait;
import se.aigr20.tidrapport.fx.navigation.trait.SettingsAccessTrait;
import se.aigr20.tidrapport.fx.navigation.trait.StageAccessTrait;
import se.aigr20.tidrapport.fx.report.cell.DayAndActivityCell;
import se.aigr20.tidrapport.fx.report.cell.DifferenceColorCell;
import se.aigr20.tidrapport.fx.report.model.ActivityRow;
import se.aigr20.tidrapport.fx.report.model.DayRow;
import se.aigr20.tidrapport.fx.report.model.ReportRow;
import se.aigr20.tidrapport.fx.settings.SettingsService;
import se.aigr20.tidrapport.fx.settings.TidrapportSettings;
import se.aigr20.tidrapport.model.DayReport;
import se.aigr20.tidrapport.model.WeekReport;
import se.aigr20.tidrapport.model.YearReport;
import se.aigr20.tidrapport.parse.ParseException;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ReportController implements NavigationTrait, StageAccessTrait, SettingsAccessTrait,
        CreatesReports {

  private StageNavigator navigator;
  private SettingsService settings;
  private ReportService reportService;
  private Stage stage;
  private YearReport selectedYearReport;

  @FXML
  private TreeTableView<ReportRow> weekTable;
  @FXML
  private ComboBox<Year> yearSelector;
  @FXML
  private ComboBox<Integer> weekSelector;
  @FXML
  private Button reloadFileBtn;
  @FXML
  private Button closeFileBtn;
  @FXML
  private Text statusContent;
  private final ObservableList<Integer> availableWeeks =
          FXCollections.observableList(new ArrayList<>());

  private Year lastSelectedYear;
  private Integer lastSelectedWeek;

  @FXML
  public void initialize() {
    reloadFileBtn
            .disableProperty()
            .bind(settings.getSettings().map(conf -> conf.tidrapportFilePath() == null));
    closeFileBtn
            .disableProperty()
            .bind(settings.getSettings().map(conf -> conf.tidrapportFilePath() == null));

    statusContent.setText("");

    yearSelector.setItems(FXCollections.observableArrayList());
    yearSelector.setOnAction(event -> {
      lastSelectedYear = yearSelector.getValue();
      if (lastSelectedYear != null) {
        loadYear(lastSelectedYear);
      }
    });
    yearSelector
            .disableProperty()
            .bind(settings.getSettings().map(conf -> conf.tidrapportFilePath() == null));

    weekSelector.setItems(availableWeeks);
    weekSelector.setOnAction(event -> {
      lastSelectedWeek = weekSelector.getValue();
      if (lastSelectedWeek != null) {
        showReport(lastSelectedWeek);
      }
    });
    weekSelector
            .disableProperty()
            .bind(settings.getSettings().map(conf -> conf.tidrapportFilePath() == null));

    initializeTableColumns();

    if (settings.getCurrent().tidrapportFilePath() != null) {
      final Path path = Path.of(settings.getCurrent().tidrapportFilePath());
      openTidrapportFile(path);
    }
  }

  @FXML
  public void reload() {
    final Task<Boolean> task = reportService.loadFile(Path.of(settings
                                                                      .getCurrent()
                                                                      .tidrapportFilePath()));
    task.setOnSucceeded(event -> {
      yearSelector.getItems().setAll(reportService.getAvailableYears());
      yearSelector.setValue(yearSelector.getItems().getLast());
    });

    Thread.ofVirtual().start(task);
  }

  @FXML
  public void pickFile() {
    final FileChooser filePicker = new FileChooser();
    filePicker.setTitle("Öppna tidrapport");
    final File file = filePicker.showOpenDialog(stage);
    if (file == null) {
      return;
    }

    openTidrapportFile(file.toPath());
  }

  @FXML
  public void closeFile() {
    final TidrapportSettings newSettings = settings.getCurrent().withTidrapportFilePath(null);
    final Task<Void> task = settings.saveSettings(newSettings);
    task.setOnSucceeded(event -> {
      selectedYearReport = null;
      yearSelector.getItems().clear();
      weekSelector.getItems().clear();
      statusContent.setText("");
      weekTable.getRoot().getChildren().clear();
    });

    Thread.ofVirtual().start(task);
  }

  @FXML
  public void openSettingsDialog() {
    final Stage dialog = new Stage();
    final StageNavigator dialogNavigator = new StageNavigator(dialog);
    dialog.setTitle("Inställningar");
    dialog.initOwner(stage);
    dialog.initModality(Modality.APPLICATION_MODAL);
    dialogNavigator.navigateTo(View.SETTINGS);

    dialog.showAndWait();

    if (settings.getCurrent().tidrapportFilePath() != null) {
      loadYear(lastSelectedYear);
    }
  }
  
  @FXML
  public void exitProgram() {
	  stage.close();
  }

  private void loadYear(final Year year) {
    final Task<YearReport> task = reportService.openYearReport(year.getValue());
    task.setOnSucceeded(event -> {
      final YearReport report = task.getValue();
      selectedYearReport = report;
      final List<Integer> weeks = report.weeks().stream().map(WeekReport::week).toList();
      weekSelector.getItems().setAll(weeks);

      final int selectedWeek = lastSelectedWeek != null && weeks.contains(lastSelectedWeek) ?
                               lastSelectedWeek :
                               weeks.getLast();
      weekSelector.setValue(selectedWeek);
    });
    Thread.ofVirtual().start(task);
  }

  private void showReport(final int week) {
    if (selectedYearReport == null) {
      throw new IllegalStateException("Ingen årsrapport vald");
    }

    final WeekReport report = selectedYearReport
            .weeks()
            .stream()
            .filter(item -> item.week() == week)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(
                    "En vecka som inte finns i årsrapporten blev vald"));
    TreeItem<ReportRow> root = new TreeItem<>();
    root.setExpanded(true);

    for (final DayReport day : report.days()) {
      final DayRow dayRow = new DayRow(day);
      final TreeItem<ReportRow> dayItem = new TreeItem<>(dayRow);
      dayItem.setExpanded(true);

      for (final ActivityRow activityRow : dayRow.getActivities()) {
        dayItem.getChildren().add(new TreeItem<>(activityRow));
      }

      root.getChildren().add(dayItem);
    }

    weekTable.setRoot(root);
    weekTable.setShowRoot(false);

    statusContent.setText("Vecka %d: %.2fh/%.2fh (%.2fh kvar)%n".formatted(report.week(),
                                                                           hours(report.total()),
                                                                           hours(report.required()),
                                                                           hours(report.difference())));
  }

  private void openTidrapportFile(final Path file) {
    final Task<Boolean> task = reportService.loadFile(file);
    task.setOnSucceeded(event -> {
      final Task<Void> saveTask = settings.saveSettings(settings
                                                                .getCurrent()
                                                                .withTidrapportFilePath(file.toString()));
      yearSelector.getItems().setAll(reportService.getAvailableYears());
      yearSelector.setValue(yearSelector.getItems().getLast());

      Thread.ofVirtual().start(saveTask);
    });
    task.setOnFailed(event -> {
      final Throwable exception = task.getException();
      final Alert errorAlert = new Alert(AlertType.ERROR);
      if (exception instanceof final ParseException parseException) {
        errorAlert.setHeaderText("Parsefel");
        errorAlert.setContentText(parseException.getMessage());
      } else {
        errorAlert.setHeaderText("Okänt fel");
        errorAlert.setContentText(exception.getMessage());
      }

      errorAlert.show();
    });

    Thread.ofVirtual().start(task);
  }

  private void initializeTableColumns() {
    weekTable.setPlaceholder(new Label("Ingen vecka öppnad"));
    weekTable
            .getColumns()
            .setAll(createNameColumn(),
                    createHoursColumn(),
                    createRequiredHoursColumn(),
                    createDiffColumn());
    weekTable.setShowRoot(false);
  }

  private TreeTableColumn<ReportRow, String> createNameColumn() {
    final TreeTableColumn<ReportRow, String> nameColumn = new TreeTableColumn<>("Dag");
    nameColumn.setCellValueFactory(features -> new SimpleStringProperty(features
                                                                                .getValue()
                                                                                .getValue()
                                                                                .getLabel()));
    nameColumn.setCellFactory(col -> new DayAndActivityCell(settings.getSettings()));

    return nameColumn;
  }

  private TreeTableColumn<ReportRow, String> createHoursColumn() {
    final TreeTableColumn<ReportRow, String> hoursColumn = new TreeTableColumn<>("Timmar");
    hoursColumn.setCellValueFactory(features -> {
      final Duration d = features.getValue().getValue().getTotal();
      return new SimpleStringProperty(format(hours(d)));
    });

    return hoursColumn;
  }

  private TreeTableColumn<ReportRow, String> createRequiredHoursColumn() {
    final TreeTableColumn<ReportRow, String> requiredColumn = new TreeTableColumn<>("Förväntad");
    requiredColumn.setCellValueFactory(features -> {
      final Duration d = features.getValue().getValue().getRequired();
      if (d != null) {
        return new SimpleStringProperty(format(hours(d)));
      }
      return null;
    });

    return requiredColumn;
  }

  private TreeTableColumn<ReportRow, String> createDiffColumn() {
    final TreeTableColumn<ReportRow, String> diffColumn = new TreeTableColumn<>("Skillnad");
    diffColumn.setCellValueFactory(features -> {
      final Duration d = features.getValue().getValue().getDifference();
      if (d != null) {
        return new SimpleStringProperty(format(hours(d.abs())));
      }
      return null;
    });
    diffColumn.setCellFactory(col -> new DifferenceColorCell());

    return diffColumn;
  }

  private String format(final double value) {
    return String.format("%.2f", value);
  }

  private double hours(final Duration duration) {
    return duration.toMinutes() / 60d;
  }

  @Override
  public ReportService getReportService() {
    return reportService;
  }

  @Override
  public void setReportService(final ReportService reportService) {
    this.reportService = reportService;
  }

  @Override
  public SettingsService getSettingsService() {
    return settings;
  }

  @Override
  public void setSettingsService(final SettingsService configuration) {
    this.settings = configuration;
  }

  @Override
  public StageNavigator getNavigator() {
    return navigator;
  }

  @Override
  public void setNavigator(final StageNavigator navigator) {
    this.navigator = navigator;
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

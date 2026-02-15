package se.aigr20.tidrapport.fx.report.cell;

import se.aigr20.tidrapport.fx.report.model.ActivityRow;
import se.aigr20.tidrapport.fx.report.model.DayRow;
import se.aigr20.tidrapport.fx.report.model.ReportRow;
import se.aigr20.tidrapport.fx.settings.TidrapportSettings;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;

public class DayAndActivityCell extends TreeTableCell<ReportRow, String> {

  private final ObservableValue<TidrapportSettings> settings;

  public DayAndActivityCell(final ObservableValue<TidrapportSettings> settings) {
    this.settings = settings;
  }

  @Override
  protected void updateItem(final String item, final boolean empty) {
    super.updateItem(item, empty);
    setOpacity(1);
    if (empty || item == null) {
      setText("");
      setStyle("");
      return;
    }

    setText(item);
    setStyle("");
    final TreeItem<ReportRow> treeItem = getTableRow().getTreeItem();
    if (treeItem != null && treeItem.getValue() instanceof DayRow) {
      setStyle("-fx-font-weight: bold");
    }
    if (treeItem != null &&
        treeItem.getValue() instanceof final ActivityRow activity &&
        settings.getValue().excludedActivities().contains(activity.getLabel())) {
      setOpacity(0.5);
    }
  }
}

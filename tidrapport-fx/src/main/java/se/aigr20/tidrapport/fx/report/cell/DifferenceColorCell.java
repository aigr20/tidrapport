package se.aigr20.tidrapport.fx.report.cell;

import se.aigr20.tidrapport.fx.report.model.ReportRow;

import java.time.Duration;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;

public class DifferenceColorCell extends TreeTableCell<ReportRow, String> {
  @Override
  protected void updateItem(final String item, final boolean empty) {
    super.updateItem(item, empty);
    final TreeItem<ReportRow> rowItem = getTableRow().getTreeItem();
    if (empty ||
        item == null ||
        rowItem == null ||
        rowItem.getValue() == null ||
        rowItem.getValue().getDifference() == null) {
      setText("");
      setStyle("");
      return;
    }
    setText(item);

    final Duration difference = rowItem.getValue().getDifference();
    if (difference.isPositive()) {
      setStyle("-fx-text-fill: red");
    } else {
      setStyle("-fx-text-fill: green");
    }
  }
}

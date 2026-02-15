package se.aigr20.tidrapport.fx.report.cell;

import se.aigr20.tidrapport.fx.report.model.ReportRow;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;

public class DifferenceColorCell extends TreeTableCell<ReportRow, String> {
  @Override
  protected void updateItem(final String item, final boolean empty) {
    super.updateItem(item, empty);
    if (empty || item == null) {
      setText("");
      setStyle("");
      return;
    }
    setText(item);
    final TreeItem<ReportRow> rowItem = getTableRow().getTreeItem();
    if (rowItem != null && rowItem.getValue().getDifference().isPositive()) {
      setStyle("-fx-text-fill: red");
    } else if (rowItem != null) {
      setStyle("-fx-text-fill: green");
    }
  }
}

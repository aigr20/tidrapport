package se.aigr20.tidrapport.fx.settings;

import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.StringConverter;

public class AutoFocusedTextFieldListCell<T> extends TextFieldListCell<T> {
  public AutoFocusedTextFieldListCell(final StringConverter<T> stringConverter) {
    super(stringConverter);
  }

  @Override
  public void startEdit() {
    super.startEdit();
    Platform.runLater(() -> {
      final TextField textField = (TextField) getGraphic();
      textField.requestFocus();
    });
  }

}

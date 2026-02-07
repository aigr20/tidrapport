module tidrapport.fx {
  requires javafx.controls;
  requires javafx.fxml;

  opens se.aigr20.tidrapport.fx to javafx.fxml;
  exports se.aigr20.tidrapport.fx to javafx.graphics;
}
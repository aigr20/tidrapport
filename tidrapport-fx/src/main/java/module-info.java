module tidrapport.fx {
  requires javafx.controls;
  requires javafx.fxml;
  requires transitive javafx.graphics;
  requires tidrapport.core;
  requires com.fasterxml.jackson.databind;
  requires jdk.localedata;

  opens se.aigr20.tidrapport.fx.report to javafx.fxml;
  opens se.aigr20.tidrapport.fx.report.model to javafx.fxml;
  opens se.aigr20.tidrapport.fx.settings to javafx.fxml, com.fasterxml.jackson.databind;
  exports se.aigr20.tidrapport.fx;
  exports se.aigr20.tidrapport.fx.settings;
}
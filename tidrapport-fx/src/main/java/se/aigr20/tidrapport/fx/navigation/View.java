package se.aigr20.tidrapport.fx.navigation;

public enum View implements NavigationConstant {
  START("start.fxml");

  private final String fxmlFile;

  View(final String fxmlFile) {
    this.fxmlFile = fxmlFile;
  }

  public String getFxmlFile() {
    return fxmlFile;
  }
}

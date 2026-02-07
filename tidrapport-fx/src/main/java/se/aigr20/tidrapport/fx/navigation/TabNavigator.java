package se.aigr20.tidrapport.fx.navigation;

import java.io.IOException;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TabPane;

public class TabNavigator<E extends Enum<E> & NavigationConstant> extends AbstractNavigator {
  private final Class<E> enumClass;
  private final TabPane tabPane;
  private final Map<E, Parent> tabMap;

  public TabNavigator(final TabPane tabPane, final Class<E> tabEnumClass) {
    enumClass = tabEnumClass;
    this.tabPane = tabPane;
    tabMap = new EnumMap<>(tabEnumClass);
    tabPane
            .getSelectionModel()
            .selectedIndexProperty()
            .addListener((observable, old, next) -> handleTabChange(old, next));
  }

  private void handleTabChange(final Number oldTabIndex, final Number newTabIndex) {
    final Optional<E> tabEnum = Arrays
            .stream(enumClass.getEnumConstants())
            .filter(constant -> constant.ordinal() == newTabIndex.intValue())
            .findAny();
    if (tabEnum.isEmpty()) {
      throw new IllegalStateException("Tab index " +
                                      newTabIndex +
                                      " not in enum " +
                                      enumClass.getName());
    }

    final Parent tabContent = tabMap.computeIfAbsent(tabEnum.get(), ignored -> {
      final FXMLLoader loader = new FXMLLoader(TabNavigator.class.getResource("/views/" +
                                                                              tabEnum
                                                                                      .get()
                                                                                      .getFxmlFile()));
      loader.setControllerFactory(this::createController);

      try {
        return loader.load();
      } catch (final IOException e) {
        throw new IllegalStateException("Failed to load FXML file " + loader.getLocation(), e);
      }
    });

    tabPane.getTabs().get(newTabIndex.intValue()).setContent(tabContent);
  }
}

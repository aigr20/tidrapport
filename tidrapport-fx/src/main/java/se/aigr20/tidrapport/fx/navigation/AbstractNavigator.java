package se.aigr20.tidrapport.fx.navigation;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import se.aigr20.tidrapport.fx.navigation.trait.ControllerTrait;
import se.aigr20.tidrapport.fx.navigation.trait.NavigationTrait;

public class AbstractNavigator {
  private static final Map<Class<? extends ControllerTrait>, Object> CONTROLLER_OBJECTS =
          new ConcurrentHashMap<>();

  /**
   * Register an object for dependency injection for controllers implementing a trait. See
   * {@link ControllerTrait} for available traits.
   *
   * @param forTrait The trait the controller should implement.
   * @param object   The object that's to be injected.
   */
  public static void registerInjectable(final Class<? extends ControllerTrait> forTrait,
                                        final Object object) {
    CONTROLLER_OBJECTS.put(forTrait, object);
  }

  protected Object createController(final Class<?> controllerClass) {
    try {
      final Object controller = controllerClass.getDeclaredConstructor().newInstance();
      if (controller instanceof ControllerTrait controllerTrait) {
        injectObjects(controllerClass, controllerTrait);
      }

      return controller;
    } catch (final InstantiationException | IllegalAccessException | InvocationTargetException |
                   NoSuchMethodException e) {
      throw new IllegalStateException("Failed to initialize controller " +
                                      controllerClass.getName());
    }
  }

  private void injectObjects(final Class<?> controllerClass, final ControllerTrait controller) {
    for (final Map.Entry<Class<? extends ControllerTrait>, Object> entry :
            CONTROLLER_OBJECTS.entrySet()) {
      final Class<? extends ControllerTrait> traitClass = entry.getKey();
      final Object grantedByTrait = entry.getValue();
      if (!traitClass.isAssignableFrom(controllerClass)) {
        continue;
      }
      switch (controller) {
        case NavigationTrait navigationCapabilities when traitClass == NavigationTrait.class ->
                navigationCapabilities.setNavigator((StageNavigator) grantedByTrait);
        default -> throw new IllegalStateException("Unhandled controller trait: " +
                                                   traitClass.getName());
      }
    }
  }
}

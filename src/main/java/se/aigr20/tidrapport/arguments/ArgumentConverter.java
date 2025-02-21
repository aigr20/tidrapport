package se.aigr20.tidrapport.arguments;

/**
 * Functional interface managing the conversion from string to a specified type.
 *
 * @param <T> The type the value should be converted to.
 */
@FunctionalInterface
public interface ArgumentConverter<T> {
  T convert(String value);
}

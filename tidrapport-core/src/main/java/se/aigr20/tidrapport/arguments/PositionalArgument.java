package se.aigr20.tidrapport.arguments;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Markerar ett fält som ett positionsbaserat kommandoradsargument. Positionsbaserade argument väntas komma efter alla
 * namngivna argument. Alla positionsbaserade argument måste alltid finnas.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PositionalArgument {
  /**
   * Definierar ordningen de positionsbaserade argumenten skall skrivas i.
   *
   * @return Ordningen för detta argumentet.
   */
  int value();

  /**
   * En klass som kan konvertera ett argument till rätt typ.
   *
   * @return Klassen som skall användas för att konvertera värdet.
   */
  Class<ArgumentConverter<?>>[] converter() default {};

  record Field(java.lang.reflect.Field field, PositionalArgument annotation) {
  }
}

package se.aigr20.tidrapport.arguments;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Markerar ett fält som ett namngivet kommandoradsargument. Namngivna argument förväntas komma före alla
 * positionsbaserade argument. Om varken {@link #longName()} eller {@link #shortName()} saknas kommer namnet på fältet
 * användas som värde för {@link #longName()}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NamedArgument {
  /**
   * Det långa namnet på ett argument. Används som --[longName].
   *
   * @return Det långa namnet, utan --.
   */
  String longName() default "";

  /**
   * Det korta namnet på argumentet. Används som -[shortName].
   *
   * @return Det korta namnet, utan -.
   */
  String shortName() default "";

  /**
   * Huruvida argumentet måste vara med i kommandoraden eller inte.
   *
   * @return Om sant måste argumentet inkluderas.
   */
  boolean required() default false;

  /**
   * En klass som kan konvertera ett argument till rätt typ.
   *
   * @return Klassen som skall användas för att konvertera värdet.
   */
  Class<? extends ArgumentConverter<?>>[] converter() default {};

  record Field(java.lang.reflect.Field field, NamedArgument annotation) {
    public String tryLongName() {
      return !annotation().longName().isBlank() ? annotation().longName() : field().getName();
    }

    public String tryShortName() {
      return !annotation().shortName().isBlank() ? annotation().shortName() : null;
    }
  }
}

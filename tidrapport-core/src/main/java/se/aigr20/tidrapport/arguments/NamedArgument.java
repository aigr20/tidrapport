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
   * Beskrivning av argumentet.
   *
   * @return Beskrivning.
   */
  String description() default "";

  /**
   * En klass som kan konvertera ett argument till rätt typ.
   *
   * @return Klassen som skall användas för att konvertera värdet.
   */
  Class<? extends ArgumentConverter<?>>[] converter() default {};

  /**
   * Om detta namngivna argument påträffas skall inläsning av övriga argument omedelbart avbrytas.
   *
   * @return Ja/Nej.
   */
  boolean stopsParsing() default false;

  record Field(java.lang.reflect.Field field, NamedArgument annotation) {
    public String tryLongName() {
      if (!annotation().longName().isBlank()) {
        return annotation().longName();
      }
      if (tryShortName() == null) {
        return field().getName();
      }
      return null;
    }

    public String tryShortName() {
      return !annotation().shortName().isBlank() ? annotation().shortName() : null;
    }

    public boolean isBooleanField() {
      return (field().getType() == boolean.class || field().getType() == Boolean.class) &&
             annotation().converter().length == 0;
    }
  }
}

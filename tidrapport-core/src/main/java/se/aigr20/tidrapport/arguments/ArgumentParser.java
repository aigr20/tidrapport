package se.aigr20.tidrapport.arguments;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Läser en array av kommandoradsargument och skapar en klassinstans innehålande dem. Fält i klassen bör vara annoterade
 * med antingen {@link NamedArgument} eller {@link PositionalArgument} för att parsern skall kunna hitta dem.
 *
 * @param <T> Klassen innehållande argumentkonfigurationen.
 */
public class ArgumentParser<T> {

  private final T argumentClassInstance;
  private final List<NamedArgument.Field> namedArgumentFields;
  private final List<PositionalArgument.Field> positionalArgumentFields;
  private final List<String> argv;
  private final Set<String> argumentNames;

  public ArgumentParser(final String[] argv, final Class<T> argumentClass) {
    try {
      argumentClassInstance = argumentClass.getConstructor().newInstance();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
    this.argv = new ArrayList<>(Arrays.asList(argv));
    namedArgumentFields = new ArrayList<>();
    positionalArgumentFields = new ArrayList<>();
    argumentNames = new HashSet<>();

    for (final var field : argumentClass.getDeclaredFields()) {
      if (field.isAnnotationPresent(NamedArgument.class)) {
        final var annotation = field.getAnnotation(NamedArgument.class);
        if (annotation.converter().length > 1) {
          throw new IllegalArgumentException(
                  "Multiple converters specified for " + argumentClass.getName() + "." + field.getName());
        }

        final var namedField = new NamedArgument.Field(field, annotation);
        namedArgumentFields.add(namedField);
        if (namedField.tryLongName() != null) {
          argumentNames.add(namedField.tryLongName());
        }
        if (namedField.tryShortName() != null) {
          argumentNames.add(namedField.tryShortName());
        }
      } else if (field.isAnnotationPresent(PositionalArgument.class)) {
        final var annotation = field.getAnnotation(PositionalArgument.class);
        if (annotation.converter().length > 1) {
          throw new IllegalArgumentException(
                  "Multiple converters specified for " + argumentClass.getName() + "." + field.getName());
        }
        positionalArgumentFields.add(new PositionalArgument.Field(field, annotation));
      }
    }

    positionalArgumentFields.sort(Comparator.comparing(field -> field.annotation().value()));
  }

  public T parse() {
    try {
      if (parseNamedArguments()) {
        parsePositionalArguments();
      }
    } catch (final IllegalAccessException e) {
      throw new IllegalStateException("Unable to access a field in " + argumentClassInstance.getClass().getName(), e);
    }

    return argumentClassInstance;
  }

  /**
   * Skapa en hjälptext lämplig för en -h/--help-parameter utifrån de argument som registrerats.
   *
   * @param command Namnet på kommandot som kör programmet.
   * @param version Programmets version. Kan vara null.
   * @return En text som beskriver alla parametrar.
   */
  public String createHelpMessage(final String command, final String version) {
    final var sb = new StringBuilder();
    if (version != null) {
      sb.append(command).append(" version ").append(version).append("\n\n");
    }
    sb.append("Användning: ").append(command).append(" ");
    if (!namedArgumentFields.isEmpty()) {
      sb.append("[alternativ...]");
    }
    if (!positionalArgumentFields.isEmpty()) {
      for (final var positionalArgument : positionalArgumentFields) {
        sb.append(" ").append('<').append(positionalArgument.field().getName()).append('>');
      }
    }

    final var nameColumn = new ArrayList<String>(namedArgumentFields.size());
    final var descriptionColumn = new ArrayList<String>(namedArgumentFields.size());
    populateOptionsColumns(nameColumn, descriptionColumn);

    final var maxNameLen = nameColumn.stream().mapToInt(String::length).max().orElse(0);
    for (int i = 0; i < namedArgumentFields.size(); i++) {
      final var name = nameColumn.get(i);
      sb.append("\n").append(name).append(" ".repeat(maxNameLen - name.length() + 2)).append(descriptionColumn.get(i));
    }

    return sb.toString();
  }

  private void populateOptionsColumns(final List<String> nameColumn, final List<String> descriptionColumn) {
    for (final var namedArgument : namedArgumentFields) {
      final var sb = new StringBuilder();
      if (namedArgument.tryShortName() != null) {
        sb.append('-').append(namedArgument.tryShortName());
      }
      if (namedArgument.tryLongName() != null) {
        if (namedArgument.tryShortName() != null) {
          sb.append(", ");
        }
        sb.append("--").append(namedArgument.tryLongName());
      }

      sb.append(" <").append(namedArgument.field().getType().getSimpleName()).append('>');
      nameColumn.add(sb.toString());
      descriptionColumn.add(namedArgument.annotation().description());
    }
  }

  private void parsePositionalArguments() throws IllegalAccessException {
    for (int i = 0; i < positionalArgumentFields.size(); i++) {
      final var argument = positionalArgumentFields.get(i);
      argument.field().setAccessible(true);
      setField(argument.field(),
               argument.annotation().converter().length > 0 ? argument.annotation().converter()[0] : null,
               argument.field().getType(),
               argv.get(i));
      argument.field().setAccessible(false);
    }
  }

  /**
   * Läs in namngivna argument.
   *
   * @return Om inläsning skall fortsätta efter inläsning av namngivna argument.
   * @throws IllegalAccessException Om åtkomst genom reflection misslyckas.
   */
  private boolean parseNamedArguments() throws IllegalAccessException {
    for (final var argument : namedArgumentFields) {
      argument.field().setAccessible(true);
      final var type = argument.field().getType();

      final var fieldValue = getNamedFieldValue(argument);
      if (argument.annotation().required() && fieldValue == null) {
        throw new IllegalArgumentException("Missing value for argument " + argument.annotation());
      }

      if (fieldValue == null) {
        argument.field().setAccessible(false);
        continue;
      }

      setField(argument.field(),
               argument.annotation().converter().length == 1 ? argument.annotation().converter()[0] : null,
               type,
               fieldValue);

      argument.field().setAccessible(false);

      if (argument.annotation().stopsParsing()) {
        return false;
      }
    }

    return true;
  }

  private void setField(final Field field,
                        final Class<? extends ArgumentConverter<?>> converter,
                        final Class<?> type,
                        final String value) throws IllegalAccessException {
    if (converter != null) {
      try {
        field.set(argumentClassInstance, converter.getConstructor().newInstance().convert(value));
      } catch (InstantiationException | InvocationTargetException | NoSuchMethodException e) {
        throw new RuntimeException(e);
      }
    } else if (type == boolean.class || type == Boolean.class) {
      field.setBoolean(argumentClassInstance, Boolean.parseBoolean(value));
    } else if (type == String.class) {
      field.set(argumentClassInstance, value);
    } else if (type == int.class || type == Integer.class) {
      field.set(argumentClassInstance, Integer.parseInt(value));
    } else if (type == long.class || type == Long.class) {
      field.set(argumentClassInstance, Long.parseLong(value));
    } else if (type == double.class || type == Double.class) {
      field.set(argumentClassInstance, Double.parseDouble(value));
    } else {
      throw new UnsupportedOperationException(
              "Could not convert the value '%s' to type %s. Consider adding an ArgumentConverter for the type.".formatted(
                      value,
                      type.getName()));
    }
  }

  private String getNamedFieldValue(final NamedArgument.Field argument) {
    String fieldValue = null;

    if (argument.tryLongName() != null) {
      fieldValue = getNamedFieldValue("--" + argument.tryLongName(), argument.isBooleanField());
    }
    if (fieldValue == null && argument.tryShortName() != null) {
      fieldValue = getNamedFieldValue("-" + argument.tryShortName(), argument.isBooleanField());
    }

    if (fieldValue == null && argument.annotation().required()) {
      throw new IllegalArgumentException("Missing value for required argument " + argument);
    }

    return fieldValue;
  }

  /**
   * Hämta ett namngivet värde och ta bort argument och värde från argv.
   *
   * @param fieldName Argumentnamnet.
   * @param isBoolean Huruvida argumentet är en boolean eller inte.
   * @return Värde på argumentet, eller null om det inte hittades.
   */
  private String getNamedFieldValue(final String fieldName, final boolean isBoolean) {
    for (int i = 0; i < argv.size(); i++) {
      if (argv.get(i).equals(fieldName) && nextInArgvIsValue(i + 1) && !isBoolean) {
        final var value = argv.remove(i + 1);
        argv.remove(i);
        return value;
      }
      final var equalsPosition = hasEqualsValue(argv.get(i));
      if (argv.get(i).startsWith(fieldName) && equalsPosition != -1 && equalsPosition == fieldName.length()) {
        final var value = argv.get(i).substring(equalsPosition + 1);
        argv.remove(i);
        return value;
      }

      if (argv.get(i).equals(fieldName) && isBoolean) {
        argv.remove(i);
        return "true"; // Specialhantering för boolean-flaggor
      }
    }
    return null;
  }

  /**
   * Ger positionen för =-tecknet i ett argument med formatet [namn]=[värde].
   *
   * @param argument Argumentet från argv.
   * @return Position på =-tecknet, eller -1 om inget hittades.
   */
  private int hasEqualsValue(final String argument) {
    for (int i = 0; i < argument.length(); i++) {
      if (argument.charAt(i) == '\\') {
        i += 1;
        continue;
      }
      if (argument.charAt(i) == '=') {
        return i;
      }
    }
    return -1;
  }

  private boolean nextInArgvIsValue(final int nextIndex) {
    return nextIndex < argv.size() && !argumentNames.contains(argv.get(nextIndex));
  }
}

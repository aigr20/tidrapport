package se.aigr20.tidrapport.arguments;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ArgumentParserTest {

  public static Stream<Arguments> booleanArguments() {
    return Stream.of(Arguments.of((Object) new String[]{"--debug"}),
                     Arguments.of((Object) new String[]{"--debug=true"}),
                     Arguments.of((Object) new String[]{"--debug", "true"}));
  }

  public static Stream<Arguments> mixedDataProvider() {
    return Stream.of(Arguments.of((Object) new String[]{"--output", "out.txt", "--debug", "in.txt"}),
                     Arguments.of((Object) new String[]{"--debug", "--output", "out.txt", "in.txt"}),
                     Arguments.of((Object) new String[]{"--debug", "--output", "out.txt", "in.txt"}));
  }

  @ParameterizedTest
  @MethodSource("booleanArguments")
  public void readsBooleanValue3Variants(final String[] argv) {
    final var sut = new ArgumentParser<>(argv, TestArguments.class);

    final var result = sut.parse();

    assertTrue(result.debug);
  }

  @Test
  public void readsStringArgument() {
    final var sut = new ArgumentParser<>(new String[]{"--file", "text.txt"}, TestArguments.class);

    final var result = sut.parse();

    assertEquals("text.txt", result.file);
  }

  @Test
  public void readsIntArgument() {
    final var sut = new ArgumentParser<>(new String[]{"--iterations", "110"}, TestArguments.class);

    final var result = sut.parse();

    assertEquals(110, result.iterations);
  }

  @Test
  public void usesSpecifiedConverter() {
    final var sut = new ArgumentParser<>(new String[]{"--accept", "Y"}, TestArguments.class);

    final var result = sut.parse();

    assertTrue(result.accept);
  }

  @Test
  public void requiredArgumentMissingThrows() {
    final var sut = new ArgumentParser<>(new String[]{}, RequiredArguments.class);

    assertThrows(IllegalArgumentException.class, sut::parse);
  }

  @Test
  public void readsPositionArgument() {
    final var sut = new ArgumentParser<>(new String[]{"text.txt"}, PositionalArguments.class);

    final var result = sut.parse();

    assertEquals("text.txt", result.file);
  }

  @ParameterizedTest
  @MethodSource("mixedDataProvider")
  public void readsNamedAndPositionalArguments(final String[] arguments) {
    final var sut = new ArgumentParser<>(arguments, MixedArguments.class);

    final var result = sut.parse();

    assertTrue(result.debug);
    assertEquals("out.txt", result.output);
    assertEquals("in.txt", result.file);
  }

  @Test
  public void readsManyArgumentsMixed() {
    final var sut = new ArgumentParser<>(new String[]{"-a", "Y", "--debug", "-f", "filen.txt", "--iterations=3", "in.txt", "ut.txt"},
                                         ManyArguments.class);

    final var result = sut.parse();

    assertTrue(result.accept);
    assertTrue(result.debug);
    assertEquals(3, result.iterations);
    assertEquals("filen.txt", result.file);
    assertEquals("in.txt", result.inFile);
    assertEquals("ut.txt", result.outFile);
  }

  public static class PositionalArguments {
    @PositionalArgument(1)
    private final String file;

    public PositionalArguments() {
      file = null;
    }
  }

  public static class RequiredArguments {
    @NamedArgument(longName = "file", required = true)
    private final String file;

    public RequiredArguments() {
      file = null;
    }
  }

  public static class TestArguments {
    @NamedArgument(longName = "debug")
    private final boolean debug;

    @NamedArgument(longName = "file")
    private final String file;

    @NamedArgument(longName = "iterations")
    private final int iterations;

    @NamedArgument(longName = "accept", converter = YesNoBooleanConverter.class)
    private final boolean accept;

    public TestArguments() {
      debug = false;
      file = null;
      iterations = 0;
      accept = false;
    }
  }

  public static class ManyArguments {
    @NamedArgument(longName = "debug")
    private final boolean debug;

    @NamedArgument(shortName = "f")
    private final String file;

    @NamedArgument(longName = "iterations")
    private final int iterations;

    @NamedArgument(longName = "accept", shortName = "a", converter = YesNoBooleanConverter.class)
    private final boolean accept;

    @PositionalArgument(2)
    private final String outFile;

    @PositionalArgument(1)
    private final String inFile;

    public ManyArguments() {
      debug = false;
      file = null;
      iterations = 0;
      accept = false;
      outFile = null;
      inFile = null;
    }
  }

  public static class MixedArguments {
    @NamedArgument(longName = "debug")
    private final boolean debug;
    @NamedArgument(longName = "output")
    private final String output;
    @PositionalArgument(1)
    private final String file;

    public MixedArguments() {
      debug = false;
      output = null;
      file = null;
    }
  }

  public static class YesNoBooleanConverter implements ArgumentConverter<Boolean> {
    @Override
    public Boolean convert(final String s) {
      if (s.equalsIgnoreCase("Y")) {
        return true;
      }
      if (s.equalsIgnoreCase("N")) {
        return false;
      }

      return false;
    }
  }
}
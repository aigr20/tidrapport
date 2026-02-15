package se.aigr20.tidrapport.fx.settings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.concurrent.Task;

public class SettingsService {
  private final ObjectProperty<TidrapportSettings> settings;

  public SettingsService(final TidrapportSettings initialSettings) {
    this.settings = new SimpleObjectProperty<>(initialSettings);
  }

  public TidrapportSettings loadSettings() throws IOException {
    final Path file = getSettingsFile();
    System.out.println("Loading settings from " + file);
    if (!Files.exists(file)) {
      return null;
    }

    try {
      final ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(file.toFile(), TidrapportSettings.class);
    } catch (final IOException e) {
      throw new IOException("Kunde inte läsa in inställningar", e);
    }
  }

  public TidrapportSettings getCurrent() {
    return settings.get();
  }

  public ObservableObjectValue<TidrapportSettings> getSettings() {
    return settings;
  }

  public void setSettings(final TidrapportSettings settings) {
    this.settings.setValue(settings);
  }

  public Task<Void> saveSettings(final TidrapportSettings settings) {
    return new Task<>() {
      @Override
      protected Void call() throws Exception {
        final Path file = getSettingsFile();

        if (!Files.exists(file)) {
          Files.createDirectories(file.getParent());
          Files.createFile(file);
        }

        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(file.toFile(), settings);
        setSettings(settings);
        return null;
      }
    };
  }

  private Path getSettingsFile() {
    return Paths.get(System.getProperty("user.home"), ".tidrapport", "settings.json");
  }
}

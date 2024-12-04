package centrikt.factory_monitoring.config_server.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/config-server/config")
public class ConfigController {

    @PostMapping("/update")
    public ResponseEntity<String> updateConfig(@RequestBody Map<String, Object> configData) {
        try {
            Path configPath = Paths.get("src/main/resources/configuration/application.yml");
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

            Yaml yaml = new Yaml(options);
            yaml.dump(configData, new FileWriter(configPath.toFile()));

            return ResponseEntity.ok("Configuration updated successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update configuration");
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getConfig() {
        try {
            Path configPath = Paths.get("src/main/resources/configuration/application.yml");
            Yaml yaml = new Yaml();
            Map<String, Object> configData = yaml.load(new FileReader(configPath.toFile()));

            return ResponseEntity.ok(configData);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

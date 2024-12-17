package centrikt.factory_monitoring.config_server.controllers;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.kv.model.GetValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/v1/config-server/config")
public class ConfigController implements centrikt.factory_monitoring.config_server.controllers.docs.ConfigController {

    private ConsulClient consulClient;

    public ConfigController(ConsulClient consulClient) {
        this.consulClient = consulClient;
    }
    @Autowired
    public void setConsulClient(ConsulClient consulClient) {
        this.consulClient = consulClient;
    }

    @GetMapping("/client")
    public ResponseEntity<String> getClientConfigValue(@RequestParam("key") String key) {
        if (key.startsWith("config/next-app")) {
            Optional<GetValue> value = Optional.ofNullable(consulClient.getKVValue(key).getValue());
            return value.map(v -> ResponseEntity.ok(v.getDecodedValue()))
                    .orElse(ResponseEntity.notFound().build());
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }


    @GetMapping("")
    public ResponseEntity<String> getConfigValue(@RequestParam("key") String key) {
        Optional<GetValue> value = Optional.ofNullable(consulClient.getKVValue(key).getValue());
        return value.map(v -> ResponseEntity.ok(v.getDecodedValue()))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("")
    public ResponseEntity<String> updateConfigValue(@RequestParam("key") String key, @RequestBody String value) {
        consulClient.setKVValue(key, value);
        return ResponseEntity.ok("Configuration updated successfully");
    }
}
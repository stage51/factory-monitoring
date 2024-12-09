package centrikt.factory_monitoring.discovery_server.controllers;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.kv.model.GetValue;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/discovery-server/config")
public class ConfigController {

    private ConsulClient consulClient;

    public ConfigController(ConsulClient consulClient) {
        this.consulClient = consulClient;
    }
    @Autowired
    public void setConsulClient(ConsulClient consulClient) {
        this.consulClient = consulClient;
    }

    @GetMapping("/keys")
    public ResponseEntity<List<String>> getAllKeys() {
        List<String> keys = consulClient.getKVKeysOnly("").getValue();
        return ResponseEntity.ok(keys);
    }


    @GetMapping("")
    public ResponseEntity<String> getConfigValue(@RequestParam("key") String key) {
        Optional<GetValue> value = Optional.ofNullable(consulClient.getKVValue(key).getValue());
        return value.map(v -> ResponseEntity.ok(v.getDecodedValue()))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("")
    public ResponseEntity<String> updateConfigValue(@RequestParam("key") String key, @RequestBody String value) {
        consulClient.setKVValue(key, value);
        return ResponseEntity.ok("Configuration updated successfully");
    }
}

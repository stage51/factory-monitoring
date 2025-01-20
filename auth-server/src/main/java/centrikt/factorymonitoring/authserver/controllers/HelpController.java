package centrikt.factorymonitoring.authserver.controllers;

import centrikt.factorymonitoring.authserver.dtos.requests.HelpRequest;
import centrikt.factorymonitoring.authserver.services.HelpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth-server/help")
@Slf4j
public class HelpController implements centrikt.factorymonitoring.authserver.controllers.docs.HelpController {

    private HelpService helpService;

    public HelpController(HelpService helpService) {
        this.helpService = helpService;
        log.info("HelpController initialized");
    }

    @Autowired
    public void setHelpService(HelpService helpService) {
        this.helpService = helpService;
        log.debug("HelpService set");
    }

    @PostMapping
    public ResponseEntity<Void> sendHelpMessage(@RequestBody HelpRequest helpRequest) {
        log.info("Processing help request with message {}", helpRequest.getMessage());
        helpService.sendHelpRequest(helpRequest);
        log.debug("Help request successfully sent with message: {}", helpRequest.getMessage());
        return ResponseEntity.ok().build();
    }
}


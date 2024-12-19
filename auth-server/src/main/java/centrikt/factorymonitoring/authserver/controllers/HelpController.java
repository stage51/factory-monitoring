package centrikt.factorymonitoring.authserver.controllers;

import centrikt.factorymonitoring.authserver.dtos.requests.HelpRequest;
import centrikt.factorymonitoring.authserver.services.HelpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth-server/help")
public class HelpController implements centrikt.factorymonitoring.authserver.controllers.docs.HelpController {
    private HelpService helpService;
    public HelpController(HelpService helpService) {
        this.helpService = helpService;
    }
    @Autowired
    public void setHelpService(HelpService helpService) {
        this.helpService = helpService;
    }

    @PostMapping
    public ResponseEntity<Void> sendHelpMessage(@RequestBody HelpRequest helpRequest) {
        helpService.sendHelpRequest(helpRequest);
        return ResponseEntity.ok().build();
    }
}

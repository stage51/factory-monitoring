package centrikt.factorymonitoring.authserver.services;

import centrikt.factorymonitoring.authserver.dtos.requests.HelpRequest;

public interface HelpService {
    void sendHelpRequest(HelpRequest helpRequest);
}

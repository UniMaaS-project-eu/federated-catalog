package be.flandersmake.edc.keycloakcatalog.keycloak_target_node;

import java.util.List;

import org.eclipse.edc.crawler.spi.TargetNode;
import org.eclipse.edc.crawler.spi.TargetNodeDirectory;
import be.flandersmake.edc.keycloakcatalog.KeycloakParticipantsService;

public class KeycloakNodeDirectory implements TargetNodeDirectory{
    private KeycloakParticipantsService.KeykloadConfiguration keycloakConfiguration;

    public KeycloakNodeDirectory(KeycloakParticipantsService.KeykloadConfiguration config ){
        this.keycloakConfiguration = config;
    }

    // method used by the crawler for getting the crawling targets
    @Override
    public List<TargetNode> getAll() {
        KeycloakHttpClient httpClient = new KeycloakHttpClient(this.keycloakConfiguration);
        return httpClient.getParticipantsTargetNode();
    }

    @Override
    public void insert(TargetNode node) {
        throw new UnsupportedOperationException("KeycloakNodeDirectory: method insert not implemented");
    }

    @Override
    public TargetNode remove(String id) {
        throw new UnsupportedOperationException("KeycloakNodeDirectory: method remove not implemented");
    }

};
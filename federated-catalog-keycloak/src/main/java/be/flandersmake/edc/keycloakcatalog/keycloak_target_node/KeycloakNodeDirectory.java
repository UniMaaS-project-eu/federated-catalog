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

    @Override
    public List<TargetNode> getAll() {
        KeycloakHttpClient httpClient = new KeycloakHttpClient(this.keycloakConfiguration);
        return httpClient.getParticipantsTargetNode();
    }

    @Override
    public void insert(TargetNode node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Method unsupported for Keycloak based implementation");
    }

    @Override
    public TargetNode remove(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Method unsupported to Keycloak based implementation");
    }

};
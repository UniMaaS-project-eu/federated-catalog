package be.flandersmake.edc.keycloakcatalog.keycloak_target_node;

import java.util.List;

import org.eclipse.edc.crawler.spi.TargetNode;
import org.eclipse.edc.crawler.spi.TargetNodeDirectory;

public class KeycloakNodeDirectory implements TargetNodeDirectory{


    public KeycloakNodeDirectory(){
    }

@Override
    public List<TargetNode> getAll() {
        KeycloakHttpClient httpClient = new KeycloakHttpClient();
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

    

}
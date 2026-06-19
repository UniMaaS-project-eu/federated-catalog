package be.flandersmake.edc.keycloakcatalog.keycloak_target_node;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.edc.crawler.spi.TargetNode;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import be.flandersmake.edc.keycloakcatalog.KeycloakParticipantsService;

//class for querying keycloack clients in the unimaass realm
public class KeycloakHttpClient {

    private HttpClient http_client;
    private final String host;
    private final String token;
    private final String port;
    private final String realm;
    private final String admin_user;
    private final String admin_password;

    private static final ObjectMapper mapper = new ObjectMapper();

    public KeycloakHttpClient(KeycloakParticipantsService.KeykloadConfiguration keycloakConfiguration) {
        this.admin_user = keycloakConfiguration.admin_user(); // System.getProperty("keycloak_admin_user");
        this.admin_password = keycloakConfiguration.admin_password(); // System.getProperty("keycloak_admin_password");
        this.host = keycloakConfiguration.host();
        this.port = keycloakConfiguration.port();
        this.realm = keycloakConfiguration.realm();
        this.http_client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        this.token = this.InitializeToken(null);
    }

    public String InitializeToken(String token) {
        if (token != null) {
            return token;
        } else {
            HttpClient client = HttpClient.newHttpClient();

            String form = "client_id=admin-cli" +
                    "&username=" + admin_user +
                    "&password=" + admin_password +
                    "&grant_type=password";

            HttpRequest tokenRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://" + host + ":" + port + "/realms/master/protocol/openid-connect/token"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(form))
                    .build();

            try {
                HttpResponse<String> response = client.send(tokenRequest, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 200) {
                    throw new RuntimeException("Failed to get token. Status: " + response.statusCode() +
                            " Body: " + response.body());
                }

                JsonNode json = mapper.readTree(response.body());
                return json.get("access_token").asText();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
    }

    public List<TargetNode> getParticipantsTargetNode() {
        List<TargetNode> participants = new ArrayList<>();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://" + host + ":" + port + "/admin/realms/" + realm + "/clients"))
                .GET()
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + token)
                .build();

        HttpResponse<String> response;
        try {
            response = http_client.send(request, HttpResponse.BodyHandlers.ofString());
            return parseParticipantsToTargetNode(response.body());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return participants;

    }

    public List<TargetNode> parseParticipantsToTargetNode(String json) {

        try {
            JsonNode root = mapper.readTree(json);

            System.out.println("KeycloakHttpClient.parseParticipantsToTargetNode");
            //System.out.println(".. json: " + json);
            System.out.println(".. Node type: " + root.getNodeType());
            System.out.println(".. Array size: " + root.size());
            List<TargetNode> participants = new ArrayList<>();

            for (JsonNode client : root) {
                    String clientId = client.path("clientId").asText();
                    if (!clientId.startsWith("did:web")) {
                        continue; // skip this client
                    }

                    System.out.println("KeycloakHttpClient.parseParticipantsToTargetNode, new participant 2");
                    System.out.println(".. clientId: " + clientId);
                    participants.add(new KeycloakParticipant(clientId).toTargetNode());
            }

            return participants;

        } catch (Exception e) {
            throw new RuntimeException("KeycloakHttpClient: Failed to parse Keycloak client list", e);
        }
    }
}

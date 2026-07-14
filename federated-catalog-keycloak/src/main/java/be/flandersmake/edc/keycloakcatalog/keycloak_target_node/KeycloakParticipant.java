package be.flandersmake.edc.keycloakcatalog.keycloak_target_node;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import org.eclipse.edc.crawler.spi.TargetNode;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class KeycloakParticipant {

    String clientId;
    String attribute;
    Boolean inferFromDID;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public KeycloakParticipant(String clientId) {
        this.clientId = clientId;
    }

    public TargetNode toTargetNode() {
        String name;

        if (!clientId.startsWith("did:web:")) {
            throw new IllegalStateException("The clientId does not start with did:web");
        }
        // Example DID: did:web:localhost%3A7093:connector1
        String withoutPrefix = clientId.substring("did:web:".length());
        String[] parts = withoutPrefix.split(":");

        // encoded host:port
        String encodedHostPort = parts[0];
        String did_url = java.net.URLDecoder.decode(encodedHostPort, java.nio.charset.StandardCharsets.UTF_8);

        // participant name
        name = parts.length > 1 ? parts[1] : null;

        return resolveFromDidWeb(did_url, name);
    }

    public static TargetNode resolveFromDidWeb(String didWebUrl, String connectorName) {
        try {
            String didJsonUrl;
            if (connectorName == null) {
                didJsonUrl = "http://" + didWebUrl + "/.well-known/did.json";
            } else {
                didJsonUrl = "http://" + didWebUrl + "/" + connectorName + "/.well-known/did.json";
            }
            System.out.println("Get did url:" + didJsonUrl);

            // Fetch DID document
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(didJsonUrl))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonNode didDoc = MAPPER.readTree(response.body());
                String rawDid = didDoc.get("id").asText(); // "did:web:localhost%3A7093"

                // Extract DSP service endpoint
                JsonNode services = didDoc.get("service");
                if (services == null || !services.isArray()) {
                    throw new IllegalStateException("No service entries found in DID document.");
                }

                String dspEndpoint = null;
                for (JsonNode svc : services) {
                    if (svc.has("type") &&
                            (svc.get("type").asText().equalsIgnoreCase("ProtocolEndpoint")
                                    || svc.get("type").asText().equalsIgnoreCase("DataspaceProtocol"))) {
                        dspEndpoint = svc.get("serviceEndpoint").asText();
                        break;
                    }
                }

                if (dspEndpoint == null) {
                    throw new IllegalStateException("No DataspaceProtocol service endpoint found in DID document.");
                }

                // Build catalog endpoint
                String catalogUrl = dspEndpoint;/*
                                                 * .endsWith("/")
                                                 * ? dspEndpoint + "catalog"
                                                 * : dspEndpoint + "/catalog";
                                                 */

                // Build TargetNode
                System.err.println("Target node from did:" + rawDid + " name:" + connectorName + " url:" + catalogUrl);
                // TargetNode target = new TargetNode(connectorName, rawDid, catalogUrl,
                // List.of("dataspace-protocol-http"));
                TargetNode target = new TargetNode(connectorName, rawDid, catalogUrl,
                        List.of("dataspace-protocol-http"));

                System.err.println("TargetNode ID = " + target.id());
                return target;

            } else {
                System.err.println("Response code is not 200 but " + response.statusCode());
                return null;
            }
        } catch (IOException | InterruptedException | NullPointerException e) {
            System.err.println("Failed to resolve DID document: " + e.getMessage());
            return null;
        }
    }
}

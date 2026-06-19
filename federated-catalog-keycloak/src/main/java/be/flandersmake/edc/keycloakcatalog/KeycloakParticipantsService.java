package be.flandersmake.edc.keycloakcatalog;

import org.eclipse.edc.crawler.spi.TargetNodeDirectory;
import org.eclipse.edc.runtime.metamodel.annotation.Configuration;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.runtime.metamodel.annotation.Settings;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;

import be.flandersmake.edc.keycloakcatalog.keycloak_target_node.KeycloakNodeDirectory;

public class KeycloakParticipantsService implements ServiceExtension {

    @Inject
    private Monitor monitor;

    @Override
    public String name() {
        return "Keycloak Target Node Directory Extension";
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        monitor.info("Initializing Keycloak TargetNodeDirectory");

        // Create your directory
        var directory = new KeycloakNodeDirectory(this.keycloakConfiguration);

        // Register it as a service so the Federated Catalog can find it
        context.registerService(TargetNodeDirectory.class, directory);

        monitor.info("Keycloak TargetNodeDirectory registered");
    }

    @Configuration
    private KeykloadConfiguration keycloakConfiguration;

    @Settings
    public record KeykloadConfiguration(
            @Setting(key = "keycloak_admin_user", description = "Keycloak Admin User", defaultValue = "admin") String admin_user,
            @Setting(key = "keycloak_admin_password", description = "Keycloak Admin Password", defaultValue = "admin") String admin_password,
            @Setting(key = "keycloak_host", description = "Keycloak Host", defaultValue = "localhost") String host,
            @Setting(key = "keycloak_port", description = "Keycloak Port", defaultValue = "5080") String port,
            @Setting(key = "keycloak_realm", description = " Keycloak Realm", defaultValue = "unimaas") String realm
    ) {
    };
}

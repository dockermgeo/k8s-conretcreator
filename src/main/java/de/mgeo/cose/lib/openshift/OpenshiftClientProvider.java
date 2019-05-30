package de.mgeo.cose.lib.openshift;

import de.mgeo.cose.lib.TerminalReader;
import de.mgeo.cose.lib.Logging;
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftConfig;
import io.fabric8.openshift.client.OpenShiftConfigBuilder;
import org.slf4j.Logger;


public class OpenshiftClientProvider {
    private static final String DEFAULT_CLUSTER = "https://cluster.mgeo.de:8443";
    private final String namespace;
    private String username;
    private String password;
    private String clusterName;
    private TerminalReader tools = new TerminalReader();
    private static Logging logging = new Logging(OpenshiftClientProvider.class.toString());
    private static Logger log = logging.getLogger();
    private boolean loginState;

    public OpenshiftClientProvider() {

        this.clusterName = setVars("OCP_CLUSTER", "[OCP_CLUSTER] Name of cluster '" + DEFAULT_CLUSTER + "'");
        if (this.clusterName.isEmpty()) {
            this.clusterName = DEFAULT_CLUSTER;
            System.setProperty("OCP_CLUSTER", this.clusterName);
        }
        this.namespace = setVars("OCP_NAMESPACE", "[OCP_NAMESPACE] Name of Project (namespace):");
        this.username = setVars("OCP_USERNAME", "[OCP_USERNAME] Your OCP Username:");
        this.password = setVars("OCP_PASSWORD", "[OCP_PASSWORD] Your OCP Password:");

        String[] checkers = {"OCP_CLUSTER", "OCP_NAMESPACE", "OCP_USERNAME", "OCP_PASSWORD"};
        Boolean isError = false;
        String errMsg = "";
        for (String varName : checkers) {
            if (System.getProperty(varName) == null || System.getProperty(varName).isEmpty()) {
                errMsg += "\t* Missing ENV: " + varName + "\n";
                isError = true;
            }
        }

        if (isError) {
            log.error("\n" + errMsg);
            System.exit(1);
        }
    }

    private String setVars(String envname, String question) {
        if (System.getenv(envname) != null) {
            System.setProperty(envname, System.getenv(envname));
            return System.getenv(envname);
        } else {
            String str = tools.getInput(question);
            System.setProperty(envname, str);
            return str;
        }
    }

    public DefaultOpenShiftClient openShiftClient() {
        OpenShiftConfig occonfig = new OpenShiftConfigBuilder()
                .withTrustCerts(true)
                .withMasterUrl(this.clusterName)
                .withUsername(this.username)
                .withPassword(this.password)
                .build();
        return new DefaultOpenShiftClient(occonfig);
    }

}

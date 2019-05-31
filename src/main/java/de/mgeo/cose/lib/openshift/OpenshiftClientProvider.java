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

    public OpenshiftClientProvider(String namespace, String clustername) {

        //Cluster & Namespace
        this.clusterName = setVarValue("OCP_CLUSTER", clustername);;
        if (this.clusterName.isEmpty()) {
            this.clusterName = DEFAULT_CLUSTER;
            setVarValue("OCP_CLUSTER",  this.clusterName);
        }
        this.namespace = setVarValue("OCP_NAMESPACE", namespace);

        //Commandline
        this.username = setVarInput("OCP_USERNAME", "[OCP_USERNAME] Your OCP Username:",false);
        this.password = setVarInput("OCP_PASSWORD", "[OCP_PASSWORD] Your OCP Password:",true);
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
    public OpenshiftClientProvider() {

        this.clusterName = setVarInput("OCP_CLUSTER", "[OCP_CLUSTER] Name of cluster '" + DEFAULT_CLUSTER + "'",false);
        if (this.clusterName.isEmpty()) {
            this.clusterName = DEFAULT_CLUSTER;
            System.setProperty("OCP_CLUSTER", this.clusterName);
        }
        this.namespace = setVarInput("OCP_NAMESPACE", "[OCP_NAMESPACE] Name of Project (namespace):",false);
        this.username = setVarInput("OCP_USERNAME", "[OCP_USERNAME] Your OCP Username:",false);
        this.password = setVarInput("OCP_PASSWORD", "[OCP_PASSWORD] Your OCP Password:",true);

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

    private String setVarValue(String envname, String enval) {
        if (System.getenv(envname) != null) {
            System.setProperty(envname, System.getenv(envname));
            return System.getenv(envname);
        } else {
            System.setProperty(envname, enval);
            return enval;
        }
    }

    private String setVarInput(String envname, String question, Boolean isPassfield) {
        if (System.getenv(envname) != null) {
            System.setProperty(envname, System.getenv(envname));
            return System.getenv(envname);
        } else {
            String str;
            if (isPassfield){
                str = tools.getInPassword(question);
            }
            else {
                str = tools.getInText(question);
            }
            System.setProperty(envname, str);
            return str;
        }
    }

    public DefaultOpenShiftClient openShiftClient() {
        OpenShiftConfig occonfig = new OpenShiftConfigBuilder()
                .withTrustCerts(true)
                .withMasterUrl(this.clusterName)
                .withNamespace(this.namespace)
                .withUsername(this.username)
                .withPassword(this.password)
                .build();
        return new DefaultOpenShiftClient(occonfig);
    }

}

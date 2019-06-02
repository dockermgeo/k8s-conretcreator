package de.mgeo.cose;

import de.mgeo.cose.controllers.ConfigCreator;
import de.mgeo.cose.controllers.ExportDefinition;
import de.mgeo.cose.controllers.ModelLoader;
import de.mgeo.cose.controllers.SecretFileCreatorIo;
import de.mgeo.cose.lib.Logging;
import de.mgeo.cose.lib.TerminalReader;
import de.mgeo.cose.lib.openshift.OpenshiftClientProvider;
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import org.slf4j.Logger;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;

@Command(name = "conretcreator.jar",
        description = "@|bold Configmap-Secret-Creator Options:|@",
        headerHeading = "@|bold,underline CoretCreator Usage|@:%n%n")
public class ConretCreator implements Runnable {

    private static Logging logging = new Logging(ConretCreator.class.toString());
    private static Logger log = logging.getLogger();

    //@Option(names = {"-o", "--create-oc"}, description = "Create/Replace a Secret or ConfigMap from YML")
    //private boolean cr_oc;

    @Option(names = {"-c", "--create"}, description = "Create/Replace a Secret or ConfigMap")
    private boolean createconfig;

    @Option(names = {"-x", "--export"}, description = "Print usage to stdout. The definition for the k8 spec.")
    private boolean exportview;

    @Option(names = {"-z", "--debug"}, description = "Enable mode DEBUG")
    private boolean isdebug;

    @Option(names = {"-s", "--secure"}, description = "Make cli-inputfields hidden")
    private boolean isvisible;

    @Option(names = {"-f", "--secrets-fs"}, description = "Create/Replace Secrets from filesystem")
    private boolean storefiles;

    @Option(names = {"-i", "--read"}, paramLabel = "FILE", description = "* Read INPUT from this YAML[s]")
    private boolean[] fileparam = new boolean[0];

    @Parameters(arity = "1..*", paramLabel = "FILE", description = "File(s) for proceeding")
    private File[] inputFiles;


    public void run() {

        if (System.getenv("TARGET_FILE") != null) {
            System.setProperty("TARGET_FILE", System.getenv("TARGET_FILE"));
        }

        String DEFAULT_LOGLEVEL = "ERROR";
        if (System.getenv("LOG_LEVEL") != null) {
            DEFAULT_LOGLEVEL = System.getenv("LOG_LEVEL");
        }
        System.setProperty("LOG_LEVEL", DEFAULT_LOGLEVEL);

        System.setProperty("IS_VISIBLE", "TRUE");
        if (isvisible) {
            System.setProperty("IS_VISIBLE", "FALSE");
        }

        if (isdebug) {
            System.setProperty("LOG_LEVEL", "DEBUG");
            System.setProperty("DELETE_FILE", "FALSE");
            System.setProperty("IS_VISIBLE", "TRUE");
        }


        if (fileparam.length > 0) {
            if (createconfig) {
                this.startConfigCreator(inputFiles[0]);
            }
            if (storefiles) {
                this.startSecretFileCreator(inputFiles[0]);
            }
            if (exportview) {
                new ExportDefinition(inputFiles[0]);
            }
        } else if (fileparam.length > 1) {
            for (File f : inputFiles) {
                if (createconfig) {
                    this.startConfigCreator(inputFiles[0]);
                }
                if (storefiles) {
                    this.startSecretFileCreator(inputFiles[0]);
                }
                if (exportview) {
                    new ExportDefinition(inputFiles[0]);
                }
            }
        }

    }

    private void login(DefaultOpenShiftClient client) {
        TerminalReader apt = new TerminalReader();

        System.out.println("\nWorking on:");
        System.out.println("\t- CLUSTER: " + client.getMasterUrl());
        System.out.println("\t- PROJECT: " + client.getNamespace());
        System.out.println("");

        try {
            log.trace(client.projects().list().toString());
        } catch (Exception e) {
            //log.error(e.getMessage());
            log.error("Can't establish connection to cluster (" + client.getMasterUrl() + ")");
            System.exit(0);
        }
    }

    private void startConfigCreator(File f) {
        //CLIENT
        ModelLoader model = new ModelLoader(f);
        OpenshiftClientProvider clientProvider = new OpenshiftClientProvider(model.getModel().getNamespace(), model.getModel().getCluster());
        DefaultOpenShiftClient client = clientProvider.openShiftClient();
        login(client);
        new ConfigCreator(client, model.getModel());
    }

    private void startSecretFileCreator(File f) {
        //CLIENT
        ModelLoader model = new ModelLoader(f);
        OpenshiftClientProvider clientProvider = new OpenshiftClientProvider(model.getModel().getNamespace(), model.getModel().getCluster());
        DefaultOpenShiftClient client = clientProvider.openShiftClient();
        login(client);
        new SecretFileCreatorIo(client, model.getModel());
    }


    public static void main(String[] args) {
        CommandLine.run(new ConretCreator(), args);
    }

}

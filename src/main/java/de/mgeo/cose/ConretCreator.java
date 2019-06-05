package de.mgeo.cose;

import de.mgeo.cose.controllers.*;
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

    @Option(names = {"-a", "--create-all"}, description = "Create/Replace ConfigMap, Secrets and Secretfile")
    private boolean createall;

    @Option(names = {"-c", "--create-configmap"}, description = "Create/Replace a ConfigMap")
    private boolean createconfig;

    @Option(names = {"-s", "--create-secret"}, description = "Create/Replace a ConfigSecret")
    private boolean secretconfig;


    @Option(names = {"-f", "--secrets-fs"}, description = "Create/Replace Secrets from filesystem")
    private boolean storefiles;

    @Option(names = {"-x", "--export"}, description = "Print usage to stdout. The definition for the k8 spec.")
    private boolean exportview;

    @Option(names = {"-z", "--debug"}, description = "Enable mode DEBUG")
    private boolean isdebug;

    @Option(names = {"-v", "--visible-hidden"}, description = "Make cli-inputfields hidden")
    private boolean isvisible;

    @Option(names = {"-r", "--createroute"}, description = "Create Pathrouter from routes defintion")
    private boolean createroute;

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
            if (exportview) {
                new ExportDefinition(inputFiles[0]);
            } else {
                ModelLoader model = new ModelLoader(inputFiles[0]);
                OpenshiftClientProvider clientProvider = new OpenshiftClientProvider(model.getModel().getNamespace(), model.getModel().getCluster());
                DefaultOpenShiftClient client = clientProvider.openShiftClient();
                login(client);

                if (createroute || createall) {
                    new RouteCreator(client, model.getModel());
                }
                if (createconfig || createall) {
                    new ConfigMapCreator(client, model.getModel());
                }
                if (secretconfig || createall) {
                    new SecretConfCreator(client, model.getModel());
                }
                if (storefiles || createall) {
                    new SecretFileCreatorIo(client, model.getModel());
                }
            }
        } else if (fileparam.length > 1) {
            for (File f : inputFiles) {
                if (exportview) {
                    new ExportDefinition(f);
                } else {
                    ModelLoader model = new ModelLoader(inputFiles[0]);
                    OpenshiftClientProvider clientProvider = new OpenshiftClientProvider(model.getModel().getNamespace(), model.getModel().getCluster());
                    DefaultOpenShiftClient client = clientProvider.openShiftClient();
                    login(client);

                    if (createroute || createall) {
                        new RouteCreator(client, model.getModel());
                    }
                    if (createconfig || createall) {
                        new ConfigMapCreator(client, model.getModel());
                    }
                    if (secretconfig || createall) {
                        new SecretConfCreator(client, model.getModel());
                    }
                    if (storefiles || createall) {
                        new SecretFileCreatorIo(client, model.getModel());
                    }
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

    public static void main(String[] args) {
        CommandLine.run(new ConretCreator(), args);
    }

}

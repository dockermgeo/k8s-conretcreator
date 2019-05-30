package de.mgeo.cose;

import de.mgeo.cose.controllers.ConfigCreatorIo;
import de.mgeo.cose.controllers.ExportDefinition;
import de.mgeo.cose.controllers.Storefiles;
import de.mgeo.cose.lib.TerminalReader;
import de.mgeo.cose.lib.Logging;
import de.mgeo.cose.lib.openshift.OpenshiftCommandProcs;
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
    private boolean cr_io;

    @Option(names = {"-f", "--secrets-fs"}, description = "Create/Replace Secrets from filesystem")
    private boolean storefiles;

    @Option(names = {"-x", "--export"}, description = "Print usage to stdout. The definition for the k8 spec.")
    private boolean xport;

    @Option(names = {"-z", "--debug"}, description = "Enable mode DEBUG")
    private boolean isdebug;

    @Option(names = {"-v", "--visible"}, description = "Make cli-inputfields viewable")
    private boolean isvisible;

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

        System.setProperty("IS_VISIBLE", "FALSE");
        if (isvisible) {
            System.setProperty("IS_VISIBLE", "TRUE");
        }

        if (isdebug) {
            System.setProperty("LOG_LEVEL", "DEBUG");
            System.setProperty("DELETE_FILE", "FALSE");
            System.setProperty("IS_VISIBLE", "TRUE");
        }

        if (cr_io) {
            //CLIENT
            OpenshiftClientProvider clientProvider = new OpenshiftClientProvider();
            DefaultOpenShiftClient client = clientProvider.openShiftClient();
            login(client);

            if (fileparam.length > 0) {
                File f = inputFiles[0];
                new ConfigCreatorIo(f, client);
            } else if (fileparam.length > 1) {
                for (File f : inputFiles) {
                    new ConfigCreatorIo(f, client);
                }
            }
        }

//        if (cr_oc) {
//            login(occ);
//            if (fileparam.length > 0) {
//                File f = inputFiles[0];
//                new ConfigCreatorOc(f);
//            } else if (fileparam.length > 1) {
//                for (File f : inputFiles) {
//                    new ConfigCreatorOc(f);
//                }
//            }
//        }

        if (storefiles) {
            OpenshiftCommandProcs occ = new OpenshiftCommandProcs();
            login(occ);
            if (fileparam.length > 0) {
                File f = inputFiles[0];
                new Storefiles(f);
            } else if (fileparam.length > 1) {
                for (File f : inputFiles) {
                    new Storefiles(f);
                }
            }
        }


        if (xport) {
            if (fileparam.length > 0) {
                File f = inputFiles[0];
                new ExportDefinition(f);
            } else if (fileparam.length > 1) {
                for (File f : inputFiles) {
                    new ExportDefinition(f);
                }
            }
        }
    }

    private void login(OpenshiftCommandProcs occ) {
        TerminalReader apt = new TerminalReader();

        if (occ.isLogin() == false) {
            log.error("You must be logged in, into your K8-System!");
            System.exit(0);
        }

        System.out.println("\nWorking on:");
        System.out.println("\t- CLUSTER: " + occ.getClusterName().replaceAll("\n", ""));
        System.out.println("\t- PROJECT: " + occ.getNamespaceName().replaceAll("\n", "").replaceAll("\"", ""));
        System.out.println("");
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

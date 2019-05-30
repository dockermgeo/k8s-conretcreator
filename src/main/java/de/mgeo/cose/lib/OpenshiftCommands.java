package de.mgeo.cose.lib;

import de.mgeo.cose.controllers.ConfigCreatorIo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class OpenshiftCommands {
    private final ProcessBuilder processBuilder;
    private static String OS = System.getProperty("os.name").toLowerCase();
    private static Boolean OS_ISWIN = false;
    private static String OS_INTERPRETER = "bash";
    private static String OS_QUALIFIER = "-c";
    private final static Logger log = LoggerFactory.getLogger(OpenshiftCommands.class);

    public OpenshiftCommands() {
        if (OS.indexOf("win") >= 0) {
            this.OS_ISWIN = true;
            this.OS_INTERPRETER = "cmd.exe";
            this.OS_QUALIFIER = "/c";
        }
        processBuilder = new ProcessBuilder();
    }

    /*
     * CREATE
     */
    public void ocCreateSecretDir(String sname,String dirname, String commandString) {
        //System.out.println("---> oc create secret generic "+sname+" from-file=" + dirname);
        processBuilder.command(OS_INTERPRETER, OS_QUALIFIER, "oc create secret generic "+sname+" --from-file=" + dirname);
        initCommand(processBuilder,commandString);
    }

    public void ocCreate(String infile, String commandString) {
        processBuilder.command(OS_INTERPRETER, OS_QUALIFIER, "oc create -f " + infile);
        initCommand(processBuilder,commandString);
    }

    /*
     * DELETE
     */
    public void deleteKeystoreEntry(String zkind, String zname, String ztype) {
        if (this.checkExists(zname,ztype) == true) {
            String outCommand="";
            if(System.getProperty("DELETE_FILE").equals("FALSE")) {
                outCommand="Deleting existing "+zkind+" "+zname;
            }
            this.onDelete(zkind.toLowerCase(), zname, outCommand);
        }
    }

    public void onDelete(String type, String name, String outCommand) {
        processBuilder.command(OS_INTERPRETER, OS_QUALIFIER, "oc delete "+ type + " " + name);
        initCommand(processBuilder, outCommand);
    }

    /*
     * others
     */
    public boolean isLogin() {
        ProcessBuilder login = processBuilder.command(OS_INTERPRETER, OS_QUALIFIER, "oc whoami");
        String existValue = this.initCommandCallback(login);

        if (existValue != "0")
            return true;

        return false;
    }

    public Boolean checkExists(String secret, String type) {
        ProcessBuilder existCommand = processBuilder.command(OS_INTERPRETER, OS_QUALIFIER, "oc get " + type.toLowerCase() + " "+secret);
        String existValue = this.initCommandCallback(existCommand);

        if (existValue != "0")
            return true;

        return false;
    }

    public String getNamespaceName() {
        processBuilder.command(OS_INTERPRETER, OS_QUALIFIER, "oc project | cut -d' ' -f3");
        return initCommandCallback(processBuilder);
    }
    public String getClusterName() {
        processBuilder.command(OS_INTERPRETER, OS_QUALIFIER, "oc project | cut -d':' -f2|tr -d '/'");
        return initCommandCallback(processBuilder);
    }
    public String getVersion() {
        processBuilder.command(OS_INTERPRETER, OS_QUALIFIER, "oc version");
        return initCommandCallback(processBuilder);
    }

    /*
     * INITS
     */
    private void initCommand(ProcessBuilder processBuilder) {
        initCommand(processBuilder, "");
    }

    private void initCommand(ProcessBuilder processBuilder, String commandOut) {
        try {
            Process process = processBuilder.start();
            List<String> cmdstr = processBuilder.command();

            if (System.getProperty("DELETE_FILE").equals("FALSE")) {
                commandOut=cmdstr.toString();
            }

//            StringBuilder output = new StringBuilder();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                output.append(line + "\n");
//            }

            int exitVal = process.waitFor();
            if (exitVal == 0) {
                if (!commandOut.isEmpty()) {
                    log.info("done\t"+commandOut);
                }
            } else {
                log.error("\t"+commandOut);
            }

        } catch (IOException e) {
            e.getMessage();
        } catch (InterruptedException e) {
            e.getMessage();
        }
    }

    public String initCommandCallback(ProcessBuilder processBuilder) {
        try {
            Process process = processBuilder.start();
            StringBuilder output = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }

            int exitVal = process.waitFor();
            if (exitVal == 0) {
                StringBuilder sout = output;
                return sout.toString();
            } else {
                return "0";
            }
        } catch (IOException e) {
            e.getMessage();
            return "0";
        } catch (InterruptedException e) {
            e.getMessage();
            return "0";
        }
    }

}

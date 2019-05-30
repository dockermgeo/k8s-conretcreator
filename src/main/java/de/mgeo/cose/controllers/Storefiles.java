package de.mgeo.cose.controllers;

import de.mgeo.cose.lib.TerminalReader;
import de.mgeo.cose.lib.openshift.OpenshiftCommandProcs;
import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.List;
import java.util.Map;


public class Storefiles {
    private  String outputfilename = "";

    private  boolean is_filedeltable = true;
    OpenshiftCommandProcs occ;
    public Storefiles(File inputfile) {
        this.setOutputfilename(inputfile.getParent() + "/.gen-" + inputfile.getName() + ".json");
        Yaml yaml = new Yaml();
        Reader yamlFile = null;
        TerminalReader tools = new TerminalReader();
        occ = new OpenshiftCommandProcs();

        if (System.getProperty("DELETE_FILE") != null) {
            if (System.getProperty("DELETE_FILE").equals("FALSE")) {
                this.is_filedeltable = false;
            }
        }
        else {
            System.setProperty("DELETE_FILE", "TRUE");
        }


        try {
            yamlFile = new FileReader(inputfile.getAbsoluteFile());
            Map<String, Object> yamlMaps = yaml.load(yamlFile);
            final List<Map<String, Object>> FILES = (List<Map<String, Object>>) yamlMaps.get("files");

            String zkind = yamlMaps.get("kind") + "";
            if (zkind.isEmpty()) {
                zkind = tools.getInput("Secret or ConfigMap");
            }

            String zname = yamlMaps.get("name") + "";
            if (zname.isEmpty()) {
                zname = tools.getInput("Name of this Definition");
            }

            occ.deleteKeystoreEntry(zkind, zname+"-files", zkind.toLowerCase());

            String basedir=inputfile.getParent();
            String wdir = basedir+"/etcsec";
            makeDir(wdir);
            for (int y = 0; y < FILES.size(); y++) {
                Object filename = FILES.get(y).get("file");
                Object targetname = FILES.get(y).get("target");
                File f = new File(filename.toString());
                if (targetname.toString().isEmpty()) {
                    targetname=f.getName();
                }
                copyFile(basedir+"/"+filename.toString(),wdir+"/"+targetname);
            }
            occ.ocCreateSecretDir(zname+"-files", wdir, "Creating filesecret '"+zname+"-files'");

            deleteDir(wdir);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

    private void  deleteDir(String dirname) {
        if (is_filedeltable) {
            try {
                FileUtils.deleteDirectory(new File(dirname));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setOutputfilename(String outname) {
        this.outputfilename=outname;
    }

    private void copyFile(String sourcefile, String targetfile) {
        File sf = new File(sourcefile);
        File tf = new File(targetfile);
        try {
            FileUtils.copyFile(sf, tf);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void makeDir(String dir) {
        try {
            FileUtils.forceMkdirParent(new File(dir));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

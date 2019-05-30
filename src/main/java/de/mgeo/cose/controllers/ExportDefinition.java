package de.mgeo.cose.controllers;

import org.yaml.snakeyaml.Yaml;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;

public class ExportDefinition {
    Yaml yaml = new Yaml();
    Reader yamlFile = null;
    String output = "";

    public ExportDefinition(File inputFile) {
        try {
            yamlFile = new FileReader(inputFile.getAbsoluteFile());
            Map<String, Object> yamlMaps = yaml.load(yamlFile);
            final List<Map<String, Object>> DATA = (List<Map<String, Object>>) yamlMaps.get("data");

            String corename = yamlMaps.get("name") + "";
            addOut("containers:");
            addOut("\tenv:");
            for (int z = 0; z < DATA.size(); z++) {
                Object name = DATA.get(z).get("name");
                printSecretstyle(corename, name.toString().toUpperCase(), name.toString());
            }
            printVolumestyle(corename);
            print();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void printVolumestyle(String secretname) {
        String filequalifier="-files";

        addOut("\tvolumeMounts:");
        addOut("\t\t- name: " +secretname+"-volume");
        addOut("\t\t  mountPath: /etc/secrets");
        addOut("\t\t  readOnly: true");

        addOut("volumes:");
        addOut("\t\t- name: "+secretname+"-volume");
        addOut("\t\t  secret:");
        addOut("\t\t\tsecretName: "+secretname+filequalifier);
    }
    public void printConfigmapstyle(String cfgmapname, String envname, String refkey) {
        addOut("\t- name: " + envname);
        addOut("\t  valueFrom:");
        addOut("\t\tconfigMapKeyRef:");
        addOut("\t\t\tname: " + cfgmapname);
        addOut("\t\t\tkey: " + refkey);
    }

    public void printSecretstyle(String secretname, String envname, String refkey) {
        addOut("\t- name: " + envname);
        addOut("\t  valueFrom:");
        addOut("\t\tsecretKeyRef:");
        addOut("\t\t\tname: " + secretname);
        addOut("\t\t\tkey: " + refkey);
    }

    private void addOut(String output) {
        this.output += output + "\n";
    }

    private void print() {
        System.out.println(output);
    }

}


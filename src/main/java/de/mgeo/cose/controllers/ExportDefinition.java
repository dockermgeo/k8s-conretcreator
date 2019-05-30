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
            addOut(" containers:");
            addOut("    env:");
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
        addOut("    volumeMounts:");
        addOut("        - name: " +secretname+"-volume");
        addOut("          mountPath: /etc/secrets");
        addOut("          readOnly: true");

        addOut("volumes:");
        addOut("    - name: "+secretname+"-volume");
        addOut("      secret:");
        addOut("        secretName: "+secretname);
    }
    public void printConfigmapstyle(String cfgmapname, String envname, String refkey) {
        addOut("    - name: " + envname);
        addOut("    valueFrom:");
        addOut("      configMapKeyRef:");
        addOut("        name: " + cfgmapname);
        addOut("        key: " + refkey);
    }

    public void printSecretstyle(String secretname, String envname, String refkey) {
        addOut("    - name: " + envname);
        addOut("    valueFrom:");
        addOut("      secretKeyRef:");
        addOut("        name: " + secretname);
        addOut("        key: " + refkey);
    }

    private void addOut(String output) {
        this.output += output + "\n";
    }

    private void print() {
        System.out.println(output);
    }

}


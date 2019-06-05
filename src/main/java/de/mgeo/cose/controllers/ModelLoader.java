package de.mgeo.cose.controllers;

import de.mgeo.cose.model.RunModel;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;

public class ModelLoader {
    private RunModel model;

    public ModelLoader(File inputfile) {
        model = new RunModel();
        Yaml yaml = new Yaml();
        Reader yamlFile = null;

        try {
            yamlFile = new FileReader(inputfile.getAbsoluteFile());
        } catch (
                FileNotFoundException e) {
            e.printStackTrace();
        }

        Map<String, Object> yamlMaps = yaml.load(yamlFile);
        model.setDataCfmObjectList ( (List<Map<String, Object>>) yamlMaps.get("configs"));
        model.setDataSecObjectList ( (List<Map<String, Object>>) yamlMaps.get("secrets"));
        model.setFilesObjectList ( (List<Map<String, Object>>) yamlMaps.get("secretfiles"));
        model.setRoutesObjectList ( (List<Map<String, Object>>) yamlMaps.get("routes"));
        model.setNamespace(yamlMaps.get("namespace") + "");
        model.setAppname(yamlMaps.get("name") + "");
        model.setCluster(yamlMaps.get("clustername") + "");
        model.setInputfile(inputfile.getAbsoluteFile());
    }

    public RunModel getModel() {
        return this.model;
    }
}

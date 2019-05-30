package de.mgeo.cose.controllers;

import de.mgeo.cose.lib.TerminalReader;
import de.mgeo.cose.lib.Logging;
import de.mgeo.cose.lib.openshift.OpenshiftStore;
import de.mgeo.cose.model.StoreConfigMap;
import de.mgeo.cose.model.StoreSecret;
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;

public class ConfigCreatorIo {
    private static TerminalReader tools = new TerminalReader();
    //private final static Logger log = LoggerFactory.getLogger(ConfigCreatorIo.class);
    private static Logging logging = new Logging(ConfigCreatorIo.class.toString());
    private static Logger log = logging.getLogger();

    public ConfigCreatorIo(File inputfile, DefaultOpenShiftClient client) {
        Yaml yaml = new Yaml();
        Reader yamlFile = null;

        try {
            yamlFile = new FileReader(inputfile.getAbsoluteFile());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Map<String, Object> yamlMaps = yaml.load(yamlFile);
        final List<Map<String, Object>> dataList = (List<Map<String, Object>>) yamlMaps.get("data");

        String kind = yamlMaps.get("kind") + "";
        if (kind.isEmpty()) {
            kind = tools.getInput("Secret or ConfigMap");
        }

        String zname = yamlMaps.get("name") + "";
        if (zname.isEmpty()) {
            zname = tools.getInput("Name of this Definition");
        }

        Boolean isSecret = true;
        if (kind.toLowerCase().equals("configmap")) {
            isSecret = false;
        }

        // Creating DATA-Set
        StoreSecret secretmodel = new StoreSecret(zname);
        StoreConfigMap configmapmodel = new StoreConfigMap(zname);
        for (int z = 0; z < dataList.size(); z++) {
            Object name = dataList.get(z).get("name");
            Object value = dataList.get(z).get("value");
            Object desc = dataList.get(z).get("desc");
            if (value == null || value.toString().isEmpty()) {
                String ENVKEY = name + "".toUpperCase();
                if (System.getenv(ENVKEY) != null && !System.getenv(ENVKEY).isEmpty()) {
                    // FROM ENV
                    value = System.getProperty(ENVKEY);
                } else {
                    //From CLI
                    value = tools.getInput(desc.toString());
                }
            }
            if (isSecret) {
                log.debug("Adding secret " + name + " with " + value);
                secretmodel.addEntry(name + "", value + "");
            } else {
                log.debug("Adding cmap " + name + " with " + value);
                configmapmodel.addEntry(name.toString(), value.toString());
            }
        }

        OpenshiftStore store = new OpenshiftStore(client, zname.toLowerCase());
        if (isSecret) {
            store.write(secretmodel);
        } else {
            store.write(configmapmodel);
        }
    }
}

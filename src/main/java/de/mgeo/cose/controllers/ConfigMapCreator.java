package de.mgeo.cose.controllers;

import de.mgeo.cose.lib.Logging;
import de.mgeo.cose.lib.TerminalReader;
import de.mgeo.cose.lib.openshift.OpenshiftStore;
import de.mgeo.cose.model.RunModel;
import de.mgeo.cose.model.StoreConfigMap;
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;

public class ConfigMapCreator {
    private static TerminalReader tools = new TerminalReader();
    private static Logging logging = new Logging(ConfigMapCreator.class.toString());
    private static Logger log = logging.getLogger();

    public ConfigMapCreator(DefaultOpenShiftClient client, RunModel model) {
        List<Map<String, Object>> dataList = model.getDataCfmObjectList();
        StoreConfigMap configmapmodel = new StoreConfigMap(model.getAppname());

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

            log.debug("Adding cmap " + name + " with " + value);
            configmapmodel.addEntry(name.toString(), value.toString());
        }

        log.debug("Store Model " + model.getAppname().toLowerCase());
        OpenshiftStore store = new OpenshiftStore(client, model.getAppname().toLowerCase());
        store.write(configmapmodel);
    }

}

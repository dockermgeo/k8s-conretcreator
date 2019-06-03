package de.mgeo.cose.controllers;

import de.mgeo.cose.lib.Logging;
import de.mgeo.cose.lib.TerminalReader;
import de.mgeo.cose.lib.openshift.OpenshiftStore;
import de.mgeo.cose.model.RunModel;
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;

public class RouteCreator {
    private static TerminalReader tools = new TerminalReader();
    private static Logging logging = new Logging(RouteCreator.class.toString());
    private static Logger log = logging.getLogger();

    public RouteCreator(DefaultOpenShiftClient client, RunModel model) {
        List<Map<String, Object>> routeList = model.getRoutesObjectList();
        OpenshiftStore store = new OpenshiftStore(client, model.getAppname().toLowerCase());
        for (int z = 0; z < routeList.size(); z++) {
            Object host = routeList.get(z).get("host");
            Object path = routeList.get(z).get("path");
            Object servicename = routeList.get(z).get("targetservice");

            store.createRoute(servicename + "", host + "", path + "");
        }
    }
}

package de.mgeo.cose.lib.openshift;

import de.mgeo.cose.model.StoreConfigMap;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import de.mgeo.cose.model.StoreSecret;


public class OpenshiftStore {

    private final String appname;
    private DefaultOpenShiftClient client;

    public OpenshiftStore(DefaultOpenShiftClient client, String appname) {
        this.client=client;
        this.appname=appname;
    }

    public void write(StoreSecret dataSecret) {
        Resource<Secret, DoneableSecret> res_sec = client.secrets().inNamespace(client.getNamespace()).withName(appname);

        try {
            Secret secmap = res_sec.createOrReplace(new SecretBuilder()
                    .withNewMetadata()
                    .withName(dataSecret.getName())
                    .withNamespace(client.getNamespace())
                    .endMetadata()
                    .withType("opaque")
                    .withStringData(dataSecret.getData())
                    .build());
        }
        finally {
            client.close();
        }
    }

    public void write(StoreConfigMap dataCmap) {
        Resource<ConfigMap, DoneableConfigMap> res_cmap = client.configMaps().inNamespace(client.getNamespace()).withName(appname);

        try {
            ConfigMap configMap = res_cmap.createOrReplace(new ConfigMapBuilder().
                    withNewMetadata().withName(dataCmap.getName()).endMetadata().
                    withData(dataCmap.getData()).
                    build());
        }
        finally {
            client.close();
        }
    }
}

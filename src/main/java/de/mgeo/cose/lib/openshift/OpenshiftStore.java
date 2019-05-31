package de.mgeo.cose.lib.openshift;

import de.mgeo.cose.lib.Logging;
import de.mgeo.cose.model.StoreConfigMap;
import de.mgeo.cose.model.StoreSecret;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class OpenshiftStore {

    private final String appname;
    private DefaultOpenShiftClient client;
    private static Logging logging = new Logging(OpenshiftStore.class.toString());
    private static Logger log = logging.getLogger();

    public OpenshiftStore(DefaultOpenShiftClient client, String appname) {
        this.client = client;
        this.appname = appname;
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
        } finally {
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
        } finally {
            client.close();
        }
    }

    /*
     * Set<Pair</srcdir/log4j.properties,log4j.properties> project-name -files, namespace
     */
    public void upsertSecret(Set<Pair<File, String>> files, String secretName, String namespace) {

        if (client.secrets().inNamespace(namespace).withName(secretName).get() != null) {
            client.secrets().inNamespace(namespace).withName(secretName).delete();
        }

        Map<String, String> secretContents = new HashMap<>();

        files.forEach(pair -> {
            try {
                File file = pair.getLeft();
                String name = pair.getRight();
                String data = new String(Base64.getEncoder().encode(IOUtils.toByteArray(new FileInputStream(file))));

                secretContents.putIfAbsent(name, data);
            } catch (IOException e) {
                log.error("Unable to read contents of \"" + pair.getLeft() + "\": " + e.getMessage());
            }
        });

        SecretBuilder secretBuilder = new SecretBuilder();
        secretBuilder = secretBuilder.withNewMetadata()
                .withName(secretName)
                .withNamespace(namespace)
                .endMetadata()
                .withData(secretContents);

        client.secrets().inNamespace(namespace).create(secretBuilder.build());
    }

}

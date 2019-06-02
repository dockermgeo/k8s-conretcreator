package de.mgeo.cose.controllers;

import de.mgeo.cose.lib.Logging;
import de.mgeo.cose.lib.FileTools;
import de.mgeo.cose.lib.openshift.OpenshiftStore;
import de.mgeo.cose.model.RunModel;
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import org.slf4j.Logger;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.*;

public class SecretFileCreatorIo {
    private static Logging logging = new Logging(SecretFileCreatorIo.class.toString());
    private static Logger log = logging.getLogger();
    private Set<Pair<File, String>> fdatas=new HashSet<Pair<File, String>>();
    //private List<Pair<File, String>> fdatas = new ArrayList<Pair<File, String>>();
    private FileTools ft = new FileTools();


    public SecretFileCreatorIo(DefaultOpenShiftClient client, RunModel model) {
        String appname=model.getAppname();
        String basedir=model.getBasedir();

        List<Map<String, Object>> fileList = model.getFilesObjectList();
        for (int y = 0; y < fileList.size(); y++) {
            Object filename = fileList.get(y).get("src");
            Object file_target = fileList.get(y).get("target");
            File f = new File(filename+"");

            if (file_target.toString().isEmpty()) {
                file_target=f.getName();
            }
            File file_secret = new File(basedir+"/"+filename+"");
            if (!file_secret.exists()) {
                log.warn("File "+basedir+"/"+filename+" not exists! - Not considered");
            }
            else {
                log.debug("Adding Secretfile: "+file_secret.getAbsoluteFile()+", file_target:"+file_target);
                fdatas.add(Pair.of(file_secret,file_target+""));
            }
        }

        //write to store
        OpenshiftStore store = new OpenshiftStore(client, model.getAppname());
        store.upsertSecret(fdatas, model.getAppname()+"-files",model.getNamespace());
    }
}

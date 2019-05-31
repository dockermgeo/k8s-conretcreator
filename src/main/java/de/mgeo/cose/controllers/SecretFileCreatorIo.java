package de.mgeo.cose.controllers;

import de.mgeo.cose.lib.Logging;
import de.mgeo.cose.lib.FileTools;
import de.mgeo.cose.model.RunModel;
import org.slf4j.Logger;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.*;

public class SecretFileCreatorIo {
    private static Logging logging = new Logging(ConfigCreatorIo.class.toString());
    private static Logger log = logging.getLogger();
    //private Set<Pair<File, String>> fdatas;
    private List<Pair<File, String>> fdatas = new ArrayList<Pair<File, String>>();
    private FileTools ft = new FileTools();


    public SecretFileCreatorIo(RunModel model) {
        String appname=model.getAppname();
        String basedir=model.getBasedir();

        List<Map<String, Object>> fileList = model.getFilesObjectList();
        for (int y = 0; y < fileList.size(); y++) {
            Object filename = fileList.get(y).get("src");
            Object targetname = fileList.get(y).get("target");
            File f = new File(filename.toString());
            if (targetname.toString().isEmpty()) {
                targetname=f.getName();
            }

            File fsecret = new File(basedir+"/"+filename.toString());
            log.debug("file:"+fsecret.getAbsoluteFile().toString()+", targetname:"+targetname);
            fdatas.add(Pair.of(fsecret,targetname+""));
        }

        //TODO write to store
    }
}

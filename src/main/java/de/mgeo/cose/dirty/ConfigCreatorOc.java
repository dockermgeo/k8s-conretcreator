//package de.mgeo.cose.dirty;
//
//import de.mgeo.cose.lib.TerminalReader;
//import org.json.simple.JSONObject;
//import org.yaml.snakeyaml.Yaml;
//
//import java.io.*;
//import java.util.List;
//import java.util.Map;
//
//
//public class ConfigCreatorOc {
//    private  String outputfilename = "";
//    private  JSONObject SAVEDATA;
//    private  boolean is_filedeltable = true;
//    OpenshiftCommandProcs occ;
//
//    public ConfigCreatorOc(File inputfile) {
//        setOutputfilename(inputfile.getParent() + "/.gen-" + inputfile.getName() + ".json");
//
//        Yaml yaml = new Yaml();
//        Reader yamlFile = null;
//        TerminalReader tools = new TerminalReader();
//        occ = new OpenshiftCommandProcs();
//
//        if (System.getProperty("DELETE_FILE") != null) {
//            if (System.getProperty("DELETE_FILE").equals("FALSE")) {
//                this.is_filedeltable = false;
//            }
//        }
//        else {
//            System.setProperty("DELETE_FILE", "TRUE");
//        }
//
//
//        try {
//            yamlFile = new FileReader(inputfile.getAbsoluteFile());
//            Map<String, Object> yamlMaps = yaml.load(yamlFile);
//            final List<Map<String, Object>> module_name = (List<Map<String, Object>>) yamlMaps.get("data");
//            final List<Map<String, Object>> FILES = (List<Map<String, Object>>) yamlMaps.get("files");
//
//            String zkind = yamlMaps.get("kind") + "";
//            if (zkind.isEmpty()) {
//                zkind = tools.getInput("Secret or ConfigMap");
//            }
//
//            String zname = yamlMaps.get("name") + "";
//            if (zname.isEmpty()) {
//                zname = tools.getInput("Name of this Definition");
//            }
//
//            JSONObject stringData = new JSONObject();
//            for (int z = 0; z < module_name.size(); z++) {
//                Object name = module_name.get(z).get("name");
//                Object value = module_name.get(z).get("value");
//                Object desc = module_name.get(z).get("desc");
//                if (value.toString().isEmpty()) {
//                    String ENVKEY = name+"".toUpperCase();
//                    if (System.getenv(ENVKEY) != null && !System.getenv(ENVKEY).isEmpty()) {
//                        // FROM ENV
//                        value=System.getProperty(ENVKEY);
//                    }
//                    else {
//                        //From CLI
//                        value = tools.getInput(desc.toString());
//                    }
//                }
//                stringData.put(name, value);
//            }
//            JSONObject zmetadata = new JSONObject();
//            zmetadata.put("name", zname.toLowerCase());
//
//            SAVEDATA = new JSONObject();
//            SAVEDATA.put("kind", zkind);
//            SAVEDATA.put("apiVersion", "v1");
//            SAVEDATA.put("metadata", zmetadata);
//            SAVEDATA.put("stringData", stringData);
//
//            occ.deleteKeystoreEntry(zkind, zname, zkind.toLowerCase());
//            saveFile(outputfilename);
//            occ.ocCreate(outputfilename, "Creating "+zkind.toLowerCase()+" '"+zname.toString()+"'");
//            deleteFile(outputfilename);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//
//    }
//
//    private void deleteFile(String dfilename) {
//        if (is_filedeltable) {
//            File FileWritefile = new File(dfilename);
//            FileWritefile.delete();
//        }
//    }
//    public void setOutputfilename(String outname) {
//        this.outputfilename=outname;
//    }
//
//    private void saveFile(String savename) {
//        if (System.getProperty("TARGET_FILE") != null) {
//            savename = System.getProperty("TARGET_FILE");
//        }
//
//        try (FileWriter file = new FileWriter(savename)) {
//            file.write(SAVEDATA.toJSONString());
//            file.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//}

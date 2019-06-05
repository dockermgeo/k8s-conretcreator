package de.mgeo.cose.model;

import java.io.File;
import java.util.List;
import java.util.Map;

public class RunModel {

    private String appname;
    private String cluster;
    private String namespace;
    private List<Map<String, Object>> dataCfmObjectList;
    private List<Map<String, Object>> dataSecObjectList;
    private List<Map<String, Object>> filesObjectList;
    private List<Map<String, Object>> routesObjectList;
    private File inputfile;

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setFilesObjectList(List<Map<String, Object>> secretfiles) {
        this.filesObjectList=secretfiles;
    }

    public List<Map<String, Object>> getFilesObjectList() {
        return filesObjectList;
    }

    public void setInputfile(File inputfile) {
        this.inputfile = inputfile;
    }

    public File getInputfile() {
        return inputfile;
    }

    public String getBasedir() {
        return this.inputfile.getParent();
    }

    public void setRoutesObjectList(List<Map<String, Object>> routes) {
        this.routesObjectList = routes;
    }

    public List<Map<String, Object>> getRoutesObjectList() {
        return routesObjectList;
    }

    public List<Map<String, Object>> getDataCfmObjectList() {
        return dataCfmObjectList;
    }

    public void setDataCfmObjectList(List<Map<String, Object>> configs) {
        this.dataCfmObjectList = configs;
    }

    public List<Map<String, Object>> getDataSecObjectList() {
        return dataSecObjectList;
    }

    public void setDataSecObjectList(List<Map<String, Object>> secrets) {
        this.dataSecObjectList = secrets;
    }

}

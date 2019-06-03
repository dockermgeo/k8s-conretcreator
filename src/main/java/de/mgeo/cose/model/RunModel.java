package de.mgeo.cose.model;

import java.io.File;
import java.util.List;
import java.util.Map;

public class RunModel {

    private String kind;
    private String appname;
    private String cluster;
    private String namespace;
    private List<Map<String, Object>> dataObjectList;
    private List<Map<String, Object>> filesObjectList;
    private List<Map<String, Object>> routesObjectList;
    private File inputfile;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

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

    public List<Map<String, Object>> getDataObjectList() {
        return dataObjectList;
    }

    public void setDataObjectList(List<Map<String, Object>> dataObjectList) {
        this.dataObjectList = dataObjectList;
    }

    public List<Map<String, Object>> getFilesObjectList() {
        return filesObjectList;
    }

    public void setFilesObjectList(List<Map<String, Object>> filesObjectList) {
        this.filesObjectList = filesObjectList;
    }

    public void setInputfile(File inputfile) {
        this.inputfile=inputfile;
    }

    public File getInputfile() {
        return inputfile;
    }
    public String getBasedir() {
        return this.inputfile.getParent();
    }

    public void setRoutesObjectList(List<Map<String, Object>> routes) {
        this.routesObjectList=routes;
    }

    public List<Map<String, Object>> getRoutesObjectList() {
        return routesObjectList;
    }
}

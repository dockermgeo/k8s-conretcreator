package de.mgeo.cose.lib;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class FileTools {

    private boolean is_filedelable=true;

    public FileTools() {
    }

    public boolean isFileDelable() {
        return is_filedelable;
    }

    public void setFileDelable(boolean is_filedeltable) {
        this.is_filedelable = is_filedeltable;
    }

    public void copyFile(String sourcefile, String targetfile) {
        File sf = new File(sourcefile);
        File tf = new File(targetfile);
        try {
            FileUtils.copyFile(sf, tf);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void  deleteDir(String dirname) {
        if (is_filedelable) {
            try {
                FileUtils.deleteDirectory(new File(dirname));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void makeDir(String dir) {
        try {
            FileUtils.forceMkdirParent(new File(dir));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

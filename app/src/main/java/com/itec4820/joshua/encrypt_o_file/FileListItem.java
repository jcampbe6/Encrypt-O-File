package com.itec4820.joshua.encrypt_o_file;

import android.content.Context;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Joshua on 10/9/2014.
 */
public class FileListItem implements Comparable<FileListItem> {
    private String name;
    private String data;
    private String date;
    private String path;
    private String fileIconName;
    private String lockIconName;
    private File file;

    public FileListItem(String aName,String someData, String aDate, String aPath, String aFileIcon, File aFile) {
        name = aName;
        data = someData;
        date = aDate;
        path = aPath;
        fileIconName = aFileIcon;
        lockIconName = "empty";
        file = aFile;
    }

    public FileListItem(String aName,String someData, String aDate, String aPath, String aFileIcon, String aLockIcon, File aFile) {
        name = aName;
        data = someData;
        date = aDate;
        path = aPath;
        fileIconName = aFileIcon;
        lockIconName = aLockIcon;
        file = aFile;
    }

    public String getFileName() {
        return name;
    }

    public String getData() {
        return data;
    }

    public String getDate() {
        return date;
    }

    public String getPath() {
        return path;
    }

    public String getFileIconName() {
        return fileIconName;
    }

    public String getLockIconName() {
        return lockIconName;
    }

    public void setLockIconName(String name) {
        lockIconName = name;
    }

    public int compareTo(FileListItem item) {
        if(this.name != null)
            return this.name.toLowerCase().compareTo(item.getFileName().toLowerCase());
        else
            throw new IllegalArgumentException();
    }

    public File getFile() {
        return file;
    }
}

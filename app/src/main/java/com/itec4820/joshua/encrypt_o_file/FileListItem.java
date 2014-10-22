package com.itec4820.joshua.encrypt_o_file;

/**
 * Created by Joshua on 10/9/2014.
 */
public class FileListItem implements Comparable<FileListItem>{
    private String name;
    private String data;
    private String date;
    private String path;
    private String image;

    public FileListItem(String n,String d, String dt, String p, String img)
    {
        name = n;
        data = d;
        date = dt;
        path = p;
        image = img;
    }
    public String getName()
    {
        return name;
    }
    public String getData()
    {
        return data;
    }
    public String getDate()
    {
        return date;
    }
    public String getPath()
    {
        return path;
    }
    public String getImage() {
        return image;
    }
    public int compareTo(FileListItem o) {
        if(this.name != null)
            return this.name.toLowerCase().compareTo(o.getName().toLowerCase());
        else
            throw new IllegalArgumentException();
    }
}

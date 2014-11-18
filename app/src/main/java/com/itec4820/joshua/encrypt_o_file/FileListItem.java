package com.itec4820.joshua.encrypt_o_file;

/**
 * Class: FileListItem
 * Created by Joshua on 10/9/2014.
 *
 * Purpose: Represents a single file as a list item.
 */
public class FileListItem implements Comparable<FileListItem> {
    private String name;
    private String size;
    private String date;
    private String path;
    private String fileIconName;
    private String lockIconName;

    /**
     * Constructor: FileListItem
     * Constructs a FileListItem object with a name, size, date, path, and a file icon name.
     * Intended for use when constructing a 'directory' list item, so no lock icon name
     * is initialized.
     * @param aName
     * @param aSize
     * @param aDate
     * @param aPath
     * @param aFileIcon
     */
    public FileListItem(String aName, String aSize, String aDate, String aPath, String aFileIcon) {
        name = aName;
        size = aSize;
        date = aDate;
        path = aPath;
        fileIconName = aFileIcon;
    }

    /**
     * Constructor: FileListItem
     * Constructs a FileListItem object with a name, size, date, path, file icon name,
     * and a lock icon name. Intended for use when constructing a 'file' list item.
     * @param aName
     * @param aSize
     * @param aDate
     * @param aPath
     * @param aFileIconName
     * @param aLockIconName
     */
    public FileListItem(String aName, String aSize, String aDate, String aPath, String aFileIconName, String aLockIconName) {
        name = aName;
        size = aSize;
        date = aDate;
        path = aPath;
        fileIconName = aFileIconName;
        lockIconName = aLockIconName;
    }

    /**
     * Method: getFileName
     * Returns the name of the associated file
     * @return name the file name
     */
    public String getFileName() {
        return name;
    }

    /**
     * Method: getSize
     * Returns the size of the associated file
     * @return size the file size
     */
    public String getSize() {
        return size;
    }

    /**
     * Method: getDate
     * Returns the last modified date of the associated file
     * @return date the last modified date
     */
    public String getDate() {
        return date;
    }

    /**
     * Method: getPath
     * Returns the path of the associated file
     * @return path the file's path
     */
    public String getPath() {
        return path;
    }

    /**
     * Method: getFileIconName
     * Returns the name of the associated file
     * @return fileIconName the name of the file
     */
    public String getFileIconName() {
        return fileIconName;
    }

    /**
     * Method: getLockIconName
     * Returns the name associated with the lock icon image
     * @return lockIconName the name of the icon
     */
    public String getLockIconName() {
        return lockIconName;
    }

    /**
     * Method: setLockIconName
     * @param name
     * Sets the lock icon name to the specified String value.
     */
    public void setLockIconName(String name) {
        lockIconName = name;
    }

    /**
     * Method: compareTo
     * Required method, since this class implements Comparable. This was done in order to simplify
     * sorting of a list containing FileListItem objects using the Collections.sort() method.
     * @param item
     * @return
     */
    public int compareTo(FileListItem item) {
        if(this.name != null)
            return this.name.toLowerCase().compareTo(item.getFileName().toLowerCase());
        else
            throw new IllegalArgumentException();
    }
}

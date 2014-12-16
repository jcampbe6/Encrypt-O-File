package com.itec4820.joshua.encrypt_o_file;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;

/**
 * Created by Joshua on 11/12/2014.
 */
public class FileUtility {

    public static String setLockIconName(File aFile) {
        String lockIconName = "unlocked";

        if (aFile.getName().endsWith(".encx")) {
            lockIconName = "locked";
        }

        return lockIconName;
    }

    public static String setFileIconName(File aFile) {
        String fileIconName = "blank_icon";
        String extension = aFile.getName().substring(aFile.getName().lastIndexOf("."));

        if (extension.equalsIgnoreCase(".encx")) {
            fileIconName = "encrypted_file_icon";
        }
        else if (extension.equalsIgnoreCase(".doc") || extension.equalsIgnoreCase(".docx")){
            fileIconName = "doc_icon";
        }
        else if (extension.equalsIgnoreCase(".xls") || extension.equalsIgnoreCase(".xlsx")) {
            fileIconName = "xls_icon";
        }
        else if (extension.equalsIgnoreCase(".ppt") || extension.equalsIgnoreCase(".pptx")) {
            fileIconName = "ppt_icon";
        }
        else if (extension.equalsIgnoreCase(".pdf")) {
            fileIconName = "pdf_icon";
        }
        else if (extension.equalsIgnoreCase(".txt")) {
            fileIconName = "txt_icon";
        }
        else if (extension.equalsIgnoreCase(".png") || extension.equalsIgnoreCase(".jpg")  || extension.equalsIgnoreCase(".jpeg")
                || extension.equalsIgnoreCase(".gif")) {
            fileIconName = "img_icon";
        }

        return fileIconName;
    }

    public static String formatDateTime(File aFile) {
        Date lastModDate = new Date(aFile.lastModified());

        //formats date
        DateFormat formatter = DateFormat.getDateInstance(DateFormat.MEDIUM);
        String date = formatter.format(lastModDate);

        //formats time
        formatter = DateFormat.getTimeInstance(DateFormat.SHORT);
        String time = formatter.format(lastModDate);

        return date + "  |  " + time;
    }

    /**
     * Method: formatFileSize
     * Formats a file size to a more readable format with units: B, kB, MB, GB, TB.
     * @param aFile the file whose size is to be formatted
     * @return the formatted file size string
     */
    public static String formatFileSize(File aFile) {
        Long size = aFile.length();

        if(size <= 0) {
            return "0";
        }

        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));

        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}

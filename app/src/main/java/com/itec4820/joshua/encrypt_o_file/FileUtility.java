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

        if (aFile.getName().endsWith(".encx")) {
            fileIconName = "encrypted_file_icon";
        }
        else if (aFile.getName().endsWith(".doc") || aFile.getName().endsWith(".docx")){
            fileIconName = "doc_icon";
        }
        else if (aFile.getName().endsWith(".xls") || aFile.getName().endsWith(".xlsx")) {
            fileIconName = "xls_icon";
        }
        else if (aFile.getName().endsWith(".ppt") || aFile.getName().endsWith(".pptx")) {
            fileIconName = "ppt_icon";
        }
        else if (aFile.getName().endsWith(".pdf")) {
            fileIconName = "pdf_icon";
        }
        else if (aFile.getName().endsWith(".txt")) {
            fileIconName = "txt_icon";
        }
        else if (aFile.getName().endsWith(".png") || aFile.getName().endsWith(".jpg")  || aFile.getName().endsWith(".jpeg")
                || aFile.getName().endsWith(".gif")) {
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

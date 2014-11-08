package com.itec4820.joshua.encrypt_o_file;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.*;
import android.os.Process;
import android.text.format.Formatter;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class FileBrowserActivity extends ListActivity {

    private File currentDirectory;
    private FileArrayAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentDirectory = new File("/storage/emulated/0");
        adapter = new FileArrayAdapter(this);

        fill(currentDirectory);
    }

    private void fill(File aFile)
    {
        File[] directoryArray = aFile.listFiles();
        this.setTitle(currentDirectory.getPath());
        List<FileListItem> directoryList = new ArrayList<FileListItem>();
        final List<FileListItem> fileList = new ArrayList<FileListItem>();

        try{
            for(File file: directoryArray)
            {
                Date lastModDate = new Date(file.lastModified());
                DateFormat formatter = DateFormat.getDateInstance(DateFormat.MEDIUM);
                //gets date
                String date = formatter.format(lastModDate);
                formatter = DateFormat.getTimeInstance(DateFormat.SHORT);
                //gets time
                String time = formatter.format(lastModDate);
                String modifiedDate = date + "  |  " + time;//formatter.format(lastModDate);

                if(file.isDirectory()){
                    File[] directoryFiles = file.listFiles();
                    int totalFiles;
                    if(directoryFiles != null){
                        totalFiles = directoryFiles.length;
                    }
                    else {
                        totalFiles = 0;
                    }
                    String numOfItems;
                    if(totalFiles == 1){
                        numOfItems = totalFiles + " item";
                    }
                    else {
                        numOfItems = totalFiles + " items";
                    }

                    directoryList.add(new FileListItem(file.getName(), numOfItems, modifiedDate, file.getAbsolutePath(), "directory_icon", file));
                }
                else
                {
                    String fileIconName = "file_icon";
                    String lockIconName = "unlocked";

                    if (file.getName().endsWith(".doc") || file.getName().endsWith(".docx")){
                        fileIconName = "doc_icon";
                    }
                    else if (file.getName().endsWith(".xls") || file.getName().endsWith(".xlsx")) {
                        fileIconName = "xls_icon";
                    }
                    else if (file.getName().endsWith(".ppt") || file.getName().endsWith(".pptx")) {
                        fileIconName = "ppt_icon";
                    }
                    else if (file.getName().endsWith(".pdf")) {
                        fileIconName = "pdf_icon";
                    }
                    else if (file.getName().endsWith(".txt")) {
                        fileIconName = "txt_icon";
                    }
                    else if (file.getName().endsWith(".png") || file.getName().endsWith(".jpg")  || file.getName().endsWith(".jpeg")
                            || file.getName().endsWith(".gif")) {
                        fileIconName = "img_icon";
                    }

                    String size = formatFileSize(file.length());

                    fileList.add(new FileListItem(file.getName(), size, modifiedDate, file.getAbsolutePath(), fileIconName, lockIconName, file));
                }
            }
        }catch(Exception e)
        {

        }
        Collections.sort(directoryList);
        Collections.sort(fileList);
        adapter.addDirectoryItems(directoryList);
        adapter.addFileItems(fileList);

        if(!aFile.getName().equalsIgnoreCase("sdcard")) {
            adapter.addDirectoryUpItem(new FileListItem("..", "Parent Directory", "", aFile.getParent(), "directory_up", null));
        }

        this.setListAdapter(adapter);
    }
    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        FileListItem fListItem = adapter.getItem(position);

        if(fListItem.getFileIconName().equalsIgnoreCase("directory_icon") || fListItem.getFileIconName().equalsIgnoreCase("directory_up")) {
            onDirectoryClick(fListItem);
        }
        else {
            onFileClick(fListItem);
        }
    }

    private void onDirectoryClick(FileListItem listItem) {
        currentDirectory = new File(listItem.getPath());
        adapter.clearLists();
        fill(currentDirectory);
    }

    private void onFileClick(final FileListItem listItem) {
        String ext = listItem.getFileName().substring(listItem.getFileName().indexOf(".")+1);

        if (!listItem.getFileName().startsWith(".") && !ext.contains(".apk")) {
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);

            MimeTypeMap mime = MimeTypeMap.getSingleton();
            String type = mime.getMimeTypeFromExtension(ext);

            intent.setDataAndType(Uri.fromFile(listItem.getFile()), type);
            startActivity(intent);
        }
        else {
            Toast.makeText(getApplicationContext(), "Can't open this type of file.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    /**
     * Method: formatFileSize
     * Formats a file size to a more readable format with units: B, kB, MB, GB, TB.
     * @param size the size to be formatted
     * @return the formatted file size string
     */
    public static String formatFileSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}

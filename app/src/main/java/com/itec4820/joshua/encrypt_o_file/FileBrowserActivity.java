package com.itec4820.joshua.encrypt_o_file;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
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
        final FileUtility fileUtility = new FileUtility();

        try{
            for(File file: directoryArray)
            {
                String modifiedDate = fileUtility.formatDateTime(file);

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

                    directoryList.add(new FileListItem(file.getName(), numOfItems, modifiedDate, file.getAbsolutePath(), "directory_icon"));
                }
                else
                {
                    String fileIconName = fileUtility.setFileIconName(file);
                    String lockIconName = fileUtility.setLockIconName(file);
                    String size = fileUtility.formatFileSize(file);

                    fileList.add(new FileListItem(file.getName(), size, modifiedDate, file.getAbsolutePath(), fileIconName, lockIconName));
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
            adapter.addDirectoryUpItem(new FileListItem("..", "Parent Directory", "", aFile.getParent(), "directory_up"));
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
        openFile(listItem.getFileName(), listItem.getPath());

        //Toast.makeText(getApplicationContext(), listItem.getPath(), Toast.LENGTH_LONG).show();
    }

    private void openFile(String fileName, String filePath) {
        String extNoPeriod = fileName.substring(fileName.lastIndexOf(".") + 1);

        if (!fileName.startsWith(".") && !extNoPeriod.equalsIgnoreCase("encx")) {
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);

            MimeTypeMap mime = MimeTypeMap.getSingleton();
            String type = mime.getMimeTypeFromExtension(extNoPeriod);

            File file = new File(filePath);

            intent.setDataAndType(Uri.fromFile(file), type);
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
}

package com.itec4820.joshua.encrypt_o_file;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.io.File;
import java.text.DateFormat;
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
        fill(currentDirectory);
    }
    private void fill(File aFile)
    {
        File[]directoryArray = aFile.listFiles();
        this.setTitle(currentDirectory.getPath());
        List<FileListItem>directory = new ArrayList<FileListItem>();
        List<FileListItem> filesList = new ArrayList<FileListItem>();
        try{
            for(File file: directoryArray)
            {
                Date lastModDate = new Date(file.lastModified());
                DateFormat formatter = DateFormat.getDateTimeInstance();
                String modifiedDate = formatter.format(lastModDate);
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

                    directory.add(new FileListItem(file.getName(),numOfItems,modifiedDate,file.getAbsolutePath(),"directory_icon"));
                }
                else
                {
                    filesList.add(new FileListItem(file.getName(),file.length() + " Byte", modifiedDate, file.getAbsolutePath(),"file_icon"));
                }
            }
        }catch(Exception e)
        {

        }
        Collections.sort(directory);
        Collections.sort(filesList);
        directory.addAll(filesList);
        if(!aFile.getName().equalsIgnoreCase("sdcard"))
            directory.add(0,new FileListItem("..","Parent Directory","",aFile.getParent(),"directory_up"));
        adapter = new FileArrayAdapter(getApplicationContext(), R.layout.activity_file_browser,directory);
        this.setListAdapter(adapter);
    }
    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(listView, view, position, id);
        FileListItem fListItem = adapter.getItem(position);
        if(fListItem.getImage().equalsIgnoreCase("directory_icon")||fListItem.getImage().equalsIgnoreCase("directory_up")){
            currentDirectory = new File(fListItem.getPath());
            fill(currentDirectory);
        }
        else
        {
            onFileClick(fListItem);
        }
    }
    private void onFileClick(FileListItem listItem)
    {
        //Toast.makeText(getApplicationContext(), "Folder Clicked: "+ currentDir, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.putExtra("GetPath",currentDirectory.toString());
        intent.putExtra("GetFileName",listItem.getName());
        setResult(RESULT_OK, intent);
        finish();
    }
}

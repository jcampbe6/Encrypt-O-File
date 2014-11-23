package com.itec4820.joshua.encrypt_o_file;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileBrowserActivity extends ListActivity {

    private File currentDirectory;
    private FileArrayAdapter adapter;
    private ListView listView;
    private static final String HOME_DIRECTORY = "/storage/emulated/0";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentDirectory = new File(HOME_DIRECTORY);
        adapter = new FileArrayAdapter(this);

        setContentView(R.layout.filebrowser_listview_layout);
        listView = (ListView) findViewById(android.R.id.list);
        ImageButton homeButton = (ImageButton) findViewById(R.id.home_button);
        final Button parentDirectoryButton = (Button) findViewById(R.id.parent_directory_button);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.clearLists();
                currentDirectory = new File(HOME_DIRECTORY);
                fill(currentDirectory);
            }
        });

        parentDirectoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentDirectory.getParent() != null) {
                    currentDirectory = new File(currentDirectory.getParent());
                    adapter.clearLists();
                    fill(currentDirectory);
                }
            }
        });

        fill(currentDirectory);
    }

    private void fill(final File aFile)
    {
        if (aFile.isDirectory()) {
            this.setTitle(currentDirectory.getPath());
            File[] fileArray = aFile.listFiles();

            if (fileArray != null) {
                List<FileListItem> directoryList = new ArrayList<FileListItem>();
                List<FileListItem> fileList = new ArrayList<FileListItem>();

                try {
                    for (File file : fileArray) {
                        String modifiedDate = FileUtility.formatDateTime(file);

                        if (file.isDirectory()) {
                            File[] directoryFiles = file.listFiles();
                            int totalFiles;
                            if (directoryFiles != null) {
                                totalFiles = directoryFiles.length;
                            }
                            else {
                                totalFiles = 0;
                            }

                            String numOfItems;
                            if (totalFiles == 1) {
                                numOfItems = totalFiles + " item";
                            }
                            else {
                                numOfItems = totalFiles + " items";
                            }

                            directoryList.add(new FileListItem(file.getName(), numOfItems, modifiedDate, file.getAbsolutePath(), "directory_icon"));
                        }
                        else {
                            String fileIconName = FileUtility.setFileIconName(file);
                            String lockIconName = FileUtility.setLockIconName(file);
                            String size = FileUtility.formatFileSize(file);

                            fileList.add(new FileListItem(file.getName(), size, modifiedDate, file.getAbsolutePath(), fileIconName, lockIconName));
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                Collections.sort(directoryList);
                Collections.sort(fileList);
                adapter.addDirectoryItems(directoryList);
                adapter.addFileItems(fileList);
            }
        }

        this.listView.setAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        FileListItem fListItem = adapter.getItem(position);

        if(fListItem.getFileIconName().equalsIgnoreCase("directory_icon")) {
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

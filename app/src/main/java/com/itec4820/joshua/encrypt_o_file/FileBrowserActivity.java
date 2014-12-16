package com.itec4820.joshua.encrypt_o_file;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileBrowserActivity extends ListActivity {

    private File currentDirectory;
    private FileArrayAdapter adapter;
    private ListView listView;
    private static ListActivity listActivity;
    private static final String HOME_DIRECTORY = Environment.getExternalStorageDirectory().getPath();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentDirectory = new File(HOME_DIRECTORY);
        adapter = new FileArrayAdapter(this);
        listActivity = this;

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

        registerForContextMenu(listView);
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
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        FileListItem listItem = adapter.getItem(info.position);
        File file = new File(listItem.getPath());

        if (!file.isDirectory()) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final LayoutInflater inflater = getLayoutInflater();
        final FileListItem listItem = adapter.getItem(info.position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
        final AlertDialog dialog;

        // custom title for dialog
        TextView title = new TextView(this);
        title.setPadding(10, 10, 10, 10);
        title.setTextColor(Color.parseColor("#FF33b5e5"));
        title.setSingleLine(false);
        title.setTextSize(20);

        switch (item.getItemId()) {
            case R.id.delete_item:
                title.setText("Delete " + listItem.getFileName() + "?");
                builder.setCustomTitle(title)
                        .setMessage("This action cannot be undone!")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                boolean deleted = deleteItem(listItem);

                                if (deleted) {
                                    adapter.removeFileItem(info.position);
                                    EncryptOToaster.makeText(listActivity, "Deleted " + listItem.getFileName(), Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    EncryptOToaster.makeText(listActivity, "Delete Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {}
                        });
                dialog = builder.create();
                dialog.show();
                return true;

            case R.id.move_item:
                title.setText("Move " + listItem.getFileName() + "?");
                builder.setCustomTitle(title)
                        .setMessage("This action cannot be undone!")
                        .setPositiveButton("Move", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                EncryptOToaster.makeText(listActivity, "Moved", Toast.LENGTH_SHORT).show();
                                //moveItem(info.position);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {}
                        });
                dialog = builder.create();
                dialog.show();
                return true;

            case R.id.rename_item:
                // custom layout view for dialog
                final View renameDialogView = inflater.inflate(R.layout.rename_file_dialog, null, false);

                title.setText("Rename " + listItem.getFileName() + "?");
                builder.setCustomTitle(title).setView(renameDialogView);
                dialog = builder.create();

                final EditText newFilenameEditText = (EditText) renameDialogView.findViewById(R.id.newFilenameText);

                String extension = listItem.getFileName().substring(listItem.getFileName().lastIndexOf("."));
                TextView extensionTextView = (TextView) renameDialogView.findViewById(R.id.extensionText);
                extensionTextView.setText(extension);

                Button confirmRenameFileButton = (Button) renameDialogView.findViewById(R.id.confirmRenameFileButton);
                Button cancelRenameFileButton = (Button) renameDialogView.findViewById(R.id.cancelRenameFileButton);

                confirmRenameFileButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String newFilename = newFilenameEditText.getText().toString();
                        if (newFilename.length() == 0) {
                            EncryptOToaster.makeText(listActivity, "Please enter a new file name.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            EncryptOToaster.makeText(listActivity, "Renamed", Toast.LENGTH_SHORT).show();
                            // Todo: add renameItem method
                            //renameItem(info.position);
                            dialog.dismiss();
                        }
                    }
                });

                cancelRenameFileButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                dialog.show();
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    private void moveItem(int position) {
        FileListItem listItem = adapter.getItem(position);
        File sourceFile = new File(listItem.getPath());
        String destinationFolder = "/system/app/";

        File destinationFile = new File(destinationFolder + listItem.getFileName());
        EncryptOToaster.makeText(this, "" + sourceFile.renameTo(destinationFile), Toast.LENGTH_SHORT).show();
        //EncryptOToaster.makeText(this, destinationFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();

        currentDirectory = new File(destinationFolder);
        adapter.clearLists();
        fill(currentDirectory);
    }

    private boolean deleteItem(FileListItem listItem) {
        File file = new File(listItem.getPath());
        return file.delete();
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
            String type = mime.getMimeTypeFromExtension(extNoPeriod.toLowerCase());

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

package com.itec4820.joshua.encrypt_o_file;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Class: FileArrayAdapter
 * Created by Joshua on 10/9/2014.
 *
 * Purpose: To adapt each item in an array of files and display them in a ListView format.
 */
public class FileArrayAdapter extends BaseAdapter {

    //static variables used to determine view layout to be used
    private static final int DIRECTORY_VIEW = 0;
    private static final int FILE_VIEW = 1;
    private static final int MAX_VIEW_TYPES = 2;

    private final Activity context;
    private static final List<FileListItem> items = new ArrayList<FileListItem>();
    private LayoutInflater inflater;
    private TreeSet fileSet = new TreeSet();

    /**
     * Constructor: FileArrayAdapter
     * @param newContext
     */
    public FileArrayAdapter(Activity newContext) {
        context = newContext;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Method: addDirectoryItem
     * @param item
     * This method is intended to add the 'previous directory' item to the beginning of
     * the items ArrayList.
     */
    public void addDirectoryUpItem(FileListItem item) {
        items.add(0, item);
    }

    /**
     * Method: addDirectoryItems
     * @param list
     * Adds each item in a list of Directories to the items ArrayList (used to fill the
     * list view).
     */
    public void addDirectoryItems(List<FileListItem> list) {
        items.addAll(list);
        notifyDataSetChanged();
    }

    /**
     * Method: addFileItems
     * @param list
     * Adds each item in a list of files to both the items ArrayList (used to fill the
     * list view) and the fileSet TreeSet (used to save the item's position which will be
     * used to determine which view layout to use).
     */
    public void addFileItems(final List<FileListItem> list) {
        for (FileListItem item: list) {
            items.add(item);

            //save file position
            fileSet.add(items.size());
            notifyDataSetChanged();
        }
    }

    /**
     * Method: clearLists
     * Clears all elements of the items ArrayList and the fileSet TreeSet to prepare for
     * a new item list.
     */
    public void clearLists() {
        items.clear();
        fileSet.clear();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public int getViewTypeCount() {
        return MAX_VIEW_TYPES;
    }

    @Override
    /**
     * Used to determine which view layout type to use for an item at a specified position.
     */
    public int getItemViewType(int position) {
        int viewType;
        if (fileSet.contains(position)) {
            viewType = FILE_VIEW;
        }
        else {
            viewType = DIRECTORY_VIEW;
        }
        return viewType;
    }

    @Override
    public FileListItem getItem(int position) {
        return items.get(position);
    }

    /**
     * Class: ViewHolder
     * To hold each list item's view variables.
     */
    static class ViewHolder {
        protected TextView fileTitle;
        protected TextView fileSize;
        protected TextView sizeAndDateModified;

        protected ImageView fileIcon;
        protected String fileIconURI;
        protected int fileIconImageResource;
        protected Drawable fileIconImage;

        protected ImageView lockIcon;
        protected String lockIconURI;
        protected int lockIconImageResource;
        protected Drawable lockIconImage;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ViewHolder viewHolder;
        int type = getItemViewType(position);
        final FileListItem listItem = items.get(position);

        if (convertView == null) {
            viewHolder = new ViewHolder();

            switch (type) {
                //if directory view, then set corresponding layout and initialize layout specific variables
                case DIRECTORY_VIEW:
                    convertView = inflater.inflate(R.layout.activity_file_browser_directory, parent, false);
                    viewHolder.fileIcon = (ImageView) convertView.findViewById(R.id.directoryIcon);
                    viewHolder.fileTitle = (TextView) convertView.findViewById(R.id.directoryTitle);
                    viewHolder.fileSize = (TextView) convertView.findViewById(R.id.directorySize);
                    break;

                //if file view, then set corresponding layout and initialize layout specific variables
                case FILE_VIEW:
                    convertView = inflater.inflate(R.layout.activity_file_browser_file, parent, false);
                    viewHolder.fileIcon = (ImageView) convertView.findViewById(R.id.fileIcon);
                    viewHolder.fileTitle = (TextView) convertView.findViewById(R.id.fileTitle);
                    viewHolder.sizeAndDateModified = (TextView) convertView.findViewById(R.id.sizeAndDateTimeModified);
                    viewHolder.lockIcon = (ImageView) convertView.findViewById(R.id.lockIcon);
                    break;
            }

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        //set file icon image based on list item's file icon name
        viewHolder.fileIconURI = "drawable/" + listItem.getFileIconName();
        viewHolder.fileIconImageResource = context.getResources().getIdentifier(viewHolder.fileIconURI, null, context.getPackageName());
        viewHolder.fileIconImage = context.getResources().getDrawable(viewHolder.fileIconImageResource);
        viewHolder.fileIcon.setImageDrawable(viewHolder.fileIconImage);

        viewHolder.fileTitle.setText(listItem.getFileName());

        switch (type) {
            //set directory layout specific variable values
            case DIRECTORY_VIEW:
                viewHolder.fileSize.setText(listItem.getSize());
                break;

            //set file layout specific variable values
            case FILE_VIEW:
                String sizeAndDate = listItem.getSize() + "  |  " + listItem.getDate();
                viewHolder.sizeAndDateModified.setText(sizeAndDate);

                //set initial lock icon image based on list item's initial lock icon name
                viewHolder.lockIconURI = "drawable/" + listItem.getLockIconName();
                viewHolder.lockIconImageResource = context.getResources().getIdentifier(viewHolder.lockIconURI, null, context.getPackageName());
                viewHolder.lockIconImage = context.getResources().getDrawable(viewHolder.lockIconImageResource);
                viewHolder.lockIcon.setImageDrawable(viewHolder.lockIconImage);

                viewHolder.lockIcon.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (listItem.getLockIconName().equalsIgnoreCase("locked")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_DARK);

                            builder.setTitle("Decrypt " + listItem.getFileName() + "?")
                                    .setMessage("Are you sure you want to decrypt?")
                                    .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    })
                                    .setNegativeButton("Decrypt", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            // decrypts and returns decrypted file
                                            File decryptedFile = Encryptor.decrypt("password", listItem.getPath());

                                            // sets list item properties and creates new list item
                                            String size = FileUtility.formatFileSize(decryptedFile);
                                            String modifiedDate = FileUtility.formatDateTime(decryptedFile);
                                            String fileIconName = FileUtility.setFileIconName(decryptedFile);
                                            String lockIconName = FileUtility.setLockIconName(decryptedFile);
                                            FileListItem newListItem = new FileListItem(decryptedFile.getName(), size, modifiedDate, decryptedFile.getAbsolutePath(), fileIconName, lockIconName);

                                            // replaces encrypted list item with decrypted list item
                                            items.set(position, newListItem);
                                            notifyDataSetChanged();

                                            //set lock icon to "unlocked" based on current file extension
                                            if (!listItem.getFileName().endsWith(".encx")) {
                                                listItem.setLockIconName("unlocked");
                                                viewHolder.lockIconURI = "drawable/" + listItem.getLockIconName();
                                                viewHolder.lockIconImageResource = context.getResources().getIdentifier(viewHolder.lockIconURI, null, context.getPackageName());
                                                viewHolder.lockIconImage = context.getResources().getDrawable(viewHolder.lockIconImageResource);
                                                viewHolder.lockIcon.setImageDrawable(viewHolder.lockIconImage);
                                            }
                                        }
                                    });

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }

                        else {

                            AlertDialog.Builder builder = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_DARK);

                            TextView title = new TextView(context);

                            title.setText("Encrypt " + listItem.getFileName() + "?");
                            title.setPadding(10, 10, 10, 10);
                            title.setTextColor(Color.parseColor("#FF33b5e5"));
                            title.setSingleLine(false);
                            title.setTextSize(20);

                            //.setTitle("Encrypt " + listItem.getFileName() + "?")
                            builder.setCustomTitle(title)
                                    .setView(inflater.inflate(R.layout.confirm_encryption_dialog, parent, false))
                                    //.setMessage("Are you sure you want to encrypt?")
                                    .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    })
                                    .setNegativeButton("Encrypt", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            // encrypts and returns encrypted file
                                            File encryptedFile = Encryptor.encrypt("password", listItem.getPath(), listItem.getFileName());

                                            // sets list item properties and creates new list item
                                            String size = FileUtility.formatFileSize(encryptedFile);
                                            String modifiedDate = FileUtility.formatDateTime(encryptedFile);
                                            String fileIconName = FileUtility.setFileIconName(encryptedFile);
                                            String lockIconName = FileUtility.setLockIconName(encryptedFile);
                                            FileListItem newListItem = new FileListItem(encryptedFile.getName(), size, modifiedDate, encryptedFile.getAbsolutePath(), fileIconName, lockIconName);

                                            // replaces decrypted list item with encrypted list item
                                            items.set(position, newListItem);
                                            notifyDataSetChanged();

                                            //set lock icon to "locked" based on current file extension
                                            if (listItem.getFileName().endsWith(".encx")) {
                                                listItem.setLockIconName("locked");
                                                viewHolder.lockIconURI = "drawable/" + listItem.getLockIconName();
                                                viewHolder.lockIconImageResource = context.getResources().getIdentifier(viewHolder.lockIconURI, null, context.getPackageName());
                                                viewHolder.lockIconImage = context.getResources().getDrawable(viewHolder.lockIconImageResource);
                                                viewHolder.lockIcon.setImageDrawable(viewHolder.lockIconImage);
                                            }
                                        }
                                    });

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                });
                break;
        }

        return convertView;
    }
}

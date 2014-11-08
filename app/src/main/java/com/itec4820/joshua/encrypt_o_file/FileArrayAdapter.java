package com.itec4820.joshua.encrypt_o_file;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        int type = getItemViewType(position);
        final FileListItem listItem = items.get(position);

        if (convertView == null) {
            viewHolder = new ViewHolder();

            switch (type) {
                //if directory view, then set corresponding layout and initialize layout specific variables
                case DIRECTORY_VIEW:
                    convertView = inflater.inflate(R.layout.activity_file_browser_directory, null);
                    viewHolder.fileIcon = (ImageView) convertView.findViewById(R.id.directoryIcon);
                    viewHolder.fileTitle = (TextView) convertView.findViewById(R.id.directoryTitle);
                    viewHolder.fileSize = (TextView) convertView.findViewById(R.id.directorySize);
                    break;

                //if file view, then set corresponding layout and initialize layout specific variables
                case FILE_VIEW:
                    convertView = inflater.inflate(R.layout.activity_file_browser_file, null);
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
                String sizeDate = listItem.getSize() + "  |  " + listItem.getDate();
                viewHolder.sizeAndDateModified.setText(sizeDate);//(listItem.getDate());

                //set initial lock icon image based on list item's initial lock icon name
                viewHolder.lockIconURI = "drawable/" + listItem.getLockIconName();
                viewHolder.lockIconImageResource = context.getResources().getIdentifier(viewHolder.lockIconURI, null, context.getPackageName());
                viewHolder.lockIconImage = context.getResources().getDrawable(viewHolder.lockIconImageResource);
                viewHolder.lockIcon.setImageDrawable(viewHolder.lockIconImage);

                viewHolder.lockIcon.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //set new name for lock icon based on current lock icon name
                        String newLockIconName;
                        if (listItem.getLockIconName().equalsIgnoreCase("unlocked")) {
                            newLockIconName = "locked";
                        }
                        else {
                            newLockIconName = "unlocked";
                        }
                        listItem.setLockIconName(newLockIconName);

                        //set new image for lock icon based on new icon name
                        viewHolder.lockIconURI = "drawable/" + listItem.getLockIconName();
                        viewHolder.lockIconImageResource = context.getResources().getIdentifier(viewHolder.lockIconURI, null, context.getPackageName());
                        viewHolder.lockIconImage = context.getResources().getDrawable(viewHolder.lockIconImageResource);
                        viewHolder.lockIcon.setImageDrawable(viewHolder.lockIconImage);

                        //display text popup with file name + new lock icon name
                        Toast.makeText(context, listItem.getFileName() + ": " + listItem.getLockIconName(), Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }

        return convertView;
    }
}

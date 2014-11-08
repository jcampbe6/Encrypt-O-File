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
 * Created by Joshua on 10/9/2014.
 */
public class FileArrayAdapter extends BaseAdapter {

    private static final int DIRECTORY_VIEW = 0;
    private static final int FILE_VIEW = 1;
    private static final int MAX_VIEW_TYPES = 2;

    private final Activity context;
    private static final List<FileListItem> items = new ArrayList<FileListItem>();
    private LayoutInflater inflater;
    private TreeSet fileSet = new TreeSet();

    public FileArrayAdapter(Activity newContext) {
        context = newContext;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addDirectoryItem(int position, FileListItem item) {
        items.add(position, item);
    }

    public void addDirectoryItems(List<FileListItem> list) {
        for (FileListItem item: list) {
            items.add(item);
            notifyDataSetChanged();
        }
    }

    public void addFileItems(final List<FileListItem> list) {
        for (FileListItem item: list) {
            items.add(item);

            //save file position
            fileSet.add(items.size());
            notifyDataSetChanged();
        }
    }

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
    public int getItemViewType(int position) {
        int viewType;
        if (fileSet.contains(position)) {
            viewType = FILE_VIEW;
        }
        else {
            viewType = DIRECTORY_VIEW;
        }
        return viewType;//fileSet.contains(position) ? FILE_VIEW : DIRECTORY_VIEW;
    }

    @Override
    public FileListItem getItem(int position) {
        return items.get(position);
    }

    static class ViewHolder {
        protected TextView fileTitle;
        protected TextView fileSize;
        protected TextView dateModified;

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
                case DIRECTORY_VIEW:
                    convertView = inflater.inflate(R.layout.activity_file_browser_directory, null);
                    viewHolder.fileIcon = (ImageView) convertView.findViewById(R.id.directoryIcon);
                    viewHolder.fileTitle = (TextView) convertView.findViewById(R.id.directoryTitle);
                    viewHolder.fileSize = (TextView) convertView.findViewById(R.id.directorySize);
                    viewHolder.dateModified = (TextView) convertView.findViewById(R.id.dateModifiedDirectory);
                    break;

                case FILE_VIEW:
                    convertView = inflater.inflate(R.layout.activity_file_browser_file, null);
                    viewHolder.fileIcon = (ImageView) convertView.findViewById(R.id.fileIcon);
                    viewHolder.fileTitle = (TextView) convertView.findViewById(R.id.fileTitle);
                    viewHolder.fileSize = (TextView) convertView.findViewById(R.id.fileSize);
                    viewHolder.dateModified = (TextView) convertView.findViewById(R.id.dateModifiedFile);
                    viewHolder.lockIcon = (ImageView) convertView.findViewById(R.id.lockIcon);
                    break;
            }

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.fileIconURI = "drawable/" + listItem.getFileIconName();
        viewHolder.fileIconImageResource = context.getResources().getIdentifier(viewHolder.fileIconURI, null, context.getPackageName());
        viewHolder.fileIconImage = context.getResources().getDrawable(viewHolder.fileIconImageResource);
        viewHolder.fileIcon.setImageDrawable(viewHolder.fileIconImage);

        viewHolder.fileTitle.setText(listItem.getFileName());
        viewHolder.fileSize.setText(listItem.getData());
        viewHolder.dateModified.setText(listItem.getDate());

        switch (type) {
            case DIRECTORY_VIEW:
                break;
            case FILE_VIEW:
                viewHolder.lockIconURI = "drawable/" + listItem.getLockIconName();
                viewHolder.lockIconImageResource = context.getResources().getIdentifier(viewHolder.lockIconURI, null, context.getPackageName());
                viewHolder.lockIconImage = context.getResources().getDrawable(viewHolder.lockIconImageResource);
                viewHolder.lockIcon.setImageDrawable(viewHolder.lockIconImage);

                viewHolder.lockIcon.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String newLockIconName = listItem.getLockIconName().equalsIgnoreCase("unlocked") ? "locked" : "unlocked";
                        listItem.setLockIconName(newLockIconName);

                        viewHolder.lockIconURI = "drawable/" + listItem.getLockIconName();
                        viewHolder.lockIconImageResource = context.getResources().getIdentifier(viewHolder.lockIconURI, null, context.getPackageName());
                        viewHolder.lockIconImage = context.getResources().getDrawable(viewHolder.lockIconImageResource);
                        viewHolder.lockIcon.setImageDrawable(viewHolder.lockIconImage);

                        Toast.makeText(context, listItem.getFileName() + ": " + listItem.getLockIconName(), Toast.LENGTH_LONG).show();
                    }
                });
                break;
        }

        return convertView;
    }
}

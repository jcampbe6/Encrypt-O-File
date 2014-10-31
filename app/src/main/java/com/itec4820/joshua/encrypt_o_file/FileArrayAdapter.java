package com.itec4820.joshua.encrypt_o_file;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Joshua on 10/9/2014.
 */
public class FileArrayAdapter extends ArrayAdapter<FileListItem> {

    private Context context;
    private int id;
    private List<FileListItem> items;

    public FileArrayAdapter(Context newContext, int textViewResourceId, List<FileListItem> newItems) {
        super(newContext, textViewResourceId, newItems);
        context = newContext;
        id = textViewResourceId;
        items = newItems;
    }
    public FileListItem getItem(int i)
    {
        return items.get(i);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(id, null);
        }

               /* create a new view of my layout and inflate it in the row */
        //convertView = ( RelativeLayout ) inflater.inflate( resource, null );

        final FileListItem listItem = items.get(position);
        if (listItem != null) {
            TextView t1 = (TextView) v.findViewById(R.id.fileTitle);
            TextView t2 = (TextView) v.findViewById(R.id.fileSize);
            TextView t3 = (TextView) v.findViewById(R.id.textView3);
                       /* Take the ImageView from layout and set the city's image */
            ImageView imageCity = (ImageView) v.findViewById(R.id.fileIcon);
            String uri = "drawable/" + listItem.getImage();
            int imageResource = context.getResources().getIdentifier(uri, null, context.getPackageName());
            Drawable image = context.getResources().getDrawable(imageResource);
            imageCity.setImageDrawable(image);

            if(t1!=null)
                t1.setText(listItem.getName());
            if(t2!=null)
                t2.setText(listItem.getData());
            if(t3!=null)
                t3.setText(listItem.getDate());
        }
        return v;
    }
}

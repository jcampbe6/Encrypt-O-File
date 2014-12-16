package com.itec4820.joshua.encrypt_o_file;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
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

    private final Activity activity;
    private static final List<FileListItem> items = new ArrayList<FileListItem>();
    private LayoutInflater inflater;
    private TreeSet<Integer> fileSet = new TreeSet<Integer>();

    /**
     * Constructor: FileArrayAdapter
     * @param activity the Activity
     */
    public FileArrayAdapter(Activity activity) {
        this.activity = activity;
        inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

            //save file position in list
            fileSet.add(items.size()-1);
            notifyDataSetChanged();
        }
    }

    /**
     * Method: removeFileItem
     * @param position
     * Removes the list item at the specified position.
     */
    public void removeFileItem(int position) {
        fileSet.remove(items.size()-1);
        items.remove(position);
        notifyDataSetChanged();
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
        viewHolder.fileIconImageResource = activity.getResources().getIdentifier(viewHolder.fileIconURI, null, activity.getPackageName());
        viewHolder.fileIconImage = activity.getResources().getDrawable(viewHolder.fileIconImageResource);
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
                viewHolder.lockIconImageResource = activity.getResources().getIdentifier(viewHolder.lockIconURI, null, activity.getPackageName());
                viewHolder.lockIconImage = activity.getResources().getDrawable(viewHolder.lockIconImageResource);
                viewHolder.lockIcon.setImageDrawable(viewHolder.lockIconImage);

                final View thisItem = convertView;

                viewHolder.lockIcon.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // encrypt
                        if (listItem.getLockIconName().equalsIgnoreCase("unlocked")) {
                            // build encryption dialog box
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity, AlertDialog.THEME_HOLO_DARK);

                            // custom title for dialog
                            TextView title = new TextView(activity);
                            title.setText("Encrypt " + listItem.getFileName() + "?");
                            title.setPadding(10, 10, 10, 10);
                            title.setTextColor(Color.parseColor("#FF33b5e5"));
                            title.setSingleLine(false);
                            title.setTextSize(20);

                            // custom layout view for dialog
                            final View encryptDialogView = inflater.inflate(R.layout.confirm_encryption_dialog, parent, false);

                            builder.setCustomTitle(title).setView(encryptDialogView);
                            final AlertDialog encryptDialog = builder.create();

                            Button encryptButton = (Button) encryptDialogView.findViewById(R.id.encrypt_button);
                            Button cancelEncryptionButton = (Button) encryptDialogView.findViewById(R.id.cancel_encryption_button);

                            final EditText passwordText = (EditText) encryptDialogView.findViewById(R.id.encrypt_password);
                            final EditText confirmPasswordText = (EditText) encryptDialogView.findViewById(R.id.confirm_encrypt_password);

                            // when password text changes, change color of confirm password
                            // text to red if passwords don't match or to green if they do match
                            passwordText.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}
                                @Override
                                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}

                                @Override
                                public void afterTextChanged(Editable password) {
                                    if (!password.toString().equals(confirmPasswordText.getText().toString())) {
                                        confirmPasswordText.setTextColor(Color.parseColor("#e30000"));
                                    }
                                    else
                                    {
                                        confirmPasswordText.setTextColor(Color.parseColor("#00ff00"));
                                    }
                                }
                            });

                            // when confirm password text changes, change color of confirm password
                            // text to red if passwords don't match or to green if they do match
                            confirmPasswordText.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}
                                @Override
                                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}

                                @Override
                                public void afterTextChanged(Editable confirmPassword) {
                                    if (!confirmPassword.toString().equals(passwordText.getText().toString())) {
                                        confirmPasswordText.setTextColor(Color.parseColor("#e30000"));
                                    }
                                    else
                                    {
                                        confirmPasswordText.setTextColor(Color.parseColor("#00ff00"));
                                    }
                                }
                            });

                            // listens for encrypt button click and then performs appropriate action
                            encryptButton.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // retrieve password from user input
                                    final String password = passwordText.getText().toString();
                                    final String confirmPassword = confirmPasswordText.getText().toString();

                                    if (password.length() == 0) {
                                        EncryptOToaster.makeText(activity, "Please enter a password.", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    if (!confirmPassword.equals(password)) {
                                        EncryptOToaster.makeText(activity, "Passwords don't match.", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    // executes encryption on a separate thread if password
                                    // isn't empty and password matches confirmation password
                                    new EncryptorTask(activity) {
                                        // performs encryption
                                        protected String doEncryptorTask() {
                                            return Encryptor.encrypt(password, listItem.getPath(), listItem.getFileName());
                                        }

                                        // updates the user interface with new list item
                                        protected void updateUI(final String filePath) {
                                            // initialize object animator
                                            final ObjectAnimator translateAnimation = new ObjectAnimator();

                                            // get display with for object animation
                                            DisplayMetrics metrics=new DisplayMetrics();
                                            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
                                            float displayWidth = metrics.widthPixels;

                                            // animates list item view
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                                                // Move the list item view over to the right
                                                translateAnimation.setTarget(thisItem);
                                                translateAnimation.setProperty(View.TRANSLATION_X);
                                                translateAnimation.setFloatValues(displayWidth);
                                                translateAnimation.setDuration(300);
                                                translateAnimation.start();
                                            }

                                            new CountDownTimer(700, 700) {
                                                @Override
                                                public void onTick(long l) {} // do nothing

                                                public void onFinish() {
                                                    // retrieves encrypted file from file path
                                                    File encryptedFile = new File(filePath);

                                                    // sets list item properties and creates new list item from encrypted file
                                                    String size = FileUtility.formatFileSize(encryptedFile);
                                                    String modifiedDate = FileUtility.formatDateTime(encryptedFile);
                                                    String fileIconName = FileUtility.setFileIconName(encryptedFile);
                                                    String lockIconName = FileUtility.setLockIconName(encryptedFile);
                                                    FileListItem newListItem = new FileListItem(encryptedFile.getName(), size, modifiedDate, encryptedFile.getAbsolutePath(), fileIconName, lockIconName);

                                                    // replaces decrypted list item with encrypted list item
                                                    items.set(position, newListItem);
                                                    notifyDataSetChanged();

                                                    // sets lock icon to "locked" based on current file extension
                                                    if (listItem.getFileName().endsWith(".encx")) {
                                                        listItem.setLockIconName("locked");
                                                        viewHolder.lockIconURI = "drawable/" + listItem.getLockIconName();
                                                        viewHolder.lockIconImageResource = activity.getResources().getIdentifier(viewHolder.lockIconURI, null, activity.getPackageName());
                                                        viewHolder.lockIconImage = activity.getResources().getDrawable(viewHolder.lockIconImageResource);
                                                        viewHolder.lockIcon.setImageDrawable(viewHolder.lockIconImage);
                                                    }

                                                    // moves new list item in from the left
                                                    new CountDownTimer(200, 200) {
                                                        @Override
                                                        public void onTick(long l) {} // do nothing

                                                        public void onFinish() {
                                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                                                                translateAnimation.reverse();
                                                            }
                                                        }
                                                    }.start();
                                                }
                                            }.start();
                                        }
                                    }.execute();

                                    encryptDialog.dismiss();
                                }
                            });

                            // closes dialog when cancel button is clicked
                            cancelEncryptionButton.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    encryptDialog.dismiss();
                                }
                            });

                            encryptDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                            encryptDialog.show();
                        }

                        // decrypt
                        else {
                            // build encryption dialog box
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity, AlertDialog.THEME_HOLO_DARK);

                            // custom title for dialog
                            TextView title = new TextView(activity);
                            title.setText("Decrypt " + listItem.getFileName() + "?");
                            title.setPadding(10, 10, 10, 10);
                            title.setTextColor(Color.parseColor("#FF33b5e5"));
                            title.setSingleLine(false);
                            title.setTextSize(20);

                            // custom layout view for dialog
                            final View decryptDialogView = inflater.inflate(R.layout.confirm_decryption_dialog, parent, false);

                            builder.setCustomTitle(title)
                                    .setView(decryptDialogView);

                            final AlertDialog decryptDialog = builder.create();

                            Button decryptButton = (Button) decryptDialogView.findViewById(R.id.decrypt_button);
                            Button cancelDecryptionButton = (Button) decryptDialogView.findViewById(R.id.cancel_decryption_button);

                            // listens for decrypt button click and then performs appropriate action
                            decryptButton.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // retrieve password from user input
                                    EditText decryptPasswordText = (EditText) decryptDialogView.findViewById(R.id.decrypt_password);
                                    decryptPasswordText.requestFocus();
                                    final String password = decryptPasswordText.getText().toString();

                                    if (password.length() == 0) {
                                        EncryptOToaster.makeText(activity, "Please enter a password.", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    // executes decryption on a separate thread
                                    new EncryptorTask(activity) {
                                        // performs decryption
                                        protected String doEncryptorTask() {
                                            return Encryptor.decrypt(password, listItem.getPath());
                                        }

                                        // updates the user interface with new list item
                                        protected void updateUI(final String filePath) {
                                            // initialize object animator
                                            final ObjectAnimator translateAnimation = new ObjectAnimator();

                                            // get display with for object animation
                                            DisplayMetrics metrics=new DisplayMetrics();
                                            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
                                            float displayWidth = metrics.widthPixels;

                                            // animates list item view
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                                                // Moves the list item view over to the right
                                                translateAnimation.setTarget(thisItem);
                                                translateAnimation.setProperty(View.TRANSLATION_X);
                                                translateAnimation.setFloatValues(displayWidth);
                                                translateAnimation.setDuration(300);
                                                translateAnimation.start();
                                            }

                                            new CountDownTimer(700, 700) {
                                                @Override
                                                public void onTick(long l) {} // do nothing

                                                public void onFinish() {
                                                    // retrieves decrypted file from file path
                                                    File decryptedFile = new File(filePath);

                                                    // sets list item properties and creates new list item from decrypted file
                                                    String size = FileUtility.formatFileSize(decryptedFile);
                                                    String modifiedDate = FileUtility.formatDateTime(decryptedFile);
                                                    String fileIconName = FileUtility.setFileIconName(decryptedFile);
                                                    String lockIconName = FileUtility.setLockIconName(decryptedFile);
                                                    FileListItem newListItem = new FileListItem(decryptedFile.getName(), size, modifiedDate, decryptedFile.getAbsolutePath(), fileIconName, lockIconName);

                                                    // replaces encrypted list item with decrypted list item
                                                    items.set(position, newListItem);
                                                    notifyDataSetChanged();

                                                    //sets lock icon to "unlocked" based on current file extension
                                                    if (!listItem.getFileName().endsWith(".encx")) {
                                                        listItem.setLockIconName("unlocked");
                                                        viewHolder.lockIconURI = "drawable/" + listItem.getLockIconName();
                                                        viewHolder.lockIconImageResource = activity.getResources().getIdentifier(viewHolder.lockIconURI, null, activity.getPackageName());
                                                        viewHolder.lockIconImage = activity.getResources().getDrawable(viewHolder.lockIconImageResource);
                                                        viewHolder.lockIcon.setImageDrawable(viewHolder.lockIconImage);
                                                    }

                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                                                        // moves new list item in from the left
                                                        new CountDownTimer(200, 200) {
                                                            @Override
                                                            public void onTick(long l) {
                                                            } // do nothing

                                                            public void onFinish() {
                                                                translateAnimation.reverse();
                                                            }
                                                        }.start();
                                                    }
                                                }
                                            }.start();
                                        }
                                    }.execute();

                                    decryptDialog.dismiss();
                                }
                            });

                            // closes dialog when cancel button is clicked
                            cancelDecryptionButton.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    decryptDialog.dismiss();
                                }
                            });

                            decryptDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                            decryptDialog.show();
                        }
                    }
                });
                break;
        }

        return convertView;
    }





}

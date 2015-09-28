package com.itsmeolivia.ribbit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.parse.ParseUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends ActionBarActivity {

    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int PICK_PHOTO_REQUEST = 1;
    public static final int TAKE_VIDEO_REQUEST = 2;
    public static final int PICK_VIDEO_REQUEST = 3;

    public static final int MEDIA_TYPE_IMAGE = 4;
    public static final int MEDIA_TYPE_VIDEO = 5;

    public static final int FILE_SIZE_LIMIT = 1024*1024*10;

    protected Uri mMediaUri;


    protected DialogInterface.OnClickListener mDialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case 0: //take pic
                    Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

                    if (mMediaUri == null) {
                        //display error
                        Toast.makeText(MainActivity.this, R.string.external_storage_error, Toast.LENGTH_LONG).show();
                    }
                    else {
                        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                        startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);
                    }
                    break;
                case 1: //choose pic

                    Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    choosePhotoIntent.setType("image/*");
                    startActivityForResult(choosePhotoIntent, PICK_PHOTO_REQUEST);

                    break;
                case 2: //take vid
                    Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);

                    if (mMediaUri == null) {
                        //display error
                        Toast.makeText(MainActivity.this, R.string.external_storage_error, Toast.LENGTH_LONG).show();
                    }
                    else {
                        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
                        takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                        startActivityForResult(takeVideoIntent, TAKE_VIDEO_REQUEST);
                    }

                    break;
                case 3: //choose vid
                    Intent chooseVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    chooseVideoIntent.setType("video/*");
                    Toast.makeText(MainActivity.this, "Must be less than 10MB!", Toast.LENGTH_LONG).show();
                    startActivityForResult(chooseVideoIntent, PICK_VIDEO_REQUEST);
                    break;
                default:
                    break;
            }
        }
    };

    SectionsPagerAdapter mSectionsPagerAdapter;

    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser == null) {
            navigateToLogin();
        }

        else {

        }
        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();

        mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_PHOTO_REQUEST || requestCode == PICK_VIDEO_REQUEST) {

                if (data == null) {
                    Toast.makeText(this, getString(R.string.login_error_message), Toast.LENGTH_LONG).show();
                }
                else {
                    mMediaUri = data.getData();
                }

                if (requestCode == PICK_VIDEO_REQUEST) {

                    int fileSize = 0;
                    InputStream inputStream = null;
                    try {
                        inputStream = getContentResolver().openInputStream(mMediaUri);
                        fileSize = inputStream.available();

                    } catch (FileNotFoundException e) {
                        //make toast if u care
                        e.printStackTrace();
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                    finally {
                        try{
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        }
                    }

                    if (fileSize >= FILE_SIZE_LIMIT) {
                        Toast.makeText(this, "File too big, choose again!", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            }

            else {
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(mMediaUri);
                sendBroadcast(mediaScanIntent);
            }

        }
        else if (resultCode != RESULT_CANCELED) {
            Toast.makeText(this, getString(R.string.login_error_message), Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.action_logout: {
                ParseUser.logOut();
                navigateToLogin();
            }
            case R.id.edit_friends: {
                Intent intent = new Intent(this, EditFriendsActivity.class);
                startActivity(intent);
            }
            case R.id.action_camera: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setItems(R.array.camera_choices, mDialogListener);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            default:

        }

        return super.onOptionsItemSelected(item);
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private Uri getOutputMediaFileUri(int mediaType) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        if (isExternalStorageAvailable()) {
            String appName = MainActivity.this.getString(R.string.app_name);

            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), appName);
            if (! mediaStorageDir.exists()){
                if (! mediaStorageDir.mkdirs()){
                    Log.d(appName, "failed to create directory");
                    return null;
                }
            }

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            File mediaFile;
            if (mediaType == MEDIA_TYPE_IMAGE){
                mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                        "IMG_"+ timeStamp + ".jpg");
            } else if(mediaType == MEDIA_TYPE_VIDEO) {
                mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                        "VID_"+ timeStamp + ".mp4");
            } else {
                return null;
            }
            return Uri.fromFile(mediaFile);
        }

        else
            return null;

    }

    private boolean isExternalStorageAvailable() {

        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

}

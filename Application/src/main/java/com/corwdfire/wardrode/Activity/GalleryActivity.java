package com.corwdfire.wardrode.Activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.corwdfire.wardrode.Adapter.CustomSpinnerAdapter;
import com.corwdfire.wardrode.Adapter.GalleryGridAdapter;
import com.corwdfire.wardrode.R;
import com.corwdfire.wardrode.Model.ImageItem;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class GalleryActivity extends AppCompatActivity implements GalleryGridAdapter.OnItemClickListener {
    private static final int CAMERA_REQUEST = 100;
    private HashMap<String, List<ImageItem>> mImageListHashMap;
    private GalleryGridAdapter mImageGridAdapter;
    private boolean mIsNextEnabled;

    private Toolbar mToolbar;
    private Spinner mSpinnerNav;
    private GridView mImagesGridView;
    private ProgressBar mProgressBar;

    private String mCurrentPhotoPath;

    private float mColumnWidth;

    private ImageItem mSelectedImageItem;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_activity);
        checkIsNextEnabled();
        getWidgets();
        setUpActionBar();
        calculateScreenWidth();
        CursorLoaderAsyncTask mCursorLoaderAsyncTask = new CursorLoaderAsyncTask();
        mCursorLoaderAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void calculateScreenWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        int screenWidth = outMetrics.widthPixels;
        int spacing = (int) (20 * getResources().getDisplayMetrics().density);
        mColumnWidth = (screenWidth - spacing) / 3;
    }

    private void getWidgets() {
        mImagesGridView = (GridView) findViewById(R.id.images);
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mSpinnerNav = (Spinner) findViewById(R.id.spinner_nav);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
    }

    private void setUpActionBar() {
        setSupportActionBar(mToolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayShowTitleEnabled(false);
            actionbar.setHomeButtonEnabled(true);
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setTitle("");
        }
    }

    private void addItemsToSpinner() {
        mSpinnerNav.setAdapter(new CustomSpinnerAdapter(getApplicationContext(), new ArrayList<>(mImageListHashMap.keySet())));
        mSpinnerNav.setSelection(0, false);
        mSpinnerNav.setPadding(0, 0, 50, 0);
        mSpinnerNav.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String folderName = (String) view.findViewById(R.id.title_view).getTag();
                mImagesGridView.post(new Runnable() {
                    @Override
                    public void run() {
                        mImagesGridView.setSelection(0);
                    }
                });
                mImageGridAdapter.updateList(mImageListHashMap.get(folderName));
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private ImageItem getImageItem(String type) {
        ImageItem imageItem = new ImageItem();
        imageItem.setType(3);
        return imageItem;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.done:
                Intent intent = new Intent();
                intent.putExtra(getString(R.string.selected_image), mSelectedImageItem);
                setResult(RESULT_OK, intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(View view, int position, Object data) {
        switch (view.getId()) {
            case R.id.grid_item_view:
                if (position == 0) {
                    startCameraActivity();
                } else {
                    mSelectedImageItem = (ImageItem) data;
                }
                checkIsNextEnabled();
                break;
        }
    }

    private void checkIsNextEnabled() {
        mIsNextEnabled = mSelectedImageItem != null && mSelectedImageItem.getPosition() > 0;
        invalidateOptionsMenu();
    }


    private void startCameraActivity() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                    createImageFilePath());
            startActivityForResult(takePictureIntent, CAMERA_REQUEST);
        }
    }

    private Uri createImageFilePath() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File image = new File(Environment.getExternalStorageDirectory() + File.separator + "IMG_" + timeStamp + ".jpg");
        mCurrentPhotoPath = image.getAbsolutePath();
        return Uri.fromFile(image);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == CAMERA_REQUEST) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    addPicToGallery();
                }
            }, 1000);
        }
    }

    private void addPicToGallery() {
        MediaScannerConnection.scanFile(getApplicationContext(),
                new String[]{mCurrentPhotoPath},
                null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        ImageItem imageItem = new ImageItem();
                        imageItem.setImageUrl(Long.parseLong(uri.getLastPathSegment()));
                        Intent intent = new Intent();
                        intent.putExtra(getString(R.string.selected_image), imageItem);
                        setResult(RESULT_OK, intent);
                        finish();
                        // new CursorLoaderAsyncTask().execute();
                    }
                });
    }

    private List<ImageItem> queryAllImages() {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.TITLE, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATA};
        final String orderBy = MediaStore.Images.Media._ID + " DESC";
        Cursor cursor = getContentResolver().query(uri, projection, null, null, orderBy);
        mImageListHashMap = new LinkedHashMap<>();

        List<ImageItem> allImageList = new ArrayList<>();
        allImageList.add(getImageItem("camera"));
        mImageListHashMap.put("All", allImageList);

        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String folderName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                    List<ImageItem> folderWiseImages;
                    if (mImageListHashMap.containsKey(folderName)) {
                        folderWiseImages = mImageListHashMap.get(folderName);
                    } else {
                        folderWiseImages = new ArrayList<>();
                        folderWiseImages.add(getImageItem("camera"));
                    }
                    ImageItem imageItem = new ImageItem();
                    imageItem.setImageUrl(cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID)));

                    folderWiseImages.add(imageItem);
                    mImageListHashMap.put(folderName, folderWiseImages);
                    addImageToAllFolder(imageItem);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return mImageListHashMap.get("All");
    }

    private void addImageToAllFolder(ImageItem item) {
        List<ImageItem> allImageList = mImageListHashMap.get("All");
        allImageList.add(item);
        mImageListHashMap.put("All", allImageList);
    }

    private class CursorLoaderAsyncTask extends AsyncTask<Void, Void, List<ImageItem>> {
        @Override
        protected List<ImageItem> doInBackground(Void... voids) {
            return queryAllImages();
        }

        @Override
        protected void onPostExecute(List<ImageItem> imageItems) {
            if (imageItems != null) {
                mProgressBar.setVisibility(View.GONE);
                if (mImageGridAdapter == null) {
                    mImageGridAdapter = new GalleryGridAdapter(GalleryActivity.this, imageItems, GalleryActivity.this, mColumnWidth);
                    mImagesGridView.setAdapter(mImageGridAdapter);
                } else {
                    mImageGridAdapter.updateList(imageItems);
                }

                addItemsToSpinner();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.gallery_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.getItem(0).setEnabled(mIsNextEnabled);
        return true;
    }


    @Override
    public void onBackPressed() {
        finish();
    }

}



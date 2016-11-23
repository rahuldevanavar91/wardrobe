package com.corwdfire.wardrode.Activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.corwdfire.wardrode.Adapter.PagerAdapter;
import com.corwdfire.wardrode.Database.DatabaseHandler;
import com.corwdfire.wardrode.Notification.NotificationReceiver;
import com.corwdfire.wardrode.R;
import com.corwdfire.wardrode.Model.FavoriteItem;
import com.corwdfire.wardrode.Model.ImageItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Random;

/**
 * Created by rahul on 11/23/16.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    public static final int SHIRT_TYPE = 1;
    public static final int PANT_TYPE = 2;

    private ViewPager mPantsViewPager;
    private ViewPager mShirtsViewPager;
    private ArrayList<ImageItem> mShirtsList;
    private ArrayList<ImageItem> mPantsList;
    private ArrayList<FavoriteItem> mFavoriteList;

    private int mPhotoRequestPosition;
    private PagerAdapter mPantPagerAdapter;
    private PagerAdapter mShirtPageAdapter;
    private DatabaseHandler mDatabaseHandler;
    private ImageView mFavoriteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabaseHandler = new DatabaseHandler(this);
        getWidgets();
        if (savedInstanceState != null) {
            mShirtsList = savedInstanceState.getParcelableArrayList(getString(R.string.saved_shirt_list));
            mPantsList = savedInstanceState.getParcelableArrayList(getString(R.string.saved_pant_list));
            mFavoriteList = savedInstanceState.getParcelableArrayList(getString(R.string.favorite_list));
            setAdapter();
        } else {
            setList();
            setAdapter();
            setNotification();
        }
    }

    private void setList() {
        mPantsList = mDatabaseHandler.getCloths(PANT_TYPE);
        mShirtsList = mDatabaseHandler.getCloths(SHIRT_TYPE);
        mFavoriteList = mDatabaseHandler.getFavorites();
        mShirtsList.add(addDummyPhoto(SHIRT_TYPE, mShirtsList.size()));
        mPantsList.add(addDummyPhoto(PANT_TYPE, mPantsList.size()));
    }

    private void setAdapter() {
        mPantPagerAdapter = new PagerAdapter(getSupportFragmentManager(), mPantsList);
        mPantsViewPager.setAdapter(mPantPagerAdapter);
        mShirtPageAdapter = new PagerAdapter(getSupportFragmentManager(), mShirtsList);
        mShirtsViewPager.setAdapter(mShirtPageAdapter);
        long pantId = mPantsList.get(mPantsViewPager.getCurrentItem()).getId();
        long shirtId = mShirtsList.get(mShirtsViewPager.getCurrentItem()).getId();
        if (isFavorite(pantId, shirtId)) {
            mFavoriteButton.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_favorite_black));
        } else {
            mFavoriteButton.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_favorite_border_black));
        }
        mPantsViewPager.setCurrentItem(getIntent().getIntExtra(getString(R.string.pant_random_num), 0));
        mShirtsViewPager.setCurrentItem(getIntent().getIntExtra(getString(R.string.shirt_random_num), 0));

    }

    private boolean isFavorite(long pantId, long shirtId) {
        if (mFavoriteList == null)
            return false;

        FavoriteItem favoriteItem = new FavoriteItem();
        favoriteItem.setShirtId(shirtId);
        favoriteItem.setPantId(pantId);
        return mFavoriteList.contains(favoriteItem);
    }


    private void getWidgets() {
        mPantsViewPager = (ViewPager) findViewById(R.id.pants_view_pager);
        mShirtsViewPager = (ViewPager) findViewById(R.id.shirts_view_pager);
        mFavoriteButton = (ImageView) findViewById(R.id.favorite_button);
        ImageView shuffleButton = (ImageView) findViewById(R.id.suffe_button);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setHomeButtonEnabled(true);
            actionbar.setDisplayHomeAsUpEnabled(true);
        }
        shuffleButton.setOnClickListener(this);
        mFavoriteButton.setOnClickListener(this);
        mPantsViewPager.addOnPageChangeListener(this);
        mShirtsViewPager.addOnPageChangeListener(this);
    }

    private ImageItem addDummyPhoto(int shirtType, int position) {
        ImageItem imageItem = new ImageItem();
        imageItem.setType(shirtType);
        imageItem.setPosition(position);
        return imageItem;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            ImageItem imageItem = data.getParcelableExtra(getString(R.string.selected_image));
            imageItem.setType(requestCode);
            if (requestCode == SHIRT_TYPE) {
                long id = addPhotoToDb(imageItem);
                imageItem.setId(id);
                mShirtsList.add(mPhotoRequestPosition, imageItem);
                mShirtPageAdapter.updateList(mShirtsList);

            } else if (requestCode == PANT_TYPE) {
                long id = addPhotoToDb(imageItem);
                imageItem.setId(id);
                mPantsList.add(mPhotoRequestPosition, imageItem);
                mPantPagerAdapter.updateList(mPantsList);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private long addPhotoToDb(ImageItem imageItem) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHandler.IMAGE_URL, imageItem.getImageUrl());
        contentValues.put(DatabaseHandler.CLOTH_TYPE, imageItem.getType());
        contentValues.put(DatabaseHandler.POSITION, mPhotoRequestPosition);
        return mDatabaseHandler.addCloths(contentValues);
    }

    public void startGalleryActivity(int position, int pantAddPhotoRequest) {
        mPhotoRequestPosition = position;
        Intent intent = new Intent(this, GalleryActivity.class);
        startActivityForResult(intent, pantAddPhotoRequest);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.favorite_button:
                ImageItem shirtItem = mShirtsList.get(mShirtsViewPager.getCurrentItem());
                ImageItem pantItem = mPantsList.get(mPantsViewPager.getCurrentItem());
                if (shirtItem.getImageUrl() == 0) {
                    Toast.makeText(this, getString(R.string.please_add_shirt), Toast.LENGTH_SHORT).show();
                } else if (pantItem.getImageUrl() == 0) {
                    Toast.makeText(this, getString(R.string.please_add_pant), Toast.LENGTH_SHORT).show();
                } else {
                    FavoriteItem favoriteItem = new FavoriteItem();
                    favoriteItem.setPantId(pantItem.getId());
                    favoriteItem.setShirtId(shirtItem.getId());
                    if (mFavoriteList.contains(favoriteItem)) {
                        mFavoriteList.remove(favoriteItem);
                        mDatabaseHandler.removeFavorite(shirtItem.getId(), pantItem.getId());
                        mFavoriteButton.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_favorite_border_black));
                        Toast.makeText(this, getString(R.string.removed_from_favorite), Toast.LENGTH_SHORT).show();
                    } else {
                        mFavoriteList.add(favoriteItem);
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(DatabaseHandler.FAVORITE_PANT_ID, pantItem.getId());
                        contentValues.put(DatabaseHandler.FAVORITE_SHIRT_ID, shirtItem.getId());
                        mFavoriteButton.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_favorite_black));
                        Toast.makeText(this, getString(R.string.added_two_wish_lsit), Toast.LENGTH_SHORT).show();
                        mDatabaseHandler.addFavorites(contentValues);
                    }
                }
                break;
            case R.id.suffe_button:
                shuffleList();
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            ImageItem shirtImageItem = mShirtsList.get(mShirtsViewPager.getCurrentItem());
            ImageItem pantImageItem = mPantsList.get(mPantsViewPager.getCurrentItem());
            if (isFavorite(pantImageItem.getId(), shirtImageItem.getId())) {
                mFavoriteButton.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_favorite_black));
            } else {
                mFavoriteButton.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_favorite_border_black));
            }
        }
    }

    private void setNotification() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, NotificationReceiver.class);
        alarmIntent.putExtra(getString(R.string.shirt_random_num), new Random().nextInt(mShirtsList.size()));
        alarmIntent.putExtra(getString(R.string.pant_random_num), new Random().nextInt(mPantsList.size()));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pendingIntent);

        Calendar alarmStartTime = Calendar.getInstance();
        alarmStartTime.set(Calendar.HOUR_OF_DAY, 6);
        alarmStartTime.set(Calendar.MINUTE, 03);
        alarmStartTime.set(Calendar.SECOND, 0);
        if (Calendar.getInstance().after(alarmStartTime)) {
            alarmStartTime.add(Calendar.DATE, 1);

        }
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

    }

    private void shuffleList() {
        Collections.shuffle(mPantsList);
        Collections.shuffle(mShirtsList);
        mPantPagerAdapter.updateList(mPantsList);
        mShirtPageAdapter.updateList(mShirtsList);
        long pantId = mPantsList.get(mPantsViewPager.getCurrentItem()).getId();
        long shirtId = mShirtsList.get(mShirtsViewPager.getCurrentItem()).getId();
        if (isFavorite(pantId, shirtId)) {
            mFavoriteButton.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_favorite_black));
        } else {
            mFavoriteButton.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_favorite_border_black));
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(getString(R.string.saved_shirt_list), mShirtsList);
        outState.putParcelableArrayList(getString(R.string.saved_pant_list), mPantsList);
        outState.putParcelableArrayList(getString(R.string.favorite_list), mFavoriteList);
    }

}

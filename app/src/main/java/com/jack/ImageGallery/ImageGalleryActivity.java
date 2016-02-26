package com.jack.ImageGallery;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jack.ImageGallery.Control.ImageGalleryController;
import com.jack.ImageGallery.MyUtil.ImageGalleryPagerAdapter;
import com.jack.ImageGallery.MyUtil.Util;
import com.jack.ImageGallery.PageTransformers.DepthPageTransformer;
import com.jack.ImageGallery.PageTransformers.ZoomOutPageTransformer;

public class ImageGalleryActivity extends AppCompatActivity
        implements
                SharedPreferences.OnSharedPreferenceChangeListener,
                ImageGalleryController.OnImageGalleryListener {
    private final String TAG = ImageGalleryActivity.class.getSimpleName();

    private ViewPager viewPager;
    private ImageGalleryPagerAdapter adapter;
    private Handler handler = new Handler();

    private ImageGalleryController imageGalleryController;

    private boolean favouritesMode;
    private boolean shuffleMode;
    private int slideshowInterval;

    @Override
    public void updateAdapter() {
        adapter.notifyDataSetChanged();
    }
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String setting_cb_shuffle_mode = getString(R.string.setting_cb_shuffle_mode);
        String setting_et_slideshow_interval = getString(R.string.setting_et_slideshow_interval);
        String setting_cb_favourites_mode = getString(R.string.setting_cb_favourites_mode);
        String setting_lp_animation_list = getString(R.string.setting_lp_animation_list);

        if ( key.equals(setting_cb_shuffle_mode)) {
            shuffleMode = sharedPreferences.getBoolean(setting_cb_shuffle_mode, false);
            setShuffleMode(shuffleMode);
        }
        else if ( key.equals(setting_et_slideshow_interval)) {
            String intervalString = sharedPreferences.getString(
                    setting_et_slideshow_interval,
                    getString(R.string.setting_et_slideshow_interval_default));

            // Can't unregisterOnSharedPreferenceChangeListener into onPause(), then :
            if (Util.isDigit(intervalString)) {
                slideshowInterval = Integer.valueOf(intervalString);
            }
        }
        else if ( key.equals(setting_cb_favourites_mode)) {
            if ( favouritesMode = sharedPreferences.getBoolean(setting_cb_favourites_mode, false)) {
                adapter.updateAdapter(imageGalleryController.getImagesCollection().getArrayFavouritesImages());
                adapter.notifyDataSetChanged();
            }
            else {
                adapter.updateAdapter(imageGalleryController.getImagesCollection().getArrayImages());
                adapter.notifyDataSetChanged();
            }
        }
        else if ( key.equals(setting_lp_animation_list)) {
            String animation = sharedPreferences.getString(
                    setting_lp_animation_list,
                    getString(R.string.setting_lp_animation_list_default));
            setAnimation(animation);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_general, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ( item.getItemId()) {
            case R.id.action_add_to_favourites :
                initDialogFavourites();
                return true;
            case R.id.action_settings :
                startActivity( new Intent( getApplicationContext(), SettingsActivity.class));
                return true;
            case R.id.action_exit :
                finish();
                return true;
            default :
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, " onCreate() ");

        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences( getApplicationContext());
        prefs.registerOnSharedPreferenceChangeListener(this);
        shuffleMode = prefs.getBoolean(
                getString(R.string.setting_cb_shuffle_mode),
                false);
        favouritesMode = prefs.getBoolean(
                getString(R.string.setting_cb_favourites_mode),
                false);
        String animation = prefs.getString(
                getString(R.string.setting_lp_animation_list),
                getString(R.string.setting_lp_animation_list_default)
        );
        String intervalString = prefs.getString(
                getString(R.string.setting_et_slideshow_interval),
                getString(R.string.setting_et_slideshow_interval_default));
        if (Util.isDigit(intervalString)) {
            slideshowInterval = Integer.valueOf( intervalString);
        }

        setContentView(R.layout.activity_image_gallery);

        initViews();

        imageGalleryController = new ImageGalleryController(this);
        if ( favouritesMode)
            adapter = new ImageGalleryPagerAdapter(
                    this,
                    imageGalleryController.getImagesCollection().getArrayFavouritesImages(),
                    shuffleMode
             );
        else
            adapter = new ImageGalleryPagerAdapter(
                    this,
                    imageGalleryController.getImagesCollection().getArrayImages(),
                    shuffleMode
            );
        viewPager.setAdapter(adapter);
        setAnimation(animation);
        setShuffleMode(shuffleMode);
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences( getApplicationContext());

        // Slideshow mode
        if ( prefs.getBoolean(getString(R.string.setting_cb_slideshow_mode), false) ) {
            startSlideshow();
        }
        else
            stopSlideshow();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopSlideshow();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences( getApplicationContext());
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    private void initViews() {
        viewPager = (ViewPager) findViewById(R.id.vpGallery);
    }

    private Runnable nextImageTask = new Runnable() {
        public void run() {
            if ( viewPager != null )
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
            changeItemViewPager();
        }
    };
    private void changeItemViewPager( ) {
        if ( viewPager != null && adapter.getCount() > 1) {
            int count = adapter.getCount();
            int position = viewPager.getCurrentItem();
            if ( position < count - 1) {
                handler.postDelayed(nextImageTask, slideshowInterval*1000);
            }
            else {
                SharedPreferences prefs =
                        PreferenceManager.getDefaultSharedPreferences( getApplicationContext());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(getString(R.string.setting_cb_slideshow_mode), false );
                editor.apply();
                stopSlideshow();
                Toast.makeText(ImageGalleryActivity.this, "Slideshow disabled", Toast.LENGTH_SHORT).show();
            }
        }
        else
            Log.e(TAG, "changeItemViewPager:" + " viewPager null");
    }
    private void startSlideshow() {
        Toast.makeText(ImageGalleryActivity.this, "Slideshow started (drag mode disabled)", Toast.LENGTH_SHORT).show();
        viewPager.beginFakeDrag();
        changeItemViewPager();
    }
    private void stopSlideshow() {
        if (viewPager.isFakeDragging())
            viewPager.endFakeDrag();
        handler.removeCallbacks(nextImageTask);
    }

    private void setShuffleMode(boolean mode) {
        adapter.setShuffleMode(mode);
        adapter.notifyDataSetChanged();
    }

    private void initDialogFavourites() {
        int itemId = viewPager.getCurrentItem();
        final long imageId = adapter.getImageId(itemId);
        if ( imageId != -1) {
            AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater li = LayoutInflater.from(this);
            final View dialogView = li.inflate(R.layout.dialog_favourites, null);
            mDialogBuilder.setView(dialogView);

            mDialogBuilder
                    .setCancelable(true)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    EditText userInput = (EditText) dialogView.findViewById(R.id.etComment);
                                    imageGalleryController.addToFavourites(
                                            (int) imageId,
                                            userInput.getText().toString()
                                    );
                                }
                            })
                    .setNegativeButton("Отмена",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            }
                    );
            AlertDialog alertDialog = mDialogBuilder.create();
            alertDialog.show();
        }
        else
            Toast.makeText( ImageGalleryActivity.this, "Nothing to add", Toast.LENGTH_LONG).show();
    }

    private void setAnimation(String animation) {
        String[] animationArray = getResources().getStringArray(R.array.animation_array);
        if ( animation.equals(animationArray[1]))
            viewPager.setPageTransformer(true, new DepthPageTransformer());
        else if ( animation.equals(animationArray[2]))
            viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        else
            viewPager.setPageTransformer(true, null);
    }
}
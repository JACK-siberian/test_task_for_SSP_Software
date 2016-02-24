package com.jack.ImageGallery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.jack.ImageGallery.MyUtil.ImageGalleryPagerAdapter;
import com.jack.ImageGallery.MyUtil.JsonHelper;
import com.jack.ImageGallery.MyUtil.Util;
import com.jack.ImageGallery.Objects.ImagesCollection;

public class ImageGalleryActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    private final String TAG = ImageGalleryActivity.class.getSimpleName();

    private ViewPager viewPager;
    private Handler handler = new Handler();
    private ImagesCollection imagesCollection;

    private boolean shuffleMode;
    private int slideshowInterval;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_general, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ( item.getItemId()) {
            case R.id.action_add_to_favourites :
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
        shuffleMode = prefs.getBoolean(getString(R.string.setting_cb_shuffle_mode), false);
        String intervalString = prefs.getString(
                getString(R.string.setting_et_slideshow_interval),
                getString(R.string.setting_et_slideshow_interval_default));
        if (Util.isDigit(intervalString)) {
            slideshowInterval = Integer.valueOf( intervalString);
        }

        setContentView(R.layout.activity_image_gallery);

        initViews();

        imagesCollection = new ImagesCollection(new JsonHelper( this,"image_list.json").getImageArray());
        setAdapterWithShuffleMode(shuffleMode);
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
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Log.e(TAG, "OnPageSelected");
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //Log.e(TAG, "OnPageScrollStateChanged");
            }
        });
    }

    private Runnable nextImageTask = new Runnable() {
        public void run() {
            if ( viewPager != null )
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
            changeItemViewPager();
        }
    };
    private void changeItemViewPager( ) {
        if ( viewPager != null ) {
            int count = viewPager.getAdapter().getCount();
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
            Log.e(TAG, "changeItemViewPager" + " viewPager null");
    }
    public void startSlideshow() {
        Toast.makeText(ImageGalleryActivity.this, "Slideshow started (drag mode disabled)", Toast.LENGTH_SHORT).show();
        viewPager.beginFakeDrag();
        changeItemViewPager();
    }
    public void stopSlideshow() {
        if (viewPager.isFakeDragging())
            viewPager.endFakeDrag();
        handler.removeCallbacks(nextImageTask);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String setting_cb_shuffle_mode = getString(R.string.setting_cb_shuffle_mode);
        String setting_et_slideshow_interval = getString(R.string.setting_et_slideshow_interval);

        if ( key.equals(setting_cb_shuffle_mode)) {
            shuffleMode = sharedPreferences.getBoolean(setting_cb_shuffle_mode, false);
            setAdapterWithShuffleMode(shuffleMode);
        }

        if ( key.equals(setting_et_slideshow_interval)) {
            String intervalString = sharedPreferences.getString(
                    setting_et_slideshow_interval,
                    getString(R.string.setting_et_slideshow_interval_default));

            // Can't unregisterOnSharedPreferenceChangeListener into onPause(), then :
            if (Util.isDigit(intervalString)) {
                slideshowInterval = Integer.valueOf(intervalString);
            }
        }
    }

    private void setAdapterWithShuffleMode( boolean mode) {
        imagesCollection.setShuffleMode(mode);
        ImageGalleryPagerAdapter adapter = new ImageGalleryPagerAdapter(this, imagesCollection.getArrayImages());
        viewPager.setAdapter(adapter);
    }
}
package com.texasgamer.zephyr.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.texasgamer.zephyr.util.analytics.IAnalyticsManager;
import com.texasgamer.zephyr.util.log.ILogger;
import com.texasgamer.zephyr.util.preference.IPreferenceManager;

import javax.inject.Inject;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;

/**
 * Base activity that handles common routines.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Inject
    protected ILogger logger;
    @Inject
    protected IPreferenceManager mPreferenceManager;
    @Inject
    protected IAnalyticsManager analyticsManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        injectDependencies();

        if (analyticsManager == null || mPreferenceManager == null) {
            throw new IllegalStateException("Dependencies not fulfilled for this Activity.");
        }

        setupContent();
    }

    private void setupContent() {
        setContentView(getLayoutResource());
        ButterKnife.bind(this);
    }

    @LayoutRes
    protected abstract int getLayoutResource();

    protected abstract void injectDependencies();
}

package com.texasgamer.zephyr.util.log;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.texasgamer.zephyr.Constants;
import com.texasgamer.zephyr.util.config.IConfigManager;

import androidx.annotation.NonNull;

public class Logger implements ILogger {

    private IConfigManager configManager;

    public Logger(IConfigManager configManager) {
        this.configManager = configManager;
    }

    @Override
    public void log(@LogPriority int priority, @NonNull String tag, @NonNull String message) {
        if (priority < Constants.MIN_LOG_LEVEL) {
            return;
        }

        switch (priority) {
            case LogPriority.VERBOSE:
                Log.v(tag, message);
                break;
            case LogPriority.DEBUG:
                Log.d(tag, message);
                break;
            case LogPriority.INFO:
                Log.i(tag, message);
                break;
            case LogPriority.WARNING:
                Log.w(tag, message);
                break;
            case LogPriority.ERROR:
                Log.e(tag, message);
                break;
        }

        if (configManager.isFirebaseCrashlyticsEnabled()) {
            logToCrashlytics(priority, tag, message);
        }
    }

    @Override
    public void log(@LogPriority int priority, @NonNull String tag, @NonNull String message, @NonNull Object... args) {
        log(priority, tag, String.format(message, args));
    }

    @Override
    public void log(@LogPriority int priority, @NonNull String tag, @NonNull Throwable throwable, @NonNull String message, @NonNull Object... args) {
        log(priority, tag, String.format(message, args) + '\n' + throwable.toString());
    }

    @Override
    public void log(@LogPriority int priority, @NonNull String tag, @NonNull Throwable throwable) {
        log(priority, tag, throwable.toString());
    }

    private void logToCrashlytics(@LogPriority int priority, @NonNull String tag, @NonNull String message) {
        if (priority < Constants.MIN_LOG_LEVEL_CRASHLYTICS) {
            return;
        }

        switch (priority) {
            case LogPriority.VERBOSE:
                Crashlytics.log(String.format("V/%s: %s", tag, message));
                break;
            case LogPriority.DEBUG:
                Crashlytics.log(String.format("D/%s: %s", tag, message));
                break;
            case LogPriority.INFO:
                Crashlytics.log(String.format("I/%s: %s", tag, message));
                break;
            case LogPriority.WARNING:
                Crashlytics.log(String.format("W/%s: %s", tag, message));
                break;
            case LogPriority.ERROR:
                Crashlytics.log(String.format("E/%s: %s", tag, message));
                break;
        }
    }
}

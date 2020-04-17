package com.texasgamer.zephyr.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.texasgamer.zephyr.BuildConfig;
import com.texasgamer.zephyr.R;
import com.texasgamer.zephyr.ZephyrApplication;
import com.texasgamer.zephyr.adapter.ZephyrCardViewPagerAdapter;
import com.texasgamer.zephyr.model.ConnectionStatus;
import com.texasgamer.zephyr.model.DismissNotificationPayload;
import com.texasgamer.zephyr.model.NotificationPayload;
import com.texasgamer.zephyr.provider.IZephyrCardProvider;
import com.texasgamer.zephyr.util.threading.ZephyrExecutors;
import com.texasgamer.zephyr.util.ImageUtils;
import com.texasgamer.zephyr.util.NetworkUtils;
import com.texasgamer.zephyr.util.eventbus.EventBusEvent;
import com.texasgamer.zephyr.util.log.ILogger;
import com.texasgamer.zephyr.util.log.LogPriority;
import com.texasgamer.zephyr.view.ZephyrCardViewPager;
import com.texasgamer.zephyr.viewmodel.MainFragmentViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Main fragment.
 */
public class MainFragment extends BaseFragment<MainFragmentViewModel, ViewDataBinding> {

    private static final String LOG_TAG = "MainFragment";

    @Inject
    ILogger logger;
    @Inject
    IZephyrCardProvider zephyrCardProvider;

    @BindView(R.id.main_carousel)
    ZephyrCardViewPager mZephyrCardViewPager;
    @BindView(R.id.connection_status_icon)
    ImageView mConnectionStatusIcon;
    @BindView(R.id.connection_status_text)
    TextView mConnectionStatusText;
    @BindView(R.id.join_code_text)
    TextView mJoinCodeText;
    @BindView(R.id.connected_options_section)
    View mConnectedOptionsSection;
    @BindView(R.id.unsupported_api_section)
    View mUnsupportedApiSection;

    private ZephyrCardViewPagerAdapter mZephyrCardViewPagerAdapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mZephyrCardViewPagerAdapter = new ZephyrCardViewPagerAdapter(getContext(), zephyrCardProvider.getCards(getContext(), getChildFragmentManager()));
        mZephyrCardViewPager.setAdapter(mZephyrCardViewPagerAdapter);

        if (getActivity() != null) {
            mViewModel.getConnectionStatus().observe(getActivity(), this::updateConnectionStatus);
            mViewModel.getJoinCode().observe(getActivity(), this::updateJoinCodeStatus);
        }

        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mZephyrCardViewPagerAdapter.setItems(zephyrCardProvider.getCards(getContext(), getChildFragmentManager()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(@Nullable String eventPayload) {
        if (EventBusEvent.SHELL_REFRESH_CARDS.equals(eventPayload)) {
            mZephyrCardViewPagerAdapter.setItems(zephyrCardProvider.getCards(getContext(), getChildFragmentManager()));
        }
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_main;
    }

    @Override
    protected void setViewBindings(View view) {

    }

    @Override
    protected MainFragmentViewModel onCreateViewModel() {
        return new MainFragmentViewModel(getActivity().getApplication());
    }

    @Override
    protected void injectDependencies() {
        ZephyrApplication.getApplicationComponent().inject(this);
    }

    @OnClick(R.id.test_notification_button)
    void sendTestNotification() {
        ZephyrExecutors.getDiskExecutor().execute(() -> {
            NotificationPayload notificationPayload = new NotificationPayload();
            notificationPayload.packageName = BuildConfig.APPLICATION_ID;
            notificationPayload.id = -1;
            notificationPayload.title = getString(R.string.test_notification_title);
            notificationPayload.body = getString(R.string.test_notification_message);
            notificationPayload.icon = ImageUtils.bitmapToBase64(ImageUtils.drawableToBitmap(getResources().getDrawable(R.drawable.ic_notifications)));

            logger.log(LogPriority.INFO, LOG_TAG, "Sending test notification");
            EventBus.getDefault().post(notificationPayload);
        });

        new Handler().postDelayed(() -> {
            DismissNotificationPayload dismissNotificationPayload = new DismissNotificationPayload();
            dismissNotificationPayload.packageName = BuildConfig.APPLICATION_ID;
            dismissNotificationPayload.id = -1;

            logger.log(LogPriority.INFO, LOG_TAG, "Dismissing test notification...");
            EventBus.getDefault().post(dismissNotificationPayload);
        }, 5000);
    }

    @OnClick(R.id.join_code_summary)
    void openConnectFragment() {
        ConnectFragment connectFragment = new ConnectFragment();
        connectFragment.show(getFragmentManager(), connectFragment.getTag());
    }

    private void updateConnectionStatus(@ConnectionStatus int connectionStatus) {
        boolean isConnected = NetworkUtils.connectionStatusToIsConnected(connectionStatus);
        mConnectionStatusIcon.setImageResource(isConnected ? R.drawable.ic_check : R.drawable.ic_error);
        mConnectionStatusText.setText(NetworkUtils.connectionStatusToString(getContext(), connectionStatus));
        mConnectedOptionsSection.setVisibility(isConnected ? View.VISIBLE : View.GONE);
        mUnsupportedApiSection.setVisibility(connectionStatus == ConnectionStatus.UNSUPPORTED_API ? View.VISIBLE : View.GONE);
    }

    private void updateJoinCodeStatus(@NonNull String joinCode) {
        mJoinCodeText.setText(joinCode.isEmpty()
                ? getString(R.string.join_code_none)
                : String.format(getString(R.string.join_code_saved), joinCode));
    }
}

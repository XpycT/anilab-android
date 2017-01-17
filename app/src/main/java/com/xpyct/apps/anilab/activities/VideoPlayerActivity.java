package com.xpyct.apps.anilab.activities;


import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;
import com.afollestad.materialdialogs.MaterialDialog;
import com.xpyct.apps.anilab.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class VideoPlayerActivity extends AbstractActivity implements EasyVideoCallback {

    public static final String TITLE = "title";
    public static final String URI = "uri";

    @Bind(R.id.player)
    EasyVideoPlayer player;

    private String mTitle;
    private String mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoplayer);
        ButterKnife.bind(this);

        assert player != null;

        mTitle = getIntent().getStringExtra(TITLE);
        mUri = getIntent().getStringExtra(URI);

        initInstances();
    }

    private void initInstances() {
        player.setCallback(this);

        // To play files, you can use Uri.fromFile(new File("..."))
        player.setSource(Uri.parse(mUri));
        setTitle(mTitle);
        player.setBottomLabelText(mTitle);
    }

    @Override
    protected void onPause() {
        super.onPause();
        player.pause();
    }

    @Override
    public void onStarted(EasyVideoPlayer player) {

    }

    @Override
    public void onPaused(EasyVideoPlayer player) {

    }

    @Override
    public void onPreparing(EasyVideoPlayer player) {
        Log.d("EVP-Sample", "onPreparing()");
    }

    @Override
    public void onPrepared(EasyVideoPlayer player) {
        Log.d("EVP-Sample", "onPrepared()");
    }

    @Override
    public void onBuffering(int percent) {
        Log.d("EVP-Sample", "onBuffering(): " + percent + "%");
    }

    @Override
    public void onError(EasyVideoPlayer player, Exception e) {
        Log.d("EVP-Sample", "onError(): " + e.getMessage());
        new MaterialDialog.Builder(this)
                .title(R.string.error)
                .content(e.getMessage())
                .positiveText(android.R.string.ok)
                .show();
    }

    @Override
    public void onCompletion(EasyVideoPlayer player) {
        Log.d("EVP-Sample", "onCompletion()");
    }

    @Override
    public void onRetry(EasyVideoPlayer player, Uri source) {
        Toast.makeText(this, "Retry", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSubmit(EasyVideoPlayer player, Uri source) {
        Toast.makeText(this, "Submit", Toast.LENGTH_SHORT).show();
    }
}

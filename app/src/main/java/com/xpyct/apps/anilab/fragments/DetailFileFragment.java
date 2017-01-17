package com.xpyct.apps.anilab.fragments;


import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.xpyct.apps.anilab.R;
import com.xpyct.apps.anilab.adapters.FileAdapter;
import com.xpyct.apps.anilab.managers.MediaIntents;
import com.xpyct.apps.anilab.managers.Utils;
import com.xpyct.apps.anilab.managers.other.LoaderDroidPublicAPI;
import com.xpyct.apps.anilab.models.File;
import com.xpyct.apps.anilab.models.Movie;
import com.xpyct.apps.anilab.models.ParseLink;
import com.xpyct.apps.anilab.models.VideoService;
import com.xpyct.apps.anilab.models.myvi.MyviFile;
import com.xpyct.apps.anilab.models.orm.Watched;
import com.xpyct.apps.anilab.models.vk.VkFile;
import com.xpyct.apps.anilab.network.AnilabApi;
import com.xpyct.apps.anilab.network.VkApi;
import com.xpyct.apps.anilab.views.OnItemClickListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.RetrofitError;
import rx.Observer;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DetailFileFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String SELECTED_MOVIE = "selected_movie";
    public static final String API_SERVICE = "api_service";
    public static final String VIDEO_QUALITY = "video_quality";

    private SharedPreferences pref;
    private Movie mMovie;
    private long apiService;
    private VkFile mVkFile = null;
    private MyviFile mMyviFile = null;
    private ParseLink mParseLink = null;
    private String mCurrentFileName;

    private MaterialDialog waitDialog;

    private FirebaseAnalytics mFirebaseAnalytics;

    public enum FileDialogAction {
        SHOW,
        DOWNLOAD,
        COPY
    }

    private FileAdapter mFileAdapter;

    private AnilabApi mApi = AnilabApi.getInstance();

    @Bind(R.id.fragment_files_recycler)
    RecyclerView mFileRecycler;
    @Bind(R.id.fragment_files_progress)
    ProgressBar mFileProgress;
    @Bind(R.id.fragment_files_refresh)
    SwipeRefreshLayout mFileRefreshLayout;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param movie movie.
     * @return A new instance of fragment DetailCommentsFragment.
     */
    public static DetailFileFragment newInstance(Movie movie,long api) {
        DetailFileFragment fragment = new DetailFileFragment();
        Bundle args = new Bundle();
        args.putSerializable(SELECTED_MOVIE, movie);
        args.putLong(API_SERVICE, api);
        fragment.setArguments(args);
        return fragment;
    }

    public DetailFileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        pref = PreferenceManager.getDefaultSharedPreferences(DetailFileFragment.this.getActivity());
        if (getArguments() != null) {
            mMovie = (Movie) getArguments().getSerializable(SELECTED_MOVIE);
            apiService = getArguments().getLong(API_SERVICE, 2);
        }
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this.getActivity());
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_detail_files, container, false);
        ButterKnife.bind(this, rootView);

        mFileAdapter = new FileAdapter(mMovie.getMovieId(), Utils.getApiServiceName(apiService));
        mFileAdapter.setOnItemClickListener(recyclerRowClickListener);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mFileRecycler.setLayoutManager(layoutManager);
        mFileRecycler.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        mFileRecycler.setHasFixedSize(true);
        mFileRecycler.setAdapter(mFileAdapter);
        mFileRefreshLayout.setOnRefreshListener(this);
        mFileRefreshLayout.setColorSchemeResources(R.color.blue,
                R.color.green,
                R.color.orange,
                R.color.red);
        getFiles();

        waitDialog = new MaterialDialog.Builder(getActivity())
                .cancelable(false)
                .autoDismiss(false)
                .title(R.string.please_wait)
                .content(R.string.please_wait)
                .progress(true, 0).build();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_files, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_remove_watched) {
            removeWatchedMarks();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void removeWatchedMarks() {
        String api_service_name = Utils.getApiServiceName(apiService);
        Watched.deleteAll(Watched.class, "movie_id = ? and service = ?", mMovie.getMovieId(), api_service_name);
        mFileAdapter.notifyDataSetChanged();
    }

    private void getFiles() {
        mFileRefreshLayout.setRefreshing(true);
        AppObservable.bindSupportFragment(this, mApi.fetchFiles(Utils.getApiServiceName(apiService), Integer.parseInt(mMovie.getMovieId())))
                .cache().subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    private Observer<ArrayList<ArrayList<File>>> observer = new Observer<ArrayList<ArrayList<File>>>() {
        @Override
        public void onNext(final ArrayList<ArrayList<File>> files) {
            ArrayList<ArrayList<File>> mFiles = files;
            updateAdapter(mFiles);
        }

        @Override
        public void onCompleted() {
            // Dismiss loading dialog
            showCommentsList();
        }

        @Override
        public void onError(final Throwable error) {
            checkRetrofitOnError(error);
        }
    };

    private void showCommentsList() {
        mFileProgress.setVisibility(View.GONE);
        mFileRecycler.setVisibility(View.VISIBLE);
        mFileRefreshLayout.setRefreshing(false);
    }

    private void hideFilesList() {
        mFileProgress.setVisibility(View.VISIBLE);
        mFileRecycler.setVisibility(View.GONE);
        mFileRefreshLayout.setRefreshing(true);
    }

    /**
     * Reset Movie Adapter, reset scroll
     *
     * @param files ArrayList movies
     */
    private void updateAdapter(ArrayList<ArrayList<File>> files) {
        mFileAdapter.updateData(files);

        if (mFileAdapter.getItemCount() == 0) mFileRecycler.scrollToPosition(0);
        checkForEmptyFileData();
    }

    /**
     * Check for empty data, and show error label
     */
    private void checkForEmptyFileData() {
        if (mFileAdapter.getItemCount() == 0) {
            mFileRecycler.setVisibility(View.GONE);
            //mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mFileRecycler.setVisibility(View.VISIBLE);
            //mEmptyView.setVisibility(View.GONE);
        }
    }

    /**
     * Retrofit error checker
     *
     * @param error
     */
    private void checkRetrofitOnError(Throwable error) {
        if (error instanceof RetrofitError) {
        }
        mFileRefreshLayout.setRefreshing(false);
        mFileProgress.setVisibility(View.GONE);
        mFileRecycler.setVisibility(View.GONE);
    }


    @Override
    public void onRefresh() {
        hideFilesList();
        getFiles();
    }

    private OnItemClickListener recyclerRowClickListener = new OnItemClickListener() {

        @Override
        public void onClick(View v, int position) {
            if (v.getId() == R.id.file_context_menu) {
                showFileActionDialog(position);
            } else {
                showWatchDialog(position, FileDialogAction.SHOW);
            }
        }
    };

    /**
     * Show file action dialog
     *
     * @param position int seleted file
     */
    private void showFileActionDialog(final int position) {
        final ArrayList<File> selectedFiles = mFileAdapter.getFiles().get(position);
        new MaterialDialog.Builder(getActivity())
                .title(selectedFiles.get(0).getPart())
                .items(getDotMenuItems(position))
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                        switch (i) {
                            case 0:
                                showWatchDialog(position, FileDialogAction.SHOW);
                                break;
                            case 1:
                                showWatchDialog(position, FileDialogAction.DOWNLOAD);
                                break;
                            case 2:
                                // Creates a new text clip to put on the clipboard
                                showWatchDialog(position, FileDialogAction.COPY);
                                break;
                            case 3:
                                markFileWatched(position);
                                break;
                        }
                    }
                }).show();
    }

    /**
     * Get menu items with watched/unwatched text
     * @param position int
     * @return int array
     */
    private int getDotMenuItems(int position){
        final ArrayList<File> selectedFiles = mFileAdapter.getFiles().get(position);
        String api_service_name = Utils.getApiServiceName(apiService);
        String part = selectedFiles.get(0).getPart();
        long count = Watched.count(Watched.class, "movie_id = ? and service = ? and part = ?", new String[]{mMovie.getMovieId(), api_service_name, part});

        return (count == 0)?R.array.file_action_names_unwatched:R.array.file_action_names_watched;
    }

    /**
     * Mark as watched or unwatched
     * @param position int
     */
    private void markFileWatched(int position){
        final ArrayList<File> selectedFiles = mFileAdapter.getFiles().get(position);
        String api_service_name = Utils.getApiServiceName(apiService);
        String part = selectedFiles.get(0).getPart();
        long count = Watched.count(Watched.class, "movie_id = ? and service = ? and part = ?", new String[]{mMovie.getMovieId(), api_service_name, part});
        if (count == 0) {
            Watched watched = new Watched(mMovie.getMovieId(), api_service_name, part);
            watched.save();
        }else{
            Watched.deleteAll(Watched.class, "movie_id = ? and service = ? and part = ?", mMovie.getMovieId(), api_service_name, part);
        }
        mFileAdapter.notifyDataSetChanged();
    }

    /**
     * Copy text to clipboard
     *
     * @param link string url to copy
     */
    private void copyToClipboard(String link) {
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("file url", link);
        clipboard.setPrimaryClip(clip);
        Snackbar.make(getActivity().findViewById(R.id.viewpager), getString(R.string.action_copied), Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Show watch or download dialog
     *
     * @param position int file position
     * @param action   FileDialogAction action
     */
    private void showWatchDialog(final int position, final FileDialogAction action) {
        final ArrayList<File> selectedFiles = mFileAdapter.getFiles().get(position);
        mCurrentFileName = selectedFiles.get(0).getPart();
        // generate server list choise
        final List<VideoService> services = new ArrayList<>();
        for (File item : selectedFiles) {
            switch (item.getService()) {
                case "vk":
                    services.add(new VideoService("vk", getString(R.string.VK), item.getOriginalLink(), item.getDownloadLink()));
                    break;
                case "sibnet":
                    services.add(new VideoService("sibnet", getString(R.string.SIBNET), item.getOriginalLink(), item.getDownloadLink()));
                    break;
                case "24video":
                    services.add(new VideoService("24video", getString(R.string.VIDEO24), item.getOriginalLink(), item.getDownloadLink()));
                    break;
                case "kivvi":
                    services.add(new VideoService("kivvi", getString(R.string.KIVVI), item.getOriginalLink(), item.getDownloadLink()));
                    break;
                case "myvi":
                    services.add(new VideoService("myvi", getString(R.string.MYVI), item.getOriginalLink(), item.getDownloadLink()));
                    break;
                case "moonwalk":
                    services.add(new VideoService("moonwalk", getString(R.string.MOONWALK), item.getOriginalLink(), item.getDownloadLink()));
                    break;
                case "anidub":
                    services.add(new VideoService("anidub", getString(R.string.ANIDUB), item.getOriginalLink(), item.getDownloadLink()));
                    break;
                case "rutube":
                    services.add(new VideoService("rutube", getString(R.string.RUTUBE), item.getOriginalLink(), item.getDownloadLink()));
                    break;
                case "youtube":
                    services.add(new VideoService("youtube", getString(R.string.YOUTUBE), item.getOriginalLink(), item.getDownloadLink()));
                    break;
            }
        }

        List<String> services_names = new ArrayList<>();
        for (VideoService item : services) {
            services_names.add(item.getValue());
        }

        new MaterialDialog.Builder(getActivity())
                .title(R.string.dialog_select_service)
                .items(services_names.toArray(new String[services_names.size()]))
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                        if (action == FileDialogAction.SHOW) {
                            //mark watched
                            String api_service_name = Utils.getApiServiceName(apiService);
                            String part = selectedFiles.get(0).getPart();
                            long count = Watched.count(Watched.class, "movie_id = ? and service = ? and part = ?", new String[]{mMovie.getMovieId(), api_service_name, part});
                            if (count == 0) {
                                Watched watched = new Watched(mMovie.getMovieId(), api_service_name, part);
                                watched.save();
                                mFileAdapter.notifyDataSetChanged();
                            }
                        }
                        String videoServiceKey = services.get(which).getKey();

                        Bundle params = new Bundle();
                        params.putString(FirebaseAnalytics.Param.ITEM_NAME, videoServiceKey);
                        mFirebaseAnalytics.logEvent("selected_video_service", params);

                        switch (videoServiceKey) {
                            case "vk":
                                Pattern p = Pattern.compile("act=(.*)&vid=(.*)", Pattern.CASE_INSENSITIVE);
                                Matcher m = p.matcher(services.get(which).getDownload_url());
                                if (m.find()) {
                                    switch (action) {
                                        case SHOW:
                                            params = new Bundle();
                                            params.putString(FirebaseAnalytics.Param.VALUE, videoServiceKey);
                                            mFirebaseAnalytics.logEvent("video_show", params);

                                            AppObservable.bindSupportFragment(DetailFileFragment.this, VkApi.getInstance(services.get(which).getDownload_url()).fetchFile(m.group(1), m.group(2)))
                                                    .cache().subscribeOn(Schedulers.newThread())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(observerVkFileShow);
                                            break;
                                        case DOWNLOAD:
                                            params = new Bundle();
                                            params.putString(FirebaseAnalytics.Param.VALUE, videoServiceKey);
                                            mFirebaseAnalytics.logEvent("video_download", params);

                                            AppObservable.bindSupportFragment(DetailFileFragment.this, VkApi.getInstance(services.get(which).getDownload_url()).fetchFile(m.group(1), m.group(2)))
                                                    .cache().subscribeOn(Schedulers.newThread())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(observerVkFileDownload);
                                            break;
                                        case COPY:
                                            AppObservable.bindSupportFragment(DetailFileFragment.this, VkApi.getInstance(services.get(which).getDownload_url()).fetchFile(m.group(1), m.group(2)))
                                                    .cache().subscribeOn(Schedulers.newThread())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(observerVkFileCopy);
                                            break;
                                    }

                                }
                                break;
                            default:
                                switch (action) {
                                    case SHOW:

                                        params = new Bundle();
                                        params.putString(FirebaseAnalytics.Param.VALUE, videoServiceKey);
                                        mFirebaseAnalytics.logEvent("video_show", params);

                                        if (services.get(which).getDownload_url().isEmpty()) {
                                            showLinkBrokenToast();
                                        } else if (services.get(which).getDownload_url().contains("myvi")) {
                                            /*AppObservable.bindSupportFragment(DetailFileFragment.this, MyviApi.getInstance(services.get(which).getDownload_url()).fetchFile("1"))
                                                    .cache().subscribeOn(Schedulers.newThread())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(observerMyviFileShow);*/
                                            startActivity(MediaIntents.newOpenWebBrowserIntent(services.get(which).getOriginal_url()));
                                        }  else if (services.get(which).getDownload_url().contains("sibnet")) {
                                            waitDialog.show();
                                            AppObservable.bindSupportFragment(DetailFileFragment.this, AnilabApi.getInstance().fetchParseLink(services.get(which).getOriginal_url()))
                                                    .cache().subscribeOn(Schedulers.newThread())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(observerSibnetFileShow);
                                        } else if (services.get(which).getDownload_url().contains("rutube")) {
                                            if (Utils.appInstalledOrNot(Utils.Const.RUTUBE_PACKAGE, getActivity())) {
                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                intent.setPackage(Utils.Const.RUTUBE_PACKAGE);
                                                intent.setData(Uri.parse(services.get(which).getDownload_url()));
                                                startActivity(intent);
                                            } else {
                                                new MaterialDialog.Builder(getActivity())
                                                        .title(R.string.rutube_not_installed_title)
                                                        .content(R.string.rutube_not_installed_content)
                                                        .positiveText(R.string.from_market)
                                                        .negativeText(R.string.cancel)
                                                        .neutralText(R.string.from_4pda)
                                                        .callback(new MaterialDialog.ButtonCallback() {
                                                            @Override
                                                            public void onPositive(MaterialDialog dialog) {
                                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                                intent.setData(Uri.parse("market://details?id=" + Utils.Const.RUTUBE_PACKAGE));
                                                                startActivity(intent);

                                                                super.onPositive(dialog);
                                                            }

                                                            @Override
                                                            public void onNeutral(MaterialDialog dialog) {

                                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                                intent.setData(Uri.parse(getString(R.string.rutube_4pda)));
                                                                startActivity(intent);

                                                                super.onNeutral(dialog);
                                                            }
                                                        }).show();
                                            }
                                        } else if (services.get(which).getDownload_url().contains("youtube")){
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(services.get(which).getDownload_url())));
                                        } else {
                                            Utils.showVideoDialog(getActivity(),mCurrentFileName,services.get(which).getDownload_url());
                                        }
                                        break;
                                    case DOWNLOAD:
                                        params = new Bundle();
                                        params.putString(FirebaseAnalytics.Param.VALUE, videoServiceKey);
                                        mFirebaseAnalytics.logEvent("video_download", params);

                                        if (services.get(which).getDownload_url().contains("myvi")) {
                                            // cant download from myvi
                                            Toast.makeText(getActivity(), R.string.error_download_myvi, Toast.LENGTH_SHORT).show();
                                        } else if (services.get(which).getDownload_url().contains("sibnet")) {
                                            waitDialog.show();
                                            AppObservable.bindSupportFragment(DetailFileFragment.this, AnilabApi.getInstance().fetchParseLink(services.get(which).getOriginal_url()))
                                                    .cache().subscribeOn(Schedulers.newThread())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(observerSibnetFileDownload);
                                        } else if (services.get(which).getDownload_url().contains("rutube")) {
                                            // cant download from rutube
                                            Toast.makeText(getActivity(), R.string.error_download_rutube, Toast.LENGTH_SHORT).show();
                                        } else {
                                            downloadFile(services.get(which).getDownload_url());
                                        }
                                        break;
                                    case COPY:
                                        copyToClipboard(services.get(which).getDownload_url());
                                        break;
                                }
                                break;
                        }
                    }
                })
                .show();
    }

    /**
     * Download file
     *
     * @param download_url String
     */
    private void downloadFile(String download_url) {

        // check for empty download link
        if (download_url.isEmpty()) {
            Toast.makeText(getActivity(),
                    getActivity().getString(R.string.link_is_broken),
                    Toast.LENGTH_SHORT).show();
        } else {
            //Check for LoadDroid
            if (Utils.appInstalledOrNot(LoaderDroidPublicAPI.LOADER_DROID_PACKAGE, getActivity())) {
                if (!LoaderDroidPublicAPI.isLoaderDroidRequireUpdate(getActivity())) {
                    Intent intent = new Intent(LoaderDroidPublicAPI.ACTION_ADD_LOADING);
                    intent.setData(Uri.parse(download_url));
                    intent.putExtra("allowed_connection", "WIFI_ONLY");
                    intent.putExtra("use_default_directory", true);
                    startActivity(intent);
                }
            } else {
                Uri url = Uri.parse(download_url);
                String name = url.getPathSegments().get(url.getPathSegments().size() - 1);

                DownloadManager.Request r = new DownloadManager.Request(Uri.parse(download_url));
                r.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "AniLab/" + name);
                r.allowScanningByMediaScanner();
                //r.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
                r.setVisibleInDownloadsUi(true);
                r.setAllowedOverRoaming(false);
                r.setTitle(mCurrentFileName);
                r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                // Start download
                DownloadManager dm = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                dm.enqueue(r);
            }
        }

    }

    private Observer<VkFile> observerVkFileShow = new Observer<VkFile>() {
        @Override
        public void onNext(final VkFile file) {
            mVkFile = file;
        }

        @Override
        public void onCompleted() {
            switch (pref.getString(VIDEO_QUALITY, "16")) {
                case "32":
                    Utils.showVideoDialog(getActivity(),mCurrentFileName,checkForVkLinkExists(mVkFile.getUrl1080()));
                    break;
                case "1":
                    Utils.showVideoDialog(getActivity(),mCurrentFileName,checkForVkLinkExists(mVkFile.getUrl720()));
                    break;
                case "2":
                    Utils.showVideoDialog(getActivity(),mCurrentFileName,checkForVkLinkExists(mVkFile.getUrl480()));
                    break;
                case "4":
                    Utils.showVideoDialog(getActivity(),mCurrentFileName,checkForVkLinkExists(mVkFile.getUrl360()));
                    break;
                case "8":
                    Utils.showVideoDialog(getActivity(),mCurrentFileName,checkForVkLinkExists(mVkFile.getUrl240()));
                    break;
                case "16":
                    showQualityDialog(FileDialogAction.SHOW);
                    break;
            }
        }

        @Override
        public void onError(final Throwable error) {
        }
    };
    private Observer<MyviFile> observerMyviFileShow = new Observer<MyviFile>() {
        @Override
        public void onNext(final MyviFile file) {
            mMyviFile = file;
        }

        @Override
        public void onCompleted() {
            //Toast.makeText(getActivity(), mMyviFile.getSprutoData().getPlaylist().get(0).getVideo().get(0).getUrl(), Toast.LENGTH_LONG).show();
            String myviUrl = mMyviFile.getSprutoData().getPlaylist().get(0).getVideo().get(0).getUrl();
            if (myviUrl.isEmpty()) {
                showLinkBrokenToast();
            } else {
                startActivity(MediaIntents.newOpenWebBrowserIntent(myviUrl));
            }
        }

        @Override
        public void onError(final Throwable error) {
        }
    };

    private Observer<ParseLink> observerSibnetFileShow = new Observer<ParseLink>() {
        @Override
        public void onNext(final ParseLink link) {
            mParseLink = link;
        }

        @Override
        public void onCompleted() {
            waitDialog.dismiss();
            String sibnetUrl = mParseLink.getDownloadLink();
            if (sibnetUrl.isEmpty()) {
                showLinkBrokenToast();
            } else {
                Utils.showVideoDialog(getActivity(),mCurrentFileName,sibnetUrl);
            }
        }

        @Override
        public void onError(final Throwable error) {
            waitDialog.dismiss();
            Snackbar.make(getView(), getActivity().getString(R.string.error_timeout_no_file), Snackbar.LENGTH_SHORT).show();
        }
    };

    private Observer<ParseLink> observerSibnetFileDownload = new Observer<ParseLink>() {
        @Override
        public void onNext(final ParseLink link) {
            mParseLink = link;
        }

        @Override
        public void onCompleted() {
            waitDialog.dismiss();
            String sibnetUrl = mParseLink.getDownloadLink();
            if (sibnetUrl.isEmpty()) {
                showLinkBrokenToast();
            } else {
                downloadFile(sibnetUrl);
            }
        }

        @Override
        public void onError(final Throwable error) {
            waitDialog.dismiss();
            Snackbar.make(getView(), getActivity().getString(R.string.error_timeout_no_file), Snackbar.LENGTH_SHORT).show();
        }
    };

    /**
     * Broken link toast
     */
    public void showLinkBrokenToast() {
        Toast.makeText(getActivity(),
                getActivity().getString(R.string.link_is_broken),
                Toast.LENGTH_SHORT).show();
    }

    private Observer<VkFile> observerVkFileCopy = new Observer<VkFile>() {
        @Override
        public void onNext(final VkFile file) {
            mVkFile = file;
        }

        @Override
        public void onCompleted() {
            switch (pref.getString(VIDEO_QUALITY, "16")) {
                case "32":
                    copyToClipboard(checkForVkLinkExists(mVkFile.getUrl1080()));
                    break;
                case "1":
                    copyToClipboard(checkForVkLinkExists(mVkFile.getUrl720()));
                    break;
                case "2":
                    copyToClipboard(checkForVkLinkExists(mVkFile.getUrl480()));
                    break;
                case "4":
                    copyToClipboard(checkForVkLinkExists(mVkFile.getUrl360()));
                    break;
                case "8":
                    copyToClipboard(checkForVkLinkExists(mVkFile.getUrl240()));
                    break;
                case "16":
                    showQualityDialog(FileDialogAction.COPY);
                    break;
            }
        }

        @Override
        public void onError(final Throwable error) {
        }
    };

    private Observer<VkFile> observerVkFileDownload = new Observer<VkFile>() {
        @Override
        public void onNext(final VkFile file) {
            mVkFile = file;
        }

        @Override
        public void onCompleted() {
            switch (pref.getString(VIDEO_QUALITY, "16")) {
                case "32":
                    downloadFile(checkForVkLinkExists(mVkFile.getUrl1080()));
                    break;
                case "1":
                    downloadFile(checkForVkLinkExists(mVkFile.getUrl720()));
                    break;
                case "2":
                    downloadFile(checkForVkLinkExists(mVkFile.getUrl480()));
                    break;
                case "4":
                    downloadFile(checkForVkLinkExists(mVkFile.getUrl360()));
                    break;
                case "8":
                    downloadFile(checkForVkLinkExists(mVkFile.getUrl240()));
                    break;
                case "16":
                    showQualityDialog(FileDialogAction.DOWNLOAD);
                    break;
            }
        }

        @Override
        public void onError(final Throwable error) {
        }
    };

    /**
     * Check for existing Vk quality link
     *
     * @param qualityLink
     * @return
     */
    private String checkForVkLinkExists(String qualityLink) {
        String output = qualityLink;
        if (output == null) {
            if (mVkFile.getUrl1080() != null) {
                output = mVkFile.getUrl1080();
            } else if (mVkFile.getUrl720() != null) {
                output = mVkFile.getUrl720();
            } else if (mVkFile.getUrl480() != null) {
                output = mVkFile.getUrl480();
            } else if (mVkFile.getUrl360() != null) {
                output = mVkFile.getUrl360();
            } else if (mVkFile.getUrl240() != null) {
                output = mVkFile.getUrl240();
            }
        }
        return output;
    }

    /**
     * Show quality choice dialog
     *
     * @param action
     */
    private void showQualityDialog(final FileDialogAction action) {
        // create quality
        List<String> qualityList = new ArrayList<>();

        if (mVkFile.getUrl1080() != null) {
            qualityList.add("1080p");
        }
        if (mVkFile.getUrl720() != null) {
            qualityList.add("720p");
        }
        if (mVkFile.getUrl480() != null) {
            qualityList.add("480p");
        }
        if (mVkFile.getUrl360() != null) {
            qualityList.add("360p");
        }
        if (mVkFile.getUrl240() != null) {
            qualityList.add("240p");
        }

        new MaterialDialog.Builder(getActivity())
                .title(R.string.dialog_select_quality)
                .items(qualityList.toArray(new String[qualityList.size()]))
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                        String quality = checkForVkLinkExists(mVkFile.getUrl240());
                        if (charSequence.equals("1080p")) {
                            quality = checkForVkLinkExists(mVkFile.getUrl1080());

                        } else if (charSequence.equals("720p")) {
                            quality = checkForVkLinkExists(mVkFile.getUrl720());

                        } else if (charSequence.equals("480p")) {
                            quality = checkForVkLinkExists(mVkFile.getUrl480());

                        } else if (charSequence.equals("360p")) {
                            quality = checkForVkLinkExists(mVkFile.getUrl360());

                        } else if (charSequence.equals("240p")) {
                            quality = checkForVkLinkExists(mVkFile.getUrl240());
                        }
                        //Log.v("VK LINK", quality);
                        if (quality.isEmpty()) {
                            showLinkBrokenToast();
                        } else {
                            Bundle params = new Bundle();
                            params.putString(FirebaseAnalytics.Param.VALUE, quality);
                            mFirebaseAnalytics.logEvent("video_quality", params);

                            switch (action) {
                                case SHOW:
                                    Utils.showVideoDialog(getActivity(),mCurrentFileName,quality);
                                    break;
                                case DOWNLOAD:
                                    downloadFile(quality);
                                    break;
                                case COPY:
                                    copyToClipboard(quality);
                                    break;
                            }
                        }
                    }
                }).show();
    }
}

package com.xpyct.apps.anilab.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.xpyct.apps.anilab.R;
import com.xpyct.apps.anilab.models.File;
import com.xpyct.apps.anilab.models.orm.Watched;
import com.xpyct.apps.anilab.views.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;


public class FileAdapter extends RecyclerView.Adapter<FilesViewHolder> {

    private Context mContext;
    private ArrayList<ArrayList<File>> mFiles;
    private String mMovieId;
    private String mApiServiceName;

    private OnItemClickListener onItemClickListener;


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public FileAdapter() {
        mFiles = new ArrayList<>();
    }

    public FileAdapter(String movieId, String apiServiceName) {
        mFiles = new ArrayList<>();
        mMovieId = movieId;
        mApiServiceName = apiServiceName;
    }

    public FileAdapter(ArrayList<ArrayList<File>> files) {
        this.mFiles = files;
    }

    public void updateData(ArrayList<ArrayList<File>> files) {
        this.mFiles = files;

        notifyDataSetChanged();
    }

    @Override
    public FilesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_detail_files_item, parent, false);
        //set the mContext
        this.mContext = parent.getContext();

        return new FilesViewHolder(rowView, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(FilesViewHolder holder, final int position) {
        final File currentFile = mFiles.get(position).get(0);
        holder.fileTitle.setText(currentFile.getPart());
        List<String> services = new ArrayList<>();
        for (File item : mFiles.get(position)) {
            services.add(item.getService());
        }
        holder.fileStatus.setText(TextUtils.join(", ", services));
        holder.fileTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onClick(v, position);
            }
        });
        holder.fileStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onClick(v, position);
            }
        });
        holder.fileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onClick(v, position);
            }
        });
        holder.fileContextMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onClick(v, position);
            }
        });

        FontAwesome.Icon icon = FontAwesome.Icon.faw_play_circle_o;
        long count = Watched.count(Watched.class, "movie_id = ? and service = ? and part = ?", new String[]{mMovieId, mApiServiceName, currentFile.getPart()});
        if (count > 0) {
            icon = FontAwesome.Icon.faw_circle;
        }
        holder.fileImage.setImageDrawable(new IconicsDrawable(mContext, icon).sizeDp(48).colorRes(R.color.accent));
        holder.fileContextMenu.setImageDrawable(new IconicsDrawable(mContext, FontAwesome.Icon.faw_ellipsis_v).sizeDp(24).paddingDp(4).colorRes(R.color.secondary_text));
    }

    public ArrayList<ArrayList<File>> getFiles() {
        return mFiles;
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }
}

class FilesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    protected final TextView fileTitle;
    protected final TextView fileStatus;
    protected final ImageView fileImage;
    protected final ImageButton fileContextMenu;

    private final OnItemClickListener onItemClickListener;

    public FilesViewHolder(View itemView, OnItemClickListener onItemClickListener) {
        super(itemView);
        this.onItemClickListener = onItemClickListener;

        fileTitle = (TextView) itemView.findViewById(R.id.title);
        fileStatus = (TextView) itemView.findViewById(R.id.status);
        fileImage = (ImageView) itemView.findViewById(R.id.file_image);
        fileContextMenu = (ImageButton) itemView.findViewById(R.id.file_context_menu);

    }

    @Override
    public void onClick(View v) {
        onItemClickListener.onClick(v, getPosition());
    }
}
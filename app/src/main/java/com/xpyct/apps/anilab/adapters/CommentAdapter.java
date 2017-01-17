package com.xpyct.apps.anilab.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xpyct.apps.anilab.R;
import com.xpyct.apps.anilab.managers.CircleTransform;
import com.xpyct.apps.anilab.models.Comment;

import java.util.ArrayList;


public class CommentAdapter extends RecyclerView.Adapter<CommentsViewHolder> {

    private Context mContext;
    private ArrayList<Comment> mComments;

    public CommentAdapter() {
        mComments = new ArrayList<>();
    }

    public CommentAdapter(ArrayList<Comment> comments) {
        this.mComments = comments;
    }

    public void updateData(ArrayList<Comment> comments) {
        this.mComments = comments;

        notifyDataSetChanged();
    }

    @Override
    public CommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_detail_comments_item, parent, false);
        //set the mContext
        this.mContext = parent.getContext();
        return new CommentsViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(CommentsViewHolder holder, int position) {
        final Comment currentComment = mComments.get(position);
        holder.commentAuthor.setText(currentComment.getAuthor());
        holder.commentDate.setText(currentComment.getDate());
        holder.commentContent.setText(Html.fromHtml(currentComment.getBody()));
        holder.commentAvatar.setImageBitmap(null);

        String avatarImage = mComments.get(position).getAvatar();

        Glide.clear(holder.commentAvatar);

        if (!avatarImage.trim().isEmpty()) {

            Glide.with(mContext)
                    .load(avatarImage)
                    .crossFade()
                    .thumbnail(0.5f)
                    .transform(new CircleTransform(mContext))
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.commentAvatar);
        }
    }

    public ArrayList<Comment> getComments() {
        return mComments;
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }
}

class CommentsViewHolder extends RecyclerView.ViewHolder {
    protected final ImageView commentAvatar;
    protected final TextView commentAuthor;
    protected final TextView commentContent;
    protected final TextView commentDate;

    public CommentsViewHolder(View itemView) {

        super(itemView);

        commentAvatar = (ImageView) itemView.findViewById(R.id.comment_avatar);
        commentAuthor = (TextView) itemView.findViewById(R.id.comment_author);
        commentContent = (TextView) itemView.findViewById(R.id.comment_content);
        commentDate = (TextView) itemView.findViewById(R.id.comment_date);

    }
}
package com.example.luisle.equiz.Adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.luisle.equiz.Model.Comment;
import com.example.luisle.equiz.Model.User;
import com.example.luisle.equiz.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.example.luisle.equiz.MyFramework.MyEssential.USERS_CHILD;
import static com.example.luisle.equiz.MyFramework.MyEssential.eQuizRef;

/**
 * Created by LuisLe on 4/8/2017.
 */

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.CommentListViewHolder> {


    private List<Comment> commentList;
    private Context myContext;
    private User commentAuthor;
    private LayoutInflater layoutInflater;

    public CommentListAdapter(Context myContext, List<Comment> commentList) {
        this.myContext = myContext;
        this.commentList = commentList;
        layoutInflater = LayoutInflater.from(myContext);
    }

    @Override
    public CommentListAdapter.CommentListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.row_comment, parent, false);
        return new CommentListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CommentListAdapter.CommentListViewHolder holder, int position) {
        final Comment comment = commentList.get(position);
        getAuthor(comment.getAuthorID());
        holder.rateBarStar.setVisibility(View.INVISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                holder.txtContent.append(comment.getContent());
                holder.rateBarStar.setRating(comment.getRate());
                holder.rateBarStar.setVisibility(View.VISIBLE);
                holder.txtAuthor.append(myContext.getResources().getString(R.string.text_by) + " " + commentAuthor.getFullName());
                holder.txtCreatedDate.append(comment.getDateCreated());

                Picasso.with(myContext)
                        .load(commentAuthor.getProfilePicture())
                        .placeholder(R.mipmap.ic_avatar)
                        .error(R.mipmap.ic_avatar)
                        .into(holder.imgUserAvatar);
            }
        }, 1000);

    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class CommentListViewHolder extends RecyclerView.ViewHolder {
        private RoundedImageView imgUserAvatar;
        private TextView txtContent, txtAuthor, txtCreatedDate;
        private RatingBar rateBarStar;
        public CommentListViewHolder(View itemView) {
            super(itemView);
            imgUserAvatar = (RoundedImageView) itemView.findViewById(R.id.imgRowComment_Avatar);
            txtContent = (TextView) itemView.findViewById(R.id.txtRowComment_Content);
            txtAuthor = (TextView) itemView.findViewById(R.id.txtRowComment_Author);
            txtCreatedDate = (TextView) itemView.findViewById(R.id.txtRowComment_Date);
            rateBarStar = (RatingBar) itemView.findViewById(R.id.rateBarRowComment_Star);
        }
    }

    private void getAuthor(String userID) {
        eQuizRef.child(USERS_CHILD).child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setAuthor(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setAuthor(DataSnapshot dataSnapshot) {
        commentAuthor = dataSnapshot.getValue(User.class);
    }

}

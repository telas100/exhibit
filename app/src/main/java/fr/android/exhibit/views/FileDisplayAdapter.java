package fr.android.exhibit.views;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import fr.android.exhibit.activities.FilePicker;
import fr.android.exhibit.activities.R;

/**
 * Created by Thibault on 20/01/2016.
 */
public class FileDisplayAdapter extends RecyclerView.Adapter<FileDisplayAdapter.FileViewHolder> {
    List<FileDisplay> mList;

    public FileDisplayAdapter(List<FileDisplay> list) {
        this.mList = list;
    }

    @Override
    public FileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_item, parent, false);
        FileViewHolder holder = new FileViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(FileViewHolder holder, int position) {
        holder.mName.setText(mList.get(position).mFileName);
        holder.mImage.setImageResource(mList.get(position).mImageRessource);
        holder.mCheckbox.setClickable(false);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class FileViewHolder extends RecyclerView.ViewHolder {
        LinearLayout mLayout;
        CardView mView;
        TextView mName;
        ImageView mImage;
        CheckBox mCheckbox;


        public FileViewHolder(View itemView) {
            super(itemView);
            mLayout = (LinearLayout)itemView.findViewById(R.id.fi_ll_layout);
            mView = (CardView)itemView.findViewById(R.id.fi_cv_card);
            mName = (TextView)itemView.findViewById(R.id.fi_tv_name);
            mImage = (ImageView)itemView.findViewById(R.id.fi_iv_image);
            mCheckbox = (CheckBox)itemView.findViewById(R.id.fi_cb_select);
            mName.setSelected(true);
        }
    }
}

package android.exhibit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Thibault on 07/01/2016.
 */
public class FileDisplayAdapter extends BaseAdapter {
    private Context mContext;
    private String[] mTitles;
    private Integer[] mRessources;

    // Constructor
    public FileDisplayAdapter(Context context,String[] titles,Integer[] ressources) {
        mContext = context;
        mTitles = titles;
        mRessources = ressources;
    }

    public int getCount() {
        return mTitles.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridView;
        if (convertView == null){
            gridView = new View(mContext);
            gridView = inflater.inflate(R.layout.file_item,null);
            TextView tv = (TextView) gridView.findViewById(R.id.tvFileTitle);
            ImageView iv = (ImageView) gridView.findViewById(R.id.ivFileImage);
            CheckBox cb = (CheckBox) gridView.findViewById(R.id.cbFileSend);

            tv.setText(mTitles[position]);
            iv.setImageResource(mRessources[position]);
        } else {
            gridView = (View) convertView;
        }
        return gridView;
    }
}
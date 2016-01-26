package fr.android.exhibit.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.android.exhibit.views.FileDisplay;
import fr.android.exhibit.views.FileDisplayAdapter;

public class FilePicker extends AppCompatActivity {
    private List<FileDisplay> list_files;
    private int mToSend = 0;
    private List<String> mFilesString;
    private FloatingActionButton mFab;

    private void initializeData() {
        list_files = new ArrayList<>();
        list_files.add(new FileDisplay("Plaquette.pdf", android.R.drawable.ic_input_get));
        list_files.add(new FileDisplay("Plaquette_étudiant.pdf", android.R.drawable.ic_input_get));
        list_files.add(new FileDisplay("Président_Joseph_Darimachy.jpeg", android.R.drawable.ic_menu_gallery));
        list_files.add(new FileDisplay("Vice_Présidente_Lillie_Watts.jpeg", android.R.drawable.ic_menu_gallery));
        list_files.add(new FileDisplay("Salon_2005.pdf", android.R.drawable.ic_input_get));
        list_files.add(new FileDisplay("Statistiques_2005.jpeg",android.R.drawable.ic_menu_gallery));
        list_files.add(new FileDisplay("Salon_2006.pdf", android.R.drawable.ic_input_get));
        list_files.add(new FileDisplay("Statistiques_2006.jpeg", android.R.drawable.ic_menu_gallery));
        list_files.add(new FileDisplay("Projets_2016.pdf", android.R.drawable.ic_input_get));
    }

    private RecyclerView mRecyclerView;
    private CoordinatorLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_picker);

        initializeData();

        mFilesString = new ArrayList<String>();

        mRecyclerView = (RecyclerView)findViewById(R.id.fpa_rv_files_list);
        mLayout = (CoordinatorLayout)findViewById(R.id.fpa_cl_layout);

        GridLayoutManager layoutManager = new GridLayoutManager((Context)this, 4);
        mRecyclerView.setLayoutManager(layoutManager);

        FileDisplayAdapter viewAdapter = new FileDisplayAdapter(list_files);
        mRecyclerView.setAdapter(viewAdapter);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mToSend > 0) {
                    String[] parcel = new String[mFilesString.size()];
                    mFilesString.toArray(parcel);
                    Intent intent = new Intent(FilePicker.this, NfcPrompter.class);
                    intent.putExtra("EXTRA_SELECTED_FILES", parcel);
                    startActivity(intent);
                } else {
                    Snackbar snack = Snackbar.make(mLayout, "Aucun fichier sélectionné.", Snackbar.LENGTH_SHORT);
                    snack.show();
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void onFileSelected(View view) {
        CardView cd = (CardView) view.findViewById(R.id.fi_cv_card);
        CheckBox cb = (CheckBox) view.findViewById(R.id.fi_cb_select);
        TextView tv = (TextView) view.findViewById(R.id.fi_tv_name);
        if (!cb.isChecked()) {
            mToSend++;
            cb.setChecked(true);
            mFilesString.add(tv.getText().toString());
        } else {
            mToSend--;
            cb.setChecked(false);
            mFilesString.remove(tv.getText().toString());
        }



    }

}

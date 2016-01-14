package fr.android.exhibit.activities;

import android.exhibit.R;
import fr.android.exhibit.data.SavedUserFiles;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;

public class ReturnOnExperience extends AppCompatActivity {
    private TextView mTextViewReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_on_experience);
        mTextViewReturn = (TextView) findViewById(R.id.tvReturnOnExperience);
        mTextViewReturn.setText(retreiveData());
    }

    private String retreiveData() {
        try {
            FileInputStream fis = null;
            ObjectInputStream ois = null;
            SavedUserFiles value = null;
            fis = openFileInput("user_files.ser");
            ois = new ObjectInputStream(fis);
            StringBuffer lu = new StringBuffer();
            while (true) {
                try{
                    value = (SavedUserFiles)ois.readObject();
                    lu.append(value.toString()+"\n");
                } catch (EOFException e) {
                    ois.close();
                    fis.close();
                    break;
                }
            }
            return lu.toString();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (OptionalDataException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

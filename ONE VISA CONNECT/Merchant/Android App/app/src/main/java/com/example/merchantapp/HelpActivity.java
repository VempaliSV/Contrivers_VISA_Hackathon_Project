package com.example.merchantapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class HelpActivity extends AppCompatActivity {

    ListView helpListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        helpListView=(ListView)findViewById(R.id.helpListView);

        ArrayList<String>helpList = new ArrayList<>();
        helpList.add("Register");
        helpList.add("User Guide");
        helpList.add("FAQS");
        helpList.add("VISA Support and Contact");

        ArrayAdapter<String>helpArrayAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,helpList);
        helpListView.setAdapter(helpArrayAdapter);
    }
}
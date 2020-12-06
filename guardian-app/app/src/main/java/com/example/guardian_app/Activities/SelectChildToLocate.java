package com.example.guardian_app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.guardian_app.Domain.DataStore;
import com.example.guardian_app.R;

import java.util.ArrayList;
import java.util.List;

public class SelectChildToLocate extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter arrayAdapter;
    private DataStore dataStore;
    private String childToLocate;
    private int itemSelectedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_child_to_locate);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            dataStore = extras.getParcelable("dataStore");
        }

        listView = (ListView) findViewById(R.id.list_view);
        List<String> childNames = new ArrayList<String>(dataStore.getChildNames());
        arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_single_choice, childNames);

        listView.setAdapter(arrayAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemSelectedPosition = position;
            }
        });

    }


    public void goToCheckChildLocation (View view){
        childToLocate = listView.getItemAtPosition(itemSelectedPosition).toString();
        Intent intent = new Intent(this, CheckChildLocation.class);
        intent.putExtra("dataStore", dataStore);
        intent.putExtra("childToLocate", childToLocate);
        startActivity(intent);
    }
}

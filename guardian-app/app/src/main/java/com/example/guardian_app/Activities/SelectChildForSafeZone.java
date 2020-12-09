package com.example.guardian_app.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.guardian_app.Dialogs.ZoneAlreadyDefinedDialog;
import com.example.guardian_app.Domain.DataStore;
import com.example.guardian_app.R;

import java.util.ArrayList;
import java.util.List;

public class SelectChildForSafeZone extends AppCompatActivity implements ZoneAlreadyDefinedDialog.DefinedDialogListener {
    private ListView listView;
    private ArrayAdapter arrayAdapter;
    private DataStore dataStore;
    private int itemSelectedPosition;
    private String childChosen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_child_for_safe_zone);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            dataStore = extras.getParcelable("dataStore");
        }

        listView = (ListView) findViewById(R.id.list_view);
        List<String> childNames = new ArrayList<String>(dataStore.getChildNames());
        arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_single_choice, childNames);

        listView.setAdapter(arrayAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemSelectedPosition = position;
            }
        });
    }

    public void goToDefineSafeZone (View view){
        childChosen = listView.getItemAtPosition(itemSelectedPosition).toString();
        if (dataStore.getSafeZoneByChildName(childChosen) != null) {
            openDialog();
        }
        else {
            Intent intent = new Intent(this, DefineSafeZone.class);
            intent.putExtra("dataStore", dataStore);
            intent.putExtra("childChosen", childChosen);
            startActivity(intent);
        }
    }

    public void openDialog() {
        ZoneAlreadyDefinedDialog dialog = new ZoneAlreadyDefinedDialog();
        dialog.show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void okButtonPressed() {
        dataStore.removeSafeZone(childChosen);
        Intent intent = new Intent(this, DefineSafeZone.class);
        intent.putExtra("dataStore", dataStore);
        startActivity(intent);
    }

    @Override
    public void cancelButtonPressed() {

    }
}
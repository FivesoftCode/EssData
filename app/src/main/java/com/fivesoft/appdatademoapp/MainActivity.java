package com.fivesoft.appdatademoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.fivesoft.database.EssData;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private Button add;
    private Button remove;

    private EssData appData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appData = EssData.with(this)
                .setDocument("Default");

        listView = findViewById(R.id.listview);
        add = findViewById(R.id.add);
        remove = findViewById(R.id.remove);

        refreshList();

        add.setOnClickListener(v -> {
            appData.addToList("Default", "list", "ItemTest", 0);
            refreshList();
        });

        remove.setOnClickListener(v -> {
            appData.removeFromList("Default", "list", 0);
            refreshList();
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            appData.setInList("Default", "list", "Clicked", position);
            refreshList();
        });

    }

    private void refreshList() {
        List<String> items = appData.getList("list");

        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);

        L.logList(items);

        listView.setAdapter(itemsAdapter);
    }
}
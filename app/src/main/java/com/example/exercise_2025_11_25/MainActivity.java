package com.example.exercise_2025_11_25;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.widget.Toolbar;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class MainActivity extends AppCompatActivity {

    List<TodoItem> todoItemList = new ArrayList<>();
    ToDoItemAdapter adapter;
    ListView todoItemListView;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        assert intent != null;
                        int itemID = intent.getIntExtra("itemID", -1); // -1 when no ID is returned meaning new item
                        String itemTitle = intent.getStringExtra("itemTitle");
                        String itemDescription = intent.getStringExtra("itemDescription");
                        boolean itemStatus = intent.getBooleanExtra("itemStatus", false);
                        LocalDateTime itemDeadline = LocalDateTime.parse(intent.getStringExtra("itemDeadline"), formatter);
                        ArrayList<String> itemContacts = intent.getStringArrayListExtra("itemContacts");
                        if (itemID == -1){
                            itemID = ID_GEN.getAndIncrement();
                            TodoItem newItem = new TodoItem(itemID, itemTitle, itemDescription, itemDeadline, itemStatus, itemContacts);
                            todoItemList.add(newItem);
                        }
                        else {
                            for (TodoItem todoItem : todoItemList) {
                                if (todoItem.getItemID() == itemID) {
                                    todoItem.setTitle(itemTitle);
                                    todoItem.setDescription(itemDescription);
                                    todoItem.setDeadline(itemDeadline);
                                    todoItem.setStatus(itemStatus);
                                    todoItem.setRelatedContacts(itemContacts);
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            });
    private static final AtomicInteger ID_GEN = new AtomicInteger(1000);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setTitle("Todo List");

        todoItemListView = findViewById(R.id.todoItemListView);
        registerForContextMenu(todoItemListView);
        adapter = new ToDoItemAdapter(MainActivity.this, R.layout.list_item_todoitem, todoItemList);
        todoItemListView.setAdapter(adapter);

        todoItemListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(MainActivity.this, ToDoItemEditActivity.class);
            TodoItem item = todoItemList.get(position);
            intent.putExtra("itemID", item.getItemID());
            intent.putExtra("itemTitle", item.getTitle());
            intent.putExtra("itemDescription",item.getDescription());
            intent.putExtra("itemDeadline", item.getDeadline().format(formatter));
            intent.putExtra("itemStatus", item.getStatus());
            intent.putStringArrayListExtra("itemContacts", item.getRelatedContacts());
            launcher.launch(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    //Option menu section
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int optionId= item.getItemId();
        if (optionId == R.id.option_menu_add){
            Intent intent = new Intent(MainActivity.this, ToDoItemEditActivity.class);
            launcher.launch(intent);
        }

        if (optionId == R.id.option_menu_delete){
            boolean found = false;
            for (int i = 0; i < todoItemList.size(); i++) {
                if (todoItemList.get(i).isSelected()){
                    todoItemList.remove(i);
                    found = true;
                    i--;
                }
            }
            if (found) {
                adapter.notifyDataSetChanged();
            }
        }

        if (optionId == R.id.option_menu_select_all) {
            for (TodoItem item1 : todoItemList) {
                item1.setSelected(true);
            }
            adapter.notifyDataSetChanged();
        }
        return super.onOptionsItemSelected(item);
    }

    //Context menu section
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.todoItemListView) {
            getMenuInflater().inflate(R.menu.context_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (item.getItemId() == R.id.context_menu_edit){
            if (info != null) {
                todoItemListView.performItemClick(info.targetView, info.position, info.id);
            }
        }
        if (item.getItemId() == R.id.context_menu_delete){
            if (info != null) {
                todoItemList.remove(info.position);
                adapter.notifyDataSetChanged();
            }
        }
        return super.onContextItemSelected(item);
    }
}
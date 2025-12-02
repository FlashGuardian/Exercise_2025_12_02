package com.example.exercise_2025_11_25;

import static android.widget.Toast.LENGTH_SHORT;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int READ_CONTACTS_REQUEST_CODE = 1;
    private final int CONTACT_LOADER = 10;
    private boolean isASC = true;
    ListView contactListView;
    List<ContactItem> contacts = new ArrayList<>();
    ArrayList<String> itemPhones = new ArrayList<>();
    Button confirmButton;
    Button cancelButton;


    private ContactListAdapter contactListAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contact);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setTitle("Related Contacts");

        Intent incomingIntent = getIntent();
        if (incomingIntent != null) {
            itemPhones = incomingIntent.getStringArrayListExtra("itemContacts");
        }

        confirmButton = findViewById(R.id.contactConfirmButton);
        cancelButton = findViewById(R.id.contactCancelButton);
        contactListView = findViewById(R.id.contactListView);
        contactListAdapter = new ContactListAdapter(this, R.layout.list_item_contact_item, contacts);
        contactListView.setAdapter(contactListAdapter);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS_REQUEST_CODE);

        cancelButton.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
        ArrayList<String> newitemPhones = new ArrayList<>();
        confirmButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ToDoItemEditActivity.class);
            for (ContactItem contact : contacts) {
                if (contact.isSelected()) {
                    newitemPhones.add(contact.getPhone());
                }
            }
            intent.putStringArrayListExtra("itemContacts", newitemPhones);
            setResult(RESULT_OK, intent);
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_CONTACTS_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LoaderManager.getInstance(this).restartLoader(this.CONTACT_LOADER,null,this);

            } else {
                setResult(RESULT_CANCELED);
                finish();
            }
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        if (id == CONTACT_LOADER) {
            String[] SELECTED_FIELDS = new String[]
                    {
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                            ContactsContract.CommonDataKinds.Phone.NUMBER,
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    };
            return new CursorLoader(this, ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    SELECTED_FIELDS,
                    null,
                    null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " " + (isASC ? "ASC" : "DESC"));
        }

        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == CONTACT_LOADER) {
            List<ContactItem> newContacts = new ArrayList<>();
            if (data != null) {
                while (!data.isClosed() && data.moveToNext()) {
                    String phone = data.getString(1);
                    String name = data.getString(2);
                    newContacts.add(new ContactItem(name, phone));
                }
                contactListAdapter.clear();
                contactListAdapter.addAll(newContacts);
                if (itemPhones != null) {
                    for (String phone : itemPhones) {
                        for (ContactItem contact : contacts) {
                            if (phone.equals(contact.getPhone())) {
                                contact.setSelected(true);
                            }
                        }
                    }
                }
                contactListAdapter.notifyDataSetChanged();
                data.close();
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        loader = null;
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.contact_sort_option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.option_menu_asc) {
            isASC = true;
            LoaderManager.getInstance(this).restartLoader(this.CONTACT_LOADER,null,this);
        }
        else if (item.getItemId() == R.id.option_menu_desc){
            isASC = false;
            LoaderManager.getInstance(this).restartLoader(this.CONTACT_LOADER,null,this);
        }
        return super.onOptionsItemSelected(item);
    }
}
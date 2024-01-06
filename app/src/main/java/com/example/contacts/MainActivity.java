package com.example.contacts;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;
    private EditText searchEditText;
    private List<Contact> allContacts; // Store all contacts initially

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycleview);
        searchEditText = findViewById(R.id.searchEditText);

        // Check and request the READ_CONTACTS permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    1);  // The second parameter is a unique code to identify the request
        } else {
            // Permission is already granted, proceed with loading contacts
            contactAdapter = new ContactAdapter(new ArrayList<>(), this);

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(contactAdapter);

            // Load all contacts initially
            allContacts = getContacts();
            contactAdapter.updateContacts(allContacts);
        }

        // Set up EditText for searching
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Filter the contacts based on the search query
                List<Contact> filteredContacts = filterContacts(editable.toString());
                contactAdapter.updateContacts(filteredContacts);
            }
        });

        // Add the clear button code here
        ImageView clearButton = findViewById(R.id.clearButton);
        clearButton.setOnClickListener(view -> {
            searchEditText.setText("");
            // Reload all contacts when the search text is cleared
            contactAdapter.updateContacts(allContacts);
        });
    }

    // Handle the result of the permission request
    @SuppressWarnings("NullableProblems")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with loading contacts
                loadContacts();
            } else {
                // Permission denied, handle accordingly (show a message, disable features, etc.)
                Toast.makeText(this, "Permission denied. Cannot make calls.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadContacts() {
        List<Contact> contacts = getContacts();

        // Update the RecyclerView with the loaded contacts
        if (contactAdapter != null) {
            contactAdapter.updateContacts(contacts);
        }
    }

    private List<Contact> getContacts() {
        List<Contact> contactList = new ArrayList<>();

        Cursor cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));


                Contact contact = new Contact(name, phoneNumber);
                contactList.add(contact);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return contactList;
    }

    private List<Contact> filterContacts(String query) {
        List<Contact> filteredContacts = new ArrayList<>();

        if (allContacts != null) {
            for (Contact contact : allContacts) {
                if (contact.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredContacts.add(contact);
                }
            }
        }

        return filteredContacts;
    }
}

package com.example.contacts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> implements Filterable {

    private List<Contact> contactList;
    private List<Contact> contactListFull;  // Full list to use for filtering
    private Context context;

    public ContactAdapter(List<Contact> contactList, Context context) {
        this.contactList = contactList;
        this.contactListFull = new ArrayList<>(contactList);  // Copy the original list
        this.context = context;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);

        holder.contactName.setText(contact.getName());

        // holder.contactImage.setImageResource(R.drawable.ic_contact_placeholder);

        // Set click listener for the call button
        holder.callButton.setOnClickListener(v -> {
            // Retrieve the phone number associated with the clicked item
            String phoneNumber = contactList.get(position).getPhoneNumber();
            // Call the method to initiate the call
            initiateCall(phoneNumber);
        });
    }

    // Method to initiate a phone call
    private void initiateCall(String phoneNumber) {
        // Check for CALL_PHONE permission before initiating the call
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {
            // If permission is granted, initiate the call
            String dial = "tel:" + phoneNumber;
            context.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        } else {
            // Request CALL_PHONE permission if not granted
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{android.Manifest.permission.CALL_PHONE}, 1);
        }

    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    @Override
    public Filter getFilter() {
        return contactFilter;
    }

    private Filter contactFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Contact> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(contactListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Contact contact : contactListFull) {
                    if (contact.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(contact);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            contactList.clear();
            contactList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public void updateContacts(List<Contact> contacts) {
        contactList.clear();
        contactList.addAll(contacts);
        notifyDataSetChanged();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        ImageView contactImage;
        TextView contactName;
        ImageView callButton;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            contactImage = itemView.findViewById(R.id.contact_image);
            contactName = itemView.findViewById(R.id.contact_name);
            callButton = itemView.findViewById(R.id.call_button);
        }
    }
}

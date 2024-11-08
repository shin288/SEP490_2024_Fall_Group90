package com.example.ftopapplication.ui.send.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ftopapplication.R;
import com.example.ftopapplication.data.model.Contact;
import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private List<Contact> contacts;
    private List<Contact> selectedContacts = new ArrayList<>();
    private OnSelectedContactsChangeListener selectedContactsChangeListener;

    public interface OnSelectedContactsChangeListener {
        void onSelectedContactsChange(int selectedCount);
    }

    public ContactAdapter(Context context, List<Contact> contacts) {
        this.contacts = contacts;
    }

    public void setOnSelectedContactsChangeListener(OnSelectedContactsChangeListener listener) {
        this.selectedContactsChangeListener = listener;
    }

    public List<Contact> getSelectedContacts() {
        return selectedContacts;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.nameTextView.setText(contact.getName());
        holder.phoneTextView.setText(contact.getPhoneNumber());
        holder.checkBox.setChecked(selectedContacts.contains(contact));

        holder.itemView.setOnClickListener(v -> {
            if (selectedContacts.contains(contact)) {
                selectedContacts.remove(contact);
                holder.checkBox.setChecked(false);
            } else {
                selectedContacts.add(contact);
                holder.checkBox.setChecked(true);
            }
            if (selectedContactsChangeListener != null) {
                selectedContactsChangeListener.onSelectedContactsChange(selectedContacts.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, phoneTextView;
        CheckBox checkBox;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.contact_name);
            phoneTextView = itemView.findViewById(R.id.contact_phone);
            checkBox = itemView.findViewById(R.id.contact_checkbox);
        }
    }
}

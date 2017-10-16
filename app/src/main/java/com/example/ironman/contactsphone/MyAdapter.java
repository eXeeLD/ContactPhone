package com.example.ironman.contactsphone;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<Contacts> contactsList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView personName;
        TextView phoneTextView;
        SimpleDraweeView draweeView;

        ViewHolder(View itemView) {
            super(itemView);
            personName = (TextView) itemView.findViewById(R.id.info_text);
            phoneTextView = (TextView) itemView.findViewById(R.id.phoneTextView);
            draweeView = (SimpleDraweeView) itemView.findViewById(R.id.sdvImage);
        }
    }


    public MyAdapter(List<Contacts> contactsList) {
        this.contactsList = contactsList;
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (contactsList.get(position).getPhoto() != null) {
            holder.draweeView.setImageURI(contactsList.get(position).getPhoto());
        }
        holder.personName.setText(contactsList.get(position).getPhoneName());
        holder.phoneTextView.setText(contactsList.get(position).getPhoneNumber());
    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }
}
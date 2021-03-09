package adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import models.User;
import pedroadmn.whatsappclone.com.R;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder> {

    private List<User> contactList;
    private Context context;

    public ContactAdapter(Context context, List<User> contactList) {
        this.contactList = contactList;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView civContactImage;
        TextView tvContactName;
        TextView tvContactEmail;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            civContactImage = itemView.findViewById(R.id.civContact);
            tvContactName = itemView.findViewById(R.id.tvContactName);
            tvContactEmail = itemView.findViewById(R.id.tvContactEmail);
        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item_adapter, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user = contactList.get(position);

        boolean isHeader = user.getEmail().isEmpty();

        holder.tvContactName.setText(user.getName());
        holder.tvContactEmail.setText(user.getEmail());

        if(user.getPhoto() != null) {
            Uri url = Uri.parse(user.getPhoto());

            Glide.with(context)
                    .load(url)
                    .into(holder.civContactImage);
        } else {
            if (isHeader) {
                holder.civContactImage.setImageResource(R.drawable.group_icon);
                holder.tvContactEmail.setVisibility(View.GONE);
            } else {
                holder.civContactImage.setImageResource(R.drawable.padrao);
                holder.tvContactEmail.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return this.contactList.size();
    }
}

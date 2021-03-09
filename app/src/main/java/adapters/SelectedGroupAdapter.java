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

public class SelectedGroupAdapter extends RecyclerView.Adapter<SelectedGroupAdapter.MyViewHolder> {

    private List<User> memberList;
    private Context context;

    public SelectedGroupAdapter(Context context, List<User> memberList) {
        this.memberList = memberList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.selected_group_item_adapter, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedGroupAdapter.MyViewHolder holder, int position) {
        User user = memberList.get(position);

        holder.tvMemberName.setText(user.getName());

        if(user.getPhoto() != null) {
            Uri url = Uri.parse(user.getPhoto());

            Glide.with(context)
                    .load(url)
                    .into(holder.civMemberImage);
        } else {
            holder.civMemberImage.setImageResource(R.drawable.padrao);
        }
    }

    @Override
    public int getItemCount() {
        return this.memberList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView civMemberImage;
        TextView tvMemberName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            civMemberImage = itemView.findViewById(R.id.civMember);
            tvMemberName = itemView.findViewById(R.id.tvMemberName);
        }
    }
}

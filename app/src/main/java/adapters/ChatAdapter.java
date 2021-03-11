package adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import helper.FirebaseUserHelper;
import models.Message;
import pedroadmn.whatsappclone.com.R;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private List<Message> messageList;
    private Context context;

    private static final int SENDER = 0;
    private static final int RECIPIENT = 1;

    public ChatAdapter(Context context, List<Message> messageList) {
        this.messageList = messageList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item;

        if (viewType == SENDER) {
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.sender_message_item_adapter, parent, false);
        } else {
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipient_message_item_adapter, parent, false);
        }
        return new ChatAdapter.MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Message message = messageList.get(position);
        String msg = message.getMessage();
        String image = message.getImage();

        if (image != null) {
            Uri url = Uri.parse(image);

            Glide.with(context)
                    .load(url)
                    .into(holder.ivChatMessagePhoto);

            holder.ivChatMessagePhoto.setVisibility(View.VISIBLE);
            holder.tvMessage.setVisibility(View.GONE);
        } else {
            holder.tvMessage.setText(msg);
            holder.ivChatMessagePhoto.setVisibility(View.GONE);
        }

        String name = message.getUserName();

        if (!name.isEmpty()) {
            holder.tvExhibitionName.setText(name);
        } else {
            holder.tvExhibitionName.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);

        String userId = FirebaseUserHelper.getUserId();

        if (userId.equals(message.getUserId())) {
            return SENDER;
        }

        return RECIPIENT;
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;
        ImageView ivChatMessagePhoto;
        TextView tvExhibitionName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvChatMessage);
            ivChatMessagePhoto = itemView.findViewById(R.id.ivChatMessagePhoto);
            tvExhibitionName = itemView.findViewById(R.id.tvExhibitionName);
        }
    }
}

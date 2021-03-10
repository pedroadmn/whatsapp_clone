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
import models.Group;
import models.Talk;
import models.User;
import pedroadmn.whatsappclone.com.R;

public class TalkAdapter extends RecyclerView.Adapter<TalkAdapter.MyViewHolder> {

    private List<Talk> talkList;
    private Context context;

    public TalkAdapter(Context context, List<Talk> talkList) {
        this.talkList = talkList;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView civTalkContactImage;
        TextView tvTalkContactName;
        TextView tvTalkLastMessage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            civTalkContactImage = itemView.findViewById(R.id.civTalkContactImage);
            tvTalkContactName = itemView.findViewById(R.id.tvTalkContactName);
            tvTalkLastMessage = itemView.findViewById(R.id.tvTalkLastMessage);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.talk_item_adapter, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Talk talk = talkList.get(position);
        User exhibitionUser = talk.getUser();

        holder.tvTalkLastMessage.setText(talk.getLastMessage());

        if ("true".equals(talk.getIsGroup())) {
            Group group = talk.getGroup();
            holder.tvTalkContactName.setText(group.getName());

            if(group.getPhoto() != null) {
                Uri url = Uri.parse(group.getPhoto());

                Glide.with(context)
                        .load(url)
                        .into(holder.civTalkContactImage);
            } else {
                holder.civTalkContactImage.setImageResource(R.drawable.padrao);
            }
        } else {
            holder.tvTalkContactName.setText(exhibitionUser.getName());

            if(exhibitionUser.getPhoto() != null) {
                Uri url = Uri.parse(exhibitionUser.getPhoto());

                Glide.with(context)
                        .load(url)
                        .into(holder.civTalkContactImage);
            } else {
                holder.civTalkContactImage.setImageResource(R.drawable.padrao);
            }
        }
    }

    @Override
    public int getItemCount() {
        return talkList.size();
    }
}

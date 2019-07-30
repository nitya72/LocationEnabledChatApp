package com.cellfishpool.app.powwow;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT=0;
    public static final int MSG_TYPE_RIGHT=1;

    private Context mContext;
    private List<Chat> mChat;

    FirebaseUser cuser;

    public MessageAdapter(Context mContext,List<Chat> mChat){
        this.mChat=mChat;
        this.mContext=mContext;

    }


    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(i==MSG_TYPE_RIGHT){
            View view=LayoutInflater.from(mContext).inflate(R.layout.chat_item_right,viewGroup,false);
            return new MessageAdapter.ViewHolder(view);
        }else {
            View view=LayoutInflater.from(mContext).inflate(R.layout.chat_item_left,viewGroup,false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder viewHolder, int i) {

        Chat chat=mChat.get(i);

        viewHolder.msg.setText(chat.getMessage());

    }


    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView msg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            msg=itemView.findViewById(R.id.showMessage);
        }
    }

    @Override
    public int getItemViewType(int position) {
        cuser=FirebaseAuth.getInstance().getCurrentUser();

        if (mChat.get(position).getSender().equals(cuser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else {
            return MSG_TYPE_LEFT;
        }
    }
}

package com.example.dai.oicq_android.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dai.oicq_android.R;
import com.example.dai.oicq_android.entity.Talk;

import java.util.ArrayList;
import java.util.List;



/**
 * @author dailiwen
 * @date 2018/06/23
 */
public class TalkAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Talk> massage = new ArrayList<>();

    public static enum ITEM_TYPE{
        ITEM_TYPE_LEFT,
        ITEM_TYPE_RIGHT
    }

    static class ReceiveViewHolder extends RecyclerView.ViewHolder {
        TextView receiveText;

        public ReceiveViewHolder(View receiveView) {
            super(receiveView);
            receiveText = (TextView) receiveView.findViewById(R.id.receive_chat);
        }
    }

    static class SendViewHolder extends RecyclerView.ViewHolder {
        TextView sendText;

        public SendViewHolder(View sendView) {
            super(sendView);
            sendText = (TextView) sendView.findViewById(R.id.send_chat);
        }
    }

    public TalkAdapter(List<Talk> message) {
        this.massage = message;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == ITEM_TYPE.ITEM_TYPE_LEFT.ordinal()) {
            return new ReceiveViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.receive_chat_box, parent, false));
        } else {
            return new SendViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.send_chat_box, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ReceiveViewHolder){
            ((ReceiveViewHolder) holder).receiveText.setText(massage.get(position).getMessage());
        }else if(holder instanceof  SendViewHolder){
            ((SendViewHolder) holder).sendText.setText(massage.get(position).getMessage());
        }
    }


    @Override
    public int getItemCount() {
        return massage.size();
    }

    @Override
    public int getItemViewType(int position) {
        return massage.get(position).getType() == 1 ? ITEM_TYPE.ITEM_TYPE_LEFT.ordinal() : ITEM_TYPE.ITEM_TYPE_RIGHT.ordinal() ;
    }
}

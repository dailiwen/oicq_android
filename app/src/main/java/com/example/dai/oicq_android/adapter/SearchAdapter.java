package com.example.dai.oicq_android.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.dai.oicq_android.R;
import com.example.dai.oicq_android.entity.Account;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dailiwen
 * @date 2018/04/03
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    List<Account> accounts = new ArrayList<>();
    private ItemClickListener itemClickListener;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView friendId;
        TextView friendName;
        TextView addBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            friendId = (TextView) itemView.findViewById(R.id.friend_id);
            friendName = (TextView) itemView.findViewById(R.id.friend_name);
            addBtn = (TextView) itemView.findViewById(R.id.add_btn);
        }
    }

    public SearchAdapter(List<Account> accounts) {
        this.accounts = accounts;
    }

    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_friend_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final SearchAdapter.ViewHolder holder, final int position) {
        Account account = accounts.get(position);
        holder.friendId.setText(account.getId());
        holder.friendName.setText(account.getLoginName());
        holder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(holder.addBtn, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    /**
     * @param itemClickListener
     */
    public void setOnItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    /**
     * item点击事件回调接口
     */
    public interface ItemClickListener {
        /**
         * 点击事件
         * @param view
         * @param position 第几个item
         */
        void onItemClick(View view, int position);
    }
}

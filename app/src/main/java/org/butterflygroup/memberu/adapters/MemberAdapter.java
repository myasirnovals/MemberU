package org.butterflygroup.memberu.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.butterflygroup.memberu.R;
import org.butterflygroup.memberu.models.MemberCard;

import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {
    public interface OnMemberClickListener {
        void onMemberClick(MemberCard card);
    }

    private List<MemberCard> memberList;
    private final OnMemberClickListener listener;

    public MemberAdapter(List<MemberCard> memberList, OnMemberClickListener listener) {
        this.memberList = memberList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        MemberCard card = memberList.get(position);

        holder.tvMerchantName.setText(card.getMerchantName());
        holder.tvMemberNumber.setText("ID: " + card.getMemberNumber());
        holder.tvTier.setText(card.getTier());

        if (listener != null) {
            holder.itemView.setOnClickListener(v -> listener.onMemberClick(card));
        } else {
            holder.itemView.setOnClickListener(null);
        }
    }


    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public void updateData(List<MemberCard> newMemberList) {
        this.memberList = newMemberList;
        notifyDataSetChanged();
    }

    public static class MemberViewHolder extends RecyclerView.ViewHolder {
        TextView tvMerchantName, tvMemberNumber, tvTier;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMerchantName = itemView.findViewById(R.id.tv_merchant_name);
            tvMemberNumber = itemView.findViewById(R.id.tv_member_number);
            tvTier = itemView.findViewById(R.id.tv_tier);
        }
    }
}
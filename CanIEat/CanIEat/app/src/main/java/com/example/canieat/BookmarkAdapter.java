package com.example.canieat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder> {

    private List<BookmarkItem> bookmarkItems;
    private final OnDeleteClickListener deleteClickListener;
    private OnItemClickListener itemClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(BookmarkItem item);
    }

    public interface OnItemClickListener {
        void onItemClick(BookmarkItem item);
    }

    public BookmarkAdapter(List<BookmarkItem> bookmarkItems, OnDeleteClickListener listener) {
        this.bookmarkItems = bookmarkItems;
        this.deleteClickListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bookmark_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookmarkItem item = bookmarkItems.get(position);

        if (item.isPlaceholderImage) {
            // humm 이미지면 이미지 숨기고 이름 중앙 표시
            holder.image.setVisibility(View.GONE);
            holder.textNameCard.setVisibility(View.GONE);
            holder.textNameCenter.setVisibility(View.VISIBLE);
            holder.textNameCenter.setText(item.name);
        } else {
            //이미지 있을 경우
            holder.image.setVisibility(View.VISIBLE);
            holder.textNameCard.setVisibility(View.VISIBLE);
            holder.textNameCenter.setVisibility(View.GONE);
            Glide.with(holder.itemView.getContext())
                    .load(item.imageUrl)
                    .into(holder.image);
            holder.textNameCard.setText(item.name);
        }

        // 삭제 버튼
        holder.deleteBtn.setOnClickListener(v -> deleteClickListener.onDeleteClick(item));

        // 아이템 클릭 → 상세 보기
        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookmarkItems.size();
    }

    public void updateList(List<BookmarkItem> newList) {
        bookmarkItems = newList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        ImageButton deleteBtn;
        TextView textNameCard;
        TextView textNameCenter;

        ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_card);
            deleteBtn = itemView.findViewById(R.id.button_delete);
            textNameCard = itemView.findViewById(R.id.text_name_card);
            textNameCenter = itemView.findViewById(R.id.text_name_center);
        }
    }
}

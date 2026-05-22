package com.example.husaybaybay.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.husaybaybay.R;
import com.example.husaybaybay.data.model.DictionaryRepository.DictionaryWord;

import java.util.List;

public class DictionaryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_WORD = 1;

    private Context context;
    private List<Object> items;

    public DictionaryAdapter(Context context, List<Object> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof String) {
            return TYPE_HEADER;
        }
        return TYPE_WORD;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_dictionary_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_dictionary_word, parent, false);
            return new WordViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_HEADER) {
            String letter = (String) items.get(position);
            ((HeaderViewHolder) holder).tvHeader.setText(letter + ".");
        } else {
            DictionaryWord dw = (DictionaryWord) items.get(position);
            ((WordViewHolder) holder).btnWord.setText(dw.word);
            ((WordViewHolder) holder).btnWord.setOnClickListener(v -> {
                Intent intent = new Intent(context, WordDetailActivity.class);
                intent.putExtra("WORD_TEXT", dw.word);
                intent.putExtra("WORD_MEANING", dw.meaning);
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeader;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.tvHeader);
        }
    }

    static class WordViewHolder extends RecyclerView.ViewHolder {
        Button btnWord;

        WordViewHolder(@NonNull View itemView) {
            super(itemView);
            btnWord = itemView.findViewById(R.id.btnWord);
        }
    }
}

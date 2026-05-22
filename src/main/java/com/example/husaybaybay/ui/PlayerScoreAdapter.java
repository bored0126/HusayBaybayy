package com.example.husaybaybay.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.husaybaybay.R;
import com.example.husaybaybay.data.model.PlayerScore;

import java.util.List;

public class PlayerScoreAdapter extends RecyclerView.Adapter<PlayerScoreAdapter.ViewHolder> {

    private final List<PlayerScore> playerList;
    private final int totalDictionaryWords;

    public PlayerScoreAdapter(List<PlayerScore> playerList, int totalDictionaryWords) {
        this.playerList = playerList;
        this.totalDictionaryWords = totalDictionaryWords;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_player_score, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlayerScore player = playerList.get(position);

        holder.tvPlayerName.setText(player.getName() != null ? player.getName() : "Unknown");
        holder.tvPlayerEmail.setText(player.getEmail() != null ? player.getEmail() : "");
        holder.tvPlayerSection.setText(player.getSection() != null ? player.getSection() : "No Section");

        // Game 1: show lastScore/highScore (out of 30 questions)
        holder.tvGame1Score.setText(player.getGame1LastScore() + "/" + player.getGame1HighScore());

        // Game 2: words completed out of total dictionary words
        holder.tvGame2Score.setText(player.getGame2WordsCompleted() + "/" + totalDictionaryWords);

        // Game 3: words completed out of total dictionary words
        holder.tvGame3Score.setText(player.getGame3WordsCompleted() + "/" + totalDictionaryWords);

        // Alternate row colors for readability
        if (position % 2 == 0) {
            holder.itemView.setBackgroundResource(R.drawable.bg_table_row);
        } else {
            holder.itemView.setBackgroundColor(0xFFFFFFFF);
        }
    }

    @Override
    public int getItemCount() {
        return playerList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlayerName, tvPlayerEmail, tvPlayerSection, tvGame1Score, tvGame2Score, tvGame3Score;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlayerName = itemView.findViewById(R.id.tvPlayerName);
            tvPlayerEmail = itemView.findViewById(R.id.tvPlayerEmail);
            tvPlayerSection = itemView.findViewById(R.id.tvPlayerSection);
            tvGame1Score = itemView.findViewById(R.id.tvGame1Score);
            tvGame2Score = itemView.findViewById(R.id.tvGame2Score);
            tvGame3Score = itemView.findViewById(R.id.tvGame3Score);
        }
    }
}

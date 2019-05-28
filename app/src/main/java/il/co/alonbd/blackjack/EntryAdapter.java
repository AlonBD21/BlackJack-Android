package il.co.alonbd.blackjack;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.EntryViewHolder> {
    private List<HallOfFame.Entry> list;

    public EntryAdapter(List<HallOfFame.Entry> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_entry, viewGroup, false);
        EntryViewHolder entryViewHolder = new EntryViewHolder(v);
        return entryViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder entryViewHolder, int i) {
        entryViewHolder.score.setText(list.get(i).getScore() + "");
        entryViewHolder.player.setText(list.get(i).getName());
        int loaction = i + 1;
        entryViewHolder.place.setText("#" + loaction + "");

        switch (loaction) {
            case 1:
                entryViewHolder.place.setTextColor(Color.rgb(255,215,0));
                break;
            case 2:
                entryViewHolder.place.setTextColor(Color.rgb(192,192,192));
                break;
            case 3:
                entryViewHolder.place.setTextColor(Color.rgb(205,127,50));
                break;
            default:
                entryViewHolder.place.setTextColor(Color.rgb(40,40,40));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class EntryViewHolder extends RecyclerView.ViewHolder {
        TextView place, player, score;

        public EntryViewHolder(@NonNull View itemView) {
            super(itemView);

            place = itemView.findViewById(R.id.place);
            player = itemView.findViewById(R.id.name);
            score = itemView.findViewById(R.id.score);
        }

    }
}

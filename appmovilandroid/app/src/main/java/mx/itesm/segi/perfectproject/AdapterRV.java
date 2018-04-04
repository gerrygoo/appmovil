package mx.itesm.segi.perfectproject;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class AdapterRV extends RecyclerView.Adapter<AdapterRV.ViewCard> {

    private String[] titles;
    private String[] startDates;
    private String[] endDates;
    private boolean[] news;
    YourProjectsFrag.Listener listener;

    public AdapterRV(String[] titles, String[] startDates, String[] endDates, boolean[] news, YourProjectsFrag.Listener listener)
    {
        this.titles = titles;
        this.startDates = startDates;
        this.endDates = endDates;
        this.news = news;
        this.listener = listener;
    }
    @Override
    public ViewCard onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView card = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.project_card, parent, false);
        return new ViewCard(card);
    }

    @Override
    public void onBindViewHolder(final ViewCard holder, final int position) {
        CardView card = holder.card;
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.itemClicked(position);
                listener.clearNews();
            }
        });
        TextView tvTitle = card.findViewById(R.id.projectCard_Title);
        TextView tvStartDate = card.findViewById(R.id.projectCard_startDate);
        TextView tvEndDate = card.findViewById(R.id.projectCard_endDate);
        tvTitle.setText(titles[position]);
        tvStartDate.setText(startDates[position]);
        tvEndDate.setText(endDates[position]);
        if(!news[position])
        {
            ImageView isNew = card.findViewById(R.id.ivNew);
            isNew.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    public class ViewCard extends RecyclerView.ViewHolder{

        private CardView card;
        public ViewCard(CardView v) {
            super(v);
            this.card = v;
        }
    }
}

package mx.itesm.segi.perfectproject;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class AdapterRV extends RecyclerView.Adapter<AdapterRV.ViewCard> {

    private String[] titles;
    private String[] descriptions;
    private String[] dates;
    private boolean[] news;

    public AdapterRV(String[] titles, String[] descriptions, String[] dates, boolean[] news)
    {
        this.titles = titles;
        this.descriptions = descriptions;
        this.dates = dates;
        this.news = news;
    }
    @Override
    public ViewCard onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView card = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_card, parent, false);
        return new ViewCard(card);
    }

    @Override
    public void onBindViewHolder(final ViewCard holder, final int position) {
        CardView card = holder.card;
        TextView tvDate = card.findViewById(R.id.notificationCard_Date);
        TextView tvTitle = card.findViewById(R.id.notificationCard_Title);
        TextView tvDescription = card.findViewById(R.id.notificationCard_Description);
        Button ok = card.findViewById(R.id.notificationCard_Ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Do something to delete notification
            }
        });
        tvDate.setText(dates[position]);
        tvTitle.setText(titles[position]);
        tvDescription.setText(descriptions[position]);
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

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import Model.Project;


public class AdapterRV extends RecyclerView.Adapter<AdapterRV.ViewCard> {

    private ArrayList<Project> projects;
    private HashMap<Project, Boolean> notifications;
    private boolean owned;

    YourProjectsFrag.Listener listener;

    public AdapterRV(ArrayList<Project> projects, Boolean owned, YourProjectsFrag.Listener listener) {
        this.projects = projects;
        this.listener = listener;
        this.owned = owned;
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
        Project currentProject = projects.get(position);

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.itemClicked(position);
                listener.clearNew(position);
            }
        });
        TextView tvTitle = card.findViewById(R.id.projectCard_Title);
        TextView tvStartDate = card.findViewById(R.id.projectCard_startDate);
        TextView tvEndDate = card.findViewById(R.id.projectCard_endDate);

        DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        
        tvTitle.setText(currentProject.getTitle());
        tvStartDate.setText(dateFormatter.format(currentProject.getStartDate()));
        
        tvEndDate.setText(dateFormatter.format(currentProject.getEndDate()));

        if (owned || notifications == null || (notifications.containsKey(currentProject) && !notifications.get(currentProject))) {
            ImageView isNew = card.findViewById(R.id.ivNew);
            isNew.setVisibility(View.INVISIBLE);
        }
    }

    public void setNotifications(HashMap<Project, Boolean> notifications) {
        this.notifications = notifications;
    }

    public void setProjects(ArrayList<Project> projects){
        this.projects = projects;
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    public class ViewCard extends RecyclerView.ViewHolder {

        private CardView card;

        public ViewCard(CardView v) {
            super(v);
            this.card = v;
        }
    }
}

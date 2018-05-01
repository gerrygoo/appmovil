package mx.itesm.segi.perfectproject;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import Model.Project;
import Model.User;


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
        final Project currentProject = projects.get(position);

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

        if(owned)
        {
            LinearLayout container = card.findViewById(R.id.project_card_container);
            final ArrayList<User> applicants = currentProject.getApplicants();
            for(int i =0;i < applicants.size();i++)
            {
                LinearLayout applicantCard = new LinearLayout(card.getContext());
                applicantCard.setOrientation(LinearLayout.HORIZONTAL);
                final int index = i;
                applicantCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /** ADD TO THE LISTENER INTERFACE THE METHOD THAT LOOKS FOR A PROFILE AND SEND THE USER IDENTIFIER TO THAT METHOD **/
                        Log.d("AdapterRV", "Look for profile " + applicants.get(index).toString());
                    }
                });

                TextView name = new TextView(card.getContext());
                name.setText(applicants.get(i).getName());
                applicantCard.addView(name);

                Button accept = new Button(card.getContext());
                accept.setText("Accept");
                accept.setBackgroundColor(card.getResources().getColor(R.color.holo_green_dark));
                accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Model.Model.getInstance().reviewApplicant(currentProject, applicants.get(index), true);
                    }
                });

                Button deny = new Button(card.getContext());
                deny.setText("Deny");
                deny.setBackgroundColor(card.getResources().getColor(R.color.holo_red_dark));
                deny.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Model.Model.getInstance().reviewApplicant(currentProject, applicants.get(index), false);
                    }
                });

                container.addView(applicantCard);
            }
        }
        
        tvEndDate.setText(dateFormatter.format(currentProject.getEndDate()));

        if (owned || notifications == null || (notifications.containsKey(currentProject) && !notifications.get(currentProject))) {
            ImageView isNew = card.findViewById(R.id.ivNew);
            isNew.setVisibility(View.INVISIBLE);
        }
    }

    public void setNotifications(HashMap<Project, Boolean> notifications) {
        this.notifications = notifications;
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

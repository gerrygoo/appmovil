package mx.itesm.segi.perfectproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.Tasks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import Model.Model;
import Model.Project;
import Model.User;


public class AdapterRV extends RecyclerView.Adapter<AdapterRV.ViewCard> {

    private ArrayList<Project> projects;
    private HashMap<String, Boolean> notifications;
    private boolean owned;

    private YourProjectsFrag.Listener listener;

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

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onBindViewHolder(final ViewCard holder, final int position) {
        final CardView card = holder.card;
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
        final ImageView ivImage = card.findViewById(R.id.projectCard_Logo);
        DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        
        tvTitle.setText(currentProject.getTitle());
        tvStartDate.setText(dateFormatter.format(currentProject.getStartDate()));

        if(owned)
        {
            final LinearLayout container = card.findViewById(R.id.project_card_container);

            new AsyncTask<Void, Void, ArrayList<User>>(){

                @Override
                protected ArrayList<User> doInBackground(Void... voids) {
                    try {
                        return Tasks.await(Model.getInstance().getApplicants(currentProject.getUID()));
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(final ArrayList<User> applicants) {
                    for(int i =0;i < applicants.size();i++)
                    {
                        LinearLayout applicantCard = new LinearLayout(card.getContext());
                        applicantCard.setPadding(8,8,8,8);
                        applicantCard.setOrientation(LinearLayout.HORIZONTAL);
                        final int index = i;
                        applicantCard.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                listener.loadProfile(applicants.get(index));
                            }
                        });

                        TextView name = new TextView(card.getContext());
                        name.setPadding(8, 8, 8, 8);
                        name.setTextSize(20);
                        DisplayMetrics displayMetrics = new DisplayMetrics();
                        ((Activity)card.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                        name.setWidth(displayMetrics.widthPixels/3);
                        name.setText(applicants.get(i).getName());
                        applicantCard.addView(name);

                        Button accept = new Button(card.getContext());
                        accept.setText("Accept");
                        accept.setBackgroundColor(card.getResources().getColor(R.color.holo_green_dark));
                        accept.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Model.getInstance().reviewApplicant(currentProject, applicants.get(index), true);
                                listener.reloadYourProjects();
                            }
                        });
                        applicantCard.addView(accept);

                        Button deny = new Button(card.getContext());
                        deny.setText("Deny");
                        deny.setBackgroundColor(card.getResources().getColor(R.color.holo_red_dark));
                        deny.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Model.getInstance().reviewApplicant(currentProject, applicants.get(index), false);
                                listener.reloadYourProjects();
                            }
                        });
                        applicantCard.addView(deny);

                        container.addView(applicantCard);
                    }
                }
            }.execute();
        }
        currentProject.setImageListener(new ImageListener() {
            @Override
            public void onImageAvailable(Bitmap image) {
                ivImage.setImageBitmap(currentProject.getImage());
            }
        });
        tvEndDate.setText(dateFormatter.format(currentProject.getEndDate()));

        if(notifications != null) {
            Log.e("NotificationViewd", notifications.toString());
        }
        if (owned || notifications == null || (notifications.containsKey(currentProject.getUID()) && notifications.get(currentProject.getUID()))) {
            ImageView isNew = card.findViewById(R.id.ivNew);
            isNew.setVisibility(View.INVISIBLE);
        }
    }

    public void setNotifications(HashMap<String, Boolean> notifications) {
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

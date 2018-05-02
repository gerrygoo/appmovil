package mx.itesm.segi.perfectproject;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.graphics.drawable.Icon;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.Tasks;

import org.w3c.dom.Text;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.StringJoiner;
import java.util.concurrent.ExecutionException;

import Model.Model;
import Model.Project;
import Model.User;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProjectInfoFrag extends Fragment {

    public static final String ARG_PROJECT = "Project";
    public static final String ARG_OWNED = "owned";
    private Project project;
    private TextView tvTitle;
    private TextView tvStartDate;
    private TextView tvEndDate;
    private TextView tvPositions;
    private TextView tvLocation;
    private TextView tvDescription;
    private ImageView ivLogo;
    private LinearLayout llContainer;
    private FloatingActionButton fabDelete;
    private FloatingActionButton fabEdit;
    private boolean owned;

    public ProjectInfoFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_project_info, container, false);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onStart() {
        super.onStart();

        final ProgressBar bar = getActivity().findViewById(R.id.mainProgress);
        bar.setVisibility(View.VISIBLE);

        new AsyncTask<Void, Void, Pair<User, ArrayList<User>>>(){

            @Override
            protected Pair<User, ArrayList<User>> doInBackground(Void... voids) {
                try {
                    Project project = getArguments().getParcelable(ARG_PROJECT);
                    User owner = Tasks.await(Model.getInstance().getUserByID(project.getOwnerUID()));
                    ArrayList<User> team = Tasks.await(Model.getInstance().getTeam(project.getUID()));
                    return new Pair<>(owner, team);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Pair<User, ArrayList<User>> pair) {
                loadProject(pair.first, pair.second);
                bar.setVisibility(View.INVISIBLE);
            }
        }.execute();
    }

    private void loadProject(final User owner, final ArrayList<User> team) {

        this.owned = getArguments().getBoolean(ARG_OWNED);
        this.project = getArguments().getParcelable(ARG_PROJECT);

        tvTitle = getActivity().findViewById(R.id.project_Title);
        tvStartDate = getActivity().findViewById(R.id.project_startDate);
        tvEndDate = getActivity().findViewById(R.id.project_endDate);
        tvPositions = getActivity().findViewById(R.id.project_positions);
        tvLocation = getActivity().findViewById(R.id.project_location);
        tvDescription = getActivity().findViewById(R.id.project_description);
        ivLogo = getActivity().findViewById(R.id.project_Logo);
        llContainer = getActivity().findViewById(R.id.project_container);
        fabDelete = getActivity().findViewById(R.id.fabDelete);
        fabEdit = getActivity().findViewById(R.id.fabEdit);

        tvTitle.setText(project.getTitle());
        tvStartDate.setText(android.text.format.DateFormat.format("dd/MM/yyyy", project.getStartDate()));
        tvEndDate.setText(android.text.format.DateFormat.format("dd/MM/yyyy", project.getEndDate()));

        ArrayList<String> positions = project.getPositions();

        String positionsText = "";
        for(int i = 0;i < positions.size();i++)
        {
            positionsText += positions.get(i);
            if(i!=positions.size()-1) positionsText += " ";
        }
        tvPositions.setText(positionsText);
        tvLocation.setText(project.getLocation());
        tvDescription.setText(project.getDescription());
        ivLogo.setImageBitmap(project.getImage());

        final TextView projectManager = new TextView(getContext());
        projectManager.setPadding(8, 8, 8, 8);
        projectManager.setTextSize(20);
        projectManager.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        projectManager.setText("PM: " + owner);
        projectManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment profile = new OtherProfileFrag();
                Bundle argsProfile = new Bundle();

                argsProfile.putParcelable(OtherProfileFrag.ARG_USER, owner);
                argsProfile.putBoolean(OtherProfileFrag.ARG_JOINED, true);

                profile.setArguments(argsProfile);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentPlacer, profile).addToBackStack(MainScreenActivity.BACK_STACK);
                transaction.commit();
            }
        });
        llContainer.addView(projectManager);
        for(int i = 0;i < team.size();i++)
        {
            TextView user = new TextView(getContext());
            user.setPadding(8, 8, 8, 8);
            user.setTextSize(20);
            user.setText(team.get(i).getName());
            final int index = i;
            user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment profile = new OtherProfileFrag();
                    Bundle argsProfile = new Bundle();

                    argsProfile.putParcelable(OtherProfileFrag.ARG_USER, team.get(index));
                    argsProfile.putBoolean(OtherProfileFrag.ARG_JOINED, true);

                    profile.setArguments(argsProfile);
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentPlacer, profile).addToBackStack(MainScreenActivity.BACK_STACK);
                    transaction.commit();
                }
            });
            llContainer.addView(user);
        }
        if(owned)
        {
            fabEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment editProject = new CreateProject();
                    Bundle argEditProject = new Bundle();
                    argEditProject.putBoolean(CreateProject.ARG_EDITING, true);
                    argEditProject.putParcelable(ARG_PROJECT, project);

                    editProject.setArguments(argEditProject);
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentPlacer, editProject).addToBackStack(MainScreenActivity.BACK_STACK);
                    transaction.commit();
                }
            });

            fabDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder confirm = new AlertDialog.Builder(getActivity());
                    confirm.setTitle("Delete project");
                    confirm.setMessage("Do you want to delete this project");
                    confirm.setCancelable(false);
                    confirm.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteProject();
                        }
                    });
                    confirm.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    confirm.show();
                }
            });
        }
        else
        {
            fabEdit.setVisibility(View.INVISIBLE);
            fabDelete.setVisibility(View.INVISIBLE);
        }
    }

    private void deleteProject() {
        Model.getInstance().deleteProject(project);
        Fragment yourProjects = new YourProjectsFrag();
        Bundle argYourProjects = new Bundle();

        argYourProjects.putParcelable(YourProjectsFrag.ARG_USER, Model.getInstance().getCurrentUser());
        argYourProjects.putBoolean(YourProjectsFrag.ARG_OWNED, owned);

        yourProjects.setArguments(argYourProjects);
        getFragmentManager().beginTransaction().replace(R.id.fragmentPlacer, yourProjects).commit();
    }

}

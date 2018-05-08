package mx.itesm.segi.perfectproject;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import Model.Model;
import Model.User;
import Model.Project;


/**
 * A simple {@link Fragment} subclass.
 */
public class YourProjectsFrag extends Fragment {

    public static final String ARG_USER = "user";
    public static final String ARG_OWNED = "owned";

    private RecyclerView rvYourProjects;
    private Listener listener;
    private User user;
    private ArrayList<Project> projects;
    private boolean owned;
    private boolean shouldRemoveBackStack;

    public YourProjectsFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_yourprojects, container, false);
        rvYourProjects = v.findViewById(R.id.list);
        rvYourProjects.setLayoutManager(new GridLayoutManager(getContext(),1));
        shouldRemoveBackStack = false;
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadProjects();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.listener = new Listener() {
            @Override
            public void itemClicked(long id) {
                shouldRemoveBackStack = true;

                renderProject((int)id);
            }

            @Override
            public void clearNew(int position) {
                Model.getInstance().viewNotification(projects.get(position));
            }

            @Override
            public void reloadYourProjects() {
                loadProjects();
            }

            @Override
            public void loadProfile(User user) {
                Fragment profile = new OtherProfileFrag();
                Bundle argsProfile = new Bundle();

                argsProfile.putParcelable(OtherProfileFrag.ARG_USER, user);
                argsProfile.putBoolean(OtherProfileFrag.ARG_JOINED, false);

                profile.setArguments(argsProfile);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentPlacer, profile).addToBackStack(MainScreenActivity.BACK_STACK);
                transaction.commit();
            }
        };
    }

    @SuppressLint("StaticFieldLeak")
    private void loadProjects() {
        this.user = getArguments().getParcelable(ARG_USER);
        this.owned = getArguments().getBoolean(ARG_OWNED);


        final ProgressBar bar = getActivity().findViewById(R.id.mainProgress);

        final AdapterRV adapterRV = new AdapterRV(new ArrayList<Project>(), owned, listener, bar);

        bar.setVisibility(View.VISIBLE);

        new AsyncTask<Void, Void, ArrayList<Project>>(){

            @Override
            protected ArrayList<Project> doInBackground(Void... voids) {
                try {
                    if(owned) {
                        ArrayList<Project> projects = Tasks.await(Model.getInstance().getOwnedProjects());
                        Log.e("Lengths", "" + Model.getInstance().getCurrentUser().getProjectsOwned() + "" + projects.size() +"");
                        return projects;
                    } else {

                        Model.getInstance().getCurrentUser().setProjectsMember(Tasks.await( Model.getInstance().getMemberedProjects(user.getUID()) ));
                        ArrayList<Project> projects = Tasks.await( Model.getInstance().getMyProjects() );
                        Log.i("Membered", projects.toString());
                        return projects;
                    }
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<Project> param) {
                if(owned){
                    adapterRV.setProjects(param);
                    adapterRV.notifyDataSetChanged();
                } else {
                    adapterRV.setProjects(param);
                    adapterRV.setNotifications(user.getNotifications());
                    adapterRV.notifyDataSetChanged();
                }
                projects = param;
                Log.e("Fetched", "true" + param.size());

                bar.setVisibility(View.INVISIBLE);
                Log.e("Invisible", "true");
            }
        }.execute();


        rvYourProjects.setAdapter(adapterRV);
    }

    private void renderProject(int position) {
        Fragment fragment = new ProjectInfoFrag();
        Bundle args = new Bundle();
        args.putParcelable(ProjectInfoFrag.ARG_PROJECT, projects.get(position));
        args.putBoolean(ProjectInfoFrag.ARG_OWNED, owned);
        fragment.setArguments(args);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentPlacer, fragment).addToBackStack(MainScreenActivity.BACK_STACK);
        transaction.commit();
    }

    static interface Listener{
        void itemClicked(long id);
        void clearNew(int position);
        void reloadYourProjects();
        void loadProfile(User user);
    }

    @Override
    public void onDetach() {
        if(shouldRemoveBackStack) {
            getActivity().getSupportFragmentManager().popBackStackImmediate(MainScreenActivity.BACK_STACK, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        super.onDetach();
    }
}

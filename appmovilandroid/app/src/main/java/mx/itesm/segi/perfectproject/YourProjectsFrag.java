package mx.itesm.segi.perfectproject;


import android.annotation.SuppressLint;
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
        };
    }

    @SuppressLint("StaticFieldLeak")
    private void loadProjects() {
        this.user = getArguments().getParcelable(ARG_USER);
        this.owned = getArguments().getBoolean(ARG_OWNED);
        final AdapterRV adapterRV = new AdapterRV(new ArrayList<Project>(), owned, listener);

        new AsyncTask<Void, Void, Pair<ArrayList<Project>, HashMap<Project, Boolean>>>(){
            @Override
            protected Pair<ArrayList<Project>, HashMap<Project, Boolean>> doInBackground(Void... voids) {
                try {
                    if(owned) {
                        ArrayList<Project> projects = Tasks.await(Model.getInstance().getOwnedProjects());
                        Log.e("Lengths", "" + Model.getInstance().getCurrentUser().getProjectsOwned() + "" + projects.size() +"");
                        return new Pair<>(projects, null);
                    } else {
                        ArrayList<Project> projects = Tasks.await(Model.getInstance().getMyProjects());
                        HashMap<Project, Boolean> notifications = Tasks.await(Model.getInstance().getNotifications());
                        return new Pair<>(projects, notifications);
                    }
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Pair<ArrayList<Project>, HashMap<Project, Boolean>> pair) {
                if(owned){
                    adapterRV.setProjects(pair.first);
                    adapterRV.notifyDataSetChanged();
                } else {
                    adapterRV.setProjects(pair.first);
                    adapterRV.setNotifications(pair.second);
                    adapterRV.notifyDataSetChanged();
                }
                projects = pair.first;
                Log.e("Fetched", "true" + pair.first.size());
            }
        }.execute();
        rvYourProjects.setAdapter(adapterRV);
    }

    private void renderProject(int position) {
        Fragment fragment = MainScreenActivity.projectToCard(projects.get(position));
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentPlacer, fragment).addToBackStack(MainScreenActivity.BACK_STACK);
        transaction.commit();
    }

    static interface Listener{
        void itemClicked(long id);
        void clearNew(int position);
    }

    @Override
    public void onDetach() {
        if(shouldRemoveBackStack) {
            getActivity().getSupportFragmentManager().popBackStackImmediate(MainScreenActivity.BACK_STACK, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        super.onDetach();
    }
}

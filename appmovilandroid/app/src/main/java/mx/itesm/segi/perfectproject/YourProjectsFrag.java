package mx.itesm.segi.perfectproject;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

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
                Model.getInstance().viewNotificaction(projects.get(position));
            }
        };
    }

    private void loadProjects() {
        this.user = getArguments().getParcelable(ARG_USER);
        this.owned = getArguments().getBoolean(ARG_OWNED);
        AdapterRV adapterRV;

        if(owned){
            this.projects = user.getProjectsOwned();
            adapterRV = new AdapterRV(user.getProjectsOwned(), true, listener);
        } else {
            adapterRV = new AdapterRV(user.getProjectsMember(), false, listener);
            adapterRV.setNotifications(user.getNotifications());
        }
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

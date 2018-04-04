package mx.itesm.segi.perfectproject;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


/**
 * A simple {@link Fragment} subclass.
 */
public class YourProjectsFrag extends Fragment {

    private RecyclerView rvYourProjects;
    private Listener listener;

    static interface Listener{
        void itemClicked(long id);
        void clearNew(int position);
    }

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
        this.listener = (Listener) context;
    }

    private void loadProjects() {
        String[] titles = getArguments().getStringArray("titles");
        String[] startDates = getArguments().getStringArray("startDates");
        String[] endDates = getArguments().getStringArray("endDates");
        boolean[] news = getArguments().getBooleanArray("news");
        AdapterRV adapterRV = new AdapterRV(titles, startDates, endDates, news, listener);
        rvYourProjects.setAdapter(adapterRV);
    }
}

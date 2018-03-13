package mx.itesm.segi.perfectproject;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFrag extends Fragment {

    private RecyclerView rvNotifications;


    public NotificationFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_notification, container, false);
        rvNotifications = v.findViewById(R.id.rvNotifications);
        rvNotifications.setLayoutManager(new GridLayoutManager(getContext(),1));
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadNotifications();
    }

    private void loadNotifications() {
        String[] titles = getArguments().getStringArray("titles");
        String[] descriptions = getArguments().getStringArray("descriptions");
        String[] dates = getArguments().getStringArray("dates");
        boolean[] news = getArguments().getBooleanArray("news");
        AdapterRV adapterRV = new AdapterRV(titles, descriptions, dates, news);
        rvNotifications.setAdapter(adapterRV);
    }

}

package mx.itesm.segi.perfectproject;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProjectInfoFrag extends Fragment {

    private TextView tvTitle;
    private TextView tvStartDate;
    private TextView tvEndDate;
    private TextView tvPositions;
    private TextView tvLocation;
    private TextView tvDescription;

    public ProjectInfoFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_project_info, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadProject();
    }

    private void loadProject() {
        tvTitle = getActivity().findViewById(R.id.project_Title);
        tvStartDate = getActivity().findViewById(R.id.project_startDate);
        tvEndDate = getActivity().findViewById(R.id.project_endDate);
        tvPositions = getActivity().findViewById(R.id.project_positions);
        tvLocation = getActivity().findViewById(R.id.project_location);
        tvDescription = getActivity().findViewById(R.id.project_description);

        String title = getArguments().getString("title");
        String startDate = getArguments().getString("startDate");
        String endDate = getArguments().getString("endDate");
        String positions = getArguments().getString("positions");
        String location = getArguments().getString("location");
        String description = getArguments().getString("description");

        tvTitle.setText(title);
        tvStartDate.setText(startDate);
        tvEndDate.setText(endDate);
        tvPositions.setText(positions);
        tvLocation.setText(location);
        tvDescription.setText(description);
    }

}

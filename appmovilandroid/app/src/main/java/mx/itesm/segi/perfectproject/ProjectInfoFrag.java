package mx.itesm.segi.perfectproject;


import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.StringJoiner;

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

    @Override
    public void onStart() {
        super.onStart();
        loadProject();
    }

    private void loadProject() {
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
        String positions[] = project.getPositions();
        String positionsText = "";
        for(int i = 0;i < positions.length;i++)
        {
            positionsText += positions[i];
            if(i!=positions.length-1) positionsText += "\n";
        }
        tvPositions.setText(positionsText);
        tvLocation.setText(project.getLocation());
        tvDescription.setText(project.getDescription());
        ivLogo.setImageBitmap(project.getImage());

        final ArrayList<User> team = project.getTeam();
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
                    /** ADD TO THE LISTENER INTERFACE THE METHOD THAT LOOKS FOR A PROFILE AND SEND THE USER IDENTIFIER TO THAT METHOD **/
                    Log.d("AdapterRV", "Look for profile " + team.get(index).toString());
                }
            });
            llContainer.addView(user);
        }
        if(owned)
        {
            fabEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /** OPEN FRAGMENT TO EDIT PROJECT **/
                }
            });

            fabDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /** CONFIRM AND DELETE**/
                }
            });
        }
        else
        {
            fabEdit.setVisibility(View.INVISIBLE);
            fabDelete.setVisibility(View.INVISIBLE);
        }
    }

}

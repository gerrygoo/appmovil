package mx.itesm.segi.perfectproject;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFrag extends Fragment {

    private EditText tvName;
    private EditText tvEmail;
    private EditText tvAbilities;
    public ProfileFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        tvName = v.findViewById(R.id.tvName);
        tvEmail = v.findViewById(R.id.tvEmail);
        tvAbilities = v.findViewById(R.id.tvAbilities);
        loadProfileInfo();
        return v;
    }

    private void loadProfileInfo() {
        tvName.setText(getArguments().getString("name"));
        tvEmail.setText(getArguments().getString("email"));
        tvAbilities.setText(getArguments().getString("abilities"));
    }
}

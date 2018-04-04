package mx.itesm.segi.perfectproject;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Switch;

import Model.User;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFrag extends Fragment {

    public final static String ARG_USER = "user";
    public final static String ARG_MODE = "mode";

    private EditText tvName;
    private EditText tvCompany;
    private EditText tvCurriculum;
    private RatingBar rbRating;
    private Switch Mode;
    private OnSwitchToggleListener listener;

    private User user;

    public ProfileFrag() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = getActivity();
        if(activity instanceof OnSwitchToggleListener){
            listener = (OnSwitchToggleListener) activity;
        } else {
            throw new ClassCastException(activity.toString() + " must implement OnSwitchToggleListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        tvName = v.findViewById(R.id.tvName);
        tvCompany = v.findViewById(R.id.tvCompany);
        tvCurriculum = v.findViewById(R.id.etCurriculum);
        rbRating = v.findViewById(R.id.rbRating);

        tvCurriculum.setKeyListener(null);
        Mode = v.findViewById(R.id.sEmployer);
        Mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                listener.OnSwitchToggle(b);
            }
        });
        loadProfileInfo();
        return v;
    }

    private void loadProfileInfo() {
        user = getArguments().getParcelable(ARG_USER);
        tvCompany.setText(user.getCompany());
        tvCurriculum.setText(user.getSkills().toString());
        tvName.setText(user.getName());
        rbRating.setRating(user.getRating());
        Mode.setChecked(getArguments().getBoolean(ARG_MODE));
    }

    public interface OnSwitchToggleListener {
        void OnSwitchToggle(boolean value);
    }
}

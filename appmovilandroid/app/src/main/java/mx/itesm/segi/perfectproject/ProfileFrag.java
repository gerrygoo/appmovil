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
import android.widget.Switch;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFrag extends Fragment {

    private EditText tvName;
    private EditText tvCompany;
    private EditText tvCurriculum;
    private Switch Mode;
    private OnSwitchToggleListener listener;

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
        tvName.setText(getArguments().getString("name"));
        tvCompany.setText(getArguments().getString("company"));
        tvCurriculum.setText(getArguments().getString("curriculum"));
        Mode.setChecked(getArguments().getBoolean("mode"));
    }

    public interface OnSwitchToggleListener {
        void OnSwitchToggle(boolean value);
    }
}

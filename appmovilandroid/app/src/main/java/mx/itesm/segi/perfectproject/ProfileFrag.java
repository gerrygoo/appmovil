package mx.itesm.segi.perfectproject;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Switch;

import com.google.android.gms.tasks.Tasks;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import Model.Model;
import Model.User;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFrag extends Fragment {

    public final static String ARG_USER = "user";
    public final static String ARG_MODE = "mode";
    private final static int PICK_PHOTO_FOR_AVATAR=1;

    private int numberOfLines;
    private EditText tvName;
    private EditText tvCompany;
    private EditText tvCurriculum;
    private EditText tvSkill;
    private ImageButton editProfilePicture;
    private EditText rateNum;
    private RatingBar rbRating;
    private Switch Mode;
    private OnSwitchToggleListener listener;
    private ImageView ivProfile;
    private LinearLayout skillLayout;
    private Button addSkill;
    private FloatingActionButton about;
    private Button logoutBtn;

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
        final View v = inflater.inflate(R.layout.fragment_profile, container, false);

        tvName = v.findViewById(R.id.tvName);
        tvCompany = v.findViewById(R.id.tvCompany);
        //tvCurriculum = v.findViewById(R.id.etCurriculum);
        editProfilePicture = v.findViewById(R.id.editProfilePic);
        rbRating = v.findViewById(R.id.rbRating);
        ivProfile=v.findViewById(R.id.ivProfile);
        rateNum = v.findViewById(R.id.rateNumber);
        addSkill = v.findViewById(R.id.btnAddSkill);
        about = v.findViewById(R.id.fabAbout);
        logoutBtn = v.findViewById(R.id.logoutBtn);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { startActivity(new Intent(getContext(), Login.class)); }
        });

//        tvCurriculum.setKeyListener(null);
        Mode = v.findViewById(R.id.sEmployer);
        Mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                listener.OnSwitchToggle(b);
            }
        });

        loadProfileInfo(v);
        Log.i("Skills:",user.getSkills().toString());
        tvCompany.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                user.setCompany(tvCompany.getText().toString());
            }
        });

        //Para poder editar la barra y guardar el valor del rate
        /*rbRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                user.setRating(v);
            }
        });*/

        editProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setProfilePic(view);
            }
        });

        addSkill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.addSkill("");
                Add_New_Line(v);

               // saveSkill(v);
            }
        });


        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment about = new AboutFrag();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentPlacer, about).addToBackStack(MainScreenActivity.BACK_STACK);
                transaction.commit();
            }
        });

        return v;
    }
/*
    private void saveSkill(View v) {
        LinearLayout ll = v.findViewById(R.id.SkillsLayout);
        for(int i=0;i<numberOfLines;i++){
            View et = ll.getChildAt(i);
            EditText s = (EditText)et;
            Log.i("Item selected",s.getText().toString());
        }
    }
*/
    public void Add_New_Line(View v) {
        LinearLayout ll = v.findViewById(R.id.SkillsLayout);
        // add edittext
        final EditText et = new EditText(getActivity().getApplicationContext());
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        et.setLayoutParams(p);
        et.setHint("Skills");
        et.setId(numberOfLines);
        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b && ((EditText)view).getText().toString().isEmpty() ) {
                    view.setVisibility(EditText.GONE);
                    view=null;
                }
            }
        });
        ll.addView(et);
        numberOfLines++;
    }

    public void Add_Line(View v, String skill) {
        LinearLayout ll = v.findViewById(R.id.SkillsLayout);
        // add edittext
        final EditText et = new EditText(getActivity().getApplicationContext());
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        et.setLayoutParams(p);
        et.setHint("Skills");
        et.setText(skill);
        et.setId(numberOfLines);
        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b && ((EditText)view).getText().toString().isEmpty() ) {
                    view.setVisibility(EditText.GONE);
                    view=null;
                }
            }
        });
        ll.addView(et);
        numberOfLines++;
    }

    public void setProfilePic(View v) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
            try {
                InputStream inputStream = getContext().getContentResolver().openInputStream(data.getData());
                final Bitmap BMimage = BitmapFactory.decodeStream(inputStream);
                ivProfile.setImageBitmap(BMimage);
                user.setProfileImage(BMimage);

                new AsyncTask<Void, Void, String>(){
                    @Override
                    protected String doInBackground(Void... voids) {
                        try {
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            BMimage.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                            return Tasks.await(Model.getInstance().uploadImageToStorage(stream, user.getUID()));
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(String url) {
                        user.setProfileImageURL(url);
                    }
                }.execute();



            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }
        else if(resultCode==Activity.RESULT_CANCELED){
            //No se eligió imagen de Profile Pic.
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void loadProfileInfo(View v) {

        final Double[] rating = {0.0};
        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    rating[0] = Tasks.await(Model.getInstance().getRating(user.getUID())) ;
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                user.setRating(rating[0]);
            }

        }.execute();

        user = getArguments().getParcelable(ARG_USER);
        ivProfile.setImageBitmap(user.getProfileImage());
        tvCompany.setText(user.getCompany());
        tvName.setText(user.getName());
        rbRating.setRating((float) user.getRating());
        rateNum.setText(String.valueOf(user.getRating()));
        Mode.setChecked(getArguments().getBoolean(ARG_MODE));
        for(int i=0;i<user.getSkills().size(); i++){
            Add_Line(v, user.getSkills().get(i));
        }
    }


    @SuppressLint("StaticFieldLeak")
    @Override
    public void onStop() {
        super.onStop();
        Log.d("Profile", "stop");
        LinearLayout ll = getActivity().findViewById(R.id.SkillsLayout);
        user.clearSkills();
        for(int i=0;i<numberOfLines;i++){
            View et = ll.getChildAt(i);
            EditText s = (EditText)et;
            if (s !=null) {
                if(!s.getText().toString().isEmpty()){
                    user.addSkill(s.getText().toString());
                }
            }

        }

        final ProgressBar bar = getActivity().findViewById(R.id.mainProgress);
        bar.setVisibility(View.VISIBLE);
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Tasks.await(Model.getInstance().updateCurrentUser());
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                bar.setVisibility(View.INVISIBLE);
            }
        }.execute();
    }

    public interface OnSwitchToggleListener {
        void OnSwitchToggle(boolean value);
    }
}

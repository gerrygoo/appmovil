package mx.itesm.segi.perfectproject;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import Model.Project;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * CreateProject.OnFragmentInteractionListener interface
 * to handle interaction events.
 * Use the {@link CreateProject#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateProject extends Fragment {
    public static final String ARG_PROJECT = "Project";

    //Model
    private Project project;

    //View
    private EditText Title;
    private String ImageUrl;
    private ImageButton Logo;
    private EditText StartDate;
    private EditText EndDate;
    private EditText Positions;
    private EditText Location;
    private EditText Description;
    private Button Create;

    private boolean shouldPutImage;


    public CreateProject() {
        // Required empty public constructor
    }

    public static CreateProject newInstance(Project project) {
        CreateProject fragment = new CreateProject();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_create_project, container, false);
        // Inflate the layout for this

        Title = result.findViewById(R.id.etTitle);
        Logo = result.findViewById(R.id.btnLogoImage);
        StartDate = result.findViewById(R.id.etStartDate);
        EndDate = result.findViewById(R.id.etEndDate);
        Positions = result.findViewById(R.id.etPositions);
        Location = result.findViewById(R.id.etLocation);
        Description = result.findViewById(R.id.etDescription);
        Create = result.findViewById(R.id.btnCreate);

        Logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setImage(view);
            }
        });

        Create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    create(view);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        return result;
    }

    public void setImage(View v){
        ImageUrl = "https://mspoweruser.com/wp-content/uploads/2016/09/Webgroesse_HighRes_Microsoft12711.jpg";
    }
    public void create(View v) throws ParseException {
        if(Title.length()==0){
            Snackbar.make(v, "Insert title", Snackbar.LENGTH_LONG).show();
        }
        else if(ImageUrl==null){
            Snackbar.make(v, "Insert logo", Snackbar.LENGTH_LONG).show();
        }
        else if(StartDate.length()==0){
            Snackbar.make(v, "Insert start date", Snackbar.LENGTH_LONG).show();
        }
        else if(EndDate.length()==0){
            Snackbar.make(v, "Insert end date", Snackbar.LENGTH_LONG).show();
        }
        else if(Positions.length()==0){
            Snackbar.make(v, "Insert positions", Snackbar.LENGTH_LONG).show();
        }
        else if(Location.length()==0){
            Snackbar.make(v, "Insert location", Snackbar.LENGTH_LONG).show();
        }
        else if(Description.length()==0){
            Snackbar.make(v, "Insert description", Snackbar.LENGTH_LONG).show();
        }
        else{
            String[] PositionsArr = Positions.getText().toString().split(" ");
            Date Start = new SimpleDateFormat("dd/MM/yyyy").parse(StartDate.getText().toString());
            Date End = new SimpleDateFormat("dd/MM/yyyy").parse(EndDate.getText().toString());
            Project project = new Project(
                    "1234",
                    null,
                    Title.getText().toString(),
                    ImageUrl,
                    PositionsArr,
                    Description.getText().toString(),
                    Location.getText().toString(),
                    Start,
                    End
            );
            Log.i("Exito",project.toString());
            //TODO: Link to DB and add project
        }
    }
}

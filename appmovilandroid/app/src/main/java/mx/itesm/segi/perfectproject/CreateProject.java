package mx.itesm.segi.perfectproject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.service.quicksettings.Tile;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import Model.Model;
import Model.Project;
import Model.Errors;


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

    @RequiresApi(api = Build.VERSION_CODES.N)
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

        myCalendar = Calendar.getInstance();

        StartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), startDateListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        EndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), endDateListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


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
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    create(view);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        return result;
    }

    public void setImage(View v) {
        ImageUrl = "https://mspoweruser.com/wp-content/uploads/2016/09/Webgroesse_HighRes_Microsoft12711.jpg";
    }

    public void create(View v) throws ParseException {
        if (Title.length() == 0) {
            Snackbar.make(v, "Insert title", Snackbar.LENGTH_LONG).show();
        } else if (ImageUrl == null) {
            Snackbar.make(v, "Insert logo", Snackbar.LENGTH_LONG).show();
        } else if (StartDate.length() == 0) {
            Snackbar.make(v, "Insert start date", Snackbar.LENGTH_LONG).show();
        } else if (EndDate.length() == 0) {
            Snackbar.make(v, "Insert end date", Snackbar.LENGTH_LONG).show();
        } else if (Positions.length() == 0) {
            Snackbar.make(v, "Insert positions", Snackbar.LENGTH_LONG).show();
        } else if (Location.length() == 0) {
            Snackbar.make(v, "Insert location", Snackbar.LENGTH_LONG).show();
        } else if (Description.length() == 0) {
            Snackbar.make(v, "Insert description", Snackbar.LENGTH_LONG).show();
        } else {
            String[] PositionsArr = Positions.getText().toString().split(" ");
            Date Start = new SimpleDateFormat("dd/MM/yyyy").parse(StartDate.getText().toString());
            Date End = new SimpleDateFormat("dd/MM/yyyy").parse(EndDate.getText().toString());
            Project project = new Project(
                    "1234",
                    Model.getInstance().getCurrentUser(),
                    Title.getText().toString(),
                    ImageUrl,
                    PositionsArr,
                    Description.getText().toString(),
                    Location.getText().toString(),
                    Start,
                    End
            );
            Log.i("Exito", project.toString());
            try {
                Model.getInstance().createProject(project);
                Title.setText("");
                ImageUrl = "";
                StartDate.setText("");
                EndDate.setText("");
                Positions.setText("");
                Location.setText("");
                Description.setText("");
                Create.setText("");
            } catch (Errors.CreateProjectException exception) {
                Snackbar.make(v, exception.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        }
    }

    Calendar myCalendar;
    final DatePickerDialog.OnDateSetListener startDateListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            StartDate.setText(dayOfMonth + "/" + monthOfYear + "/" + year);
        }

    };
    final DatePickerDialog.OnDateSetListener endDateListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            EndDate.setText(dayOfMonth + "/" + monthOfYear + "/" + year);
        }

    };
}

package mx.itesm.segi.perfectproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Tasks;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import Model.Errors;
import Model.Model;
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
    public static final String ARG_EDITING = "Editing";

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
    private Bitmap BMimage;

    private boolean shouldPutImage;
    private static final int PICK_PHOTO_FOR_AVATAR = 1;


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

        myCalendar = Calendar.getInstance();
        Logo.setBackground(getResources().getDrawable(android.R.drawable.btn_default));
        ImageUrl="";

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

        try{
            if(getArguments().getBoolean(ARG_EDITING) == true)
            {
                project = getArguments().getParcelable(ARG_PROJECT);
                editProject();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public void setImage(View v) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
            try {
                InputStream inputStream = getContext().getContentResolver().openInputStream(data.getData());
                BMimage = BitmapFactory.decodeStream(inputStream);
                Log.i("Imagen Guardada: ", inputStream.toString());
                Drawable d = new BitmapDrawable(getResources(), BMimage);
                Logo.setBackground(d);
                ImageUrl=" ";
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }
        else if(resultCode==Activity.RESULT_CANCELED){
            ImageUrl="";
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void editProject()
    {
        Title.setText(project.getTitle());
        BMimage = project.getImage();
        Drawable d = new BitmapDrawable(getResources(), BMimage);
        Logo.setBackground(d);
        ImageUrl=" ";
        StartDate.setText(android.text.format.DateFormat.format("dd/MM/yyyy", project.getStartDate()));
        EndDate.setText(android.text.format.DateFormat.format("dd/MM/yyyy", project.getEndDate()));
        ArrayList<String> positions = project.getPositions();
        String positionsText = "";
        for(int i = 0; i < positions.size(); i++)
        {
            positionsText += positions.get(i);
            if(i!=positions.size()-1) positionsText += " ";
        }
        Positions.setText(positionsText);
        Location.setText(project.getLocation());
        Description.setText(project.getDescription());
        Create.setText("Guardar");
        Create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    save(view);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    public void save(View v) throws ParseException {
        if (Title.length() == 0) {
            Snackbar.make(v, "Insert title", Snackbar.LENGTH_LONG).show();
        } else if (ImageUrl.length() == 0) {
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
            ArrayList<String> positions = new ArrayList(Arrays.asList(Positions.getText().toString().split(",")));
            int counter = 0;
            for (String position : positions) {
                positions.set(counter, position.trim());
                counter++;
            }
            Date Start = new SimpleDateFormat("dd/MM/yyyy").parse(StartDate.getText().toString());
            Date End = new SimpleDateFormat("dd/MM/yyyy").parse(EndDate.getText().toString());
            project = new Project(
                    project.getUID(),
                    Model.getInstance().getCurrentUser(),
                    Title.getText().toString(),
                    BMimage,
                    positions,
                    Description.getText().toString(),
                    Location.getText().toString(),
                    Start,
                    End
            );
            Log.i("Exito", project.toString());

            final ProgressBar bar = getActivity().findViewById(R.id.mainProgress);
            bar.setVisibility(View.VISIBLE);

            new AsyncTask<Void, Void, Void>(){
                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        Tasks.await(Model.getInstance().updateProject(project));

                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    Fragment fragment = new ProjectInfoFrag();
                    Bundle args = new Bundle();
                    args.putParcelable(ProjectInfoFrag.ARG_PROJECT, project);
                    args.putBoolean(ProjectInfoFrag.ARG_OWNED, true);
                    fragment.setArguments(args);
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentPlacer, fragment).addToBackStack(MainScreenActivity.BACK_STACK);
                    transaction.commit();
                    bar.setVisibility(View.INVISIBLE);
                }
            }.execute();
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void create(View v) throws ParseException {
        if (Title.length() == 0) {
            Snackbar.make(v, "Insert title", Snackbar.LENGTH_LONG).show();
        } else if (ImageUrl.length() == 0) {
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
            ArrayList<String> positions = new ArrayList(Arrays.asList(Positions.getText().toString().split(",")));
            int counter = 0;
            for (String position : positions) {
                positions.set(counter, position.trim());
                counter++;
            }
            Date Start = new SimpleDateFormat("dd/MM/yyyy").parse(StartDate.getText().toString());
            Date End = new SimpleDateFormat("dd/MM/yyyy").parse(EndDate.getText().toString());
            project = new Project(
                    null,
                    Model.getInstance().getCurrentUser(),
                    Title.getText().toString(),
                    BMimage,
                    positions,
                    Description.getText().toString(),
                    Location.getText().toString(),
                    Start,
                    End
            );
            Log.i("Exito", project.toString());

            final ProgressBar bar = getActivity().findViewById(R.id.mainProgress);
            bar.setVisibility(View.VISIBLE);

            new AsyncTask<Void, Void, Void>(){

                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        Tasks.await(Model.getInstance().createProject(project));

                    } catch (ExecutionException | InterruptedException | Errors.CreateProjectException e) {
                        Toast.makeText(getContext(), "Error creating project", Toast.LENGTH_LONG  ).show();
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    Title.setText("");
                    Logo.setBackground(getResources().getDrawable(android.R.drawable.btn_default));
                    ImageUrl="";
                    StartDate.setText("");
                    EndDate.setText("");
                    Positions.setText("");
                    Location.setText("");
                    Description.setText("");

                    bar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(), "Project Created", Toast.LENGTH_LONG  ).show();
                }
            }.execute();
        }
    }

    Calendar myCalendar;
    final DatePickerDialog.OnDateSetListener startDateListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            StartDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
        }

    };
    final DatePickerDialog.OnDateSetListener endDateListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            EndDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
        }

    };
}

package mx.itesm.ins.proyectomovilandroid;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Calendar;

public class MainScreenActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment emptyFrag = new Fragment();
            switch (item.getItemId()) {
                case R.id.navigation_profile:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentPlacer, emptyFrag).commit();
                    return true;
                case R.id.navigation_browse:
                    Fragment card = new ProjectCard();
                    Bundle args = new Bundle();
                    args.putParcelable(ProjectCard.ARG_PROJECT,new Project(
                            "Super Titulo",
                            "https://upload.wikimedia.org/wikipedia/commons/thumb/5/53/Google_%22G%22_Logo.svg/2000px-Google_%22G%22_Logo.svg.png",
                            new String[]{ "Programador" },
                            "Super descripcion",
                            "La Condesa",
                            Calendar.getInstance().getTime(),
                            Calendar.getInstance().getTime())
                    );
                    card.setArguments(args);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentPlacer, card).commit();
                    return true;
                case R.id.navigation_notifications:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentPlacer, emptyFrag).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}

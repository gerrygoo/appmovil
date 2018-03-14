package mx.itesm.segi.perfectproject;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import java.util.Arrays;
import java.util.Calendar;

public class MainScreenActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment emptyFrag = new Fragment();
            switch (item.getItemId()) {
                case R.id.navigation_profile:
                    Bundle argsProfile = new Bundle();
                    argsProfile.putString("name", "Willy Wonka");
                    argsProfile.putString("company", "Chocolate Factory");
                    argsProfile.putString("curriculum", "Developer, Designer, Project Manager");
                    Fragment profile = new ProfileFrag();
                    profile.setArguments(argsProfile);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentPlacer, profile).commit();
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
                    Bundle argNotifications = new Bundle();
                    argNotifications.putStringArray("dates", new String[]{Calendar.getInstance().getTime().toString(), Calendar.getInstance().getTime().toString(), Calendar.getInstance().getTime().toString()});
                    argNotifications.putStringArray("titles", new String[]{"Notification 1", "Notification 2", "Notification 3"});
                    argNotifications.putStringArray("descriptions", new String[]{"Description 1", "Description 2", "Description 3"});
                    argNotifications.putBooleanArray("news", new boolean[]{true, false, true});
                    Fragment notifications = new NotificationFrag();
                    notifications.setArguments(argNotifications);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentPlacer, notifications).commit();
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

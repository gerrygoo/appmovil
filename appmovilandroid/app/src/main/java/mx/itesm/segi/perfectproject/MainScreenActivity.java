package mx.itesm.segi.perfectproject;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.DragEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Calendar;

public class MainScreenActivity extends AppCompatActivity {

    private final long duration = 100;
    private float startingX, startingY, initialTouchX, initialTouchY;

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
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentPlacerTop, profile).commit();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentPlacerBottom, emptyFrag).commit();
                    return true;
                case R.id.navigation_browse:
                    Fragment google  = GoogleCard();
                    Fragment microsoft  = MicrosoftCard();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentPlacerTop, google).commit();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentPlacerBottom, microsoft).commit();
                    return true;
                case R.id.navigation_notifications:
                    Bundle argNotifications = new Bundle();
                    argNotifications.putStringArray("dates", new String[]{Calendar.getInstance().getTime().toString(), Calendar.getInstance().getTime().toString(), Calendar.getInstance().getTime().toString()});
                    argNotifications.putStringArray("titles", new String[]{"Notification 1", "Notification 2", "Notification 3"});
                    argNotifications.putStringArray("descriptions", new String[]{"Description 1", "Description 2", "Description 3"});
                    argNotifications.putBooleanArray("news", new boolean[]{true, false, true});
                    Fragment notifications = new NotificationFrag();
                    notifications.setArguments(argNotifications);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentPlacerTop, notifications).commit();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentPlacerBottom, emptyFrag).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        final View draggableView = findViewById(R.id.fragmentPlacerTop);
        draggableView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, final MotionEvent motionEvent) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {


                    startingX = draggableView.getX();
                    startingY = draggableView.getY();

                    int[] coordinates = new int[2];
                    draggableView.getLocationInWindow(coordinates);


                    initialTouchX = motionEvent.getRawX() - coordinates[0];
                    initialTouchY = motionEvent.getRawY() - coordinates[1];

                    draggableView.startDragAndDrop(null, new PointDragShadowBuilder(draggableView, motionEvent.getX(), motionEvent.getY()), null, View.DRAG_FLAG_OPAQUE);
                    //draggableView.startDragAndDrop(null, null, null, View.DRAG_FLAG_OPAQUE);
                    //draggableView.setVisibility(View.GONE);

                    return true;
                }
                return false;
            }
        });

        findViewById(R.id.container).setOnDragListener(new View.OnDragListener() {


            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                switch (dragEvent.getAction()){
                    case DragEvent.ACTION_DRAG_ENDED:

//                        draggableView.setVisibility(View.VISIBLE);
//                        draggableView.setX(lastTouchX - initialTouchX);
//                        draggableView.setY(lastTouchY - initialTouchY);

                        ObjectAnimator moveX = ObjectAnimator.ofFloat(draggableView, "translationX",startingX);
                        ObjectAnimator moveY = ObjectAnimator.ofFloat(draggableView, "translationY", startingY);

                        AnimatorSet set = new AnimatorSet();
                        set.playTogether(moveX,moveY);
                        set.setDuration(duration);

                        set.start();
                        break;
                    case DragEvent.ACTION_DROP:
                        break;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        draggableView.setX(dragEvent.getX() - initialTouchX);
                        draggableView.setY(dragEvent.getY() - initialTouchY);
                        break;
                }
                return true;
            }
        });
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private Fragment GoogleCard(){
        Fragment card = new ProjectCard();
        Bundle args = new Bundle();
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 10);
        args.putParcelable(ProjectCard.ARG_PROJECT,new Project(
                "Smart Cars",
                "https://pmcvariety.files.wordpress.com/2015/08/google-placeholder-logo.jpg?w=1000&h=563&crop=1",
                new String[]{ "Programmer", "Product Manager", "Experience Designer" },
                "The project focuses on building a self driving car, in which you are required to know Machine Learning and Artificial Intelligence Algorithms in order to be eligible for this project",
                "Mountain View, California, United States",
                Calendar.getInstance().getTime(),
                endDate.getTime())
        );
        card.setArguments(args);
        return card;
    }

    private Fragment MicrosoftCard(){
        Fragment card = new ProjectCard();
        Bundle args = new Bundle();
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 18);

        Calendar startDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 8);

        args.putParcelable(ProjectCard.ARG_PROJECT,new Project(
                "Cortana Search",
                "https://mspoweruser.com/wp-content/uploads/2016/09/Webgroesse_HighRes_Microsoft12711.jpg",
                new String[]{ "Programmer", "Program Manager", "Tester" },
                "This project focuses on implementing a Natural Language search for Cortana, for this we require that you have knowledge and background on Natural Language Processing or Artificial Intelligence",
                "Redmond, Washington, United States",
                startDate.getTime(),
                endDate.getTime())
        );
        card.setArguments(args);
        return card;
    }
}

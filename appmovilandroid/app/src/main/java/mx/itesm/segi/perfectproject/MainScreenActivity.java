package mx.itesm.segi.perfectproject;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Choreographer;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.Calendar;

import Model.Project;

public class MainScreenActivity extends AppCompatActivity implements ProfileFrag.OnSwitchToggleListener, ProjectCard.OnCardButtonClickListener{

    private final long duration = 100;
    private final boolean EMPLOYEE = false, EMPLOYER = true;
    private final int INITIAL_MENU_ITEM = 1;
    private float startingX, startingY, initialTouchX, initialTouchY;
    private boolean accountMode = EMPLOYEE;
    private Fragment[] activeFragments;
    private View[] views;

    private int currentProject = 0;
    private final Project[] projects = new Project[]{
            MicrosoftProject(),
            GoogleProject(),
            MicrosoftProject(),
            GoogleProject(),
            MicrosoftProject(),
            GoogleProject(),
            MicrosoftProject(),
            GoogleProject(),
            MicrosoftProject(),
            GoogleProject()
    };

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            return handleNavigation(item.getItemId());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        final View draggableView = findViewById(R.id.fragmentPlacerDraggable);
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

                        animateDragTo(startingX, startingY);

                        float deltaX = draggableView.getX() - startingX;
                        float screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

                        Log.i("DELTA X", deltaX + "");

                        if(deltaX > screenWidth/3){
                            handleYes();
                        } else if(deltaX < -screenWidth/3 ){
                            handleNo();
                        }

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

        renderBrowse();
        views = new View[]{
                findViewById(R.id.fragmentPlacer),
                findViewById(R.id.fragmentPlacerDraggable),
        };

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().getItem(INITIAL_MENU_ITEM).setChecked(true);
    }

    private Fragment projectToCard(Project project){
        Fragment card = new ProjectCard();
        Bundle args = new Bundle();
        args.putParcelable(ProjectCard.ARG_PROJECT, project);
        card.setArguments(args);
        return card;
    }

    private Project GoogleProject(){
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 10);
        return new Project(
                "12345",
                null,
                "Smart Cars",
                "https://pmcvariety.files.wordpress.com/2015/08/google-placeholder-logo.jpg?w=1000&h=563&crop=1",
                new String[]{ "Programmer", "Product Manager", "Experience Designer" },
                "The project focuses on building a self driving car, in which you are required to know Machine Learning and Artificial Intelligence Algorithms in order to be eligible for this project",
                "Mountain View, California, United States",
                Calendar.getInstance().getTime(),
                endDate.getTime());
    }

    private Project MicrosoftProject(){
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 18);

        Calendar startDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 8);

        return new Project(
                "12321",
                null,
                "Cortana Search",
                "https://mspoweruser.com/wp-content/uploads/2016/09/Webgroesse_HighRes_Microsoft12711.jpg",
                new String[]{ "Programmer", "Program Manager", "Tester" },
                "This project focuses on implementing a Natural Language search for Cortana, for this we require that you have knowledge and background on Natural Language Processing or Artificial Intelligence",
                "Redmond, Washington, United States",
                startDate.getTime(),
                endDate.getTime());
    }

    private boolean handleNavigation(int id){
        clearFragments();
        switch (id) {
            case R.id.navigation_profile:
                usingFragments();
                renderProfile();
                return true;
            case R.id.navigation_browse:
                if(accountMode == EMPLOYEE) {
                    usingFragments(true);
                    renderBrowse();
                }else {
                    usingFragments();
                    renderNotifications();
                }
                return true;
            case R.id.navigation_notifications:
                usingFragments();
                if(accountMode == EMPLOYEE) {
                    renderNotifications();
                } else {
                    renderCreateProject();
                }
                return true;
        }
        return false;
    }

    private void renderProfile(){
        Bundle argsProfile = new Bundle();
        argsProfile.putString("name", "Willy Wonka");
        argsProfile.putString("company", "Chocolate Factory");
        argsProfile.putString("curriculum", "Developer, Designer, Project Manager");
        argsProfile.putBoolean("mode", accountMode);
        Fragment profile = new ProfileFrag();
        profile.setArguments(argsProfile);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentPlacer, profile).commit();
        activeFragments = new Fragment[]{profile};
    }

    private void renderBrowse(){
        renderCards(currentProject, currentProject+1);
    }

    private void renderCards(int first, int second){
        Fragment top, bottom;
        if(first < projects.length) {
            top = projectToCard(projects[first]);
        } else {
            top = new Fragment();
        }
        if(second < projects.length) {
            bottom = projectToCard(projects[second]);
        } else {
            bottom = new Fragment();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentPlacerDraggable, top).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentPlacer, bottom).commit();
        activeFragments = new Fragment[]{top, bottom};
    }

    private void renderCreateProject(){
        Fragment newProject = new CreateProject();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentPlacer, newProject).commit();
        activeFragments = new Fragment[]{newProject};
    }

    private void renderNotifications(){
        Bundle argNotifications = new Bundle();
        argNotifications.putStringArray("dates", new String[]{Calendar.getInstance().getTime().toString(), Calendar.getInstance().getTime().toString(), Calendar.getInstance().getTime().toString()});
        argNotifications.putStringArray("titles", new String[]{"Notification 1", "Notification 2", "Notification 3"});
        argNotifications.putStringArray("descriptions", new String[]{"Description 1", "Description 2", "Description 3"});
        argNotifications.putBooleanArray("news", new boolean[]{true, false, true});
        Fragment notifications = new NotificationFrag();
        notifications.setArguments(argNotifications);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentPlacer, notifications).commit();
    }

    private AnimatorSet animateDragTo(float x, float y){
        View draggableView = findViewById(R.id.fragmentPlacerDraggable);

        ObjectAnimator moveX = ObjectAnimator.ofFloat(draggableView, "translationX",x);
        ObjectAnimator moveY = ObjectAnimator.ofFloat(draggableView, "translationY",y);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(moveX,moveY);
        set.setDuration(duration);

        set.start();
        return set;
    }

    public void handleYes(){
        float screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        animateDragTo(+screenWidth, startingY).addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                changeCards();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    public void handleNo(){
        float screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        animateDragTo(-screenWidth, startingY).addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                changeCards();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    private void changeCards(){
        currentProject++;
        renderCards(currentProject, currentProject);
        Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
            @Override
            public void doFrame(long l) {
                View draggableView = findViewById(R.id.fragmentPlacerDraggable);
                draggableView.setX(startingX);
                draggableView.setY(startingY);
                renderBrowse();
            }
        });
    }

    private void usingFragments(){
        findViewById(R.id.fragmentPlacer).setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void usingFragments(boolean draggable){
        findViewById(R.id.fragmentPlacer).setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if(draggable) {
            findViewById(R.id.fragmentPlacerDraggable).setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    private void clearFragments(){
        for(Fragment f: activeFragments) {
            getSupportFragmentManager().beginTransaction().remove(f).commit();
        }
        for(View v: views){
            v.setLayoutParams(new FrameLayout.LayoutParams(0, 0));
        }
    }

    @Override
    public void OnSwitchToggle(boolean value) {
        accountMode = value;

        BottomNavigationView navigation = findViewById(R.id.navigation);
        Menu menu = navigation.getMenu();
        if(accountMode == EMPLOYEE){
            menu.getItem(1).setIcon(R.drawable.ic_home_black_24dp);
            menu.getItem(1).setTitle(R.string.navigation_browse);
            menu.getItem(2).setIcon(R.drawable.ic_notifications_black_24dp);
            menu.getItem(2).setTitle(R.string.navigation_notifications);
        }else {
            menu.getItem(1).setIcon(R.drawable.ic_work_black_24dp);
            menu.getItem(1).setTitle(R.string.navigation_my_projects);
            menu.getItem(2).setIcon(R.drawable.ic_add_black_24dp);
            menu.getItem(2).setTitle(R.string.navigation_create_project);
        }
    }
}

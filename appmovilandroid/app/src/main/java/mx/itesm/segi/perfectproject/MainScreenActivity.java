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
import android.support.v4.app.FragmentManager;
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

import java.util.ArrayList;

import Model.Model;
import Model.IModel;
import Model.Project;

public class MainScreenActivity extends AppCompatActivity implements ProfileFrag.OnSwitchToggleListener, ProjectCard.OnCardButtonClickListener {

    public static final String BACK_STACK = "Main Activity Back";
    private final long duration = 100;
    private final boolean EMPLOYEE = false, EMPLOYER = true;
    private final int INITIAL_MENU_ITEM = 1;
    private float startingX, startingY, initialTouchX, initialTouchY;
    private boolean accountMode = EMPLOYEE;

    private Fragment[] activeFragments;
    private View[] views;

    private int currentProject = 0;
    private ArrayList<Project> projects;

    private IModel model;

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

        model = Model.getInstance();
        views = new View[]{
                findViewById(R.id.fragmentPlacer),
                findViewById(R.id.fragmentPlacerDraggable),
        };

        renderBrowse();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().getItem(INITIAL_MENU_ITEM).setChecked(true);
    }

    public static Fragment projectToCard(Project project){
        Fragment card = new ProjectCard();
        Bundle args = new Bundle();
        args.putParcelable(ProjectCard.ARG_PROJECT, project);
        card.setArguments(args);
        return card;
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
                    renderYourProjects(true);
                }
                return true;
            case R.id.navigation_myprojects:
                usingFragments();
                if(accountMode == EMPLOYEE) {
                    renderYourProjects(false);
                } else {
                    renderCreateProject();
                }
                return true;
        }
        return false;
    }

    private void renderProfile(){

        Fragment profile = new ProfileFrag();
        Bundle argsProfile = new Bundle();

        argsProfile.putParcelable(ProfileFrag.ARG_USER, Model.getInstance().getCurrentUser());
        argsProfile.putBoolean(ProfileFrag.ARG_MODE, accountMode);

        profile.setArguments(argsProfile);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentPlacer, profile).commit();
        activeFragments = new Fragment[]{profile};
    }

    private void renderBrowse(){
        currentProject = 0;
        this.projects = model.getAvailableProjects();
        renderCards(currentProject, currentProject+1);
    }

    private void renderCards(int first, int second){
        Fragment top, bottom;
        if(first < projects.size()) {
            top = projectToCard(projects.get(first));
        } else {
            top = new Fragment();
        }
        if(second < projects.size()) {
            bottom = projectToCard(projects.get(second));
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

    private void renderYourProjects(boolean owned){
        Fragment yourProjects = new YourProjectsFrag();
        Bundle argYourProjects = new Bundle();

        argYourProjects.putParcelable(YourProjectsFrag.ARG_USER, Model.getInstance().getCurrentUser());
        argYourProjects.putBoolean(YourProjectsFrag.ARG_OWNED, owned);

        yourProjects.setArguments(argYourProjects);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentPlacer, yourProjects).commit();
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
        Model.getInstance().reviewProject(projects.get(currentProject), true);
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
        Model.getInstance().reviewProject(projects.get(currentProject), false);
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
        getSupportFragmentManager().popBackStackImmediate(MainScreenActivity.BACK_STACK, FragmentManager.POP_BACK_STACK_INCLUSIVE);
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
            menu.getItem(2).setIcon(R.drawable.ic_work_black_24dp);
            menu.getItem(2).setTitle(R.string.navigation_my_projects);
        }else {
            menu.getItem(1).setIcon(R.drawable.ic_work_black_24dp);
            menu.getItem(1).setTitle(R.string.navigation_my_projects);
            menu.getItem(2).setIcon(R.drawable.ic_add_black_24dp);
            menu.getItem(2).setTitle(R.string.navigation_create_project);
        }
    }
}

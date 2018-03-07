package mx.itesm.ins.proyectomovilandroid;

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
import android.util.Log;
import android.view.DragEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

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

        final View draggableView = findViewById(R.id.fragmentPlacer);
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
}

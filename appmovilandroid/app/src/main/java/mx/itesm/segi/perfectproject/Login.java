package mx.itesm.segi.perfectproject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;

import java.util.concurrent.ExecutionException;

import Model.Errors;
import Model.User;
import Model.Model;

public class Login extends AppCompatActivity {

    private TextInputEditText emailTxt;
    private TextInputEditText passwordTxt;
    private TextView errorText;

    @Override
    public void onBackPressed() { getSupportFragmentManager().popBackStack(); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailTxt = findViewById(R.id.email);
        passwordTxt = findViewById(R.id.password);
        errorText = findViewById(R.id.errorText);

        findViewById(R.id.signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toSignup();
            }
        });

        findViewById(R.id.signin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSubmit();
            }
        });

        final ProgressBar bar = findViewById(R.id.loginProgress);

        findViewById(R.id.forgot).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View view) {

                final String email = emailTxt.getText().toString();
                if(email.isEmpty()){
                    errorText.setText("Please add an email");
                } else {
                    bar.setVisibility(View.VISIBLE);
                    new AsyncTask<Void, Void, Pair<Boolean, String>>(){
                        @Override
                        protected Pair<Boolean, String> doInBackground(Void... voids) {
                            try {
                                Tasks.await(Model.getInstance().resetPassword(email));
                                return new Pair<>(true, "Reset password email sent!");
                            } catch (ExecutionException | InterruptedException e) {
                                return new Pair<>(false, e.getMessage());
                            }
                        }

                        @Override
                        protected void onPostExecute(Pair<Boolean, String> pair) {
                            if(pair.first){
                                errorText.setTextColor(Color.rgb(0,128, 0));
                            } else {
                                errorText.setTextColor(Color.RED);
                            }
                            errorText.setText(pair.second);
                            bar.setVisibility(View.INVISIBLE);
                        }
                    }.execute();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        errorText.setText("");
    }

    @SuppressLint("StaticFieldLeak")
    private void authenticate(final String email, final String password){
        final Context context = this;

        final ProgressBar bar = findViewById(R.id.loginProgress);
        bar.setVisibility(View.VISIBLE);

        new AsyncTask<Void, Void, Boolean>(){
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    return Tasks.await(Model.getInstance().authenticate(email, password));
                } catch (Errors.AuthException | InterruptedException | ExecutionException e) {
                    errorText.setTextColor(Color.RED);
                    errorText.setText(e.getMessage());
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if(success){
                    startActivity(new Intent(context, MainScreenActivity.class));
                } else {
                    errorText.setTextColor(Color.RED);
                    errorText.setText(R.string.Generic_Auth_error);
                }
                bar.setVisibility(View.INVISIBLE);
            }
        }.execute();

//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Model.getInstance().authenticate(email, password).addOnCompleteListener(new OnCompleteListener<Boolean>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Boolean> task) {
//                            if(task.isSuccessful()){
//                                if(task.getResult()){
//                                } else {
//                                    errorText.setText("Given credentials failed to authenticate.");
//                                }
//                            } else {
//                                errorText.setText(task.getException().getMessage());
//                            }
//                        }
//                    });
//                } catch (Errors.AuthException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    private void handleSubmit() {
        final String
                email = emailTxt.getText().toString(),
                password = passwordTxt.getText().toString();

        if( email.isEmpty() || password.isEmpty() ) {
            errorText.setText(R.string.Missing_fields);
            return;
        }

        authenticate(email, password);
    }

    private void toSignup() {
        startActivity(new Intent(this, SignUp.class));
    }
}

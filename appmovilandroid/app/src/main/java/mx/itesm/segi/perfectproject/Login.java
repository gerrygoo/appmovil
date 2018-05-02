package mx.itesm.segi.perfectproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;

import java.util.concurrent.ExecutionException;

import Model.Errors;
import Model.Model;

public class Login extends AppCompatActivity {

    private TextInputEditText emailTxt;
    private TextInputEditText passwordTxt;
    private TextView errorText;

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
    }


    @SuppressLint("StaticFieldLeak")
    private void authenticate(final String email, final String password){
        final Context context = this;

        new AsyncTask<Void, Void, Boolean>(){
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    return Tasks.await(Model.getInstance().authenticate(email, password));
                } catch (Errors.AuthException | InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if(success){
                    startActivity(new Intent(context, MainScreenActivity.class));
                } else {
                    errorText.setText("Given credentials failed to authenticate.");
                }
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

    @SuppressLint("StaticFieldLeak")
    private void handleSubmit() {
        final String
                email = emailTxt.getText().toString(),
                password = passwordTxt.getText().toString();
        final Context context = this;

        authenticate(email, password);
    }

    private void toSignup() {
        startActivity(new Intent(this, SignUp.class));
    }
}

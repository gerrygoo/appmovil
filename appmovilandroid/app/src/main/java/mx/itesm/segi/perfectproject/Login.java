package mx.itesm.segi.perfectproject;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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


    private boolean authenticate(String email, String password){
        return !email.isEmpty() && !password.isEmpty();
    }

    private void handleSubmit() {
        String
                email = emailTxt.getText().toString(),
                password = passwordTxt.getText().toString();

        if ( authenticate(email, password) ) {

            startActivity(new Intent(this, MainScreenActivity.class));
        } else {

            errorText.setText("Given credentials failed to authenticate.");
        }

    }

    private void toSignup() {
        startActivity(new Intent(this, SignUp.class));
    }
}

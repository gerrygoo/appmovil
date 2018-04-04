package mx.itesm.segi.perfectproject;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import Model.*;

public class Login extends AppCompatActivity {

    public static Store StoreRef = new Store();

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

    private void handleSubmit() {
        String
                email = emailTxt.getText().toString(),
                password = passwordTxt.getText().toString();




        if ( !email.isEmpty() && !password.isEmpty() ) {

            User user;
            try {
                user = StoreRef.authenticate(email, password);

                //write local user
                Log.d("Logged with: ", user.toString() );



            } catch (Errors.AuthException e) {
                errorText.setText( e.getMessage() );
            }
        }

    }

    private void toSignup() {
        startActivity(new Intent(this, SignUp.class));
    }
}

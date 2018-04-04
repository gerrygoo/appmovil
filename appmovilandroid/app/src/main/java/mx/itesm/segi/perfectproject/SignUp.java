package mx.itesm.segi.perfectproject;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.xml.sax.helpers.LocatorImpl;

import Model.*;

public class SignUp extends AppCompatActivity {

    private TextInputEditText
        emailTxt, emailConfirmTxt,
        passwordTxt, passwordConfirmTxt;
    private TextView errorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailTxt = findViewById(R.id.email);
        emailConfirmTxt = findViewById(R.id.email_2);
        passwordTxt =  findViewById(R.id.password);
        passwordConfirmTxt =  findViewById(R.id.password_2);
        errorText = findViewById(R.id.errorText);

        findViewById(R.id.submitBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSubmit();
            }
        });
    }

    private boolean fieldsValid(String email, String emailConfirm, String password, String passwordConfirm){
        return !email.isEmpty()
                && !password.isEmpty()
                && !emailConfirm.isEmpty()
                && !passwordConfirm.isEmpty()
                && email.equals(emailConfirm)
                && password.equals(passwordConfirm);
    }

    private void handleSubmit() {
        String
                email = emailTxt.getText().toString(),
                emailComfirm = emailConfirmTxt.getText().toString(),
                password = passwordTxt.getText().toString(),
                passwordConfirm = passwordConfirmTxt.getText().toString();

        if ( fieldsValid(email, emailComfirm, password, passwordConfirm) ) {
            try {
                Login.StoreRef.register(
                        new User(
                                Integer.toString( email.hashCode() ),
                                email,
                                email,
                                email,
                                email,
                                0,
                                false
                                ),
                password);
            } catch ( Errors.RegisterException e ) {
                errorText.setText( e.getMessage() );
            }
            startActivity(new Intent(this, MainScreenActivity.class));
        } else {
            errorText.setText("Incoeherent data entered.");
        }

    }


}

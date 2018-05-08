package mx.itesm.segi.perfectproject;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import Model.Model;
import Model.User;
import Model.Errors;

public class SignUp extends AppCompatActivity {

    private TextInputEditText
            emailTxt, emailConfirmTxt,
            passwordTxt, passwordConfirmTxt,
            nameTxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailTxt = findViewById(R.id.email);
        emailConfirmTxt = findViewById(R.id.email_2);
        passwordTxt = findViewById(R.id.password);
        passwordConfirmTxt = findViewById(R.id.password_2);
        nameTxt = findViewById(R.id.name);

        findViewById(R.id.submitBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSubmit();
            }
        });
    }

    private void handleSubmit() {
        String
                email = emailTxt.getText().toString(),
                emailConfirm = emailConfirmTxt.getText().toString(),
                password = passwordTxt.getText().toString(),
                passwordConfirm = passwordConfirmTxt.getText().toString(),
                name = nameTxt.getText().toString();

        if (email.isEmpty())
        {
            emailTxt.setError("Email cannot be empty!");
            return;
        }
        else if (emailConfirm.isEmpty())
        {
            emailConfirmTxt.setError("Email confirmation cannot be empty!");
            return;
        }
        else if (password.isEmpty())
        {
            passwordTxt.setError("Password cannot be empty!");
            return;
        }
        else if (passwordConfirm.isEmpty())
        {
            passwordConfirmTxt.setError("Password confirmation cannot be empty!");
            return;
        }
        else if (name.isEmpty())
        {
            nameTxt.setError("Name cannot be empty!");
            return;
        }
        else if (!email.equals(emailConfirm))
        {
            emailTxt.setError("Email does not match!");
            emailConfirmTxt.setError("Email does not match!");
            return;
        }
        else if (!password.equals(passwordConfirm))
        {
            passwordTxt.setError("Password does not match!");
            passwordConfirmTxt.setError("Password does not match!");
            return;
        }

        try {
            Model.getInstance().register(new User(email, name), password);
        } catch (Errors.RegisterException exception) {
            if(exception.getError() == Errors.RegisterError.AccountAlreadyExists || exception.getError() == Errors.RegisterError.UsernameInUse){
                emailTxt.setError("That account is already in use");
                return;
            }
            if(exception.getError() == Errors.RegisterError.InvalidPassword){
                passwordTxt.setError(exception.getMessage());
                return;
            }
            Toast.makeText(this,exception.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }
        finish();
    }


}

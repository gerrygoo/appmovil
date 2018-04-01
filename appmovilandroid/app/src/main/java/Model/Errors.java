package Model;

/**
 * Created by ianne on 1/04/2018.
 */

public class Errors {
    public enum AuthError {
        InvalidUsername,
        InvalidPassword,
        ServiceUnavailable
    }

    public enum RegisterError{
        UsernameInUse,
        InvalidUsername,
        InvalidPassword,
        MissingItem
    }

    public enum CreateProjectError {
        MissingItem
    }

    public class AuthException extends Exception{
        String message;
        AuthError error;

        public AuthException(String message, AuthError error){
            this.error = error;
            this.message = message;
        }

        @Override
        public String getMessage() {
            return message;
        }

        public AuthError getError() {
            return error;
        }
    }

    public class RegisterException extends Exception {
        String message;
        RegisterError error;

        public RegisterException(String message, RegisterError error){
            this.error = error;
            this.message = message;
        }

        @Override
        public String getMessage() {
            return message;
        }

        public RegisterError getError() {
            return error;
        }
    }

    public class CreateProjectException extends Exception {
        String message;
        CreateProjectError error;

        public CreateProjectException(String message, CreateProjectError error){
            this.error = error;
            this.message = message;
        }

        @Override
        public String getMessage() {
            return message;
        }

        public CreateProjectError getError() {
            return error;
        }
    }
}

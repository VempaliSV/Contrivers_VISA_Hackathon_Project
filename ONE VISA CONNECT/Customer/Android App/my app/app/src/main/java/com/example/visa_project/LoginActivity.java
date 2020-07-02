package com.example.visa_project;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.example.visa_project.API.RequestLogin;
import com.example.visa_project.API.RequestRefresh;
import com.example.visa_project.MLE.RSA;
import com.example.visa_project.SessionManager.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    public static SessionManager session;
    public static LinearLayout llProgressBar;
    Animation loginAnim;


    public boolean isPasswordValid(String password){
        return password != null && password.length() >= 2; // needs to be 8 but set 2 just for testing
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void login(String email, String password) throws Throwable {
        RSA cipher = new RSA(LoginActivity.this);

        RequestLogin requestLogin = new RequestLogin(LoginActivity.this);
        requestLogin.execute("Enter server port" + "/login", email, password);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
            int[] scrcoords = new int[2];
            view.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + view.getLeft() - scrcoords[0];
            float y = ev.getRawY() + view.getTop() - scrcoords[1];
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                ((InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
        }
        return super.dispatchTouchEvent(ev);
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final TextInputLayout passwordTextInput = findViewById(R.id.inputTextPassword);
        final TextInputEditText passwordEditText = findViewById(R.id.editTextPassword);
        final TextInputLayout emailTextInput = findViewById(R.id.inputTextEmail);
        final TextInputEditText emailEditText = findViewById(R.id.editTextEmail);

        MaterialButton loginButton = findViewById(R.id.loginButton);
        MaterialButton createNewUserButton = findViewById(R.id.createNewUserButton);

        loginAnim = AnimationUtils.loadAnimation(this,R.anim.login_page_animation);

        passwordTextInput.setAnimation(loginAnim);
        passwordEditText.setAnimation(loginAnim);
        emailEditText.setAnimation(loginAnim);
        emailTextInput.setAnimation(loginAnim);
        loginButton.setAnimation(loginAnim);
        createNewUserButton.setAnimation(loginAnim);

        llProgressBar = findViewById(R.id.llProgressBar);

        session = new SessionManager(getApplicationContext());

        if(session.isLoggedIn()){
            if(session.isTimeOver()){
                llProgressBar.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                // CALL API by providing refresh Token
                RequestRefresh requestRefresh = new RequestRefresh(LoginActivity.this);
                requestRefresh.execute("Enter server port" + "/refresh");
            }
            else {
                Intent newIntent = new Intent(LoginActivity.this, HomeActivity.class);
                newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(newIntent);
            }
        }


        loginButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if(!isEmailValid(email)){
                    emailTextInput.setError(getString(R.string.error_email));
                }else if (!isPasswordValid(password)) {
                    passwordTextInput.setError(getString(R.string.error_password));
                }else {
                    //check from the server for validity
                    emailEditText.setError(null);
                    passwordTextInput.setError(null);

                    llProgressBar.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    try {
                        login(email, password);
                    } catch (Throwable throwable) {
                        llProgressBar.setVisibility(View.GONE);
                        throwable.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Internal Server Error", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Open the activity_register activity
        createNewUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // Clear the error once more than 8 characters are typed.
        passwordEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (isPasswordValid(passwordEditText.getText().toString())) {
                    passwordTextInput.setError(null); //Clear the error
                }
                return false;
            }
        });

        emailEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                emailTextInput.setError(null);
                return false;
            }
        });

    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
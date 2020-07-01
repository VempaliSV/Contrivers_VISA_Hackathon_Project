package com.example.merchantapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
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
import com.example.merchantapp.API.RequestRegister;
import com.example.merchantapp.utils.PasswordStrength;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.merchantapp.StartActivity.isoCodes;

public class RegisterActivity extends AppCompatActivity {

    Animation RegisterAnim;

    TextInputLayout passwordTextInput;
    TextInputEditText passwordEditText;
    TextInputLayout confirmPasswordTextInput;
    TextInputEditText confirmPasswordEditText;
    TextInputLayout emailTextInput;
    TextInputEditText emailEditText;
    TextInputEditText nameEditText;
    TextInputLayout nameTextInput;
    TextInputEditText numberEditText;
    TextInputLayout numberTextInput;
    TextInputEditText countryEditText;
    TextInputLayout countryTextInput;
    TextInputEditText stateEditText;
    TextInputLayout stateTextInput;
    TextInputEditText zipCodeEditText;
    TextInputLayout zipCodeTextInput;

    public static LinearLayout llProgressBarRegister;

    public void register(String email, String password, String mobileNumber, String username,String country,String state,String zipCode){
        llProgressBarRegister.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        RequestRegister requestRegister = new RequestRegister(RegisterActivity.this);
        requestRegister.execute("https://merchant-api-v1.herokuapp.com/register", email, password, mobileNumber, username,country,state,zipCode);
    }

    public boolean isPasswordValid(String password){
        return password != null && password.length() >= 8;
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public void setPasswordStrength(String password){
        PasswordStrength passwordStrength = PasswordStrength.calculate(password);
        int color = (int) passwordStrength.color;
        String msg = passwordStrength.msg;;
        passwordTextInput.setHelperText(msg);
        passwordTextInput.setHelperTextColor(ColorStateList.valueOf(color));
    }

    public boolean isConfirmPasswordCorrect(String password, String confirmPassword){
        return (password).equals(confirmPassword);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        passwordTextInput = findViewById(R.id.inputTextRegPassword);
        passwordEditText = findViewById(R.id.editTextRegPassword);
        confirmPasswordTextInput = findViewById(R.id.inputTextConfirmRegPassword);
        confirmPasswordEditText = findViewById(R.id.editTextConfirmRegPassword);
        emailTextInput = findViewById(R.id.inputTextEmail);
        emailEditText = findViewById(R.id.editTextEmail);
        nameEditText = findViewById(R.id.editTextName);
        numberEditText = findViewById(R.id.editTextNumber);
        numberTextInput = findViewById(R.id.inputTextNumber);
        nameTextInput = findViewById(R.id.inputTextName);
        countryEditText = findViewById(R.id.editTextCountry);
        countryTextInput = findViewById(R.id.inputTextCountry);
        stateEditText = findViewById(R.id.editTextState);
        stateTextInput = findViewById(R.id.inputTextState);
        zipCodeEditText = findViewById(R.id.editTextZipCode);
        zipCodeTextInput = findViewById(R.id.inputTextZipCode);


        MaterialButton registerButton = findViewById(R.id.registerButton);
        MaterialButton backButton = findViewById(R.id.backButton);

        RegisterAnim = AnimationUtils.loadAnimation(this,R.anim.register_page_animation);

        passwordTextInput.setAnimation(RegisterAnim);
        passwordEditText.setAnimation(RegisterAnim);
        emailEditText.setAnimation(RegisterAnim);
        emailTextInput.setAnimation(RegisterAnim);
        registerButton.setAnimation(RegisterAnim);
        backButton.setAnimation(RegisterAnim);
        confirmPasswordEditText.setAnimation(RegisterAnim);
        confirmPasswordTextInput.setAnimation(RegisterAnim);
        numberEditText.setAnimation(RegisterAnim);
        nameEditText.setAnimation(RegisterAnim);
        nameTextInput.setAnimation(RegisterAnim);
        numberTextInput.setAnimation(RegisterAnim);
        zipCodeTextInput.setAnimation(RegisterAnim);
        zipCodeEditText.setAnimation(RegisterAnim);
        countryTextInput.setAnimation(RegisterAnim);
        countryEditText.setAnimation(RegisterAnim);
        stateTextInput.setAnimation(RegisterAnim);
        stateEditText.setAnimation(RegisterAnim);


        llProgressBarRegister = findViewById(R.id.llProgressBarRegister);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String mobileNumber = numberEditText.getText().toString();
                String username = nameEditText.getText().toString();
                String country = countryEditText.getText().toString();
                String zipCode  = zipCodeEditText.getText().toString();
                String state  = stateEditText.getText().toString();

                if(!isEmailValid(email)){
                    emailTextInput.setError(getString(R.string.error_email));
                }
                else if (!isPasswordValid(password)) {
                    passwordTextInput.setError(getString(R.string.error_password));
                }
                else if(isEmailValid(email)&&isPasswordValid(password)){
                    if(!isConfirmPasswordCorrect(password,confirmPasswordEditText.getText().toString())){
                        confirmPasswordTextInput.setError(getString(R.string.error_confirm_password));
                    }
                    else {
                        emailEditText.setError(null);
                        passwordTextInput.setError(null);
                        if(country.equals("India") || country.equals("United States of America")) country = isoCodes.getCountryCode(country);
                        else{
                            Toast.makeText(RegisterActivity.this, "Select valid country", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        register(email, password, mobileNumber, username, country, state, zipCode);
                    }
                }
            }
        });

        passwordEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                String password = passwordEditText.getText().toString();
                if (isPasswordValid(password)) {
                    passwordTextInput.setError(null);
                    setPasswordStrength(password);
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

        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent); // this stacks up the activities.....
            }
        });
    }
}
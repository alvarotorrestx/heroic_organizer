package com.example.heroicorganizer.ui.login;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.shapes.Shape;
import android.util.Patterns;
import android.os.Bundle;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import com.example.heroicorganizer.MainActivity;
import com.example.heroicorganizer.R;
import com.example.heroicorganizer.callback.LoginCallback;
import com.example.heroicorganizer.callback.RegisterCallback;
import com.example.heroicorganizer.databinding.ActivityOnboardingBinding;
import android.app.DatePickerDialog;
import com.example.heroicorganizer.model.User;
import com.example.heroicorganizer.presenter.LoginPresenter;
import com.example.heroicorganizer.presenter.RegisterPresenter;

import java.util.Calendar;

import com.example.heroicorganizer.ui.ToastMsg;
import com.google.firebase.auth.FirebaseAuth;


public class Onboarding extends AppCompatActivity {

    private ActivityOnboardingBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Test to see if user is logged in
        boolean isSignedIn = FirebaseAuth.getInstance().getCurrentUser() != null;

        // Redirect to MainActivity if user is signed in
        if (isSignedIn) {
            Intent intent = new Intent(this, com.example.heroicorganizer.MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }

        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final View loginForm = findViewById(R.id.loginForm);
        final View registerForm = findViewById(R.id.registerForm);
        final View appTitle = findViewById(R.id.App_Title);
        final Button loginBtn = findViewById(R.id.Login);
        final Button registerBtn = findViewById(R.id.Register);
        final EditText firstNameEditText = findViewById(R.id.createFirstName);
        final EditText lastNameEditText = findViewById(R.id.createLastName);
        final EditText dobEditText = findViewById(R.id.dateOfBirth);
        final EditText registerUsernameEditText = findViewById(R.id.createUsername);
        final Button submitRegisterBtn = findViewById(R.id.submitRegister);
        final Button submitLoginBtn = findViewById(R.id.submitLogin);
        final EditText emailEditText = findViewById(R.id.email);
        final EditText createPasswordEditText = findViewById(R.id.createPassword);
        final EditText confirmPasswordEditText = findViewById(R.id.confirmPassword);
        final Button backBtn = findViewById(R.id.back);

        final Button forgotBtn = findViewById(R.id.ForgotPassword);

        // Helper to undo red invalidations when user starts typing again
        // Login Fields
        addResetOnType(usernameEditText);
        addResetOnType(passwordEditText);

        // Register Fields
        addResetOnType(firstNameEditText);
        addResetOnType(lastNameEditText);
        addResetOnType(dobEditText);
        addResetOnType(registerUsernameEditText);
        addResetOnType(emailEditText);
        addResetOnType(createPasswordEditText);
        addResetOnType(confirmPasswordEditText);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginForm.getVisibility() == View.VISIBLE || registerForm.getVisibility() == View.VISIBLE) {
                    registerForm.setVisibility(View.GONE);
                    loginForm.setVisibility(View.GONE);
                    submitRegisterBtn.setVisibility(View.GONE);
                    submitLoginBtn.setVisibility(View.GONE);
                    appTitle.setVisibility(View.VISIBLE);
                    loginBtn.setVisibility(View.VISIBLE);
                    registerBtn.setVisibility(View.VISIBLE);
                    backBtn.setVisibility(View.GONE);
                    forgotBtn.setVisibility(View.GONE);
                }
            }
        });

        dobEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(Onboarding.this,
                        (view, selectedYear, selectedMonth, selectedDay) -> {
                            //YYYY-MM-DD
                            String dob = selectedYear + "-" + String.format("%02d", selectedMonth + 1)
                                    + "-" + String.format("%02d", selectedDay);
                            dobEditText.setText(dob);
                        }, year, month, day);

                datePickerDialog.show();
            }
        });

        submitLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();


                if (email.isEmpty() || password.isEmpty()) {
                    ToastMsg.show(Onboarding.this, "Please fill out all the fields.");
                    usernameEditText.setBackgroundResource(R.drawable.field_bg_error);
                    passwordEditText.setBackgroundResource(R.drawable.field_bg_error);
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    ToastMsg.show(Onboarding.this, "Invalid email format");
                    usernameEditText.setBackgroundResource(R.drawable.field_bg_error);
                    passwordEditText.setBackgroundResource(R.drawable.field_bg_error);
                    return;
                }

                User loginUser = new User();
                loginUser.setEmail(email);
                loginUser.setPassword(password);

                // Disable the login button
                submitLoginBtn.setEnabled(false);
                // Show loading on login submit
                ToastMsg.show(Onboarding.this, "Loading...");

                LoginPresenter.loginUser(loginUser, new LoginCallback() {
                    @Override
                    public void onSuccess(String email) {
                        submitLoginBtn.setEnabled(true);
                        ToastMsg.show(Onboarding.this, email + " successfully logged in!");

                        Intent intent = new Intent(Onboarding.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                        finish();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        submitLoginBtn.setEnabled(true);
                        ToastMsg.show(Onboarding.this, errorMessage);
                    }
                });
            }
        });

        submitRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = firstNameEditText.getText().toString().trim();
                String lastName = lastNameEditText.getText().toString().trim();
                String dob = dobEditText.getText().toString().trim();
                String username = registerUsernameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = createPasswordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();


                if (firstName.isEmpty() || lastName.isEmpty() || dob.isEmpty() ||
                        username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    ToastMsg.show(Onboarding.this, "Please fill out all the fields.");
                    firstNameEditText.setBackgroundResource(R.drawable.field_bg_error);
                    lastNameEditText.setBackgroundResource(R.drawable.field_bg_error);
                    dobEditText.setBackgroundResource(R.drawable.field_bg_error);
                    registerUsernameEditText.setBackgroundResource(R.drawable.field_bg_error);
                    emailEditText.setBackgroundResource(R.drawable.field_bg_error);
                    createPasswordEditText.setBackgroundResource(R.drawable.field_bg_error);
                    confirmPasswordEditText.setBackgroundResource(R.drawable.field_bg_error);
                    return;
                }

                if (!firstName.matches("^[a-zA-Z][a-zA-Z\\- ]{1,49}$")) {
                    ToastMsg.show(Onboarding.this, "First Name must be 2–50 characters.\nLetters, spaces, and hyphens (-) allowed.");
                    firstNameEditText.setBackgroundResource(R.drawable.field_bg_error);
                    return;
                }

                if (!lastName.matches("^[a-zA-Z][a-zA-Z\\- ]{1,49}$")) {
                    ToastMsg.show(Onboarding.this, "Last Name must be 2–50 characters.\nLetters, spaces, and hyphens (-) allowed.");
                    lastNameEditText.setBackgroundResource(R.drawable.field_bg_error);
                    return;
                }

                // Age limiter - 13 yrs
                String[] parts = dob.split("-");
                if (parts.length != 3) {
                    ToastMsg.show(Onboarding.this, "Date of Birth must be in format YYYY-MM-DD");
                    dobEditText.setBackgroundResource(R.drawable.field_bg_error);
                    return;
                }
                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]) - 1;
                int day = Integer.parseInt(parts[2]);

                Calendar birthDate = Calendar.getInstance();
                birthDate.set(year, month, day);

                Calendar todayMinus13 = Calendar.getInstance();
                todayMinus13.add(Calendar.YEAR, -13);

                // If birthDate is less than 13 years
                if (birthDate.after(todayMinus13)) {
                    ToastMsg.show(Onboarding.this, "You must be at least 13 years old to register.");
                    dobEditText.setBackgroundResource(R.drawable.field_bg_error);
                    return;
                }

                if (!username.matches("^[A-Za-z0-9_-]{5,30}$")) {
                    ToastMsg.show(Onboarding.this, "Username must be 5–30 characters.\nLetters, numbers, _, and - allowed.");
                    registerUsernameEditText.setBackgroundResource(R.drawable.field_bg_error);
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    ToastMsg.show(Onboarding.this, "Invalid email format");
                    emailEditText.setBackgroundResource(R.drawable.field_bg_error);
                    return;
                }

                if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%]).{8,24}$")) {
                    ToastMsg.show(Onboarding.this,
                            "Password must be 8–24 characters. Uppercase letter. Lowercase letter. Number. Special character (!@#$%).");
                    createPasswordEditText.setBackgroundResource(R.drawable.field_bg_error);
                    confirmPasswordEditText.setBackgroundResource(R.drawable.field_bg_error);
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    ToastMsg.show(Onboarding.this, "Passwords do not match");
                    createPasswordEditText.setBackgroundResource(R.drawable.field_bg_error);
                    confirmPasswordEditText.setBackgroundResource(R.drawable.field_bg_error);
                    return;
                }

                // Disable the register button
                submitRegisterBtn.setEnabled(false);
                // Show loading on register submit
                ToastMsg.show(Onboarding.this, "Loading...");

                /// verify and save credentials here
                User newUser = new User(firstName, lastName, dob, email, username, password, "user");


                RegisterPresenter.registerUser(newUser, new RegisterCallback() {
                    @Override
                    public void onSuccess(String username) {
                        submitRegisterBtn.setEnabled(true);
                        ToastMsg.show(Onboarding.this, username + " successfully registered!");
                        startActivity(new Intent(Onboarding.this, MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        submitRegisterBtn.setEnabled(true);
                        ToastMsg.show(Onboarding.this, errorMessage);
                    }
                });
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginForm.setVisibility(View.VISIBLE);
                submitLoginBtn.setVisibility(View.VISIBLE);
                loginBtn.setVisibility(View.GONE);
                registerForm.setVisibility(View.GONE);
                appTitle.setVisibility(View.GONE);
                registerBtn.setVisibility(View.GONE);
                backBtn.setVisibility(View.VISIBLE);
                forgotBtn.setVisibility(View.VISIBLE);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginForm.setVisibility(View.GONE);
                registerForm.setVisibility(View.VISIBLE);
                submitRegisterBtn.setVisibility(View.VISIBLE);
                appTitle.setVisibility(View.GONE);
                loginBtn.setVisibility(View.GONE);
                registerBtn.setVisibility(View.GONE);
                backBtn.setVisibility(View.VISIBLE);
            }
        });

        forgotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastMsg.show(Onboarding.this, "TODO: Add forgot password form LOL");
            }
        });


        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
//                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
//                        passwordEditText.getText().toString());
            }
        };

        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //               if (actionId == EditorInfo.IME_ACTION_DONE) {
//                    loginViewModel.login(usernameEditText.getText().toString(),
//                            passwordEditText.getText().toString());
                //               }
                return false;
            }
        });


    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        ToastMsg.show(getApplicationContext(), welcome);
    }

    private void showLoginFailed(@StringRes Integer errorString) {

    }

    private void addResetOnType(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editText.setBackgroundResource(R.drawable.field_bg);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

}
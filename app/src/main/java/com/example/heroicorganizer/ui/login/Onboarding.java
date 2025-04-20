package com.example.heroicorganizer.ui.login;

import android.app.Activity;
import android.util.Log;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import com.example.heroicorganizer.MainActivity;
import com.example.heroicorganizer.R;
import com.example.heroicorganizer.databinding.ActivityOnboardingBinding;
import android.app.DatePickerDialog;
import com.example.heroicorganizer.model.User;
import com.example.heroicorganizer.presenter.RegisterPresenter;
import android.content.SharedPreferences;
import java.util.Calendar;
import com.example.heroicorganizer.ui.ToastMsg;


public class Onboarding extends AppCompatActivity {

    private ActivityOnboardingBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                String inputUsername = usernameEditText.getText().toString().trim();
                String inputPassword = passwordEditText.getText().toString().trim();

                //
                // TESTER CREDENTIALS JUST TO GET LOGIN WORKING
                //
                if (inputUsername.equals("tester1") && inputPassword.equals("Tester123!")) {

                    //storing a boolean state inside new preferences file
                    SharedPreferences.Editor editor = getSharedPreferences("heroic_preferences", MODE_PRIVATE).edit();
                    editor.putBoolean("isSignedIn", true);
                    editor.commit();

                    Intent intent = new Intent(Onboarding.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }  else {
                ToastMsg.show(Onboarding.this, "Invalid credentials");
            }
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
                    return;
                }

                if (!firstName.matches("^[a-zA-Z][a-zA-Z\\- ]{1,49}$")) {
                    ToastMsg.show(Onboarding.this, "First Name must be 2–50 characters.\nLetters, spaces, and hyphens (-) allowed.");
                    return;
                }

                if (!lastName.matches("^[a-zA-Z][a-zA-Z\\- ]{1,49}$")) {
                    ToastMsg.show(Onboarding.this, "Last Name must be 2–50 characters.\nLetters, spaces, and hyphens (-) allowed.");
                    return;
                }

                if (!username.matches("^[a-z0-9-]{5,30}$")) {
                    ToastMsg.show(Onboarding.this, "Username must be 5–30 lowercase characters.\nLetters, numbers, and hyphens (-) allowed.");
                    return;
                }

                if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                    ToastMsg.show(Onboarding.this, "Email must be valid.\ne.g. bwayne@wayneenterprises.com");
                    return;
                }

                if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%]).{8,24}$")) {
                    ToastMsg.show(Onboarding.this,
                            "Password must be 8–24 characters. Uppercase letter. Lowercase letter. Number. Special character (!@#$%).");
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    ToastMsg.show(Onboarding.this, "Passwords do not match");
                    return;
                }

                /// verify and save credentials here
                User newUser = new User(firstName, lastName, dob, email, username, password, "user");
                RegisterPresenter.registerUser(newUser);

                //startActivity(new Intent(Onboarding.this, MainActivity.class));
                //finish();
                ToastMsg.show(Onboarding.this, "Account created");

                registerForm.setVisibility(View.GONE);
                submitRegisterBtn.setVisibility(View.GONE);
                appTitle.setVisibility(View.VISIBLE);
                loginBtn.setVisibility(View.VISIBLE);
                registerBtn.setVisibility(View.VISIBLE);
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
}
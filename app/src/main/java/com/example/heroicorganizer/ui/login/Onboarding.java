package  com.example.heroicorganizer.ui.login;

import android.app.Activity;
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
import java.util.Calendar;

public class Onboarding extends AppCompatActivity {

    private LoginViewModel loginViewModel;
private ActivityOnboardingBinding binding;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

     binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
     setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.Login;
        final ProgressBar loadingProgressBar = binding.loading;

        final View loginForm = findViewById(R.id.loginForm);
        final View registerForm = findViewById(R.id.registerForm);
        final View appTitle = findViewById(R.id.App_Title);
        final Button loginBtn = findViewById(R.id.Login);
        final Button registerBtn = findViewById(R.id.Register);
        final EditText firstNameEditText = findViewById(R.id.createFirstName);
        final EditText lastNameEditText = findViewById(R.id.createLastName);
        final EditText dobEditText = findViewById(R.id.dateOfBirth);
        final Button submitRegisterBtn = findViewById(R.id.submitRegister);
        final EditText emailEditText = findViewById(R.id.email);
        final EditText createPasswordEditText = findViewById(R.id.createPassword);
        final EditText confirmPasswordEditText = findViewById(R.id.confirmPassword);

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

        submitRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = firstNameEditText.getText().toString().trim();
                String lastName = lastNameEditText.getText().toString().trim();
                String dob = dobEditText.getText().toString().trim();
                String username = usernameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = createPasswordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                if (firstName.isEmpty() || lastName.isEmpty() || dob.isEmpty() ||
                        username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(Onboarding.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(Onboarding.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                /// verify and save credentials here

                startActivity(new Intent(Onboarding.this, MainActivity.class));
                finish();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginForm.setVisibility(View.VISIBLE);
                registerForm.setVisibility(View.GONE);
                appTitle.setVisibility(View.GONE);
                registerBtn.setVisibility(View.GONE);
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
            }
        });



        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                    Intent intent = new Intent(Onboarding.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
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
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}
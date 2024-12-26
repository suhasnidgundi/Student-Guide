package com.zeal.studentguide.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zeal.studentguide.R;
import com.zeal.studentguide.databinding.ActivityRegisterBinding;
import com.zeal.studentguide.models.User;
import com.zeal.studentguide.utils.FirebaseManager;
import com.zeal.studentguide.utils.PreferenceManager;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private FirebaseManager firebaseManager;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseManager = FirebaseManager.getInstance();
        preferenceManager = new PreferenceManager(this);

        setupListeners();
        setupSpinner();
    }

    private void setupListeners() {
        binding.buttonRegister.setOnClickListener(v -> handleRegistration());
        binding.textLogin.setOnClickListener(v -> finish());
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.user_roles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerRole.setAdapter(adapter);
    }

    private void handleRegistration() {
        if (validateInputs()) {
            showLoading();
            String name = binding.editTextName.getText().toString();
            String email = binding.editTextEmail.getText().toString().trim();
            String password = binding.editTextPassword.getText().toString().trim();
            String role = binding.spinnerRole.getSelectedItem().toString().trim();

            firebaseManager.registerUser(name, email, password, role, new FirebaseManager.FirebaseCallback<User>() {
                @Override
                public void onSuccess(User user) {
                    hideLoading();
                    preferenceManager.setLoggedIn(true);
                    preferenceManager.setUserRole(user.getRole());
                    preferenceManager.setUserId(user.getUserId());

                    Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onError(Exception e) {
                    hideLoading();
                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            hideLoading();
        }
    }

    private boolean validateInputs() {
        boolean isValid = true;
        String name = binding.editTextName.getText().toString();
        String email = binding.editTextEmail.getText().toString();
        String password = binding.editTextPassword.getText().toString();

        if (name.isEmpty()) {
            binding.editTextName.setError("Name is required");
            isValid = false;
        }

        if (email.isEmpty()) {
            binding.editTextEmail.setError("Email is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.editTextEmail.setError("Invalid email format");
            isValid = false;
        }

        if (password.isEmpty()) {
            binding.editTextPassword.setError("Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            binding.editTextPassword.setError("Password must be at least 6 characters");
            isValid = false;
        }

        return isValid;
    }

    private void showLoading() {
        binding.buttonRegister.setEnabled(false);
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        binding.buttonRegister.setEnabled(true);
        binding.progressBar.setVisibility(View.GONE);
    }
}
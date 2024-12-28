package com.zeal.studentguide.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.zeal.studentguide.MainActivity;
import com.zeal.studentguide.databinding.ActivityLoginBinding;
import com.zeal.studentguide.models.User;
import com.zeal.studentguide.utils.FirebaseManager;
import com.zeal.studentguide.utils.PreferenceManager;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseManager firebaseManager;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseManager = FirebaseManager.getInstance();
        preferenceManager = new PreferenceManager(this);

        setupListeners();
    }

    private void setupListeners() {
        binding.buttonLogin.setOnClickListener(v -> handleLogin());
        binding.textRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void handleLogin() {
        if (validateInputs()) {
            showLoading();
            String email = binding.editTextEmail.getText().toString();
            String password = binding.editTextPassword.getText().toString();

            firebaseManager.loginUser(email, password, new FirebaseManager.FirebaseCallback<User>() {
                @Override
                public void onSuccess(User user) {
                    hideLoading();
                    preferenceManager.setLoggedIn(true);
                    preferenceManager.setUserId(user.getUserId());
                    preferenceManager.setUsername(user.getName());
                    preferenceManager.setUserEmail(user.getEmail());
                    preferenceManager.setUserRole(user.getRole());

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }

                @Override
                public void onError(Exception e) {
                    hideLoading();
                    Toast.makeText(LoginActivity.this,
                            "Login failed: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }

        hideLoading();
    }

    private boolean validateInputs() {
        boolean isValid = true;
        String email = binding.editTextEmail.getText().toString();
        String password = binding.editTextPassword.getText().toString();

        if (email.isEmpty()) {
            binding.editTextEmail.setError("Email is required");
            isValid = false;
        }

        if (password.isEmpty()) {
            binding.editTextPassword.setError("Password is required");
            isValid = false;
        }

        return isValid;
    }

    private void showLoading() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.buttonLogin.setEnabled(false);
    }

    private void hideLoading() {
        binding.progressBar.setVisibility(View.GONE);
        binding.buttonLogin.setEnabled(true);
    }
}
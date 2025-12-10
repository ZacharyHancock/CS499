package com.example.cs360app;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * LoginActivity handles user authentication and new user registration.
 * Users provide a username and password, which are validated using DBHelper.
 *
 * Behavior:
 * - Login button → verifies credentials and navigates to DataActivity.
 * - Register button → attempts to create a new user account.
 *
 * This Activity serves as the entry point for the application’s secure features.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText usernameInput, passwordInput;
    private DBHelper db;
    private Button loginButton, registerButton;


    /**
     * Initializes UI elements, sets up database helper, and attaches
     * button listeners for login and registration actions.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI fields
        usernameInput = findViewById(R.id.editUser);
        passwordInput = findViewById(R.id.editPass);

        // Initialize db and ensure it opens
        db = new DBHelper(this);
        db.getWritableDatabase(); // test if open

        //Buttons
        registerButton = findViewById(R.id.registerButton);
        loginButton = findViewById(R.id.loginButton);

        // Button Listeners
        registerButton.setOnClickListener(v -> registerUser());
        loginButton.setOnClickListener(v -> loginUser());
    }


    /**
     * Attempts to log the user in by checking username/password
     * against stored credentials using DBHelper.
     *
     * If the user exists → navigate to main DataActivity.
     * Otherwise → show an error message.
     */
    public void loginUser() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (db.checkUser(username, password)) {
            // Successful login, go to DataActivity
            startActivity(new Intent(this, DataActivity.class)); // go to main data screen
        } else {
            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Registers a new user by attempting to insert the provided
     * username/password into the database.
     *
     * If the username already exists → show error message.
     * Otherwise → register successfully.
     */
    public void registerUser() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (db.addUser(username, password)) {
            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show();
        }
    }
}

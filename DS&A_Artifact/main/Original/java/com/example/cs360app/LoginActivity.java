package com.example.cs360app;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


public class LoginActivity extends AppCompatActivity {

    private EditText usernameInput, passwordInput;
    private DBHelper db;
    private Button loginButton, registerButton;

    // onCreate initializes EditTexts, db, and buttons as well as button listeners
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameInput = findViewById(R.id.editUser);
        passwordInput = findViewById(R.id.editPass);
        db = new DBHelper(this);
        db.getWritableDatabase(); // test if open

        registerButton = findViewById(R.id.registerButton);
        loginButton = findViewById(R.id.loginButton);

        registerButton.setOnClickListener(v -> registerUser());
        loginButton.setOnClickListener(v -> loginUser());
    }

    // checks if user exists in db and allows login if so, prompts them that the credentials are wrong if not
    public void loginUser() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (db.checkUser(username, password)) {
            startActivity(new Intent(this, DataActivity.class)); // go to main data screen
        } else {
            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
        }
    }

    // If the user is not in the db the register button will add them, if so they will be told that user already exists
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

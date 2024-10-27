
package com.example.reto1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginUsuario extends AppCompatActivity {
    Switch switcher;//Esto es para cambiar el tema
    boolean nightMODE;//Esto es para cambiar el tema
    SharedPreferences sharedPreferences;//Esto es para cambiar el tema
    SharedPreferences.Editor editor;//Esto es para cambiar el tema

    private EditText editTextGmail;
    private EditText editTextContrasenya;
    private Button btnInicioSesion;
    private String ClienteActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_usuario);

        getSupportActionBar().hide(); //Esto es para cambiar el tema

        switcher = findViewById(R.id.switcher);
        //We used sharedPreferences to save mode if exit the app and go back again
        sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        nightMODE = sharedPreferences.getBoolean("night", false);//light mode is default mode


        if (nightMODE) {
            switcher.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        switcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nightMODE) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("night", false);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("night", true);

                }
                editor.apply();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextGmail = findViewById(R.id.LoginGmailtxt);
        editTextContrasenya = findViewById(R.id.LoginContrasenyatxt);
        btnInicioSesion = findViewById(R.id.btnLogin);
        View txtRegistro = findViewById(R.id.txtRegistro);  //Para que al clickar nos lleve a la página de registro

        btnInicioSesion.setOnClickListener(v -> {
            String gmail = editTextGmail.getText().toString().trim();
            String contrasenya = editTextContrasenya.getText().toString().trim();

            if (!gmail.isEmpty() && !contrasenya.isEmpty()) {
                guardarEnFirestore(gmail, contrasenya);
            } else {
                Toast.makeText(LoginUsuario.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            }
        });


        txtRegistro.setOnClickListener(v -> {
            Intent intent = new Intent(LoginUsuario.this, RegistroUsuario.class);
            startActivity(intent);


        });


    }

    private void guardarEnFirestore(String gmail, String contrasenya) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Comprobar si el usuario ya existe
        db.collection("Clientes")
                .whereEqualTo("Email", gmail)
                .whereEqualTo("Contraseña", contrasenya)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        // Usuario ya registrado
                        Toast.makeText(LoginUsuario.this, "Bienvenido", Toast.LENGTH_SHORT).show();

                        boolean esEntrenador = true; // Valor por defecto
                        for (DocumentSnapshot document : task.getResult()) {
                            esEntrenador = document.getBoolean("esEntrenador");
                        }

                        // Redirigir según el valor de esEntrenador
                        Intent intent;
                        if (esEntrenador) {
                            intent = new Intent(LoginUsuario.this, PantallaWorkouts.class);
                        } else {
                            intent = new Intent(LoginUsuario.this, AppCliente.class);
                            ClienteActual = AppCliente.mObtenerDatosCliente(gmail);
                            AppCliente.mCargarWorkouts();

                        }

                        intent.putExtra("gmail", gmail);  // Aquí pasamos el gmail
                        intent.putExtra("contrasenya", contrasenya);
                        startActivity(intent);
                    } else {
                        // Usuario no registrado
                        Toast.makeText(LoginUsuario.this, "Error, Usuario no registrado.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
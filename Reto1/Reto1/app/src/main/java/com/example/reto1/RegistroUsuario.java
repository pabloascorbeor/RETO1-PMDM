package com.example.reto1;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class RegistroUsuario extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registro_usuario);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        EditText editTextGmail = findViewById(R.id.Gmailtxt);
        EditText editTextContrasenya = findViewById(R.id.Contrasenyatxt);
        RadioGroup radioGroup = findViewById(R.id.radioGroup); //Para saber si es Entrenador o Cliente
        Button btnRegistrar = findViewById(R.id.btnRegistro);
        EditText editTextApellido = findViewById(R.id.Apellidotxt);
        EditText editTextNombre = findViewById(R.id.Nombretxt);

        DatePicker datePicker = findViewById(R.id.fechaNacimiiento);

        btnRegistrar.setOnClickListener(v -> {
            String gmail = editTextGmail.getText().toString().trim();
            String contrasenya = editTextContrasenya.getText().toString().trim();
            String nombre = editTextNombre.getText().toString().trim();
            String apellido = editTextApellido.getText().toString().trim();

            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth() + 1; // Months are 0-based, so add 1
            int year = datePicker.getYear();

            String fechaNacimiento = day + "/" + month + "/" + year; // Formato de fecha dd/mm/yyyy

// Obtener el ID del RadioButton seleccionado
            int selectedId = radioGroup.getCheckedRadioButtonId();
            boolean esEntrenador = false;

            if (selectedId == R.id.radioEntrenador) {
                esEntrenador = true; // Si se seleccionó "Entrenador"
            }

            if (!gmail.isEmpty() && !contrasenya.isEmpty() && !nombre.isEmpty() && !apellido.isEmpty()) {
                guardarEnFirestore(gmail, contrasenya, nombre, apellido, fechaNacimiento, esEntrenador);
            } else {
                Toast.makeText(RegistroUsuario.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            }
        });


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Reto1GymApp");

// Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
// This method is called once with the initial value and again
// whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
// Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });




// Método para habilitar el modo de borde a borde (si es necesario)

    }

    private void guardarEnFirestore(String gmail, String contrasenya, String nombre, String apellido, String fechaNacimiento, boolean esEntrenador) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

// Comprobar si el usuario ya existe
        db.collection("Clientes")
                .whereEqualTo("Email", gmail)
                .whereEqualTo("Contraseña", contrasenya)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
// Usuario ya registrado
                        Toast.makeText(RegistroUsuario.this, "Usuario ya registrado", Toast.LENGTH_SHORT).show();
                    } else {
// Usuario no registrado, proceder a guardar
                        Map<String, Object> cliente = new HashMap<>();
                        cliente.put("Email", gmail);
                        cliente.put("Contraseña", contrasenya);
                        cliente.put("Nombre", nombre);
                        cliente.put("Apellido", apellido);

// Convertir la fecha a un objeto Date
                        String[] fechaParts = fechaNacimiento.split("/");
                        int day = Integer.parseInt(fechaParts[0]);
                        int month = Integer.parseInt(fechaParts[1]);
                        int year = Integer.parseInt(fechaParts[2]);

                        java.util.Date fechaNa = new java.util.Date(year - 1900, month - 1, day);

                        cliente.put("FechaNa", fechaNa);

                        cliente.put("esEntrenador", esEntrenador);
                        cliente.put("nivelUsuario", 0);

// Guardar los datos en la colección "clientes"
                        db.collection("Clientes")

                                .add(cliente)
                                .addOnSuccessListener(documentReference -> {
                                    Toast.makeText(RegistroUsuario.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegistroUsuario.this, LoginUsuario.class);
                                    intent.putExtra("gmail", gmail);
                                    startActivity(intent);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(RegistroUsuario.this, "Error al registrar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RegistroUsuario.this, "Error al verificar usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


}public class RegistroUsuario extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registro_usuario);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        EditText editTextGmail = findViewById(R.id.Gmailtxt);
        EditText editTextContrasenya = findViewById(R.id.Contrasenyatxt);
        RadioGroup radioGroup = findViewById(R.id.radioGroup); //Para saber si es Entrenador o Cliente
        Button btnRegistrar = findViewById(R.id.btnRegistro);
        EditText editTextApellido = findViewById(R.id.Apellidotxt);
        EditText editTextNombre = findViewById(R.id.Nombretxt);

        DatePicker datePicker = findViewById(R.id.fechaNacimiiento);

        btnRegistrar.setOnClickListener(v -> {
            String gmail = editTextGmail.getText().toString().trim();
            String contrasenya = editTextContrasenya.getText().toString().trim();
            String nombre = editTextNombre.getText().toString().trim();
            String apellido = editTextApellido.getText().toString().trim();

            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth() + 1; // Months are 0-based, so add 1
            int year = datePicker.getYear();

            String fechaNacimiento = day + "/" + month + "/" + year; // Formato de fecha dd/mm/yyyy

// Obtener el ID del RadioButton seleccionado
            int selectedId = radioGroup.getCheckedRadioButtonId();
            boolean esEntrenador = false;

            if (selectedId == R.id.radioEntrenador) {
                esEntrenador = true; // Si se seleccionó "Entrenador"
            }

            if (!gmail.isEmpty() && !contrasenya.isEmpty() && !nombre.isEmpty() && !apellido.isEmpty()) {
                guardarEnFirestore(gmail, contrasenya, nombre, apellido, fechaNacimiento, esEntrenador);
            } else {
                Toast.makeText(RegistroUsuario.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            }
        });


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Reto1GymApp");

// Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
// This method is called once with the initial value and again
// whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
// Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });




// Método para habilitar el modo de borde a borde (si es necesario)

    }

    private void guardarEnFirestore(String gmail, String contrasenya, String nombre, String apellido, String fechaNacimiento, boolean esEntrenador) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

// Comprobar si el usuario ya existe
        db.collection("Clientes")
                .whereEqualTo("Email", gmail)
                .whereEqualTo("Contraseña", contrasenya)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
// Usuario ya registrado
                        Toast.makeText(RegistroUsuario.this, "Usuario ya registrado", Toast.LENGTH_SHORT).show();
                    } else {
// Usuario no registrado, proceder a guardar
                        Map<String, Object> cliente = new HashMap<>();
                        cliente.put("Email", gmail);
                        cliente.put("Contraseña", contrasenya);
                        cliente.put("Nombre", nombre);
                        cliente.put("Apellido", apellido);

// Convertir la fecha a un objeto Date
                        String[] fechaParts = fechaNacimiento.split("/");
                        int day = Integer.parseInt(fechaParts[0]);
                        int month = Integer.parseInt(fechaParts[1]);
                        int year = Integer.parseInt(fechaParts[2]);

                        java.util.Date fechaNa = new java.util.Date(year - 1900, month - 1, day);

                        cliente.put("FechaNa", fechaNa);

                        cliente.put("esEntrenador", esEntrenador);
                        cliente.put("nivelUsuario", 0);

// Guardar los datos en la colección "clientes"
                        db.collection("Clientes")

                                .add(cliente)
                                .addOnSuccessListener(documentReference -> {
                                    Toast.makeText(RegistroUsuario.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegistroUsuario.this, LoginUsuario.class);
                                    intent.putExtra("gmail", gmail);
                                    startActivity(intent);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(RegistroUsuario.this, "Error al registrar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RegistroUsuario.this, "Error al verificar usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


}public class RegistroUsuario extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registro_usuario);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        EditText editTextGmail = findViewById(R.id.Gmailtxt);
        EditText editTextContrasenya = findViewById(R.id.Contrasenyatxt);
        RadioGroup radioGroup = findViewById(R.id.radioGroup); //Para saber si es Entrenador o Cliente
        Button btnRegistrar = findViewById(R.id.btnRegistro);
        EditText editTextApellido = findViewById(R.id.Apellidotxt);
        EditText editTextNombre = findViewById(R.id.Nombretxt);

        DatePicker datePicker = findViewById(R.id.fechaNacimiiento);

        btnRegistrar.setOnClickListener(v -> {
            String gmail = editTextGmail.getText().toString().trim();
            String contrasenya = editTextContrasenya.getText().toString().trim();
            String nombre = editTextNombre.getText().toString().trim();
            String apellido = editTextApellido.getText().toString().trim();

            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth() + 1; // Months are 0-based, so add 1
            int year = datePicker.getYear();

            String fechaNacimiento = day + "/" + month + "/" + year; // Formato de fecha dd/mm/yyyy

// Obtener el ID del RadioButton seleccionado
            int selectedId = radioGroup.getCheckedRadioButtonId();
            boolean esEntrenador = false;

            if (selectedId == R.id.radioEntrenador) {
                esEntrenador = true; // Si se seleccionó "Entrenador"
            }

            if (!gmail.isEmpty() && !contrasenya.isEmpty() && !nombre.isEmpty() && !apellido.isEmpty()) {
                guardarEnFirestore(gmail, contrasenya, nombre, apellido, fechaNacimiento, esEntrenador);
            } else {
                Toast.makeText(RegistroUsuario.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            }
        });


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Reto1GymApp");

// Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
// This method is called once with the initial value and again
// whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
// Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });




// Método para habilitar el modo de borde a borde (si es necesario)

    }

    private void guardarEnFirestore(String gmail, String contrasenya, String nombre, String apellido, String fechaNacimiento, boolean esEntrenador) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

// Comprobar si el usuario ya existe
        db.collection("Clientes")
                .whereEqualTo("Email", gmail)
                .whereEqualTo("Contraseña", contrasenya)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
// Usuario ya registrado
                        Toast.makeText(RegistroUsuario.this, "Usuario ya registrado", Toast.LENGTH_SHORT).show();
                    } else {
// Usuario no registrado, proceder a guardar
                        Map<String, Object> cliente = new HashMap<>();
                        cliente.put("Email", gmail);
                        cliente.put("Contraseña", contrasenya);
                        cliente.put("Nombre", nombre);
                        cliente.put("Apellido", apellido);

// Convertir la fecha a un objeto Date
                        String[] fechaParts = fechaNacimiento.split("/");
                        int day = Integer.parseInt(fechaParts[0]);
                        int month = Integer.parseInt(fechaParts[1]);
                        int year = Integer.parseInt(fechaParts[2]);

                        java.util.Date fechaNa = new java.util.Date(year - 1900, month - 1, day);

                        cliente.put("FechaNa", fechaNa);

                        cliente.put("esEntrenador", esEntrenador);
                        cliente.put("nivelUsuario", 0);

// Guardar los datos en la colección "clientes"
                        db.collection("Clientes")

                                .add(cliente)
                                .addOnSuccessListener(documentReference -> {
                                    Toast.makeText(RegistroUsuario.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegistroUsuario.this, LoginUsuario.class);
                                    intent.putExtra("gmail", gmail);
                                    startActivity(intent);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(RegistroUsuario.this, "Error al registrar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RegistroUsuario.this, "Error al verificar usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


}
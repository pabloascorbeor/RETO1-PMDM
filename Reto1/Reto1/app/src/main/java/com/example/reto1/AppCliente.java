package com.example.reto1;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AppCliente extends AppCompatActivity {

    private Spinner spinnerNivel; // Declarar el Spinner


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_app_cliente);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Inicializar el Spinner
        spinnerNivel = findViewById(R.id.spinner_nivel); // Asegúrate de que el ID sea correcto

        // Cargar niveles del usuario
        cargarNiveles();

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

        String gmail = getIntent().getStringExtra("gmail");
        String contrasenya = getIntent().getStringExtra("contrasenya");

        // Mostrar los datos en un TextView (asegúrate de tener un TextView en tu layout)
        TextView textView = findViewById(R.id.textViewDatosUsuario);
        textView.setText("Gmail: " + gmail);




    }



    private void cargarNiveles() {
        Clientes cl = this.clienteActual; // Asegúrate de que clienteActual esté inicializado

        // Crear una lista para los niveles
        List<Integer> nivelClienteList = new ArrayList<>();
        int nivelUsuario = (int) cl.getNivelUsuario();

        for (int i = 0; i <= nivelUsuario; i++) {
            nivelClienteList.add(i); // Añadir niveles a la lista
        }

        // Crear un adaptador y asignarlo al Spinner
        ArrayAdapter<Integer> nivelClienteAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, nivelClienteList);
        nivelClienteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNivel.setAdapter(nivelClienteAdapter);
    }

    static String mObtenerDatosCliente(String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Clientes")
                .whereEqualTo("Email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String nombre = document.getString("Nombre");
                                String apellido = document.getString("Apellido");
                                String fechaNa = document.getDate("FechaNa").toString();
                                boolean esEntrenador = document.getBoolean("esEntrenador");

                            }
                        } else {
                            Log.d(TAG, "No se encontraron documentos");
                        }
                    } else {
                        Log.w(TAG, "Error al obtener documentos.", task.getException());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error al acceder a Firestore", e);
                });
        return email;
    }


    static void mCargarWorkouts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Workouts")
                .get() // Obtiene todos los documentos de la colección "Workouts"
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            // Usar un HashMap para almacenar los datos del documento
                            Map<String, Object> workoutData = document.getData();
                            if (workoutData != null) {
                                // Obtener los datos
                                String id = document.getId();
                                long nivelWorkout = (long) workoutData.get("nivelWorkout");

                                String nombreWorkout = (String) workoutData.get("nombreWorkout");
                                long numEjerWorkout = (long) workoutData.get("numEjerWorkout");
                                String video = (String) workoutData.get("video");


                                Log.d(TAG, "Workout cargado: " +
                                        "ID: " + id +
                                        ", Nivel: " + nivelWorkout +
                                        ", Nombre: " + nombreWorkout +
                                        ", Número de Ejercicios: " + numEjerWorkout +
                                        ", Video: " + video);
                            }
                        }
                    } else {
                        Log.w(TAG, "Error al obtener documentos.", task.getException());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error al acceder a Firestore", e);
                });
    }

}
package com.example.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Database.DatabaseAux;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class registrar extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registrar);
    }

    public void changeToSesion(View view) {
        Intent nIntent = new Intent(registrar.this, iniciar_sesion.class);
        startActivity(nIntent);
    }

    public void insertValues(View v) {

        TextView nameTextView = findViewById(R.id.insertName);
        TextView emailTextView = findViewById(R.id.insertEmail);
        TextView passwordTextView = findViewById(R.id.insertContrasena);
        TextView directionTextView= findViewById(R.id.insertDireccion);
        TextView telephoneTextView = findViewById(R.id.insertTelf);
        TextView dateTextView= findViewById(R.id.insertFecha);


        String nameString = nameTextView.getText().toString();
        String emailString = emailTextView.getText().toString();
        String passwordString = passwordTextView.getText().toString();
        String directionString = directionTextView.getText().toString();
        String telephoneString = telephoneTextView.getText().toString();
        String dateString = dateTextView.getText().toString();

        if(!nameString.isEmpty() && !emailString.isEmpty() && !passwordString.isEmpty()
                && !directionString.isEmpty() && !telephoneString.isEmpty() && !dateString.isEmpty()){

            DatabaseAux aux = new DatabaseAux(registrar.this);
            SQLiteDatabase db = aux.getWritableDatabase();

            // Verificar si el usuario ya existe por nombre
            Cursor cursor = db.query("users", null, "name=?", new String[]{nameString}, null, null, null);

                // Ya existe un usuario con el mismo nombre
                Toast.makeText(this, "El usuario ya existe", Toast.LENGTH_SHORT).show();
                cursor.close();

                // No existe un usuario con el mismo nombre, se agrega
                ContentValues values = new ContentValues();
                values.put("name", nameString);
                values.put("password", passwordString);
                values.put("date", dateString);
                values.put("email", emailString);
                values.put("telephone", telephoneString);
                values.put("direction", directionString);

                long res = db.insert("users", null, values);
                if (res >= 0) {
                    Toast.makeText(this, "Insertado correctamente", Toast.LENGTH_SHORT).show();
                    UserData.setUserDirection(directionString);
                    UserData.setUserName(nameString);
                    UserData.setUserID((int) res);


                    FirebaseFirestore firestoreDb = FirebaseFirestore.getInstance();

                    // Crear usuario
                    Map<String, Object> user = new HashMap<>();
                    user.put("id", String.valueOf(res));
                    user.put("name", nameString);
                    user.put("password", passwordString);
                    user.put("date", dateString);
                    user.put("email", emailString);
                    user.put("telephone", telephoneString);
                    user.put("direction", directionString);

                    // Verificar si el usuario ya existe por nombre
                    firestoreDb.collection("users")
                            .whereEqualTo("name", nameString)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (task.getResult().isEmpty()) {
                                            // No existe un usuario con el mismo nombre, se puede agregar
                                            firestoreDb.collection("users").document(nameString)
                                                    //Se agrega
                                                    .set(user)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Log.d("DEBUG", "Usuario agregado exitosamente");
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.d("ERROR", "Error al agregar usuario");
                                                        }
                                                    });
                                        } else {
                                            // Ya existe un usuario con el mismo nombre
                                            Log.d("ERROR", "El usuario ya existe");
                                        }
                                    } else {
                                        Log.d("ERROR", "Error al verificar la existencia del usuario");
                                    }
                                }
                            });

                    Intent nIntent = new Intent(registrar.this, Comidas.class);
                    startActivity(nIntent);
                } else {
                    Toast.makeText(this, "Fallo al insertar", Toast.LENGTH_SHORT).show();
                }
                nameTextView.setText("");
                emailTextView.setText("");
                passwordTextView.setText("");
                directionTextView.setText("");
                telephoneTextView.setText("");
                dateTextView.setText("");
        }else{
            Toast.makeText(this, "Campos vacíos", Toast.LENGTH_SHORT).show();
        }
        }
        }



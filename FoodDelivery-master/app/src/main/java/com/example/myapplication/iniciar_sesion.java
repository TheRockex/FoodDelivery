package com.example.myapplication;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class iniciar_sesion extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iniciar_sesion);
    }
    public void changeToRegistrar(View view) {
        Intent nIntent = new Intent(iniciar_sesion.this, registrar.class);
        startActivity(nIntent);
    }
    public void iniciarSesion(View view) {
        TextView nameTextView = findViewById(R.id.deleteName);
        TextView contraTextView = findViewById(R.id.deleteContrasena);

        String nameString = nameTextView.getText().toString();
        String contraString = contraTextView.getText().toString();

                   FirebaseFirestore firestoreDb = FirebaseFirestore.getInstance();

                   //Para saber si la cuenta existe o no
                   firestoreDb.collection("users")
                           .whereEqualTo("name", nameString)
                           .whereEqualTo("password", contraString)
                           .get()
                           .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                               @Override
                               //Sino existe ponga usuario no encontrado y si existe le ponga usuario encontrado y le deje entrar
                               public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                   if (task.isSuccessful()) {
                                       if (task.getResult().isEmpty()) {
                                           Toast.makeText(iniciar_sesion.this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                                       } else {
                                           Toast.makeText(iniciar_sesion.this, "Usuario encontrado", Toast.LENGTH_SHORT).show();

                                           // Obtener la primera coincidencia (asumiendo que no hay duplicados)
                                           DocumentSnapshot document = task.getResult().getDocuments().get(0);

                                           // Obtener nombre y dirección
                                           String userName = document.getString("name");
                                           String userAddress = document.getString("direction");
                                           String userID = document.getString("id");

                                           UserData.setUserDirection(userAddress);
                                           UserData.setUserName(userName);
                                           UserData.setUserID(Integer.parseInt(userID));


                                           Intent nIntent = new Intent(iniciar_sesion.this, Comidas.class);
                                           startActivity(nIntent);
                                       }
                                   } else {
                                       Toast.makeText(iniciar_sesion.this, "ERROR en la busqueda", Toast.LENGTH_SHORT).show();
                                   }
                               }
                           });
               }
    }


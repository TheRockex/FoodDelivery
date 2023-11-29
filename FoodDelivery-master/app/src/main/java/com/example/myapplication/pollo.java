package com.example.myapplication;

import android.animation.Animator;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.myapplication.Database.DatabaseAux;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class pollo extends AppCompatActivity {

    private LottieAnimationView lottieAnimationView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pollo);
        //Animacion para el añadir el producto al carrito
        lottieAnimationView = findViewById(R.id.lottieAnimationView);
        lottieAnimationView.setAnimation(R.raw.cartacept);
        lottieAnimationView.playAnimation();

        // Iniciar la animación automáticamente durante un segundo
        lottieAnimationView.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Detener la animación después de un segundo
                lottieAnimationView.pauseAnimation();
                // Agregar un Listener de clic después de un segundo para reiniciar la animación y llamar a AnyadirPollo
                lottieAnimationView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Iniciar la animación si está detenida
                        lottieAnimationView.playAnimation();
                        // Remover el Listener para evitar múltiples clics
                        lottieAnimationView.setOnClickListener(null);
                        // Agregar un Listener para detectar el final de la animación
                        lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                // Llamar a AnyadirPollo al finalizar la animación
                                AnyadirPollo();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {
                            }
                        });
                    }
                });
            }
        }, 1000); // Retardo de un segundo (1000 milisegundos)
    }
    public void changeToRegistrar(View view) {
        Intent nIntent = new Intent(pollo.this, Comidas.class);
        startActivity(nIntent);
    }

    public void AnyadirPollo() {

        //Añadir el pollo a la base de datos
        String nameString = "Pollo al Curry";
        double precioInt = 12;


        DatabaseAux aux = new DatabaseAux(pollo.this);
        SQLiteDatabase db = aux.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("nombreproduct", nameString);
            values.put("precioproduct", precioInt);


            long res = db.insert("pedido", null, values);
            if(res >= 0) {
                Toast.makeText(this, "Añadido correctamente", Toast.LENGTH_SHORT).show();
                Intent nIntent = new Intent(pollo.this, Comidas.class);
                startActivity(nIntent);
            }
            else {
                Toast.makeText(this, "Fallo al añadir", Toast.LENGTH_SHORT).show();
            }

        FirebaseFirestore firestoreDb = FirebaseFirestore.getInstance();
        Map<String, String> pedido = new HashMap<>();
        pedido.put("id", String.valueOf(res));
        pedido.put("nombreproduct", nameString);
        pedido.put("precioproduct", String.valueOf(precioInt));

        firestoreDb.collection("pedido").document(String.valueOf(res))
                .set(pedido)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("DEBUG", "Bien");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ERROR", "Mal");
                    }
                });
    }
}

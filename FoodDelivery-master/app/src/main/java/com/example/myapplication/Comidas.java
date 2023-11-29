package com.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.Database.DatabaseAux;



public class Comidas extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comidas);

        //Para saber si hay algún producto en el carrito
        SQLiteDatabase db = new DatabaseAux(this).getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM pedido", null);
        int totalProductos = 0;
        if (cursor.moveToFirst()) {
            do {
                totalProductos++;
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();

        ImageView imageViewCirculo = findViewById(R.id.circulo);
        //Si hay algun producto que se muestre el circulo rojo sino no
        if (totalProductos > 0) {
            imageViewCirculo.setVisibility(View.VISIBLE);
        }else{
            imageViewCirculo.setVisibility(View.INVISIBLE);
        }
    }

    public void changeToPollo(View view) {
        Intent nIntent = new Intent(Comidas.this, pollo.class);
        startActivity(nIntent);
    }

    public void changeToCarrito(View view) {
        Intent nIntent = new Intent(Comidas.this, pedido.class);
        startActivity(nIntent);
    }
    public void changeToHamburguesa(View view) {
        Intent nIntent = new Intent(Comidas.this, hamburguesa.class);
        startActivity(nIntent);
    }

}

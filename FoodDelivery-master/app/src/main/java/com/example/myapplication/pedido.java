package com.example.myapplication;

import android.animation.Animator;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.myapplication.Database.DatabaseAux;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class pedido extends AppCompatActivity {
    private Integer total;
    private String metodoPagoSeleccionado = null;
    private TextView dayTextView;
    private TextView mesTextView;
    private TextView horaTextView;
    private int dayValue = 1;
    private int mesValue = 1;
    private int horaValue = 1;
    private LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);

        // Obtener la dirección del usuario
        String userDirection = UserData.getUserDirection();
        int userID = UserData.getUserID();

        // Crear TextView para mostrar la dirección
        final TextView direccionTextView = new TextView(this);
        direccionTextView.setText("Dirección: " + userDirection);
        direccionTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        direccionTextView.setTextColor(Color.BLACK);

        // Crear ImageView para el icono de lápiz
        ImageView lapizIcon = new ImageView(this);
        lapizIcon.setImageResource(R.drawable.ic_lapiz);
        lapizIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Mostrar AlertDialog para editar la dirección
                showEditAddressDialog(direccionTextView,userID);
            }
        });

        // Crear LinearLayout para contener TextView e ImageView para la direccion
        LinearLayout contentLayout = new LinearLayout(this);
        contentLayout.setOrientation(LinearLayout.HORIZONTAL);
        contentLayout.addView(direccionTextView);
        contentLayout.addView(lapizIcon);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(20, 0, 0, 0);
        lapizIcon.setLayoutParams(layoutParams);



        // Obtener el LinearLayout del ScrollView para la direccion
        LinearLayout fillContentShow = findViewById(R.id.fillContentShow);
        fillContentShow.addView(contentLayout);

        //Animacion para el cuando le den a confirmar pedido
        Button finalizar = findViewById(R.id.finalizarButton);
        LottieAnimationView lottieAnimationView = findViewById(R.id.lottieAnimationViewBike);
        lottieAnimationView.setAnimation(R.raw.bike);

        finalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Iniciar la animación al hacer clic en el botón
                if(total > 0){
                    lottieAnimationView.playAnimation();

                    // Agregar un Listener para detectar el final de la animación
                    lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            Toast.makeText(pedido.this, "Gracias por su pedido", Toast.LENGTH_SHORT).show();
                            Intent nIntent = new Intent(pedido.this, valorar.class);
                            startActivity(nIntent);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    });
                }else{
                    Toast.makeText(pedido.this, "No hay nada en el carrito", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //Para mostrar todos los productos que hay en el carrito
        SQLiteDatabase db = new DatabaseAux(this).getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM pedido", null);

        //Se muestren en el fillContentShow2
        LinearLayout layout = findViewById(R.id.fillContentShow2);
        total = 0;
        Double Preciototal = 0.0;

        if (cursor.moveToFirst()) {
            do {
                //Salga el nombre y precio, pero tambien se guarde el id por si quisiera borrarlo
                final int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String precio = cursor.getString(2);
                Preciototal += cursor.getDouble(2);
                total++;

                // Crear un nuevo LinearLayout horizontal
                LinearLayout rowLayout = new LinearLayout(this);
                rowLayout.setOrientation(LinearLayout.HORIZONTAL);

                // Agregar un TextView para el nombre
                TextView nameTextView = new TextView(this);
                nameTextView.setText(name + "      " + precio);
                nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                nameTextView.setTextColor(Color.BLACK);
                rowLayout.addView(nameTextView);

                // Agregar un ImageView para el icono de eliminación con un margen a la derecha
                ImageView deleteIcon = new ImageView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(10, 0, 0, 0);
                deleteIcon.setLayoutParams(params);
                deleteIcon.setImageResource(R.drawable.ic_delete);
                deleteIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        eliminarElemento(id);
                    }
                });
                rowLayout.addView(deleteIcon);

                layout.addView(rowLayout);

            } while (cursor.moveToNext());
            TextView totalTextView = new TextView(this);
            totalTextView.setText("TOTAL " + "     " + Preciototal);
            totalTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
            totalTextView.setTextColor(Color.BLACK);
            layout.addView(totalTextView);
        }

        // Obtén la referencia al TextView que al pulsarlo abrirá el cuadro de diálogo de paypal
        TextView textViewAbrirDialog = findViewById(R.id.paypal);

        textViewAbrirDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Muestra el cuadro de diálogo cuando se presiona el texto
                mostrarPaypal();
            }
        });

        // Obtén la referencia al TextView que al pulsarlo se actualice a "Efectivo" el metodoPagoSeleccionado
        TextView efectivo = findViewById(R.id.efectivo);

        // Configura el OnClickListener para el TextView
        efectivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Actualiza el método de pago seleccionado
                metodoPagoSeleccionado = "Efectivo";

                // Actualiza los TextView correspondientes
                actualizarTextViews();
            }
        });

        // Obtén la referencia al TextView que al pulsarlo abrirá el cuadro de diálogo para la tarjeta de crédito
        TextView textViewAbrirDialog2 = findViewById(R.id.tarjeta);

        textViewAbrirDialog2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Muestra el cuadro de diálogo cuando se presiona el texto
                mostrarTarjeta();
            }
        });

        findViewById(R.id.programar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });
    }



    private void showEditAddressDialog(TextView direccionTextView, int ID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar Dirección");

        // Crear un EditText para que el usuario ingrese la nueva dirección
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Botón de aceptar para actualizar la dirección
        builder.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Obtener la nueva dirección ingresada por el usuario
                String newAddress = input.getText().toString();
                // Actualizar el TextView con la nueva dirección
                modifyValues(newAddress);
                direccionTextView.setText("Dirección: " + newAddress);
            }
        });

        // Botón negativo para cancelar la edición
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Mostrar el AlertDialog
        builder.show();

    }

    public void modifyValues(String newAddress){
        //Para modificar la dirección del cliente en la base de datos
        String oldDirection = UserData.getUserDirection();
        String oldID = String.valueOf(UserData.getUserID());
        String oldName = String.valueOf(UserData.getUserName());


        String newDirection = newAddress;



        SQLiteDatabase db = new DatabaseAux(this).getWritableDatabase();

        if(db != null && !oldDirection.trim().isEmpty() && !newDirection.trim().isEmpty()) {
            ContentValues values = new ContentValues();
            values.put("name", oldName);
            values.put("id", oldID);
            values.put("direction", newDirection);
            String [] si = {oldID,oldName, oldDirection};
            long mod = db.update("users", values, "id = ? AND name = ? AND direction = ?", si);

            if(mod > 0) {
                db.close();
            }

            FirebaseFirestore firestoreDb = FirebaseFirestore.getInstance();
            Map<String, Object> updates = new HashMap<>();
            updates.put("name", oldName);
            updates.put("id", oldID);
            updates.put("direction", newDirection);

            firestoreDb.collection("users").document(oldName)
                    .update(updates)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("DEBUG", "La actualización fue exitosa");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("ERROR", "Error al actualizar el documento", e);
                        }
                    });

        } else {
            Toast.makeText(this, "Tontito", Toast.LENGTH_LONG).show();
        }
    }
    private void eliminarElemento(int id) {
        //Eliminar la comida de la base de datos que quiere borrar el cliente por su id
        SQLiteDatabase db = new DatabaseAux(this).getWritableDatabase();
        db.delete("pedido", "id=?", new String[]{String.valueOf(id)});

        FirebaseFirestore firestoreDb = FirebaseFirestore.getInstance();
        firestoreDb.collection("pedido").document(String.valueOf(id))
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("DEBUG", "Documento eliminado de Firestore");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("ERROR", "Error al eliminar documento de Firestore", e);

                    }
                });


        Intent nIntent = new Intent(pedido.this, pedido.class);
        startActivity(nIntent);
    }
    public void changeToComidas(View view) {
        Intent nIntent = new Intent(pedido.this, Comidas.class);
        startActivity(nIntent);
    }

    private void mostrarPaypal() {
        // Crea un AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);

        // Infla el diseño del cuadro de diálogo
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_paypal, null);

        // Obtén referencias a los EditText dentro del cuadro de diálogo
        EditText editTextNombreDestinatario = dialogView.findViewById(R.id.editTextNombreDestinatario);
        EditText editTextUsuarioPayPal = dialogView.findViewById(R.id.editTextUsuarioPayPal);
        EditText editTextCorreoTelefono = dialogView.findViewById(R.id.editTextCorreoTelefono);

        // Configura el contenido del cuadro de diálogo
        builder.setView(dialogView)
                .setTitle("Introduce información de PayPal")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Aquí puedes obtener los valores de los EditText
                        String nombreDestinatario = editTextNombreDestinatario.getText().toString();
                        String usuarioPayPal = editTextUsuarioPayPal.getText().toString();
                        String correoTelefono = editTextCorreoTelefono.getText().toString();

                        // Actualiza el método de pago seleccionado
                        metodoPagoSeleccionado = "PayPal";

                        // Actualiza los TextView correspondientes
                        actualizarTextViews();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Cancela el cuadro de diálogo
                        dialog.cancel();
                    }
                });

        // Muestra el cuadro de diálogo
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void mostrarTarjeta() {
        // Crea un constructor de AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialogTheme);

        // Infla el diseño del cuadro de diálogo
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_tarjeta, null);

        // Obtén referencias a los EditText dentro del cuadro de diálogo
        EditText editTextNumeroTarjeta = dialogView.findViewById(R.id.editTextNumeroTarjeta);
        EditText editTextFechaCaducidad = dialogView.findViewById(R.id.editTextFechaCaducidad);
        EditText editTextCVC = dialogView.findViewById(R.id.editTextCVC);

        // Configura el contenido del cuadro de diálogo
        builder.setView(dialogView)
                .setTitle("Introduce información de la tarjeta")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Aquí puedes obtener los valores de los EditText
                        String numeroTarjeta = editTextNumeroTarjeta.getText().toString();
                        String fechaCaducidad = editTextFechaCaducidad.getText().toString();
                        String cvc = editTextCVC.getText().toString();

                        // Actualiza el método de pago seleccionado
                        metodoPagoSeleccionado = "Tarjeta de Crédito";

                        // Actualiza los TextView correspondientes
                        actualizarTextViews();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Cancela el cuadro de diálogo
                        dialog.cancel();
                    }
                });

        // Muestra el cuadro de diálogo
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void actualizarTextViews() {
        // Obtén referencias a los TextView
        TextView textViewPaypal = findViewById(R.id.paypal);
        TextView textViewTarjetaCredito = findViewById(R.id.tarjeta);
        TextView textViewEfectivo = findViewById(R.id.efectivo);

        // Actualiza los TextView según el método de pago seleccionado
        if ("PayPal".equals(metodoPagoSeleccionado)) {
            // Muestra el ícono de "tick" al lado de PayPal y oculta los demás
            textViewPaypal.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_green, 0);
            textViewTarjetaCredito.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            textViewEfectivo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            // Oculta los demás TextView si es necesario
        } else if ("Tarjeta de Crédito".equals(metodoPagoSeleccionado)) {
            // Muestra el ícono de "tick" al lado de Tarjeta de Crédito y oculta los demás
            textViewPaypal.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            textViewTarjetaCredito.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_green, 0);
            textViewEfectivo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            // Oculta los demás TextView si es necesario
        }else if("Efectivo".equals(metodoPagoSeleccionado)){
            // Muestra el ícono de "tick" al lado de Tarjeta de Crédito y oculta los demás
            textViewPaypal.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            textViewTarjetaCredito.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            textViewEfectivo.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_green, 0);
        }
    }
    private void showAlertDialog() {
        //Salga Dialog para decir el dia,hora y mes del pedido
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.hora_layout, null);
        builder.setView(dialogView);

        dayTextView = dialogView.findViewById(R.id.dayTextView);
        updateDayTextView();

        mesTextView = dialogView.findViewById(R.id.mesTextView);
        updateMesTextView();

        horaTextView = dialogView.findViewById(R.id.horaTextView);
        updateHoraTextView();

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.show();
    }

    public void incrementDay(View view) {
        if(dayValue < 31){
            dayValue++;
            updateDayTextView();
        }
    }

    public void decrementDay(View view) {
        if (dayValue > 1) {
            dayValue--;
            updateDayTextView();
        }
    }

    private void updateDayTextView() {
        if (dayTextView != null) {
            dayTextView.setText(String.valueOf(dayValue));
        }
    }

    private void updateMesTextView() {
        if (mesTextView != null) {
            mesTextView.setText(String.valueOf(mesValue));
        }
    }

    public void incrementMes(View view) {
        if(mesValue < 12){
            mesValue++;
            updateMesTextView();
        }
    }

    public void decrementMes(View view) {
        if (mesValue > 1) {
            mesValue--;
            updateMesTextView();
        }
    }
    private void updateHoraTextView() {
        if (horaTextView != null) {
            horaTextView.setText(String.valueOf(horaValue  + ": 00"));
        }
    }

    public void incrementHora(View view) {
        if(horaValue < 23){
            horaValue++;
            updateHoraTextView();
        }
    }

    public void decrementHora(View view) {
        if (horaValue > 0) {
            horaValue--;
            updateHoraTextView();
        }
    }

}

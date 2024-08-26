package com.sergio.agendaonline;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuPrincipal extends AppCompatActivity {

    Button CerrarSesion, BorrarCuenta;
    FirebaseAuth fA;
    FirebaseUser user;
    TextView NombresPrincipal, CorreoPrincipal;
    ProgressBar ProgressBarDatos;
    DatabaseReference Usuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Agenda Online");

        NombresPrincipal = findViewById(R.id.NombresPrincipal);
        CorreoPrincipal = findViewById(R.id.CorreoPrincipal);
        ProgressBarDatos = findViewById(R.id.ProgressBarDatos);

        Usuarios = FirebaseDatabase.getInstance().getReference("Usuarios");
        BorrarCuenta = findViewById(R.id.BorrarCuenta);
        CerrarSesion = findViewById(R.id.CerrarSesion);
        fA = FirebaseAuth.getInstance();
        user = fA.getCurrentUser();

        CerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SalirAplicacion();
            }
        });

        BorrarCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alerta = new AlertDialog.Builder(MenuPrincipal.this);
                alerta.setMessage("Esto borrará su cuenta para siempre.")
                        .setPositiveButton("Borrar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                BorrarUser();
                            }
                        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog titAlerta = alerta.create();
                titAlerta.setTitle("Borrar cuenta");
                titAlerta.show();
            }
        });
    }

    private void BorrarUser() {
        fA.getCurrentUser().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Usuarios.child(user.getUid()).removeValue();
                Toast.makeText(MenuPrincipal.this, "Cuenta Borrada", Toast.LENGTH_LONG).show();
                startActivity(new Intent(MenuPrincipal.this, MainActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println(e.getMessage());
                Toast.makeText(MenuPrincipal.this, "ERROR: "+e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onStart() {
        ComprobarSesion();
        super.onStart();
    }

    private void ComprobarSesion(){
        if (user!=null){
            CargaDeDatos();
        }else{
            startActivity(new Intent(MenuPrincipal.this, MainActivity.class));
            finish();
        }
    }

    private void CargaDeDatos(){
        Usuarios.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Si el usuario existe
                if (snapshot.exists()){
                    //El progressBar se oculta
                    ProgressBarDatos.setVisibility(View.GONE);
                    //Mostramos Datos
                    NombresPrincipal.setVisibility(View.VISIBLE);
                    CorreoPrincipal.setVisibility(View.VISIBLE);
                    BorrarCuenta.setVisibility(View.VISIBLE);
                    CerrarSesion.setVisibility(View.VISIBLE);
                    //Traemos datos
                    String nombres = ""+snapshot.child("nombres").getValue();
                    String correo = ""+snapshot.child("correo").getValue();
                    //Seteamos datos
                    NombresPrincipal.setText(nombres);
                    CorreoPrincipal.setText(correo);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SalirAplicacion() {
        fA.signOut();
        startActivity(new Intent(MenuPrincipal.this, MainActivity.class));
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_LONG).show();
    }
}
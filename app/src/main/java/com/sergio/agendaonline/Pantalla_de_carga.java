package com.sergio.agendaonline;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Pantalla_de_carga extends AppCompatActivity {

    FirebaseAuth fA;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_de_carga);

        fA = FirebaseAuth.getInstance();

        int tiempo = 3000;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /*startActivity(new Intent(Pantalla_de_carga.this, MainActivity.class));
                finish();*/
                VerificarUsuario();
            }
        }, tiempo);
    }

    private void VerificarUsuario(){
        FirebaseUser fU = fA.getCurrentUser();

        if (fU==null){
            startActivity(new Intent(Pantalla_de_carga.this, MainActivity.class));
            finish();
        }else{
            startActivity(new Intent(Pantalla_de_carga.this, MenuPrincipal.class));
            finish();
        }
    }
}
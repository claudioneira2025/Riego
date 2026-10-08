package com.example.riego

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class BienvenidaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bienvenida)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val usuario = intent.getStringExtra("usuario") ?: ""
        val txtBienvenida = findViewById<TextView>(R.id.txtBienvenida)
        txtBienvenida.text = "Bienvenido, $usuario"
    }

    // Método que se ejecuta al presionar el botón de Preferencias mediante android:onClick
    fun onPreferenciasClick(view: View) {
        // Obtiene el nombre de usuario que llegó como extra en el Intent actual
        val usuario = intent.getStringExtra("usuario") ?: ""
        // Crea un nuevo Intent explícito para navegar hacia PreferenciasActivity
        val intentPreferencias = Intent(this, PreferenciasActivity::class.java)
        // Adjunta el nombre de usuario recibido como dato extra con la clave "usuario"
        intentPreferencias.putExtra("usuario", usuario)
        // Inicia la actividad PreferenciasActivity
        startActivity(intentPreferencias)
    }
}
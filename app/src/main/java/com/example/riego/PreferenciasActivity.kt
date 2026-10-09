package com.example.riego

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.riego.databinding.ActivityPreferenciasBinding

class PreferenciasActivity : AppCompatActivity() {

    // ViewBinding para la pantalla de preferencias
    private lateinit var binding: ActivityPreferenciasBinding

    companion object {
        private const val PREFS_NAME = "RiegoPrefs"
        private const val KEY_NOTIFICACIONES = "notificaciones_activas"
        private const val KEY_IDIOMA_POS = "idioma_posicion"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inflado con ViewBinding
        binding = ActivityPreferenciasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Respetar las barras del sistema (Edge to Edge)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Cargar preferencias almacenadas
        cargarPreferencias()

        // Listener para el botón Guardar Preferencias
        binding.btnGuardarPreferencias.setOnClickListener {
            guardarPreferencias()
        }
    }

    /**
     * Carga las configuraciones del usuario desde SharedPreferences
     */
    private fun cargarPreferencias() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val notificaciones = prefs.getBoolean(KEY_NOTIFICACIONES, true)
        val idiomaPos = prefs.getInt(KEY_IDIOMA_POS, 0)

        binding.swNotificaciones.isChecked = notificaciones
        if (idiomaPos in 0 until binding.spIdioma.count) {
            binding.spIdioma.setSelection(idiomaPos)
        }
    }

    /**
     * Guarda el estado del switch y la posición seleccionada del Spinner
     */
    private fun guardarPreferencias() {
        val notificaciones = binding.swNotificaciones.isChecked
        val idiomaPos = binding.spIdioma.selectedItemPosition

        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            putBoolean(KEY_NOTIFICACIONES, notificaciones)
            putInt(KEY_IDIOMA_POS, idiomaPos)
            apply()
        }

        Toast.makeText(this, getString(R.string.pref_saved_message), Toast.LENGTH_SHORT).show()
        // Cierra la actividad para volver a la pantalla anterior
        finish()
    }
}
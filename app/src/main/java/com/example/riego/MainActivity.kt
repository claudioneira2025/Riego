package com.example.riego

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.riego.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // ViewBinding para acceso seguro y tipado a las vistas del layout
    private lateinit var binding: ActivityMainBinding

    // Contador de intentos fallidos de inicio de sesión
    private var intentosFallidos: Int = 0
    private val maxIntentos: Int = 3

    companion object {
        // Constantes para persistencia con SharedPreferences
        private const val PREFS_NAME = "RiegoPrefs"
        private const val KEY_RECORDAR = "recordar_usuario"
        private const val KEY_USUARIO = "usuario_guardado"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inflado de la vista utilizando ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ajuste de márgenes para respetar las barras del sistema (Edge to Edge)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Cargar usuario guardado si la opción 'Recordarme' estaba activa previamente
        cargarPreferenciasUsuario()

        // Configuración de listeners de clics
        binding.btnIngresar.setOnClickListener {
            procesarIngreso()
        }

        binding.btnLimpiar.setOnClickListener {
            limpiarCampos()
        }
    }

    /**
     * Carga las credenciales guardadas en SharedPreferences si 'Recordarme' estaba activo
     */
    private fun cargarPreferenciasUsuario() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val recordar = prefs.getBoolean(KEY_RECORDAR, false)
        if (recordar) {
            val usuarioGuardado = prefs.getString(KEY_USUARIO, "") ?: ""
            binding.edtUsuario.setText(usuarioGuardado)
            binding.chkRecordarme.isChecked = true
        }
    }

    /**
     * Valida los campos del formulario y procesa el inicio de sesión
     */
    private fun procesarIngreso() {
        // Verificar si el usuario ha sido bloqueado por superar los intentos permitidos
        if (intentosFallidos >= maxIntentos) {
            Toast.makeText(this, getString(R.string.login_error_locked), Toast.LENGTH_LONG).show()
            return
        }

        val usuario = binding.edtUsuario.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()
        val recordarme = binding.chkRecordarme.isChecked

        var esValido = true

        // Validación del campo de usuario / correo
        if (usuario.isEmpty()) {
            binding.tilUsuario.error = getString(R.string.login_error_empty_user)
            esValido = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(usuario).matches()) {
            binding.tilUsuario.error = getString(R.string.login_error_invalid_email)
            esValido = false
        } else {
            binding.tilUsuario.error = null
        }

        // Validación del campo de contraseña
        if (password.isEmpty()) {
            binding.tilPassword.error = getString(R.string.login_error_empty_password)
            esValido = false
        } else if (password.length < 6) {
            binding.tilPassword.error = getString(R.string.login_error_short_password)
            esValido = false
        } else {
            binding.tilPassword.error = null
        }

        if (!esValido) {
            intentosFallidos++
            val restantes = maxIntentos - intentosFallidos
            if (intentosFallidos >= maxIntentos) {
                Toast.makeText(this, getString(R.string.login_error_locked), Toast.LENGTH_LONG).show()
                binding.btnIngresar.isEnabled = false
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.login_error_attempt_warning, intentosFallidos, maxIntentos),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            // Reiniciar contador de intentos fallidos
            intentosFallidos = 0

            // Guardar o eliminar el usuario de SharedPreferences según el checkbox
            val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().apply {
                putBoolean(KEY_RECORDAR, recordarme)
                if (recordarme) {
                    putString(KEY_USUARIO, usuario)
                } else {
                    remove(KEY_USUARIO)
                }
                apply()
            }

            // Iniciar BienvenidaActivity pasando el usuario como extra
            val intent = Intent(this, BienvenidaActivity::class.java)
            intent.putExtra("usuario", usuario)
            startActivity(intent)
        }
    }

    /**
     * Limpia los campos de entrada y restablece los errores y el checkbox
     */
    private fun limpiarCampos() {
        binding.edtUsuario.setText("")
        binding.tilUsuario.error = null
        binding.edtPassword.setText("")
        binding.tilPassword.error = null
        binding.chkRecordarme.isChecked = false
        binding.edtUsuario.requestFocus()
    }
}
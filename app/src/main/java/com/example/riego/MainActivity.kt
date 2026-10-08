package com.example.riego

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    // Variable a nivel de clase para registrar el estado de visualización de la contraseña (inicialmente oculta)
    private var mostrandoPassword: Boolean = false
    // Variable a nivel de clase para contabilizar los intentos fallidos de inicio de sesión iniciada en 0
    private var intentosFallidos: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnIngresar = findViewById<Button>(R.id.btnIngresar)
        btnIngresar.setOnClickListener {
            onIngresarClick(it)
        }
    }

    // Método que procesa la validación e inicio de sesión al hacer clic en Ingresar
    fun onIngresarClick(view: View) {
        // Obtiene la vista del campo usuario a través de su identificador en el layout
        val edtUsuario = findViewById<EditText>(R.id.edtUsuario)
        // Obtiene la vista del campo contraseña a través de su identificador en el layout
        val edtPassword = findViewById<EditText>(R.id.edtPassword)
        // Obtiene la vista del checkbox recordarme a través de su identificador en el layout
        val chkRecordarme = findViewById<CheckBox>(R.id.chkRecordarme)

        // Obtiene el texto escrito en el campo usuario sin espacios al inicio ni al final
        val usuario = edtUsuario.text.toString().trim()
        // Obtiene el texto escrito en el campo contraseña sin espacios al inicio ni al final
        val password = edtPassword.text.toString().trim()
        // Obtiene el estado booleano de la casilla recordarme
        val recordarme = chkRecordarme.isChecked

        // Variable de bandera booleana para comprobar si todas las validaciones son exitosas
        var esValido = true

        // Valida si el campo usuario se encuentra vacío
        if (usuario.isEmpty()) {
            // Muestra mensaje de error directo en el campo de usuario indicando que es obligatorio
            edtUsuario.error = "Ingresa tu correo"
            // Cambia el estado de validación a falso
            esValido = false
        // Valida si el texto ingresado no tiene un formato de correo electrónico válido
        } else if (!Patterns.EMAIL_ADDRESS.matcher(usuario).matches()) {
            // Muestra mensaje de error directo en el campo de usuario por formato de email inválido
            edtUsuario.error = "Ingresa un email válido"
            // Cambia el estado de validación a falso
            esValido = false
        }

        // Valida si el campo contraseña se encuentra vacío
        if (password.isEmpty()) {
            // Muestra mensaje de error directo en el campo de contraseña indicando que es obligatoria
            edtPassword.error = "Ingresa tu contraseña"
            // Cambia el estado de validación a falso
            esValido = false
        // Valida si la contraseña tiene menos de 6 caracteres
        } else if (password.length < 6) {
            // Muestra mensaje de error directo indicando el requisito de longitud mínima
            edtPassword.error = "La contraseña debe tener al menos 6 caracteres"
            // Cambia el estado de validación a falso
            esValido = false
        }

        // Evalúa si falló alguna de las validaciones de los campos
        if (!esValido) {
            // Incrementa en 1 el contador de intentos fallidos
            intentosFallidos++
        } else {
            // Instancia un Intent para abrir la actividad BienvenidaActivity
            val intent = Intent(this, BienvenidaActivity::class.java)
            // Adjunta el nombre del usuario al Intent con la clave "usuario"
            intent.putExtra("usuario", usuario)
            // Lanza la nueva actividad de bienvenida
            startActivity(intent)
        }
    }

    // Método invocado automáticamente al presionar el botón Limpiar mediante android:onClick
    fun onLimpiarClick(view: View) {
        // Obtiene la vista del campo de usuario a través de su identificador en el layout
        val edtUsuario = findViewById<EditText>(R.id.edtUsuario)
        // Obtiene la vista del campo de contraseña a través de su identificador en el layout
        val edtPassword = findViewById<EditText>(R.id.edtPassword)
        // Obtiene la vista del checkbox de recordarme a través de su identificador en el layout
        val chkRecordarme = findViewById<CheckBox>(R.id.chkRecordarme)

        // Vacía el contenido de texto del campo usuario
        edtUsuario.setText("")
        // Elimina cualquier mensaje de error visual en el campo usuario
        edtUsuario.error = null
        // Vacía el contenido de texto del campo contraseña
        edtPassword.setText("")
        // Elimina cualquier mensaje de error visual en el campo contraseña
        edtPassword.error = null
        // Desmarca la casilla de verificación estableciendo su estado en falso
        chkRecordarme.isChecked = false
    }

    // Método invocado al presionar el ImageButton para alternar la visibilidad de la contraseña
    fun onMostrarPasswordClick(view: View) {
        // Obtiene la referencia al campo de texto de la contraseña
        val edtPassword = findViewById<EditText>(R.id.edtPassword)

        // Verifica si la contraseña actualmente está oculta (mostrandoPassword es false)
        if (!mostrandoPassword) {
            // Cambia el inputType para que los caracteres sean visibles
            edtPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            // Actualiza la variable de clase a true indicando que ahora es visible
            mostrandoPassword = true
        } else {
            // Cambia el inputType de vuelta a formato de contraseña para ocultar el texto
            edtPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            // Actualiza la variable de clase a false indicando que volvió a ocultarse
            mostrandoPassword = false
        }

        // Mantiene la posición del cursor de texto al final del contenido
        edtPassword.setSelection(edtPassword.text.length)
    }
}
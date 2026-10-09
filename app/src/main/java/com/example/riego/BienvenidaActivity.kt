package com.example.riego

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.riego.databinding.ActivityBienvenidaBinding
import kotlin.random.Random

class BienvenidaActivity : AppCompatActivity() {

    // ViewBinding para acceder a los componentes de la interfaz de forma segura
    private lateinit var binding: ActivityBienvenidaBinding

    // Variables de estado del sistema de riego y humedad
    private var nivelHumedad: Int = 45
    private var estaRegando: Boolean = false
    private var modoAutomaticoActivo: Boolean = false

    companion object {
        private const val PREFS_NAME = "RiegoPrefs"
        private const val KEY_RIEGO_AUTO = "riego_automatico"
        private const val UMBRAL_HUMEDAD_MINIMA = 30
        private const val UMBRAL_HUMEDAD_MAXIMA = 70
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inflado con ViewBinding
        binding = ActivityBienvenidaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Respetar las barras del sistema (Edge to Edge)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obtener el nombre de usuario recibido desde MainActivity
        val usuario = intent.getStringExtra("usuario") ?: ""
        binding.txtBienvenida.text = getString(R.string.welcome_user, usuario)

        // Cargar preferencias previas del sistema
        cargarConfiguracion()

        // Inicializar interfaz visual de humedad y riego
        actualizarVistaHumedad(nivelHumedad)
        actualizarVistaRiego()

        // Listener para el botón Consultar Humedad
        binding.btnConsultarHumedad.setOnClickListener {
            consultarSensorHumedad()
        }

        // Listener para el Switch de Riego Automático
        binding.swRiegoAutomatico.setOnCheckedChangeListener { _, isChecked ->
            modoAutomaticoActivo = isChecked
            guardarModoAutomatico(isChecked)

            val mensaje = if (isChecked) {
                getString(R.string.msg_auto_irrigation_enabled)
            } else {
                getString(R.string.msg_auto_irrigation_disabled)
            }
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()

            // Si se activa el modo automático y la humedad es baja, activar riego
            if (modoAutomaticoActivo && nivelHumedad < UMBRAL_HUMEDAD_MINIMA && !estaRegando) {
                activarRiegoAutomatico()
            }
        }

        // Listener para el botón de Riego Manual (Iniciar/Detener)
        binding.btnControlRiego.setOnClickListener {
            alternarRiegoManual()
        }

        // Navegación a PreferenciasActivity
        binding.btnPreferencias.setOnClickListener {
            val intentPreferencias = Intent(this, PreferenciasActivity::class.java)
            intentPreferencias.putExtra("usuario", usuario)
            startActivity(intentPreferencias)
        }

        // Botón de Cerrar Sesión
        binding.btnCerrarSesion.setOnClickListener {
            finish()
        }
    }

    /**
     * Carga el estado guardado del riego automático en SharedPreferences
     */
    private fun cargarConfiguracion() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        modoAutomaticoActivo = prefs.getBoolean(KEY_RIEGO_AUTO, false)
        binding.swRiegoAutomatico.isChecked = modoAutomaticoActivo
    }

    /**
     * Guarda el estado del switch de riego automático
     */
    private fun guardarModoAutomatico(activo: Boolean) {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_RIEGO_AUTO, activo).apply()
    }

    /**
     * Simula la lectura del sensor de humedad del suelo
     */
    private fun consultarSensorHumedad() {
        // Simulación: genera una lectura realista entre 15% y 85%
        nivelHumedad = Random.nextInt(15, 86)
        actualizarVistaHumedad(nivelHumedad)

        Toast.makeText(
            this,
            getString(R.string.msg_humidity_updated, nivelHumedad),
            Toast.LENGTH_SHORT
        ).show()

        // Lógica de automatización si el modo automático está encendido
        if (modoAutomaticoActivo) {
            if (nivelHumedad < UMBRAL_HUMEDAD_MINIMA && !estaRegando) {
                activarRiegoAutomatico()
            } else if (nivelHumedad >= UMBRAL_HUMEDAD_MAXIMA && estaRegando) {
                // Suelo saturado: detener riego automáticamente
                estaRegando = false
                actualizarVistaRiego()
                Toast.makeText(this, getString(R.string.msg_irrigation_stopped), Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Actualiza la interfaz gráfica con el valor y estado de humedad
     */
    private fun actualizarVistaHumedad(porcentaje: Int) {
        binding.txtHumedadValor.text = getString(R.string.humidity_percent_format, porcentaje)
        binding.progressHumedad.progress = porcentaje

        when {
            porcentaje < UMBRAL_HUMEDAD_MINIMA -> {
                binding.txtHumedadEstado.text = getString(R.string.humidity_status_dry)
                val colorSeco = ContextCompat.getColor(this, R.color.status_dry)
                binding.txtHumedadEstado.setTextColor(colorSeco)
                binding.progressHumedad.progressTintList = ColorStateList.valueOf(colorSeco)
                binding.txtHumedadValor.setTextColor(colorSeco)
            }
            porcentaje in UMBRAL_HUMEDAD_MINIMA..UMBRAL_HUMEDAD_MAXIMA -> {
                binding.txtHumedadEstado.text = getString(R.string.humidity_status_optimal)
                val colorOptimo = ContextCompat.getColor(this, R.color.status_optimal)
                binding.txtHumedadEstado.setTextColor(colorOptimo)
                binding.progressHumedad.progressTintList = ColorStateList.valueOf(colorOptimo)
                binding.txtHumedadValor.setTextColor(colorOptimo)
            }
            else -> {
                binding.txtHumedadEstado.text = getString(R.string.humidity_status_wet)
                val colorHumedo = ContextCompat.getColor(this, R.color.status_wet)
                binding.txtHumedadEstado.setTextColor(colorHumedo)
                binding.progressHumedad.progressTintList = ColorStateList.valueOf(colorHumedo)
                binding.txtHumedadValor.setTextColor(colorHumedo)
            }
        }
    }

    /**
     * Activa el riego debido a una condición de baja humedad en modo automático
     */
    private fun activarRiegoAutomatico() {
        estaRegando = true
        actualizarVistaRiego()
        Toast.makeText(
            this,
            getString(R.string.msg_auto_irrigation_activated, nivelHumedad),
            Toast.LENGTH_LONG
        ).show()
    }

    /**
     * Alterna manualmente el estado del riego al presionar el botón
     */
    private fun alternarRiegoManual() {
        estaRegando = !estaRegando
        actualizarVistaRiego()

        val mensaje = if (estaRegando) {
            getString(R.string.msg_manual_irrigation_started)
        } else {
            getString(R.string.msg_irrigation_stopped)
        }
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    /**
     * Actualiza el botón y texto de estado según si el riego está activo o detenido
     */
    private fun actualizarVistaRiego() {
        if (estaRegando) {
            binding.txtEstadoRiego.text = getString(R.string.irrigation_state_running)
            binding.txtEstadoRiego.setTextColor(ContextCompat.getColor(this, R.color.water_blue))
            binding.btnControlRiego.text = getString(R.string.btn_stop_irrigation)
            binding.btnControlRiego.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.status_dry)
            )
        } else {
            binding.txtEstadoRiego.text = getString(R.string.irrigation_state_idle)
            binding.txtEstadoRiego.setTextColor(ContextCompat.getColor(this, R.color.gray_dark))
            binding.btnControlRiego.text = getString(R.string.btn_start_manual_irrigation)
            binding.btnControlRiego.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.green_primary)
            )
        }
    }
}
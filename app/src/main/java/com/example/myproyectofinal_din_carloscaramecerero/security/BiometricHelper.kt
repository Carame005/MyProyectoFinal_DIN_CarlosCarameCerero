package com.example.myproyectofinal_din_carloscaramecerero.security

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/**
 * Helper pequeño y reutilizable para invocar BiometricPrompt de forma segura.
 * Instanciar con una FragmentActivity (Activity que herede de FragmentActivity) para evitar errores
 * de constructor.
 */
class BiometricHelper(private val activity: FragmentActivity) {

    private val executor = ContextCompat.getMainExecutor(activity)

    private val callback = object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            onSuccess?.invoke()
        }

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            onError?.invoke(errorCode, errString.toString())
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            onFail?.invoke()
        }
    }

    private val biometricPrompt: BiometricPrompt by lazy {
        BiometricPrompt(activity, executor, callback)
    }

    private val promptInfo by lazy {
        BiometricPrompt.PromptInfo.Builder()
            .setTitle("Iniciar sesión rápido")
            .setSubtitle("Usa tu huella o reconocimiento facial")
            // Permitir autenticación con credenciales del dispositivo como fallback
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .build()
    }

    private var onSuccess: (() -> Unit)? = null
    private var onError: ((Int, String) -> Unit)? = null
    private var onFail: (() -> Unit)? = null

    /**
     * Comprueba si la biometría está disponible en el dispositivo.
     */
    fun isBiometricAvailable(context: Context): Boolean {
        val manager = BiometricManager.from(context)
        return when (manager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
    }

    /**
     * Inicia el flujo de autenticación.
     * onSuccess: se invoca en el hilo UI si la autenticación es correcta.
     */
    fun authenticate(onSuccess: () -> Unit, onError: (Int, String) -> Unit, onFail: () -> Unit) {
        this.onSuccess = onSuccess
        this.onError = onError
        this.onFail = onFail
        biometricPrompt.authenticate(promptInfo)
    }
}

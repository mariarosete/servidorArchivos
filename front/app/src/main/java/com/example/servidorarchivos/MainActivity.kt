package com.example.servidorarchivos

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import java.io.*
import java.net.Socket

class MainActivity : AppCompatActivity() {

    private lateinit var tvSalida: TextView
    private lateinit var tvFilesList: TextView
    private lateinit var spinnerArchivos: Spinner
    private lateinit var btnListar: Button
    private lateinit var btnDescarga: Button
    private lateinit var btnSubir: Button
    private lateinit var btnSel: Button
    private lateinit var btnRefrescar: ImageButton
    private lateinit var tvArchivoSeleccionado: TextView

    //Reemplaza esta IP por la dirección IP local del ordenador donde se ejecuta el servidor Java
    private val ip = "XXX.XXX.X.XXX"
    private val puerto = 6000
    private var archivoSel: Uri? = null
    private var nombreArchivoSel: String = ""
    private val SEL_ARCHIVO = 1001

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvSalida = findViewById(R.id.tvOut)
        tvFilesList = findViewById(R.id.tvFilesList)
        spinnerArchivos = findViewById(R.id.spinnerArchivos)
        btnListar = findViewById(R.id.btnListar)
        btnDescarga = findViewById(R.id.btnDownload)
        btnSubir = findViewById(R.id.btnSubir)
        btnSel = findViewById(R.id.btnSeleccionar)
        btnRefrescar = findViewById(R.id.btnRefrescar)
        btnListar.setOnClickListener { listarArchivos() }
        btnDescarga.setOnClickListener { descargarArchivo() }
        btnSel.setOnClickListener { seleccionarArchivo() }
        tvArchivoSeleccionado = findViewById(R.id.tvArchivoSeleccionado)

        btnSubir.setOnClickListener {
            if (archivoSel != null) {
                subirArchivo()
            } else {
                mostrarMensaje("Selecciona un archivo primero.")
            }
        }

        btnRefrescar.setOnClickListener {
            reiniciarInterfaz()
        }

        reiniciarInterfaz()

    }

    private fun reiniciarInterfaz() {
        archivoSel = null
        nombreArchivoSel = ""
        tvSalida.text = "Bienvenid@ al servidor de archivos"
        tvFilesList.text = "(No hay archivos listados)"
        tvArchivoSeleccionado.text = "Ningún archivo seleccionado"

        val nombresConPlaceholder = listOf("Selecciona un archivo...")
        val adapter = ArrayAdapter(this, R.layout.spinner_item, nombresConPlaceholder)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerArchivos.adapter = adapter
        spinnerArchivos.setSelection(0)
    }

    private fun listarArchivos() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val socket = Socket(ip, puerto)
                val lector = BufferedReader(InputStreamReader(socket.getInputStream()))
                val escritor = PrintWriter(socket.getOutputStream(), true)

                escritor.println("1")

                val listaArchivos = mutableListOf<String>()
                var linea: String?

                while (lector.readLine().also { linea = it } != null) {
                    linea?.let {
                        if (!it.contains("Bienvenid@") && !it.contains("Ficheros disponibles")) {
                            listaArchivos.add(it.trim())
                        }
                    }
                }

                lector.close()
                socket.close()

                withContext(Dispatchers.Main) {
                    actualizarListaDeArchivos(listaArchivos)
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    mostrarMensaje("Error al listar archivos: ${e.message}")
                }
            }
        }
    }

    private fun actualizarListaDeArchivos(nombres: List<String>) {
        val listaText = if (nombres.isEmpty())
            "(No hay archivos listados)"
        else
            nombres.joinToString("\n") { "· $it" }

        tvFilesList.text = listaText

        val nombresConPlaceholder = listOf("Selecciona un archivo...") + nombres
        val adapter = ArrayAdapter(this, R.layout.spinner_item, nombresConPlaceholder)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerArchivos.adapter = adapter
        spinnerArchivos.setSelection(0)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun descargarArchivo() {
        val nombreArchivo = spinnerArchivos.selectedItem?.toString()

        if (nombreArchivo.isNullOrEmpty() || nombreArchivo == "Selecciona un archivo...") {
            mostrarMensaje("Por favor, selecciona un archivo para descargar.")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val socket = Socket(ip, puerto)
                val entrada = DataInputStream(socket.getInputStream())
                val escritor = PrintWriter(socket.getOutputStream(), true)

                entrada.readLine()
                escritor.println("2")
                escritor.println(nombreArchivo)

                val respuesta = entrada.readLine()

                if (respuesta == "Iniciando descarga...") {
                    val tamañoArchivo = entrada.readLine().toLong()
                    val contenido = contentResolver

                    val contentValues = ContentValues().apply {
                        put(MediaStore.Downloads.DISPLAY_NAME, nombreArchivo)
                        put(MediaStore.Downloads.MIME_TYPE, obtenerTipoArchivo(nombreArchivo))
                        put(MediaStore.Downloads.IS_PENDING, 1)
                    }

                    val descarga = contenido.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    var totalBytesRecibidos = 0

                    descarga?.let {
                        contenido.openOutputStream(it)?.use { flujoSalida ->
                            val buffer = ByteArray(4096)

                            while (totalBytesRecibidos < tamañoArchivo) {
                                val bytesLeidos = entrada.read(buffer, 0, minOf(buffer.size, (tamañoArchivo - totalBytesRecibidos).toInt()))
                                if (bytesLeidos == -1) break
                                flujoSalida.write(buffer, 0, bytesLeidos)
                                totalBytesRecibidos += bytesLeidos
                            }

                            flujoSalida.flush()
                        }

                        contentValues.clear()
                        contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
                        contenido.update(descarga, contentValues, null, null)
                    }

                    socket.close()

                    withContext(Dispatchers.Main) {
                        if (totalBytesRecibidos.toLong() == tamañoArchivo) {
                            mostrarMensaje("Descarga completada: $nombreArchivo")
                        } else {
                            mostrarMensaje("Error: Solo se recibieron $totalBytesRecibidos de $tamañoArchivo bytes.")
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        mostrarMensaje("Error: $respuesta")
                    }
                }

                entrada.close()
                socket.close()

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    mostrarMensaje("Error durante la descarga: ${e.message}")
                }
            }
        }
    }

    private fun obtenerTipoArchivo(nombreArchivo: String): String {
        return when {
            nombreArchivo.endsWith(".pdf", true) -> "application/pdf"
            nombreArchivo.endsWith(".jpg", true) -> "image/jpeg"
            nombreArchivo.endsWith(".png", true) -> "image/png"
            nombreArchivo.endsWith(".txt", true) -> "text/plain"
            nombreArchivo.endsWith(".mp4", true) -> "video/mp4"
            else -> "*/*"
        }
    }

    private fun seleccionarArchivo() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, SEL_ARCHIVO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SEL_ARCHIVO && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                archivoSel = uri
                nombreArchivoSel = obtenerNombreArchivo(uri)
                tvArchivoSeleccionado.text = "Seleccionado: $nombreArchivoSel"
                mostrarMensaje("Archivo seleccionado: $nombreArchivoSel")
            }
        }
    }

    private fun obtenerNombreArchivo(descarga: Uri): String {
        var nombre = "archivo_desconocido"
        contentResolver.query(descarga, null, null, null, null)?.use { cursor ->
            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && index != -1) {
                nombre = cursor.getString(index)
            }
        }
        return nombre
    }

    private fun subirArchivo() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val socket = Socket(ip, puerto)
                val lector = BufferedReader(InputStreamReader(socket.getInputStream()))
                val escritor = PrintWriter(socket.getOutputStream(), true)

                lector.readLine()
                escritor.println("3")
                escritor.println(nombreArchivoSel)

                val respuesta = lector.readLine()

                if (respuesta == "Iniciando subida...") {
                    val uri = archivoSel ?: run {
                        withContext(Dispatchers.Main) {
                            mostrarMensaje("Error: No hay archivo seleccionado.")
                        }
                        socket.close()
                        return@launch
                    }

                    val flujoEntrada = contentResolver.openInputStream(uri)
                    if (flujoEntrada == null) {
                        withContext(Dispatchers.Main) {
                            mostrarMensaje("Error: No se pudo abrir el archivo.")
                        }
                        socket.close()
                        return@launch
                    }

                    val flujoSalida = socket.getOutputStream()
                    val buffer = ByteArray(4096)
                    var bytesLeidos: Int
                    var totalBytesEnviados = 0

                    while (flujoEntrada.read(buffer).also { bytesLeidos = it } != -1) {
                        flujoSalida.write(buffer, 0, bytesLeidos)
                        totalBytesEnviados += bytesLeidos
                    }

                    flujoSalida.flush()
                    socket.shutdownOutput()
                    flujoEntrada.close()
                    lector.readLine()

                    withContext(Dispatchers.Main) {
                        mostrarMensaje("Archivo '$nombreArchivoSel' subido correctamente. ($totalBytesEnviados bytes enviados)")
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        mostrarMensaje("Error en subida: $respuesta")
                    }
                }

                lector.close()
                socket.close()

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    mostrarMensaje("Error al subir archivo: ${e.message}")
                }
            }
        }
    }
}

# 📁 Servidor de Archivos – Cliente Android + Servidor TCP

![Banner Servidor](https://github.com/mariarosete/servidorArchivos/blob/main/bannerServidorArchivos.png?raw=true)

Desarrollo de servidor backend en Java utilizando sockets TCP para la gestión de conexiones concurrentes y transferencia de archivos en red local. 
Implementa arquitectura cliente-servidor con cliente Android en Kotlin que consume el servicio.

---

## 🎥 Demo en vídeo

Puedes ver el funcionamiento de la aplicación aquí:

👉 https://mariarosete.vercel.app/assets/Servidor-I_unzXxa.mp4


---

## 📑 Tabla de contenidos

- [🎥 Demo en vídeo](#-demo-en-vídeo)
- [🛠 Tecnologías utilizadas](#-tecnologías-utilizadas)
- [🎯 Objetivos del proyecto](#-objetivos-del-proyecto)
- [🚀 Funcionalidades destacadas](#-funcionalidades-destacadas)
- [🏆 Logros obtenidos](#-logros-obtenidos)
- [⚠️ Limitaciones encontradas](#️-limitaciones-encontradas)
- [💡 Propuestas de mejora](#-propuestas-de-mejora)
- [💻 Cómo ejecutar el proyecto](#-cómo-ejecutar-el-proyecto)
- [📸 Capturas de pantalla](#-capturas-de-pantalla)
- [📩 Contacto](#-contacto)

---

## 🛠 Tecnologías utilizadas

![Android Studio](https://img.shields.io/badge/Android_Studio-3DDC84?style=for-the-badge&logo=android-studio&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Material Design](https://img.shields.io/badge/Material%20Design-757575?style=for-the-badge&logo=material-design&logoColor=white)
![Sockets TCP](https://img.shields.io/badge/Sockets-TCP-blue?style=for-the-badge)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)

---

## 🎯 Objetivos del proyecto

El objetivo de este proyecto es implementar un sistema de gestión de archivos en red local mediante **sockets TCP**, conectando:

- 🖥 Un **servidor** desarrollado en **Java** (Eclipse).
- 📱 Un **cliente Android** desarrollado en **Kotlin** (Android Studio).

Las funcionalidades disponibles son:

- 📄 Listar los ficheros disponibles en el servidor.
- 📥 Descargar un fichero desde el servidor por nombre.
- 📤 Subir un fichero al servidor, enviando nombre y contenido.

---

## 🚀 Funcionalidades destacadas

- Listado dinámico de archivos disponibles en el servidor.
- Descarga de ficheros seleccionados desde un Spinner.
- Subida de cualquier tipo de archivo mediante el selector del sistema.
- Indicador del archivo actualmente seleccionado.
- Interfaz moderna basada en Material Design con estilos personalizados.
- Soporte para múltiples tipos de archivo (`.pdf`, `.jpg`, `.png`, `.txt`, `.mp4`, etc.).
- Botón de reinicio que limpia los campos y restablece el estado inicial.

---

## 🏆 Logros obtenidos

- Comunicación efectiva cliente-servidor mediante **Sockets TCP**.
- Intercambio de datos binarios (archivos) con flujo controlado.
- Listado remoto de archivos desde el cliente Android.
- Subida y descarga de archivos entre dispositivos heterogéneos.

---

## ⚠️ Limitaciones encontradas

- 🔀 Diferencias entre **Java** y **Kotlin** dificultan la integración directa de estructuras de datos.
- ⏳ Uso de **corutinas** en operaciones de red puede provocar errores si no se gestionan bien.
- 📂 Problemas con flujos de datos: algunos archivos podían recibirse vacíos o con tamaños incorrectos.
- 📱 Restricciones de almacenamiento en Android obligaron a usar **Scoped Storage** y APIs como `contentResolver.query()`.

---

## 💡 Propuestas de mejora

- 📁 Organización de archivos por carpetas en el servidor.
- 📶 Indicadores visuales de progreso en subida/descarga.
- 🔐 Soporte para autenticación de cliente y acceso restringido.

---

## 💻 Cómo ejecutar el proyecto

Este proyecto incluye dos partes:  
1. 🖥 **Servidor TCP en Java**  
2. 📱 **Cliente Android**

---

### 🖥 Ejecutar el servidor Java (backend)

1. Clona este repositorio o descárgalo como ZIP:

   ```bash
   git clone https://github.com/mariarosete/servidorArchivos.git
   ```

2. Abre el proyecto en tu entorno Java preferido (por ejemplo, **Eclipse**, **IntelliJ IDEA** o **NetBeans**).
3. Localiza la clase `ServidorFicheros.java` en el paquete `servidor` (ruta: `src/servidor/ServidorFicheros.java`).
4. ▶️ Ejecuta con clic derecho sobre ServidorFicheros.java > Run As > Java Application

📌 Asegúrate de que:
- El puerto definido en el servidor coincide con el del cliente (por defecto: `6000`).
- El **firewall esté desactivado** o permita conexiones entrantes al puerto utilizado.
- El **directorio de almacenamiento** existe o se creará automáticamente en la ruta especificada dentro del código.

🔗 [Repositorio Servidor Archivos (Backend)](https://github.com/mariarosete/servidorArchivos/tree/main/back)

---

### 📱 Ejecutar el cliente Android

1. Clona este repositorio o descárgalo como ZIP:

   ```bash
   git clone https://github.com/mariarosete/servidorArchivos.git
   ```

2. Abre la carpeta del cliente en **Android Studio**.
3. En el archivo `MainActivity.kt`, **reemplaza la dirección IP** por la IP local de tu ordenador (donde se ejecuta el servidor Java).  

📌 Por ejemplo:

```kotlin
private val ip = "XXX.XXX.X.XXX"
```

4. Conecta un dispositivo físico o usa un emulador.
5. Ejecuta el proyecto (▶️ Run).

📌 **Importante**:

- El dispositivo Android y el servidor Java deben estar conectados a la **misma red local**.
- Si estás usando un **emulador**, asegúrate de que tiene acceso a la red local.
- Puedes obtener tu **IP local** ejecutando:
  - `ipconfig` en **Windows**
  - `ifconfig` o `ip a` en **Linux/macOS**

🔗 [Repositorio Servidor Archivos (Frontend)](https://github.com/mariarosete/servidorArchivos/tree/main/front)

---

## 📸 Capturas de pantalla

| 🧾 Pantalla de inicio | 📁 Archivos disponibles |
|----------------------|--------------------------|
| ![Inicio](https://github.com/mariarosete/servidorArchivos/blob/main/screenshots/Inicio.png?raw=true) | ![Listado](https://github.com/mariarosete/servidorArchivos/blob/main/screenshots/Listado.png?raw=true) |

| 📥 Descarga desde Spinner | 📤 Selección y subida |
|------------------------|---------------------------|
| ![Spinner](https://github.com/mariarosete/servidorArchivos/blob/main/screenshots/spinner.png?raw=true) | ![Subida](https://github.com/mariarosete/servidorArchivos/blob/main/screenshots/Subida.png?raw=true) |


---

## 📩 Contacto

<p align="center">
  <a href="mailto:marlarosete89@gmail.com">
    <img src="https://img.shields.io/badge/Gmail-D14836?style=for-the-badge&logo=gmail&logoColor=white" />
  </a>
  <a href="https://linkedin.com/in/mariarosetesuarez">
    <img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white" />
  </a>
  <a href="https://github.com/mariarosete">
    <img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white" />
  </a>
    <a href="https://mariarosete.vercel.app/">
    <img src="https://img.shields.io/badge/Portfolio-000000?style=for-the-badge&logo=vercel&logoColor=white"></a>
</p>

---

📌 *Proyecto desarrollado de forma integral, incluyendo el frontend Android y el backend en Java para comunicación mediante sockets.*

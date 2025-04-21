package mauricio.u.latina.bolanos.rosales.data.repository

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import mauricio.u.latina.bolanos.rosales.data.database.interfaces.MensajesDao
import mauricio.u.latina.bolanos.rosales.model.Mensajes

class MensajesRepository(private val mensajesDao: MensajesDao) {

    // Obtener todos los mensajes como flujo
    fun obtenerTodosLosMensajes(): Flow<List<Mensajes>> {
        return mensajesDao.observarMensajesFlow()
            .map { mensajes -> mensajes.sortedBy { it.fechaHora } }
    }

    // Obtener mensajes de un remitente específico
    fun obtenerMensajesPorRemitente(remitente: String): Flow<List<Mensajes>> {
        return obtenerTodosLosMensajes()
            .map { mensajes -> mensajes.filter { it.remitente == remitente } }
    }

    // Crear un nuevo mensaje
    suspend fun crearMensaje(remitente: String, contenido: String) {
        val nuevoMensaje = Mensajes.crear(remitente, contenido)
        if (nuevoMensaje.isValid()) {
            mensajesDao.crearMensaje(nuevoMensaje)
        } else {
            throw IllegalArgumentException("El mensaje no es válido")
        }
    }

    // Actualizar un mensaje existente
    suspend fun actualizarMensaje(mensaje: Mensajes) {
        if (mensaje.isValid()) {
            mensajesDao.actualizarMensaje(mensaje)
        } else {
            throw IllegalArgumentException("El mensaje no es válido")
        }
    }

    // Eliminar un mensaje por su ID
    suspend fun eliminarMensaje(id: String) {
        mensajesDao.eliminarMensaje(id)
    }

    // Obtener un mensaje por su ID
    suspend fun obtenerMensajePorId(id: String): Mensajes? {
        return mensajesDao.obtenerMensajePorId(id)
    }
}
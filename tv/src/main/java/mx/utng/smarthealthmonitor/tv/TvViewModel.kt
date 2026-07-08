package mx.utng.smarthealthmonitor.tv
 
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.utng.smarthealthmonitor.data.MockData
 
class TvViewModel : ViewModel() {
 
    // Usamos MutableStateFlow simulando el repositorio, ya que el módulo TV
    // no puede depender del módulo App directamente.
    private val _fc = MutableStateFlow(88)
    val fc: StateFlow<Int> = _fc.asStateFlow()
 
    private val _historial = MutableStateFlow(MockData.historialFC)
    val historial: StateFlow<List<LecturaFC>> = _historial.asStateFlow()

    init {
        // Simulador de datos en vivo:
        // A los 5 segundos de abrir la pantalla, inyectamos una lectura nueva
        // simulando que llegó un dato nuevo desde el reloj.
        viewModelScope.launch {
            delay(5000)
            val nuevaLista = _historial.value.toMutableList()
            nuevaLista.add(LecturaFC(id = 99, valorBpm = 130, hora = "En vivo"))
            _historial.value = nuevaLista
        }
    }
}

package mx.utng.smarthealthmonitor.tv
 
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import kotlinx.coroutines.launch
import mx.utng.smarthealthmonitor.data.MockData
 
class MainFragment : BrowseSupportFragment() {
 
    private val viewModel: TvViewModel by viewModels()
    private lateinit var histAdapter: ArrayObjectAdapter
    private lateinit var estadoAdapter: ArrayObjectAdapter
    private lateinit var avanzadasAdapter: ArrayObjectAdapter
 
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
 
        // Configuración del BrowseFragment
        title        = "SmartHealth TV"
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
 
        // Color de la marca en el sidebar
        brandColor = ContextCompat.getColor(requireContext(), R.color.sh_primary)
 
        cargarFilas()
        observarDatos()

        // Listener para navegar al detalle de la lectura
        setOnItemViewClickedListener { itemViewHolder, item, rowViewHolder, row ->
            if (item is LecturaFC) {
                val detail = DetailFragment.newInstance(item.id, item.valorBpm, item.hora)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_browse_fragment, detail)
                    .addToBackStack(null)  // Back regresa al BrowseFragment
                    .commit()
            }
        }
    }
 
    private fun observarDatos() {
        // Observar historial
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.historial.collect { lecturas ->
                        histAdapter.clear()
                        lecturas.forEach { histAdapter.add(it) }
                    }
                }
                
                // Observar FC actual
                launch {
                    viewModel.fc.collect { bpm ->
                        if (::estadoAdapter.isInitialized && estadoAdapter.size() > 0) {
                            // Actualizamos el primer elemento (el de latidos) si existe
                            val lecturaAnterior = estadoAdapter.get(0) as LecturaFC
                            val lecturaNueva = lecturaAnterior.copy(valorBpm = bpm, hora = "Real-time: ${bpm} bpm")
                            
                            estadoAdapter.replace(0, lecturaNueva)
                        }
                    }
                }
                
                // Observar Estadisticas
                launch {
                    viewModel.estadisticas.collect { stats ->
                        if (::estadoAdapter.isInitialized) {
                            estadoAdapter.clear()
                            // Agregar un mock para real-time FC que se actualice por MQTT
                            estadoAdapter.add(LecturaFC(id=0, valorBpm=viewModel.fc.value, hora="Real-time"))
                            stats.forEach { estadoAdapter.add(it) }
                        }
                    }
                }
                
                // Observar Consultas Avanzadas
                launch {
                    viewModel.avanzadas.collect { lecturas ->
                        if (::avanzadasAdapter.isInitialized) {
                            avanzadasAdapter.clear()
                            lecturas.forEach { avanzadasAdapter.add(it) }
                        }
                    }
                }
            }
        }
    }
 
    private fun cargarFilas() {
        val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
 
        // ── Fila 1: Estado actual (3 dispositivos) ───────────
        estadoAdapter = ArrayObjectAdapter(FCCardPresenter())
        estadoAdapter.add(LecturaFC(id=0, valorBpm=88, hora="Real-time"))
        rowsAdapter.add(ListRow(HeaderItem("Estado Actual (3 dispositivos)"), estadoAdapter))
 
        // ── Fila 2: Historial de FC ────────────────────
        histAdapter = ArrayObjectAdapter(FCCardPresenter())
        rowsAdapter.add(ListRow(HeaderItem("Historial FC"), histAdapter))
        
        // ── Fila 3: Consultas Avanzadas (Reto extra) ──
        avanzadasAdapter = ArrayObjectAdapter(FCCardPresenter())
        rowsAdapter.add(ListRow(HeaderItem("Consultas Avanzadas"), avanzadasAdapter))
 
        this.adapter = rowsAdapter
    }
}

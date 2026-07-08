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
    }
 
    private fun observarDatos() {
        // Observar historial
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.historial.collect { lecturas ->
                    histAdapter.clear()
                    lecturas.forEach { histAdapter.add(it) }
                }
            }
        }
    }
 
    private fun cargarFilas() {
        val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
 
        // ── Fila 1: Estado actual (FC + Pasos) ───────────
        estadoAdapter = ArrayObjectAdapter(FCCardPresenter())
        estadoAdapter.add(LecturaFC(id=0, valorBpm=88, hora="Ahora"))
        estadoAdapter.add(LecturaFC(id=1, valorBpm=4250, hora="Pasos"))
        rowsAdapter.add(ListRow(HeaderItem("Estado actual"), estadoAdapter))
 
        // ── Fila 2: Historial de FC ────────────────────
        histAdapter = ArrayObjectAdapter(FCCardPresenter())
        rowsAdapter.add(ListRow(HeaderItem("Historial FC"), histAdapter))
        
        // ── Fila 3: Alertas recientes (Reto adicional) ──
        val alertasAdapter = ArrayObjectAdapter(FCCardPresenter())
        MockData.alertasRecientes.forEach { alertasAdapter.add(it) }
        rowsAdapter.add(ListRow(HeaderItem("Alertas recientes"), alertasAdapter))
 
        this.adapter = rowsAdapter
    }
}

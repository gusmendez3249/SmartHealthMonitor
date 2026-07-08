package mx.utng.smarthealthmonitor.tv
 
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import mx.utng.smarthealthmonitor.data.MockData
 
class DetailFragment : DetailsSupportFragment(),
    OnActionClickedListener {
 
    companion object {
        const val ARG_LECTURA_ID = "lectura_id"
        const val ACTION_PLAY    = 1L
        const val ACTION_BACK    = 2L
        const val ACTION_TREND   = 3L
 
        fun newInstance(lecturaId: Int): DetailFragment {
            return DetailFragment().apply {
                arguments = Bundle().also {
                    it.putInt(ARG_LECTURA_ID, lecturaId)
                }
            }
        }
    }
 
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = arguments?.getInt(ARG_LECTURA_ID) ?: return
 
        viewLifecycleOwner.lifecycleScope.launch {
            // Buscamos en el MockData en lugar del DAO ya que no dependemos del módulo app directamente
            val lectura = (MockData.historialFC + MockData.alertasRecientes).find { it.id == id } 
                ?: LecturaFC(id, 0, "Desconocido")
            construirDetalle(lectura)
        }
    }
 
    private fun construirDetalle(lectura: LecturaFC) {
        val selector = ClassPresenterSelector()
 
        val dpPresenter = FullWidthDetailsOverviewRowPresenter(
            DetailsDescriptionPresenter()
        )
        dpPresenter.setOnActionClickedListener(this)
        selector.addClassPresenter(DetailsOverviewRow::class.java, dpPresenter)
 
        val row = DetailsOverviewRow(lectura)
        // Ícono de corazón como imagen del detalle
        val iconRes = if (lectura.esNormal)
            android.R.drawable.ic_menu_compass  // placeholder OK
        else
            android.R.drawable.ic_dialog_alert  // placeholder error
        row.imageDrawable = ContextCompat.getDrawable(requireContext(), iconRes)
 
        // Botones de acción
        val actions = ArrayObjectAdapter()
        actions.add(Action(ACTION_PLAY, "▶ Reproducir alerta"))
        actions.add(Action(ACTION_BACK, "← Volver al historial"))
        actions.add(Action(ACTION_TREND, "📊 Ver tendencia"))
        row.actionsAdapter = actions
 
        val adapter = ArrayObjectAdapter(selector)
        adapter.add(row)
        this.adapter = adapter
    }
 
    override fun onActionClicked(action: Action) {
        when (action.id) {
            ACTION_PLAY -> {
                Toast.makeText(context, "Reproducir", Toast.LENGTH_SHORT).show()
            }
            ACTION_BACK -> requireActivity().onBackPressed()
            ACTION_TREND -> {
                val tendencia = MockData.historialFC.takeLast(5).joinToString(", ") { "${it.valorBpm} bpm" }
                Toast.makeText(context, "Tendencia: $tendencia", Toast.LENGTH_LONG).show()
            }
        }
    }
}

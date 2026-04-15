package kelompok3.fnmtv.fnmtvmobile.View.Administrator.Admin

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter // <--- FIX 1: Diganti jadi IndexAxisValueFormatter
import kelompok3.fnmtv.fnmtvmobile.Adapter.Administrator.Admin.TopBeritaAdapter
import kelompok3.fnmtv.fnmtvmobile.Controller.Administrator.Admin.AnalitikController
import kelompok3.fnmtv.fnmtvmobile.R
import kelompok3.fnmtv.fnmtvmobile.databinding.FragmentStatistikBeritaBinding
import java.text.NumberFormat
import java.util.*

class statistikBeritaFragment : Fragment() {
    private var _binding: FragmentStatistikBeritaBinding? = null
    private val binding get() = _binding!!
    private lateinit var controller: AnalitikController

    private var activePeriod = "Hari Ini"
    private var activeMetric = "Views"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStatistikBeritaBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_refresh) {
            // Panggil ulang fungsi load data dari SQLite
            refreshAllData()
            Toast.makeText(context, "Data analitik berhasil disegarkan!", Toast.LENGTH_SHORT).show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = AnalitikController(requireContext())

        setupListeners()
        refreshAllData()
    }

    private fun setupListeners() {
        binding.toggleMainPeriod.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                activePeriod = binding.root.findViewById<TextView>(checkedId).text.toString()
                refreshAllData()
            }
        }

        binding.toggleMetricChart.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                activeMetric = when(checkedId) {
                    R.id.btn_metric_views -> "Views"
                    R.id.btn_metric_pengunjung -> "Pengunjung"
                    else -> "Komentar"
                }
                updateTrendChart()
            }
        }
    }

    private fun refreshAllData() {
        val summary = controller.getSummaryData(activePeriod)
        binding.tvSummaryViews.text = summary["views"].toString()
        binding.tvSummaryBerita.text = summary["berita"].toString()
        binding.tvSummaryKomentar.text = summary["komentar"].toString()

        val nominal = summary["pendapatan"] as Double
        binding.tvSummaryPendapatan.text = "Rp " + NumberFormat.getInstance(Locale("id", "ID")).format(nominal)

        updateTrendChart()
        renderHeatmap()

        // <--- FIX 2: Ganti lv_top_berita jadi lvTopBerita
        binding.lvTopBerita.adapter = TopBeritaAdapter(requireContext(), controller.getTop10News())
    }

    private fun updateTrendChart() {
        val rawData = controller.getTrendChartData(activeMetric, activePeriod)
        val entries = ArrayList<Entry>()
        val labels = ArrayList<String>()

        // PENYELAMAT CRASH: Kalau data kosong, kita paksa kasih nilai 0
        if (rawData.isEmpty()) {
            entries.add(Entry(0f, 0f))
            labels.add("-")
        } else {
            rawData.forEachIndexed { index, pair ->
                entries.add(Entry(index.toFloat(), pair.second))
                labels.add(pair.first)
            }
        }

        val dataSet = LineDataSet(entries, "Total $activeMetric")
        dataSet.color = Color.RED
        dataSet.lineWidth = 3f
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataSet.setDrawFilled(true)
        dataSet.fillDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.bg_gradient_chart)

        binding.trendChart.apply {
            data = LineData(dataSet)
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.labelRotationAngle = -45f
            axisRight.isEnabled = false
            description.isEnabled = false
            animateX(1000)
            invalidate()
        }
    }

    private fun renderHeatmap() {
        binding.gridHeatmap.removeAllViews()
        binding.gridHeatmap.columnCount = 7 // Kunci! Paksa cuma 7 kolom (Minggu - Sabtu)

        val data = controller.getHeatmapData() // Sekarang kembaliannya List<Int> isi 7 angka
        val hariNames = arrayOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab")

        // SIHIR RESPONSIVE: Ubah pixel mentah jadi DP biar nggak offside di layar sempit
        val sizeInDp = (35 * resources.displayMetrics.density).toInt()
        val marginInDp = (4 * resources.displayMetrics.density).toInt()

        // Render Header Hari
        for (name in hariNames) {
            val tv = TextView(context).apply {
                text = name
                textSize = 10f
                gravity = Gravity.CENTER
                setPadding(marginInDp, marginInDp, marginInDp, marginInDp)
            }
            binding.gridHeatmap.addView(tv)
        }

        // Render Kotak Heatmap (FIX: Langsung pakai 'qty' dari perulangan)
        for (qty in data) {
            val color = when {
                qty == 0 -> Color.parseColor("#EEEEEE")
                qty < 3 -> Color.parseColor("#FFCDD2")
                qty < 6 -> Color.parseColor("#E57373")
                else -> Color.parseColor("#D32F2F") // Makin merah
            }

            val box = View(context).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = sizeInDp
                    height = sizeInDp
                    setMargins(marginInDp, marginInDp, marginInDp, marginInDp)
                }
                setBackgroundColor(color)
            }
            binding.gridHeatmap.addView(box)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

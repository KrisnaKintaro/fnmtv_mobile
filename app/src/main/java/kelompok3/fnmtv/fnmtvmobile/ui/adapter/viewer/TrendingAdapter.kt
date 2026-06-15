package kelompok3.fnmtv.fnmtvmobile.ui.adapter.viewer

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kelompok3.fnmtv.fnmtvmobile.data.model.viewer.BeritaItem
import kelompok3.fnmtv.fnmtvmobile.databinding.ItemTrendingViewerBinding
import kelompok3.fnmtv.fnmtvmobile.ui.viewer.DetailBeritaActivity

class TrendingAdapter(private val listTrending: List<BeritaItem>) :
    RecyclerView.Adapter<TrendingAdapter.TrendingViewHolder>() {

    inner class TrendingViewHolder(val binding: ItemTrendingViewerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(berita: BeritaItem, position: Int) {
            // 1. Set Angka Peringkat (Rank 1, 2, 3 dst)
            binding.tvRank.text = (position + 1).toString()

            // Warna Rank: Rank 1-3 Merah Terang, sisanya Abu-abu
            if (position > 2) {
                binding.tvRank.setTextColor(Color.parseColor("#7A7570"))
            }

            // 2. Set Teks Judul dan Views
            binding.tvJudulTrending.text = berita.judulBerita
            binding.tvViewsTrending.text = "👁 ${berita.jumlahView ?: "0"} views"

            // 3. Set Badge (HOT untuk Rank 1, NAIK untuk Rank 2)
            when (position) {
                0 -> {
                    binding.tvBadge.visibility = View.VISIBLE
                    binding.tvBadge.text = "HOT"
                    binding.tvBadge.setBackgroundColor(Color.parseColor("#FFE0E0"))
                    binding.tvBadge.setTextColor(Color.parseColor("#CC0000"))
                }
                1 -> {
                    binding.tvBadge.visibility = View.VISIBLE
                    binding.tvBadge.text = "NAIK"
                    binding.tvBadge.setBackgroundColor(Color.parseColor("#E0F7FA"))
                    binding.tvBadge.setTextColor(Color.parseColor("#00838F"))
                }
                else -> {
                    binding.tvBadge.visibility = View.GONE
                }
            }

            // 4. Klik Item pindah ke Detail
            binding.root.setOnClickListener {
                val intent = Intent(itemView.context, DetailBeritaActivity::class.java)
                intent.putExtra("BERITA_SLUG", berita.slug)
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingViewHolder {
        val binding = ItemTrendingViewerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TrendingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrendingViewHolder, position: Int) {
        holder.bind(listTrending[position], position)
    }

    override fun getItemCount(): Int = listTrending.size
}

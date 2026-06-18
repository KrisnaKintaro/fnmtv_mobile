package kelompok3.fnmtv.fnmtvmobile.ui.adapter.viewer

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kelompok3.fnmtv.fnmtvmobile.data.model.viewer.BeritaItem
import kelompok3.fnmtv.fnmtvmobile.databinding.ItemBeritaViewerBinding
import kelompok3.fnmtv.fnmtvmobile.ui.viewer.DetailBeritaActivity

class BeritaAdapter(private var listBerita: List<BeritaItem>) :
    RecyclerView.Adapter<BeritaAdapter.BeritaViewHolder>() {

    // ── Simpan data asli supaya filter bisa di-reset ──────────────────────
    private var listAsli: List<BeritaItem> = listBerita.toList()

    // ── updateData: tetap sama persis seperti milik Anda ─────────────────
    fun updateData(newList: List<BeritaItem>) {
        listBerita = newList
        listAsli   = newList.toList()   // perbarui juga data asli
        notifyDataSetChanged()
    }

    // ── FUNGSI BARU: filter berdasarkan keyword ───────────────────────────
    fun filter(keyword: String) {
        listBerita = if (keyword.isBlank()) {
            listAsli.toList()
        } else {
            val q = keyword.lowercase().trim()
            listAsli.filter { berita ->
                berita.judulBerita?.lowercase()?.contains(q) == true ||
                        berita.kategori?.namaKategori?.lowercase()?.contains(q) == true
            }
        }
        notifyDataSetChanged()
    }

    // ── FUNGSI BARU: reset ke semua data ──────────────────────────────────
    fun resetFilter() {
        listBerita = listAsli.toList()
        notifyDataSetChanged()
    }

    // ─────────────────────────────────────────────────────────────────────
    // Kode asli Anda — tidak ada yang diubah di bawah ini
    // ─────────────────────────────────────────────────────────────────────

    inner class BeritaViewHolder(val binding: ItemBeritaViewerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(berita: BeritaItem) {
            binding.tvJudulBerita.text = berita.judulBerita
            binding.tvKategori.text = berita.kategori?.namaKategori?.uppercase() ?: "UMUM"

            val views = "${berita.jumlahView ?: "0"} Views"
            val tanggal = berita.waktuPublikasi?.substringBefore("T") ?: ""
            binding.tvTanggal.text = "$tanggal • $views"

            val imgUrl = if (berita.fotoThumbnail?.startsWith("http") == true) {
                berita.fotoThumbnail
            } else {
                "https://baru.fenomenatv.com/uploads/thumbnail/${berita.fotoThumbnail}"
            }

            Glide.with(itemView.context)
                .load(imgUrl)
                .centerCrop()
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(binding.ivThumbnail)

            // Klik Item pindah ke Detail
            binding.root.setOnClickListener {
                val intent = Intent(itemView.context, DetailBeritaActivity::class.java)
                intent.putExtra("BERITA_SLUG", berita.slug)
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeritaViewHolder {
        val binding = ItemBeritaViewerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BeritaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BeritaViewHolder, position: Int) {
        holder.bind(listBerita[position])
    }

    override fun getItemCount(): Int = listBerita.size
}
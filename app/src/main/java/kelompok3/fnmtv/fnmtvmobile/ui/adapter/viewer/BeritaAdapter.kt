package kelompok3.fnmtv.fnmtvmobile.ui.adapter.viewer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kelompok3.fnmtv.fnmtvmobile.data.model.viewer.BeritaItem
import kelompok3.fnmtv.fnmtvmobile.databinding.ItemBeritaViewerBinding

class BeritaAdapter(private var listBerita: List<BeritaItem>) :
    RecyclerView.Adapter<BeritaAdapter.BeritaViewHolder>() {

    fun updateData(newList: List<BeritaItem>) {
        listBerita = newList
        notifyDataSetChanged()
    }

    inner class BeritaViewHolder(val binding: ItemBeritaViewerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(berita: BeritaItem) {
            // 1. Pasang Teks
            binding.tvJudulBerita.text = berita.judulBerita
            binding.tvKategori.text = berita.kategori?.namaKategori?.uppercase() ?: "UMUM"

            // 2. Format Views & Tanggal
            val views = "${berita.jumlahView} Views"
            // Kalau tanggal dari API kepanjangan, nanti bisa kita format. Sementara tampilkan apa adanya:
            val tanggal = berita.waktuPublikasi?.substringBefore("T") ?: ""
            binding.tvTanggal.text = "$tanggal • $views"

            // 3. Load Gambar pake Glide
            val imgUrl = if (berita.fotoThumbnail?.startsWith("http") == true) {
                berita.fotoThumbnail
            } else {
                "https://baru.fenomenatv.com/uploads/thumbnail/${berita.fotoThumbnail}"
            }

            Glide.with(itemView.context)
                .load(imgUrl)
                .centerCrop()
                .into(binding.ivThumbnail)

            // 4. Kalau Item diklik, nanti pindah ke Detail (Bisa ditabahin nanti)
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
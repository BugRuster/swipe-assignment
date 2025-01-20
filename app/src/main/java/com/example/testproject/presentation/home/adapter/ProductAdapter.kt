import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.testproject.R
import com.example.testproject.databinding.ItemProductBinding
import com.example.testproject.domain.model.Product
import com.google.android.material.chip.Chip
import java.io.File
import java.text.NumberFormat
import java.util.Locale
// ProductAdapter.kt
// ProductAdapter.kt
class ProductAdapter : ListAdapter<Product, ProductAdapter.ProductViewHolder>(DiffCallback) {

override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
    val binding = ItemProductBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
    )
    return ProductViewHolder(binding)
}

override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
    holder.bind(getItem(position))
}

class ProductViewHolder(
    private val binding: ItemProductBinding
) : RecyclerView.ViewHolder(binding.root) {

    private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    fun bind(product: Product) {
        binding.apply {
            tvProductName.text = product.productName
            chipProductType.text = product.productType
            tvPrice.text = currencyFormatter.format(product.price)
            tvTax.text = "${product.tax}% tax"

            // Find the offline chip by ID and set its visibility
            root.findViewById<Chip>(R.id.chipOffline)?.apply {
                isVisible = product.isPendingUpload
            }

            // Load image using Coil
            product.imageUrl?.let {
                if (it.startsWith("http")) {
                    // Remote image
                    ivProduct.load(it) {
                        crossfade(true)
                        placeholder(android.R.drawable.ic_menu_gallery)
                        error(android.R.drawable.ic_menu_gallery)
                    }
                } else {
                    // Local image
                    ivProduct.load(File(it)) {
                        crossfade(true)
                        placeholder(android.R.drawable.ic_menu_gallery)
                        error(android.R.drawable.ic_menu_gallery)
                    }
                }
            } ?: run {
                ivProduct.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        }
    }
}

private object DiffCallback : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem == newItem
    }
}
}
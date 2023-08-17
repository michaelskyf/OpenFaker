package pl.michaelskyf.openfaker

import android.content.ClipData
import android.content.ClipboardManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.TextView
import androidx.core.content.getSystemService
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.snackbar.Snackbar
import pl.michaelskyf.openfaker.databinding.OptionRowBinding

class PropertyAdapter(private val properties: List<Property>) : RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder>()
{
    inner class PropertyViewHolder(binding: OptionRowBinding): ViewHolder(binding.root) {
        val icon = binding.propertyImage
        val name = binding.propertyName
        val currentValue = binding.propertyCurrentValue
        val fakeValue = binding.propertyFakeValue
        val realValue = binding.propertyRealValue
        val isActive = binding.propertyToggleFake
        val expanded = binding.propertyExpanded
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val optionRowBinding = OptionRowBinding.inflate(inflater, parent, false)
        return PropertyViewHolder(optionRowBinding)
    }

    override fun getItemCount(): Int {
        return properties.size
    }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        holder.icon.foreground = properties[position].icon
        holder.name.text = properties[position].name
        holder.realValue.text = properties[position].getRealValue()
        holder.fakeValue.text = properties[position].fakeValue
        holder.isActive.isChecked = properties[position].isActive

        val switchCallback = {
            _: CompoundButton, isChecked: Boolean ->
            val value = when (isChecked)
            {
                true -> holder.fakeValue.text
                false -> holder.realValue.text
            }

            holder.currentValue.text = value
        }

        holder.isActive.setOnCheckedChangeListener(switchCallback)
        holder.itemView.setOnClickListener {
            _ ->
            if (holder.expanded.visibility == View.VISIBLE)
            {
                holder.expanded.visibility = View.GONE
                holder.currentValue.visibility = View.VISIBLE
            }
            else
            {
                holder.expanded.visibility = View.VISIBLE
                holder.currentValue.visibility = View.GONE
            }
        }

        holder.realValue.setOnClickListener { view -> copyToClipboard(view as TextView, "Real value") }
        holder.fakeValue.setOnClickListener { view -> copyToClipboard(view as TextView, "Fake value") }

        switchCallback(holder.isActive, holder.isActive.isChecked)
    }

    private fun copyToClipboard(view: TextView, label: String) {

        val clipboard = view.context.getSystemService<ClipboardManager>()
        val clip = ClipData.newPlainText(label, view.text)

        clipboard?.setPrimaryClip(clip)
        Snackbar.make(view, "Copied to clipboard", Snackbar.LENGTH_LONG).show()

    }
}
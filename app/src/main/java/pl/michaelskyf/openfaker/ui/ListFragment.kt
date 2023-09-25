package pl.michaelskyf.openfaker.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.Settings.Secure
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.LinearLayoutManager
import pl.michaelskyf.openfaker.R
import pl.michaelskyf.openfaker.databinding.FragmentListBinding
import pl.michaelskyf.openfaker.ui_module_bridge.HookData
import pl.michaelskyf.openfaker.ui_module_bridge.LuaFakerModuleFactory
import pl.michaelskyf.openfaker.ui_module_bridge.MethodData

class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        _binding = FragmentListBinding.inflate(inflater, container, false)

        val adapter = PropertyAdapter(createProperties())

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter

        return binding.root
    }

    @SuppressLint("HardwareIds")
    private fun createProperties(): List<Property> {
        val properties = listOf(
            Property(
                getIcon(R.drawable.ic_launcher_foreground), "Android ID",
                { Secure.getString(requireContext().contentResolver, Secure.ANDROID_ID) },
                MethodData("android.provider.Settings\$Secure", "getString", arrayOf()),
                false
            )
        )

        properties.forEach {
            if (checkIfHookIsActive(it.methodData)) {
                it.isActive = true
            }
        }

        return properties
    }

    private fun checkIfHookIsActive(methodData: MethodData): Boolean {
        val prefs = requireContext().getSharedPreferences("open_faker_module_method_hooks", Context.MODE_WORLD_READABLE)
        val dataTunnel = UISharedPreferencesMutableDataTunnel(prefs)

        val receivedDataResult = dataTunnel[methodData.className, methodData.methodName]
        if (receivedDataResult.isSuccess) {
            val receivedData = receivedDataResult.getOrThrow()

            return methodData == receivedData
        }

        return false
    }

    private fun getIcon(id: Int): Drawable
        = AppCompatResources.getDrawable(requireContext(), id)!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
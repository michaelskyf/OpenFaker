package pl.michaelskyf.openfaker.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import pl.michaelskyf.openfaker.databinding.FragmentListBinding
import pl.michaelskyf.openfaker.ui.modules.AndroidID
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

    private fun createProperties(): List<Property> {
        val context = requireContext()

        val properties = listOf(
            AndroidID.getProperty(context)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
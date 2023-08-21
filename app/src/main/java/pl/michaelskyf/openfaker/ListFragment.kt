package pl.michaelskyf.openfaker

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.os.Bundle
import android.provider.Settings.Secure
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import pl.michaelskyf.openfaker.databinding.FragmentListBinding
import pl.michaelskyf.openfaker.xposed.ExpectedFunctionArgument
import pl.michaelskyf.openfaker.xposed.JsonToMap

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

    private fun createProperties(): List<Property> = buildList {

        val arguments = JsonToMap.MethodArguments(Secure::class.java.name, "getString",
            "Fake value", arrayOf( ExpectedFunctionArgument(ContentResolver::class.java, null, ExpectedFunctionArgument.CompareOperation.AlwaysTrue),
                ExpectedFunctionArgument(String::class.java, "android_id") ))

        this.add(Property(resources.getDrawable(android.R.drawable.ic_secure, null), "Android ID",
            { Secure.getString(context?.contentResolver, Secure.ANDROID_ID) }, arguments, false))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package pl.michaelskyf.openfaker.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import pl.michaelskyf.openfaker.databinding.FragmentListBinding
import pl.michaelskyf.openfaker.lua.LuaFakerModuleFactory
import pl.michaelskyf.openfaker.ui_module_bridge.MethodHookHolder

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
        val fakerData = UIFakerData(requireContext()).getOrThrow()

        val lua = """
            function registerModule(moduleRegistry)
                moduleRegistry:exactMatchArguments({argument:ignore()})
            end
            
            function runModule(hookParameters)
                local arguments = hookParameters:getArguments()
                local obj = hookParameters:getThisObject()
                local method = hookParameters:getMethod()
                
                local result = method:invoke(obj, {"/sdcard/Music/WakacyjnyRomans.opus"})
                logger:log("Hi")
                hookParameters:setResult(result)
                
                return true
            end
        """.trimIndent()

        val result = fakerData.set(MediaPlayer::class.java.name, "setDataSource", arrayOf(
            MethodHookHolder(
                MediaPlayer::class.java.name,
                "setDataSource",
                arrayOf(String::class.java.name),
                LuaFakerModuleFactory(lua, 0),
                MethodHookHolder.WhenToHook.Before
            )
        ))

        result.getOrThrow()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
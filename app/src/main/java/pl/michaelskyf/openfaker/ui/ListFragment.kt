package pl.michaelskyf.openfaker.ui

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import pl.michaelskyf.openfaker.BuildConfig
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

        /*val lua = """
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
        ))*/

        val appId = "pl.nextcamera"
        val activity = "$appId.MainActivity"
        val lua = """
            asyncTask = {}
            function asyncTask.run()
                local jActivityManager = luajava.bindClass("android.app.ActivityManagerNative")
                local newTask = 0x10000000
                local context = asyncTask["context"]
                local userHandle = luajava.bindClass("android.os.UserHandle").CURRENT
                local contentResolver = context:getContentResolver()
                
                local name = luajava.newInstance("android.content.ComponentName", "$appId", "$activity")
                local intent = luajava.newInstance("android.content.Intent")
                intent:setComponent(name)
                
                local activityOptions = luajava.bindClass("android.app.ActivityOptions"):makeBasic()
                
                local result = jActivityManager:getDefault():startActivityAsUser(
                    null, context:getBasePackageName(),
                    intent, intent:resolveTypeIfNeeded(contentResolver),
                    null, null, 0, newTask, null, activityOptions:toBundle(), userHandle:getIdentifier())
                
                logger:log(result)
            end
            
            function registerModule(moduleRegistry)
                moduleRegistry:exactMatchArguments({argument:ignore()})
            end
            
            function runModule(hookParameters)
                
                local jAsyncTask = luajava.bindClass("android.os.AsyncTask")
                local proxy = luajava.createProxy("java.lang.Runnable",  asyncTask)
                
                local context = hookParameters:getThisObject():getContext()
                asyncTask["context"] = context
                
                jAsyncTask:execute(proxy)
                logger:log("Task executed")
                
                return true
            end
        """.trimIndent()

        val result = fakerData.set("com.android.systemui.statusbar.phone.KeyguardBottomAreaView", "launchCamera", arrayOf(
            MethodHookHolder(
                "com.android.systemui.statusbar.phone.KeyguardBottomAreaView",
                "launchCamera",
                arrayOf(String::class.java.name),
                LuaFakerModuleFactory(lua, 0),
                MethodHookHolder.WhenToHook.Before
            )
        ))

        val lua2 = """
            function registerModule(moduleRegistry)
                moduleRegistry:exactMatchArguments({argument:ignore()})
            end
            
            function runModule(hookParameters)
            
                local activity = hookParameters:getThisObject()
                activity:setShowWhenLocked(true)
                logger:log("Activity set")
                
                return false
            end
        """.trimIndent()
        fakerData.set(activity, "onCreate", arrayOf(
            MethodHookHolder(
                activity,
                "onCreate",
                arrayOf(Bundle::class.java.name),
                LuaFakerModuleFactory(lua2, 0),
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
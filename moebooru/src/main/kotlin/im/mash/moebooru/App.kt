package im.mash.moebooru

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Environment
import android.support.v7.app.AppCompatDelegate
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.google.firebase.FirebaseApp
import im.mash.moebooru.core.module.AppModule
import im.mash.moebooru.core.module.CoreComponent
import im.mash.moebooru.core.module.DaggerCoreComponent
import im.mash.moebooru.util.DeviceContext
import io.fabric.sdk.android.Fabric
import java.io.File

class App : Application() {

    companion object {
        private const val TAG = "MoebooruApp"
        lateinit var app: App
        lateinit var coreComponent: CoreComponent
        lateinit var moePath: String
    }

    internal val settings: Settings by lazy { Settings(coreComponent.sharedPreferences()) }
    private val deviceContext: Context by lazy { if (Build.VERSION.SDK_INT < 24) this else DeviceContext(this) }

    override fun onCreate() {
        super.onCreate()
        app = this
        coreComponent = DaggerCoreComponent.builder().appModule(AppModule(this)).build()
        FirebaseApp.initializeApp(deviceContext)
        if (settings.enabledCrashReport && !Fabric.isInitialized()) {
            Fabric.with(this, Crashlytics())
        }
        AppCompatDelegate.setDefaultNightMode(settings.nightMode)
        initPath()
    }

    private fun initPath() {
        val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Moebooru")
        when (dir.exists()){
            false -> {
                if (!dir.mkdirs()) {
                    Log.i(TAG, "Create dir failed!")
                } else {
                    moePath = dir.absolutePath
                }
            }
            true -> {
                if (dir.isFile) {
                    if (dir.delete()) {
                        if (!dir.mkdir()) {
                            Log.i(TAG, "Create dir failed!")
                        } else {
                            moePath = dir.absolutePath
                        }
                    } else {
                        Log.i(TAG, "Delete moe file failed!")
                    }
                } else {
                    moePath = dir.absolutePath
                }
            }
        }
    }
}
package im.mash.moebooru

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Environment
import android.support.v7.app.AppCompatDelegate
import com.bumptech.glide.load.model.Headers
import com.crashlytics.android.Crashlytics
import com.google.firebase.FirebaseApp
import im.mash.moebooru.common.di.CoreComponent
import im.mash.moebooru.common.di.DaggerCoreComponent
import im.mash.moebooru.core.module.AppModule
import im.mash.moebooru.download.DownloadManager
import im.mash.moebooru.util.CrashHandler
import im.mash.moebooru.util.DeviceContext
import im.mash.moebooru.util.glideHeader
import im.mash.moebooru.util.logi
import io.fabric.sdk.android.Fabric
import java.io.File

class App : Application() {

    companion object {
        private const val TAG = "MoebooruApp"
        lateinit var app: App
        lateinit var coreComponent: CoreComponent
        lateinit var glideHeaders: Headers
    }

    internal val settings: Settings by lazy { Settings(coreComponent.sharedPreferences()) }
    private val deviceContext: Context by lazy { if (Build.VERSION.SDK_INT < 24) this else DeviceContext(this) }
    internal val downloadManager: DownloadManager by lazy { DownloadManager.getInstance() }

    override fun onCreate() {
        super.onCreate()
        app = this
        CrashHandler.getInstance().init(this)
        coreComponent = DaggerCoreComponent.builder().appModule(AppModule(this)).build()
        FirebaseApp.initializeApp(deviceContext)
        if (settings.enabledCrashReport && !Fabric.isInitialized()) {
            Fabric.with(this, Crashlytics())
        }
        AppCompatDelegate.setDefaultNightMode(settings.nightMode)
        glideHeaders = this.glideHeader
    }

    fun getMoePath(): String {
        var  path = ""
        val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Moebooru")
        when (dir.exists()){
            false -> {
                path = if (!dir.mkdirs()) {
                    logi(TAG, "Create dir failed!")
                    File(app.getExternalFilesDir(""), "Moebooru").absolutePath
                } else {
                    dir.absolutePath
                }
            }
            true -> {
                path = if (dir.isFile) {
                    if (dir.delete()) {
                        if (!dir.mkdir()) {
                            logi(TAG, "Create dir failed!")
                            File(app.getExternalFilesDir(""), "Moebooru").absolutePath
                        } else {
                            dir.absolutePath
                        }
                    } else {
                        logi(TAG, "Delete moe file failed!")
                        File(app.getExternalFilesDir(""), "Moebooru").absolutePath
                    }
                } else {
                    dir.absolutePath
                }
            }
        }
        return path
    }
}
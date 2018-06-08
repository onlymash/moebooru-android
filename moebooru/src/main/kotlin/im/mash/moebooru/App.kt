package im.mash.moebooru

import android.app.Application
import android.support.v7.app.AppCompatDelegate
import im.mash.moebooru.core.module.AppModule
import im.mash.moebooru.core.module.CoreComponent
import im.mash.moebooru.core.module.DaggerCoreComponent

class App : Application() {

    companion object {
        private const val TAG = "MoebooruApp"
        lateinit var app: App
        lateinit var coreComponent: CoreComponent
    }

    internal val settings: Settings by lazy { Settings(this) }

    override fun onCreate() {
        super.onCreate()
        app = this
        coreComponent = DaggerCoreComponent.builder().appModule(AppModule(this)).build()
        AppCompatDelegate.setDefaultNightMode(settings.nightMode)
    }
}
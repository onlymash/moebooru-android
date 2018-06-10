package im.mash.moebooru.common

import im.mash.moebooru.App
import im.mash.moebooru.detail.di.DaggerDetailComponent
import im.mash.moebooru.detail.di.DetailComponent
import im.mash.moebooru.main.di.DaggerMainComponent
import im.mash.moebooru.main.di.MainComponent
import javax.inject.Singleton

@Singleton
object MoeDH {

    private var mainComponent: MainComponent? = null

    fun mainComponent(): MainComponent {
        if (mainComponent == null) {
            mainComponent = DaggerMainComponent.builder().coreComponent(App.coreComponent).build()
        }
        return mainComponent as MainComponent
    }

    fun destroyMainComponent() {
        mainComponent = null
    }

    private var detailComponent: DetailComponent? = null

    fun detailComponent(): DetailComponent {
        if (detailComponent == null) {
            detailComponent = DaggerDetailComponent.builder().mainComponent(mainComponent()).build()
        }
        return detailComponent as DetailComponent
    }

    fun destroyDetailComponent() {
        detailComponent = null
    }
}
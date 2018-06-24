package im.mash.moebooru.main.fragment

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.view.ViewCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import im.mash.moebooru.R
import im.mash.moebooru.common.base.ToolbarFragment
import im.mash.moebooru.common.data.local.entity.Booru
import im.mash.moebooru.core.scheduler.Outcome
import im.mash.moebooru.helper.getViewModel
import im.mash.moebooru.main.MainActivity
import im.mash.moebooru.main.adapter.BooruAdapter
import im.mash.moebooru.main.viewmodel.BooruViewModel
import im.mash.moebooru.util.logi

class BooruFragment : ToolbarFragment() {

    companion object {
        private const val TAG = "BooruFragment"
    }

    private lateinit var booruAdapter: BooruAdapter
    private lateinit var booruView: RecyclerView

    private lateinit var addBooru: FloatingActionButton

    private var boorus: MutableList<Booru> = mutableListOf()

    private val mainActivity: MainActivity by lazy { activity as MainActivity }
    private val booruViewModel: BooruViewModel by lazy { this.getViewModel<BooruViewModel>(mainActivity.booruViewModelFactory) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_booru, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        initViewModel()
    }

    private fun initView(view: View) {
        toolbar.setTitle(R.string.boorus)
        addBooru = view.findViewById(R.id.add_booru)
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            val paddingButton = insets.systemWindowInsetBottom
            view.setPadding(0, 0, 0, paddingButton)
            insets
        }
        booruView = view.findViewById(R.id.booru_list)
        booruView.layoutManager = LinearLayoutManager(this.requireContext(), LinearLayout.VERTICAL, false)
        booruView.addItemDecoration(DividerItemDecoration(this.requireContext(), DividerItemDecoration.VERTICAL))
        booruAdapter = BooruAdapter(this.requireContext(), boorus)
        booruView.adapter = booruAdapter
        booruAdapter.setBooruChangeListener(object : BooruAdapter.BooruChangeListener {
            override fun onBooruEdit(booru: Booru) {

            }

            override fun onBooruDelete(booru: Booru) {
                AlertDialog.Builder(mainActivity)
                        .setTitle("Delete booru")
                        .setMessage("Do you confirm delete the booru?")
                        .setPositiveButton(getString(R.string.ok)) { _, _ ->
                            booruViewModel.deleteBooru(booru)
                        }
                        .setNegativeButton(getString(R.string.cancel), null)
                        .show()
            }
        })
    }

    private fun initViewModel() {
        booruViewModel.booruOutcome.observe(this, Observer<Outcome<MutableList<Booru>>> { outcome ->
            when (outcome) {
                is Outcome.Progress -> {

                }
                is Outcome.Success -> {
                    boorus = outcome.data
                    booruAdapter.updateData(boorus)
                    mainActivity.initHeaderItem(boorus)
                    logi(TAG, "Boorus size: ${boorus.size}")
                }
                is Outcome.Failure -> {

                }
            }
        })
        booruViewModel.loadBoorus()
    }
}
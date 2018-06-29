package im.mash.moebooru.main.fragment

import android.arch.lifecycle.Observer
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.view.ViewCompat
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.R
import im.mash.moebooru.Settings
import im.mash.moebooru.common.base.ToolbarFragment
import im.mash.moebooru.common.data.local.entity.User
import im.mash.moebooru.common.viewmodel.UserViewModel
import im.mash.moebooru.core.scheduler.Outcome
import im.mash.moebooru.helper.getViewModel
import im.mash.moebooru.main.MainActivity
import im.mash.moebooru.search.SearchActivity
import im.mash.moebooru.util.HashUtil
import im.mash.moebooru.util.logi
import im.mash.moebooru.util.takeSnackbarShort
import okhttp3.HttpUrl

class AccountFragment : ToolbarFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        private const val TAG = "AccountFragment"
    }

    private lateinit var infoLayout: LinearLayout
    private lateinit var loginLayout: ScrollView

    private lateinit var inputUsername: AutoCompleteTextView
    private lateinit var inputPassword: TextInputEditText

    private lateinit var progressBar: ProgressBar
    private lateinit var setAccount: Button

    private lateinit var infoId: TextView
    private lateinit var infoName: TextView
    private lateinit var myFavorites: Button
    private lateinit var removeAccount: Button

    private var user: User? = null
    private var users: MutableList<User> = mutableListOf()

    private var requesting = true

    private var paddingButton = 0

    private val mainActivity: MainActivity by lazy { activity as MainActivity }
    private val userViewModel: UserViewModel by lazy { this.getViewModel<UserViewModel>(mainActivity.userViewModelFactory) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_account, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            paddingButton = insets.systemWindowInsetBottom
            val lp = view.layoutParams as FrameLayout.LayoutParams
            lp.setMargins(0, 0, 0, paddingButton)
            view.layoutParams = lp
            insets
        }
        initView(view)
        initUserViewModel()
        mainActivity.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    private fun initUserViewModel() {
        userViewModel.userOutcome.observe(this, Observer<Outcome<MutableList<User>>> { outcome ->
            when (outcome) {
                is Outcome.Progress -> {

                }
                is Outcome.Success -> {
                    requesting = false
                    progressBar.visibility = View.GONE
                    setAccount.visibility = View.VISIBLE
                    users = outcome.data
                    initUser()
                    logi(TAG, "User: $users, size: ${users.size}")
                }
                is Outcome.Failure -> {
                    requesting = false
                    infoLayout.visibility = View.GONE
                    loginLayout.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                    setAccount.visibility = View.VISIBLE
                    outcome.e.printStackTrace()
                }
            }
        })
        userViewModel.loadUsers()
    }

    private fun initUser() {
        val host = app.settings.activeProfileHost
        user = null
        users.forEach {  user ->
            if (user.site == host) {
                this.user = user
                return@forEach
            }
        }
        if (user != null) {
            infoLayout.visibility = View.VISIBLE
            loginLayout.visibility = View.GONE
            setInfo(user!!)
        } else {
            infoLayout.visibility = View.GONE
            loginLayout.visibility = View.VISIBLE
            takeSnackbarShort(this.view!!, "User not found", paddingButton)
        }
    }

    private fun setInfo(user: User) {
        infoId.text = user.id.toString()
        infoName.text = user.name
    }

    private fun initView(view: View) {
        toolbar.setTitle(R.string.title_account)
        infoLayout = view.findViewById(R.id.account_info_layout)
        loginLayout = view.findViewById(R.id.set_account_layout)

        inputUsername = view.findViewById(R.id.username)
        inputPassword = view.findViewById(R.id.password)

        progressBar = view.findViewById(R.id.progress_bar)
        setAccount = view.findViewById(R.id.set_account)

        infoId = view.findViewById(R.id.tv_id)
        infoName = view.findViewById(R.id.tv_name)

        myFavorites = view.findViewById(R.id.btn_favorites)
        removeAccount = view.findViewById(R.id.btn_remove)

        inputPassword.setOnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptSetAccount()
            }
            false
        }

        setAccount.setOnClickListener {
            attemptSetAccount()
        }

        myFavorites.setOnClickListener {
            if (user != null) {
                val name = user!!.name
                val intent = Intent(this.requireContext(), SearchActivity::class.java).apply {
                    putExtra("keyword", "vote:3:$name order:vote")
                }
                startActivity(intent)
            }
        }
        removeAccount.setOnClickListener {
            if (user != null) {
                AlertDialog.Builder(this.requireContext())
                        .setTitle("Remove account")
                        .setMessage("Do you confirm remove the account?")
                        .setPositiveButton(getString(R.string.ok)) { _, _ ->
                            userViewModel.deleteUser(user!!)
                        }
                        .setNegativeButton(getString(R.string.cancel), null)
                        .show()
            }
        }
    }

    private fun getHttpUrl(username: String): HttpUrl {
        return HttpUrl.Builder()
                .scheme(app.settings.activeProfileSchema)
                .host(app.settings.activeProfileHost)
                .addPathSegment("user.json")
                .addQueryParameter("name", username)
                .build()
    }

    private fun attemptSetAccount() {
        if (requesting) {
            return
        }
        val username = inputUsername.text.toString()
        val password = inputPassword.text.toString()
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            takeSnackbarShort(this.view!!, "Username and password can not be null", paddingButton)
            return
        }
        val hashSalt = mainActivity.getHashSalt()

        setAccount.visibility = View.INVISIBLE
        progressBar.visibility = View.VISIBLE
        requesting = true
        val passwordHash = when (TextUtils.isEmpty(hashSalt)) {
            true -> HashUtil.sha1(password)
            else -> HashUtil.sha1(hashSalt.replace("your-password", password))
        }
        userViewModel.getUser(getHttpUrl(username), passwordHash)
    }

    override fun onSharedPreferenceChanged(sp: SharedPreferences?, key: String?) {
        when (key) {
            Settings.ACTIVE_PROFILE_HOST -> {
                initUser()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainActivity.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }
}
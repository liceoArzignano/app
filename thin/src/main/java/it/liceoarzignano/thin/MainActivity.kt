package it.liceoarzignano.thin

import android.content.ComponentName
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import it.liceoarzignano.foundation.db.entities.Ping
import it.liceoarzignano.foundation.extensions.get
import it.liceoarzignano.foundation.extensions.getSubjects
import it.liceoarzignano.foundation.help.HelpActivity
import it.liceoarzignano.foundation.marks.MarksFragment
import it.liceoarzignano.foundation.pings.PingsFragment
import it.liceoarzignano.foundation.util.PreferenceKeys
import it.liceoarzignano.foundation.util.QuarterManager
import it.liceoarzignano.thin.onboarding.OnBoardActivity
import it.liceoarzignano.thin.pings.ThinPingsFragment
import it.liceoarzignano.thin.preferences.PreferencesActivity

class MainActivity : AppCompatActivity() {
    private lateinit var marksFragment: MarksFragment
    private lateinit var pingsFragment: ThinPingsFragment

    private lateinit var mToolbar: Toolbar
    private lateinit var mFab: FloatingActionButton

    private lateinit var mViewModel: MainViewModel
    private lateinit var mPrefs: SharedPreferences
    private lateinit var mQuarterManager: QuarterManager

    private var hasSeenPings = false
    private var hasSeenMarks = false

    private var mTabsConnection: CustomTabsServiceConnection? = null
    private var mTabClient: CustomTabsClient? = null
    private lateinit var mTabSession: CustomTabsSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        mQuarterManager = QuarterManager(this)

        // OnBoard asap if needed
        showOnBoardingIfNeeded()

        mViewModel = ViewModelProviders.of(this)[MainViewModel::class.java]
        pingsFragment = ThinPingsFragment()
        marksFragment = MarksFragment()

        setContentView(R.layout.activity_main)

        mToolbar = findViewById(R.id.main_toolbar)
        setSupportActionBar(mToolbar)
        mToolbar.overflowIcon = ContextCompat.getDrawable(this, R.drawable.ic_menu)

        mFab = findViewById(R.id.main_fab)

        val bottomBar = findViewById<BottomNavigationView>(R.id.main_bottom_bar)
        bottomBar.setOnNavigationItemSelectedListener { item -> switchToFragment(item.itemId) }

        initializeFragments()

        mViewModel.marksList.observe(this, Observer { updateMarks() })
        mViewModel.pingList.observe(this, Observer(pingsFragment::update))

        // Setup tabs client for faster loading on slower devices
        setupTabsService()
        checkForExtraIntent()

        checkForQuarterChange()
    }

    override fun onDestroy() {
        super.onDestroy()

        unbindService(mTabsConnection)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode != QuarterManager.REQUEST_STORAGE_ACCESS) {
            return
        }

        if (grantResults.isNotEmpty()) {
            when (grantResults[0]) {
                PackageManager.PERMISSION_GRANTED -> onQuarterStartExport()
                PackageManager.PERMISSION_DENIED -> onQuarterPermissionError()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu) = true.also {
        menuInflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.main_preferences -> openPreferences()
        R.id.main_help -> openHelp()
        else -> false
    }

    fun openCustomTab(url: String) {
        val intent = CustomTabsIntent.Builder(mTabSession)
                .addDefaultShareMenuItem()
                .setShowTitle(true)
                .setToolbarColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setStartAnimations(this, R.anim.slide_in_right, R.anim.slide_out_left)
                .setExitAnimations(this, android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right)
                .build()

        intent.launchUrl(this, Uri.parse(url))
    }

    private fun initializeFragments() {
        // Cycle through all the fragments to initialize the views.
        // The last will be the one shown by default

        switchToFragment(R.id.bottom_bar_marks)
        switchToFragment(R.id.bottom_bar_pings)
    }

    private fun switchToFragment(itemId: Int): Boolean {
        val fragment = when (itemId) {
            R.id.bottom_bar_pings -> pingsFragment
            R.id.bottom_bar_marks -> marksFragment
            else -> null
        } ?: return false

        supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment, fragment)
                .commit()

        when (fragment) {
            is PingsFragment ->
                if (hasSeenPings) {
                    // Delay the update to allow the views to be created
                    Handler().postDelayed({ fragment.update(mViewModel.pingList.value) },  50)
                } else {
                    hasSeenPings = true
                }
            is MarksFragment ->
                if (hasSeenMarks) {
                    // Delay the update to allow the views to be created
                    Handler().postDelayed({ updateMarks() }, 50)
                } else {
                    hasSeenMarks = true
                }
        }

        if (fragment.shouldShowFab()) {
            mFab.show()
        } else {
            mFab.hide()
        }

        mFab.setOnClickListener { fragment.onFabClicked() }

        return true
    }

    private fun openPreferences(): Boolean {
        val intent = Intent(this, PreferencesActivity::class.java)
        startActivity(intent)
        return true
    }

    private fun openHelp(): Boolean {
        val intent = Intent(this, HelpActivity::class.java)
        startActivity(intent)
        return true
    }

    private fun updateMarks() {
        val subjects = mPrefs[PreferenceKeys.KEY_ADDRESS, "0"].toInt().getSubjects(this)
        val overview = mViewModel.getOverviewMarks(subjects)
        marksFragment.update(overview)
    }

    private fun setupTabsService() {
        mTabsConnection = object : CustomTabsServiceConnection() {
            override fun onCustomTabsServiceConnected(name: ComponentName?,
                                                      client: CustomTabsClient?) {
                mTabClient = client
                if (mTabClient == null) {
                    return
                }

                mTabClient!!.warmup(0L)
                mTabSession = mTabClient!!.newSession(null)
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                mTabClient = null
            }
        }

        CustomTabsClient.bindCustomTabsService(this, CUSTOM_TAB_TARGET, mTabsConnection)
    }

    private fun checkForExtraIntent() {
        val ping = intent.getParcelableExtra<Ping>(EXTRA_PING) ?: return
        if (ping.url.isEmpty()) {
            return
        }

        openCustomTab(ping.url)
    }

    private fun showOnBoardingIfNeeded() {
        if (mPrefs[PreferenceKeys.KEY_ONBOARD_COMPLETED, false]) {
            return
        }

        val intent = Intent(this, OnBoardActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun checkForQuarterChange() {
        if (!QuarterManager.shouldDo(this)) {
            return
        }

        mQuarterManager.onShowPrompt = this::onQuarterShowHelper
        mQuarterManager.show()
    }

    private fun onQuarterShowHelper(hasPermission: Boolean) {
        MaterialDialog.Builder(this)
                .title(R.string.marks_quarter_change_title)
                .content(R.string.marks_quarter_change_intro_message)
                .positiveText(R.string.action_proceed)
                .negativeText(R.string.action_dismiss)
                .onPositive { _, _ ->
                    if (!hasPermission) mQuarterManager.requestPermission()
                    else onQuarterStartExport()
                }
                .show()
    }

    private fun onQuarterStartExport() {
        val dialog = MaterialDialog.Builder(this)
                .progress(true, 100)
                .progressIndeterminateStyle(true)
                .content(R.string.marks_quarter_change_export_wip)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .show()

        mQuarterManager.onExport(mViewModel.getMarksStatic(), {
            dialog.dismiss()
            onQuarterPostExport()
        })
    }

    private fun onQuarterPostExport() {
        val progressDialog = MaterialDialog.Builder(this)
                .progress(true, 100)
                .progressIndeterminateStyle(true)
                .content(R.string.marks_quarter_change_delete_wip)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .build()

        val doneDialog = MaterialDialog.Builder(this)
                .title(R.string.marks_quarter_change_title)
                .content(R.string.marks_quarter_change_done)
                .positiveText(android.R.string.ok)
                .build()

        MaterialDialog.Builder(this)
                .title(R.string.marks_quarter_change_title)
                .content(R.string.marks_quarter_change_delete)
                .positiveText(R.string.action_delete)
                .negativeText(android.R.string.no)
                .onPositive { _, _ ->
                    progressDialog.show()
                    mViewModel.deleteAllMarks {
                        progressDialog.dismiss()
                        doneDialog.show()
                    }
                }
                .show()
    }

    private fun onQuarterPermissionError() {
        MaterialDialog.Builder(this)
                .title(R.string.marks_quarter_change_title)
                .content(R.string.marks_quarter_change_permission_error)
                .positiveText(R.string.action_ask_again)
                .negativeText(android.R.string.cancel)
                .onPositive { _, _ -> mQuarterManager.requestPermission() }
                .show()
    }

    companion object {
        const val EXTRA_PING = "extra_parcel_ping"
        private const val CUSTOM_TAB_TARGET = "com.android.chrome"
    }
}

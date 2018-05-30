package it.liceoarzignano.curve

import android.content.ComponentName
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import it.liceoarzignano.curve.notes.NotesFragment
import it.liceoarzignano.curve.pings.CurvePingsFragment
import it.liceoarzignano.curve.preferences.PreferencesActivity
import it.liceoarzignano.foundation.db.entities.Note
import it.liceoarzignano.foundation.db.entities.Ping
import it.liceoarzignano.foundation.extensions.applyTheme
import it.liceoarzignano.foundation.extensions.get
import it.liceoarzignano.foundation.extensions.getAddressName
import it.liceoarzignano.foundation.extensions.getSubjects
import it.liceoarzignano.foundation.help.HelpActivity
import it.liceoarzignano.foundation.marks.MarksFragment
import it.liceoarzignano.foundation.pings.PingsFragment
import it.liceoarzignano.foundation.util.PreferenceKeys
import it.liceoarzignano.foundation.util.QuarterManager

class MainActivity : AppCompatActivity() {
    private lateinit var marksFragment: MarksFragment
    private lateinit var pingsFragment: CurvePingsFragment
    private lateinit var notesFragment: NotesFragment

    private lateinit var mCoordinator: CoordinatorLayout
    private lateinit var mToolbar: Toolbar
    private lateinit var mFab: FloatingActionButton
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mNavigationView: NavigationView

    private lateinit var mViewModel: MainViewModel
    private lateinit var mPrefs: SharedPreferences
    private lateinit var mQuarterManager: QuarterManager

    private var hasSeenPings = false
    private var hasSeenMarks = false
    private var hasSeenNotes = false

    private var mTabsConnection: CustomTabsServiceConnection? = null
    private var mTabClient: CustomTabsClient? = null
    private lateinit var mTabSession: CustomTabsSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        mQuarterManager = QuarterManager(this)

        mViewModel = ViewModelProviders.of(this)[MainViewModel::class.java]
        pingsFragment = CurvePingsFragment()
        marksFragment = MarksFragment()
        notesFragment = NotesFragment()

        setContentView(R.layout.activity_main)

        mToolbar = findViewById(R.id.main_toolbar)
        setSupportActionBar(mToolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu_drawer)
        }

        mCoordinator = findViewById(R.id.main_coordinator)
        mFab = findViewById(R.id.main_fab)
        mDrawerLayout = findViewById(R.id.main_drawer_layout)
        mNavigationView = findViewById(R.id.main_navigation_view)
        mNavigationView.setNavigationItemSelectedListener {
            mDrawerLayout.closeDrawers()

            Handler().postDelayed({ when (it.itemId) {
                R.id.nav_drawer_pings,
                R.id.nav_drawer_marks,
                R.id.nav_drawer_notes -> {
                    switchToFragment(it.itemId)
                    it.isChecked = true
                }
                R.id.nav_drawer_preferences -> {
                    openPreferences()
                    it.isChecked = false
                }
                R.id.nav_drawer_help -> {
                    openHelp()
                    it.isChecked = false
                }
            } }, 200)
            true
        }

        initializeFragments()

        mViewModel.marksList.observe(this, Observer { updateMarks() })
        mViewModel.notesList.observe(this, Observer(notesFragment::update))
        mViewModel.pingList.observe(this, Observer(pingsFragment::update))

        // Setup tabs client for faster loading on slower devices
        setupTabsService()
        checkForExtraIntent()

        checkForQuarterChange()
    }

    override fun onResume() {
        super.onResume()

        setupNavHeader()
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

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            mDrawerLayout.openDrawer(GravityCompat.START)
            true
        }
        else -> super.onOptionsItemSelected(item)
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

    fun deleteNote(note: Note) {
        mViewModel.deleteNote(note)
        Snackbar.make(mCoordinator, R.string.action_deleted, Snackbar.LENGTH_LONG)
                .applyTheme(this)
                .show()
    }

    private fun initializeFragments() {
        // Cycle through all the fragments to initialize the views.
        // The last will be the one shown by default

        switchToFragment(R.id.nav_drawer_notes)
        switchToFragment(R.id.nav_drawer_marks)
        switchToFragment(R.id.nav_drawer_pings)
    }

    private fun switchToFragment(itemId: Int): Boolean {
        val fragment = when (itemId) {
            R.id.nav_drawer_pings -> pingsFragment
            R.id.nav_drawer_marks -> marksFragment
            R.id.nav_drawer_notes -> notesFragment
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
            is NotesFragment ->
                if (hasSeenNotes) {
                        // Delay the update to allow the views to be created
                        Handler().postDelayed({ fragment.update(mViewModel.notesList.value) }, 50)
                    } else {
                        hasSeenNotes = true
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

    private fun setupNavHeader() {
        val header = mNavigationView.getHeaderView(0) ?: return

        val username = header.findViewById<TextView>(R.id.nav_header_user_name)
        val address = header.findViewById<TextView>(R.id.nav_header_address)

        username.text = mPrefs[PreferenceKeys.KEY_USER_NAME,
                getString(R.string.preferences_username_default)]
        address.text = mPrefs[PreferenceKeys.KEY_ADDRESS, "0"].toInt().getAddressName(this)
    }

    private fun checkForExtraIntent() {
        val ping = intent.getParcelableExtra<Ping>(EXTRA_PING) ?: return
        if (ping.url.isEmpty()) {
            return
        }

        openCustomTab(ping.url)
    }

    private fun openPreferences() {
        val intent = Intent(this, PreferencesActivity::class.java)
        startActivity(intent)
    }

    private fun openHelp() {
        val intent = Intent(this, HelpActivity::class.java)
        startActivity(intent)
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

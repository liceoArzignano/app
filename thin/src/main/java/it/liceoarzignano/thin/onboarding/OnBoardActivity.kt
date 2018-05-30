package it.liceoarzignano.thin.onboarding

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IntDef
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.liceoarzignano.foundation.extensions.edit
import it.liceoarzignano.foundation.extensions.set
import it.liceoarzignano.foundation.util.PreferenceKeys
import it.liceoarzignano.thin.MainActivity
import it.liceoarzignano.thin.R

class OnBoardActivity : AppCompatActivity() {
    private lateinit var mQuestionView: TextView
    private lateinit var mAnswerView: TextView
    private lateinit var mBackButtonView: ImageView
    private lateinit var mHintView: TextView

    private lateinit var mAdapter: OnBoardAdapter
    private lateinit var mPrefs: SharedPreferences

    @Status
    private var status = STATUS_HELLO

    override fun onCreate(savedInstance: Bundle?) {
        super.onCreate(savedInstance)

        setContentView(R.layout.activity_onboard)

        mQuestionView = findViewById(R.id.onboard_question)
        mAnswerView = findViewById(R.id.onboard_answer)
        mBackButtonView = findViewById(R.id.onboard_back)
        mHintView = findViewById(R.id.onboard_hint)

        val recyclerView = findViewById<RecyclerView>(R.id.onboard_answer_selector)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.itemAnimator = DefaultItemAnimator()

        mAdapter = OnBoardAdapter(this::onAnswerSelected)
        recyclerView.adapter = mAdapter

        mBackButtonView.setOnClickListener { onBackPressed() }
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this)

        setupHello()
    }

    override fun onBackPressed() {
        when (status) {
            STATUS_HELLO -> super.onBackPressed()
            STATUS_READY -> {
                mAdapter.list = emptyList()
                switchToStatus(STATUS_ADDRESS)
            }
        }
    }

    private fun switchToStatus(@Status newStatus: Int) {
        when (newStatus) {
            STATUS_HELLO -> setupHello()
            STATUS_ADDRESS -> setupAddress()
            STATUS_READY -> setupReady()
            else -> throw IllegalArgumentException("$newStatus is not a valid status!")
        }
        status = newStatus
    }

    private fun setupHello() {
        clearConversation()

        val question = getString(R.string.onboard_hello_question)
        val answers = listOf(getString(R.string.onboard_hello_answer_a),
                getString(R.string.onboard_hello_answer_b))
        val hint = getString(R.string.onboard_reply_hint_a)
        postQuestion(question, answers, hint)
    }

    private fun setupAddress() {
        clearConversation()
        hideBackButton()

        val question = getString(R.string.onboard_address_question)
        val answers = resources.getStringArray(R.array.addresses).toList()
        postQuestion(question, answers)
    }

    private fun setupReady() {
        clearConversation()
        showBackButton()

        val question = getString(R.string.onboard_ready_question)
        val answers = listOf(getString(R.string.onboard_ready_answer))
        val hint = getString(R.string.onboard_reply_hint_b)
        postQuestion(question, answers, hint)
    }

    private fun postQuestion(question: String, answers: List<String>, hint: String = "") {
        Handler().postDelayed({ mQuestionView.text =  question }, 150)
        Handler().postDelayed({ mQuestionView.visibility = View.VISIBLE }, 500)
        Handler().postDelayed({ mAdapter.list = answers }, 1250)
        if (hint.isNotEmpty()) {
            Handler().postDelayed({
                mHintView.text = hint
                mHintView.visibility = View.VISIBLE
            }, 1000)
        }
    }

    private fun postAnswer(answer: String, result: Int) {
        mAnswerView.text = answer
        mAnswerView.visibility = View.VISIBLE

        Handler().postDelayed({ handleAnswer(result) }, 1500)
    }

    private fun onAnswerSelected(answer: String, result: Int) {
        mAdapter.list = emptyList()
        Handler().postDelayed({ postAnswer(answer, result) }, 500)
    }

    private fun handleAnswer(result: Int) {
        when (status) {
            STATUS_HELLO -> switchToStatus(STATUS_ADDRESS)
            STATUS_ADDRESS -> {
                mPrefs.edit { editor -> editor[PreferenceKeys.KEY_ADDRESS] = result.toString() }
                switchToStatus(STATUS_READY)
            }
            STATUS_READY -> {
                mPrefs.edit { editor -> editor[PreferenceKeys.KEY_ONBOARD_COMPLETED] = true }
                clearConversation()
                Handler().postDelayed(this::finishOnBoard, 750)
            }
        }
    }

    private fun clearConversation() {
        mAnswerView.visibility = View.GONE
        mQuestionView.visibility = View.GONE
        mHintView.visibility = View.GONE
    }

    private fun showBackButton() {
        mBackButtonView.alpha = 0f
        mBackButtonView.visibility = View.VISIBLE
        mBackButtonView.animate()
                .alpha(1f)
                .withEndAction {
                    mBackButtonView.isClickable = true
                    mBackButtonView.isFocusable = true
                }
                .start()
    }

    private fun hideBackButton() {
        if (mBackButtonView.visibility == View.GONE) {
            return
        }

        mBackButtonView.alpha = 1f
        mBackButtonView.animate()
                .alpha(0f)
                .withEndAction {
                    mBackButtonView.visibility = View.GONE
                    mBackButtonView.isClickable = false
                    mBackButtonView.isFocusable = false
                }
                .start()
    }

    private fun finishOnBoard() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object {

        @IntDef(STATUS_HELLO, STATUS_ADDRESS, STATUS_READY)
        @Retention(AnnotationRetention.SOURCE)
        annotation class Status
        private const val STATUS_HELLO = 0
        private const val STATUS_ADDRESS = 1
        private const val STATUS_READY = 2

    }
}

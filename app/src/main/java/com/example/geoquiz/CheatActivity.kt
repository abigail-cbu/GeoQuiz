package com.example.geoquiz

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders

const val EXTRA_ANSWER_SHOWN = "com.bignerdranch.android.geoquiz.answer_shown"
private const val EXTRA_ANSWER_IS_TRUE = "com.bignerdranch.android.geoquiz.answer_is_true"
private const val TAG = "CheatActivity"

class CheatActivity : AppCompatActivity() {

    private lateinit var answerTextView: TextView
    private lateinit var showAnswerButton: Button
    private lateinit var apiTextView: TextView

    // private var answerIsTrue = false


    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProviders.of(this).get(QuizViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)

        quizViewModel.cheaterAnswer = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)

        answerTextView = findViewById(R.id.answer_text_view)
        apiTextView = findViewById(R.id.api_text_view)
        showAnswerButton = findViewById(R.id.show_answer_button)
        showAnswerButton.setOnClickListener {
            //quizViewModel.isCheater = true
            quizViewModel.setCheated(true) // Ch. 6: Tracking cheat status by question
            updateCheatUI()
        }

        updateCheatUI()
        setAPI()
    }

    private fun setAnswerShownResult(isAnswerShown: Boolean) {
        val data = Intent().apply {
            putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown)
        }
        setResult(Activity.RESULT_OK, data)
    }

    companion object {
        fun newIntent(packageContext: Context, answerIsTrue: Boolean) : Intent {
            return Intent(packageContext, CheatActivity::class.java).apply {
                putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
            }
        }
    }

    // Ch. 6: Closing Loopholes for Cheaters
    private fun updateCheatUI() {

        val answerText = when {
            quizViewModel.cheaterAnswer -> R.string.true_button
            else -> R.string.false_button
        }

        if (quizViewModel.currentQuestionCheated) {
            answerTextView.setText(answerText)
            setAnswerShownResult(true)
        }
    }

    // Ch. 7: Reporting the Device's Android Version
    private fun setAPI() {
        Log.d(TAG, Build.VERSION.SDK_INT.toString())
        var api = "API Level " + Build.VERSION.SDK_INT.toString()
        apiTextView.setText(api)
    }
}

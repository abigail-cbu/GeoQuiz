package com.example.geoquiz

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModelProviders

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
private const val REQUEST_CODE_CHEAT = 0

class MainActivity : AppCompatActivity() {

    // lateinit means you will provide a non-null View value before use
    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: ImageButton // Ch. 2 Challenge: Button --> ImageButton
    private lateinit var prevButton: ImageButton
    private lateinit var cheatButton: Button
    private lateinit var questionTextView: TextView
    private lateinit var cheatTokenTextView: TextView

    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProviders.of(this).get(QuizViewModel::class.java)
    }

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        setContentView(R.layout.activity_main)

        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        quizViewModel.currentIndex = currentIndex

        trueButton          = findViewById(R.id.true_button)
        falseButton         = findViewById(R.id.false_button)
        nextButton          = findViewById(R.id.next_button)
        prevButton          = findViewById(R.id.prev_button)
        cheatButton         = findViewById(R.id.cheat_button)
        questionTextView    = findViewById(R.id.question_text_view)
        cheatTokenTextView  = findViewById(R.id.cheat_token_text_view)

        trueButton.setOnClickListener { view: View ->
            checkAnswer(true)

        }

        falseButton.setOnClickListener { view: View ->
            checkAnswer(false)
        }

        nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
        }

        // Ch. 2 Challenge: Add a Listener to TextView to see NEXT question
        questionTextView.setOnClickListener { view: View ->
            quizViewModel.moveToNext()
            updateQuestion()
        }

        // Ch. 2 Challenge: Add Prev Button
        prevButton.setOnClickListener {
            quizViewModel.moveToPrev()
            updateQuestion()
        }

        cheatButton.setOnClickListener { view ->
            // Start Cheat Activity
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                val options =
                    ActivityOptions.makeClipRevealAnimation(view, 0, 0, view.width, view.height)
                startActivityForResult(intent, REQUEST_CODE_CHEAT, options.toBundle())
            }
            else {
                startActivityForResult(intent, REQUEST_CODE_CHEAT)
            }
        }

        updateQuestion()
        setCheatTokens()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        if (requestCode == REQUEST_CODE_CHEAT) {
            // Ch. 6: Tracking cheat status by question
            quizViewModel.setCheated(data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false)
        }

        setCheatTokens()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.i(TAG, "onSaveInstanceState")
        savedInstanceState.putInt(KEY_INDEX, quizViewModel.currentIndex)
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        questionTextView.setText(questionTextResId)

        disableBtns()
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer

        quizViewModel.setQuestion(userAnswer == correctAnswer) // Ch. 3: Prevent Repeat Answers

//        val messageResId =
//            if (userAnswer == correctAnswer) {
//                R.string.correct_toast
//            } else {
//                R.string.incorrect_toast
//            }

        val messageResId = when {
            quizViewModel.currentQuestionCheated -> R.string.judgment_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }

        val toast = Toast.makeText(this, messageResId, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.TOP, 0, 0) // CH. 1 Challenge
        toast.show()

        disableBtns()

        if (quizViewModel.isQuizDone) {
            gradeQuiz()
        }
    }

    private fun disableBtns() {
        // Ch. 3: Preventing Repeat Answers
        trueButton.isEnabled = !quizViewModel.questionDone
        falseButton.isEnabled = !quizViewModel.questionDone
    }

    // Ch. 3: Graded Quiz
    private fun gradeQuiz() {
        var message = "Grade: " + quizViewModel.grade.toString() + "%"
        val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast.show()

    }

    // Ch. 7: Limited Cheats
    private fun setCheatTokens() {
        var tokenText = "Cheat Tokens Remaining: " + quizViewModel.cheatTokens.toString()
        cheatTokenTextView.setText(tokenText)

        cheatButton.isEnabled = quizViewModel.cheatTokens > 0

    }
}

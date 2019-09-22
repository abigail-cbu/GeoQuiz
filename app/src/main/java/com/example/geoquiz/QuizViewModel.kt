package com.example.geoquiz

import android.util.Log
import androidx.lifecycle.ViewModel

private const val TAG = "QuizViewModel"
private const val TOKENS = 3

class QuizViewModel : ViewModel() {
    var currentIndex = 0
    //var isCheater = false

    var cheaterAnswer = false

    var cheatTokens = TOKENS

    private var numOfCorrect : Double = 0.0

    var numQuestionsDone : Int = 0

    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )

    // below is the business logic
    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    // Ch. 6: Tracking cheat status by question
    val currentQuestionCheated: Boolean
        get() = questionBank[currentIndex].cheated

    val size: Number
        get() = questionBank.size

    val questionDone: Boolean
        get() = questionBank[currentIndex].done

    val isQuizDone: Boolean
        get() = numQuestionsDone == questionBank.size

    val grade: Double
        get() = (numOfCorrect / questionBank.size) * 100

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    fun moveToPrev() {
        currentIndex = (currentIndex - 1)
        if (currentIndex == -1)
            currentIndex = questionBank.size - 1
    }

    fun setQuestion(correct: Boolean) {
        questionBank[currentIndex].done = true
        numQuestionsDone++
        if (correct) {
            numOfCorrect++
//            var x = numOfCorrect.div(questionBank.size)
//            var y = x.times(100)
//            //grade = numOfCorrect.toBigDecimal()
            Log.d(TAG, "numOfCorrect: " + numOfCorrect)
        }
    }

    fun setCheated(cheated: Boolean) {
        questionBank[currentIndex].cheated = cheated
        cheatTokens--
    }
}
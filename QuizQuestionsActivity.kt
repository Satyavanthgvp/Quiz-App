package com.example.quizapp

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class QuizQuestionsActivity : AppCompatActivity() {
    private var userName: String? = null
    private lateinit var questionsList: ArrayList<Question>
    private var currentQuestionIndex = 0
    private var selectedAlternativeIndex = -1
    private var isAnswerChecked = false
    private var totalScore = 0

    private var tvQuestion: TextView? = null
    private var ivImage: ImageView? = null
    private var progressBar: ProgressBar? = null
    private var tvProgress: TextView? = null
    private var btnSubmit: Button? = null
    private var tvAlternatives: List<TextView>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_questions)

        userName = intent.getStringExtra(Constants.USER_NAME)
        tvQuestion = findViewById(R.id.tvQuestion)
        ivImage = findViewById(R.id.ivImage)
        tvProgress = findViewById(R.id.tvProgress)
        btnSubmit = findViewById(R.id.btnSubmit)
        tvAlternatives = listOf(
            findViewById(R.id.optionOne),
            findViewById(R.id.optionTwo),
            findViewById(R.id.optionThree),
            findViewById(R.id.optionFour)
        )

        // Load questions asynchronously
        lifecycleScope.launch {
            questionsList = Constants.getQuestions()
            updateQuestion()
        }

        // Set up listeners
        btnSubmit?.setOnClickListener {
            handleAnswerSubmission()
        }

        tvAlternatives?.forEachIndexed { index, textView ->
            textView.setOnClickListener {
                if (!isAnswerChecked) {
                    selectedAlternativeView(textView, index)
                }
            }
        }
    }

    private fun handleAnswerSubmission() {
        if (!isAnswerChecked) {
            if (selectedAlternativeIndex == -1) {
                Toast.makeText(this, "Please select an option", Toast.LENGTH_SHORT).show()
                return
            }

            checkAnswer()

            btnSubmit?.text = if (currentQuestionIndex == questionsList.size - 1) "FINISH" else "GO TO NEXT QUESTION"
            isAnswerChecked = true
        } else {
            goToNextQuestion()
        }
    }

    private fun checkAnswer() {
        val correctAnswerIndex = questionsList[currentQuestionIndex].correctAnswerIndex
        val selectedView = tvAlternatives!![selectedAlternativeIndex]

        if (selectedAlternativeIndex == correctAnswerIndex) {
            answerView(selectedView, R.drawable.correct_option_border_bg)
            totalScore++
        } else {
            answerView(selectedView, R.drawable.wrong_option_border_bg)
            answerView(tvAlternatives!![correctAnswerIndex], R.drawable.correct_option_border_bg)
        }
    }

    private fun goToNextQuestion() {
        if (currentQuestionIndex < questionsList.size - 1) {
            currentQuestionIndex++
            updateQuestion()
        } else {
            finishQuiz()
        }
        isAnswerChecked = false
    }

    private fun updateQuestion() {
        defaultAlternativesView()
        selectedAlternativeIndex = -1 // Reset selected alternative index
        tvQuestion?.text = questionsList[currentQuestionIndex].questionText
        tvProgress?.text = "${currentQuestionIndex + 1}/${questionsList.size}"
        tvAlternatives?.forEachIndexed { index, textView ->
            textView.text = questionsList[currentQuestionIndex].alternatives[index]
        }
        btnSubmit?.text = "SUBMIT THIS ANSWER"
    }

    private fun defaultAlternativesView() {
        tvAlternatives?.forEach { textView ->
            textView.typeface = Typeface.DEFAULT
            textView.setTextColor(Color.parseColor("#7A8089"))
            textView.background = ContextCompat.getDrawable(this, R.drawable.default_option_border_bg)
        }
    }

    private fun selectedAlternativeView(option: TextView, index: Int) {
        defaultAlternativesView()
        selectedAlternativeIndex = index
        option.setTextColor(Color.parseColor("#363A43"))
        option.setTypeface(option.typeface, Typeface.BOLD)
        option.background = ContextCompat.getDrawable(this, R.drawable.selected_option_border_bg)
    }

    private fun answerView(view: TextView, drawableId: Int) {
        view.background = ContextCompat.getDrawable(this, drawableId)
        view.setTextColor(Color.WHITE)
    }

    private fun finishQuiz() {
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra(Constants.USER_NAME, userName)
            putExtra(Constants.TOTAL_QUESTIONS, questionsList.size)
            putExtra(Constants.SCORE, totalScore)
        }
        startActivity(intent)
        finish()
    }
}
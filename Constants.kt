package com.example.quizapp

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

data class Question(
    val id: Int,
    val questionText: String,
    val alternatives: ArrayList<String>,
    val correctAnswerIndex: Int,
)

object Constants {
    val USER_NAME: String = "user_name"
    val TOTAL_QUESTIONS: String = "total_questions"
    val SCORE: String = "score"

    // This function should be called from a coroutine or another asynchronous context
    suspend fun getQuestions(): ArrayList<Question> {
        return withContext(Dispatchers.IO) {  // Use the IO dispatcher for network operations
            val questionsList = ArrayList<Question>()

            val url = "https://raw.githubusercontent.com/Satyavanthgvp/AICTE-kotlin/main/data.json"

            // Fetch data from URL
            val jsonString = try {
                URL(url).readText()
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext arrayListOf<Question>() // Return an empty list in case of an error
            }

            // Parse JSON using Gson
            val dataList = parseJson(jsonString)

            // Use dataList to create Question objects and add them to questionsList
            for (data in dataList) {
                val question = Question(
                    data.id,
                    data.questionText,
                    data.alternatives,
                    data.correctAnswerIndex,
                )
                questionsList.add(question)
            }

            questionsList

        }
    }

    private fun parseJson(jsonString: String): List<Question> {
        val gson = Gson()
        return gson.fromJson(jsonString, Array<Question>::class.java).toList()
    }
}
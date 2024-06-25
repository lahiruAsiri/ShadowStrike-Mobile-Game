package com.example.ShadowStrike

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GameOver : AppCompatActivity() {
    private lateinit var tvPoints: TextView
    private lateinit var tvHighest: TextView
    private lateinit var ivNewHighest: ImageView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)

        tvPoints = findViewById(R.id.tvPoints)
        tvHighest = findViewById(R.id.tvHighest)
        ivNewHighest = findViewById(R.id.ivNewHighest)
        sharedPreferences = getSharedPreferences("my_pref", MODE_PRIVATE)

        var highest = sharedPreferences.getInt("highest", 0)

        val points = intent.getIntExtra("points", 0)

        tvPoints.text = points.toString()

        if (points > highest) {
            ivNewHighest.visibility = View.VISIBLE

            highest = points

            val editor = sharedPreferences.edit()
            editor.putInt("highest", highest)
            editor.apply()
        }

        tvHighest.text = highest.toString()
    }

    fun restart(view: View) {
        val intent = Intent(this@GameOver, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun exit(view: View) {
        finish()
    }
}
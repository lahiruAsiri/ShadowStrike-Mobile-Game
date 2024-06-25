package com.example.ShadowStrike

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Rect
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import java.util.Random

class GameView(private var context: Context) : View(context){
    var backgrund: Bitmap
    var ground: Bitmap
    var ninja: Bitmap
    var rectBackground: Rect
    var rectGround: Rect
    private var handler: Handler
    val UPDATE_MILLIS: Long = 30
    var runnable: Runnable
    var textPaint = Paint()
    var healthPaint = Paint()
    var TEXT_SIZE = 120f
    var points = 0
    var life = 3
    var random: Random
    var ninjaX: Float
    var ninjaY: Float
    var oldX = 0f
    var oldninjaX = 0f
    var spikes: ArrayList<spike>
    var explosions: ArrayList<Explosion>

    fun getGameHandler(): Handler {
        return handler
    }
    fun getGameContext(): Context {
        return context
    }

    init {
        backgrund = BitmapFactory.decodeResource(resources, R.drawable.background)
        ground = BitmapFactory.decodeResource(resources, R.drawable.ground)
        ninja = BitmapFactory.decodeResource(resources, R.drawable.ninja)
        val display = (getContext() as Activity).windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        dWidth = size.x
        dHeight = size.y
        rectBackground = Rect(0, 0, dWidth, dHeight)
        rectGround = Rect(0, dHeight - ground.height, dWidth, dHeight)
        handler = Handler()
        runnable = Runnable { invalidate() }
        textPaint.color = Color.rgb(225, 165, 0)
        textPaint.textSize = TEXT_SIZE
        textPaint.textAlign = Paint.Align.LEFT
        textPaint.setTypeface(ResourcesCompat.getFont(context, R.font.asd))
        healthPaint.color = Color.GREEN
        random = Random()
        ninjaX = (dWidth / 2 - ninja.width / 2).toFloat()
        ninjaY = (dHeight - ground.height - ninja.height).toFloat()
        spikes = ArrayList()
        explosions = ArrayList()
        for (i in 0..2) {
            val Spike = spike(context)
            spikes.add(Spike)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(backgrund, null, rectBackground, null)
        canvas.drawBitmap(ground, null, rectGround, null)
        canvas.drawBitmap(ninja, ninjaX, ninjaY, null)
        for (i in spikes.indices) {
            canvas.drawBitmap(
                spikes[i].getspike(spikes[i].spikeFrame)!!,
                spikes[i].spikeX.toFloat(),
                spikes[i].spikeY.toFloat(),
                null
            )
            spikes[i].spikeFrame++
            if (spikes[i].spikeFrame > 2) {
                spikes[i].spikeFrame = 0
            }
            spikes[i].spikeY += spikes[i].spikeVelocity
            if (spikes[i].spikeY + spikes[i].spikeHeight >= dHeight - ground.height) {
                points += 10
                val explosion = Explosion(context)
                explosion.explosionX = spikes[i].spikeX
                explosion.explosionY = spikes[i].spikeY
                explosions.add(explosion)
                spikes[i].resetPosition()
            }

        }
        val iterator = spikes.iterator()
        while (iterator.hasNext()) {
            val spike = iterator.next()
            if (spike.spikeX + spike.spikeWidth >= ninjaX &&
                spike.spikeX <= ninjaX + ninja.width &&
                spike.spikeY + spike.spikeHeight >= ninjaY &&
                spike.spikeY + spike.spikeHeight <= ninjaY + ninja.height) {
                life--
                spike.resetPosition()
                iterator.remove()
                if (life == 0) {
                    val intent = Intent(context, GameOver::class.java)
                    intent.putExtra("points", points)
                    context.startActivity(intent)
                    (context as Activity).finish()
                    break
                }
            }
        }

        val iterator1 = explosions.iterator()
        while (iterator1.hasNext()) {
            val explosion = iterator1.next()
            val explosionBitmap = explosion.getExplosion(explosion.explosionFrame)
            if (explosionBitmap != null) {
                canvas.drawBitmap(
                    explosionBitmap,
                    explosion.explosionX.toFloat(),
                    explosion.explosionY.toFloat(),
                    null
                )
            }

            explosion.explosionFrame++
            if (explosion.explosionFrame > 3) {
                iterator1.remove()
            }
        }

        if (life == 2) {
            healthPaint.color = Color.YELLOW
        } else if (life == 1) {
            healthPaint.color = Color.RED
        }
        canvas.drawRect(
            (dWidth - 200).toFloat(),
            30f,
            (dWidth - 200 + 60 * life).toFloat(),
            80f,
            healthPaint
        )
        canvas.drawText("" + points, 20f, TEXT_SIZE, textPaint)
        handler.postDelayed(runnable, UPDATE_MILLIS)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y
        if (touchY >= ninjaY) {
            val action = event.action
            if (action == MotionEvent.ACTION_DOWN) {
                oldX = event.x
                oldninjaX = ninjaX
            }
            if (action == MotionEvent.ACTION_MOVE) {
                val shift = oldX - touchX
                val newshipX = oldninjaX - shift
                ninjaX =
                    if (newshipX <= 0) 0f else if (newshipX >= dWidth - ninja.width) (dWidth - ninja.width).toFloat() else newshipX
            }
        }
        return true
    }

    companion object {
        var dWidth: Int = 0
        var dHeight: Int = 0
    }
}

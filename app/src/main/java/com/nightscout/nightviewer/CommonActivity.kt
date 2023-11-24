package com.nightscout.nightviewer

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.method.LinkMovementMethod
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat

open class CommonActivity : AppCompatActivity() {

    var updateTimer: CountDownTimer? = null
    lateinit var fullscreenContent: ConstraintLayout
    val hideHandler = Handler(Looper.myLooper()!!)
    var isFullscreen: Boolean = false

    val internetBroadcaster = InternetBroadcaster()

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */

    @SuppressLint("ClickableViewAccessibility")

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.menu_preference -> {
                val i = Intent(this, PreferencesActivity::class.java)
                startActivity(i)

                return true
            }
            R.id.menu_about -> {
                val dialogView = LayoutInflater.from(this).inflate(R.layout.menu_about_layout, null)
                val messageTextView = dialogView.findViewById<TextView>(R.id.about_message)
                messageTextView.movementMethod = LinkMovementMethod.getInstance()

                val about_message: AlertDialog = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogTheme))
                    .setPositiveButton("Thank you", null)
                    .setIcon(R.mipmap.ic_main_round)
                    .setTitle(R.string.about_title)
                    .setView(dialogView)
                    .create()
                about_message.show()

                val positiveButton: Button = about_message.getButton(AlertDialog.BUTTON_POSITIVE)
                positiveButton.setTextColor(Color.parseColor("#000000"))

                (about_message.findViewById(android.R.id.message) as TextView).movementMethod = LinkMovementMethod.getInstance()

                val htmlMessage = HtmlCompat.fromHtml(getString(R.string.menu_about_message), HtmlCompat.FROM_HTML_MODE_LEGACY)
                messageTextView.text = htmlMessage
                messageTextView.movementMethod = LinkMovementMethod.getInstance()

                return true
            }
            R.id.menu_exit -> {
                onBackPressed()
                return true
            }
            else -> return false
        }
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onOptionsMenuClosed(menu: Menu?) {
        super.onOptionsMenuClosed(menu)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
    }

    fun toggle() {
        if (isFullscreen) {
            hide()
        } else {
            show()
        }
    }

    fun hide() {

        // Hide UI first
        supportActionBar?.hide()
        //fullscreenContentControls.visibility = View.GONE
        isFullscreen = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        hideHandler.removeCallbacks(showPart2Runnable)
        hideHandler.postDelayed(hidePart2Runnable, UI_ANIMATION_DELAY.toLong())

    }

    fun show() {

        // Show the system bar
        if (Build.VERSION.SDK_INT >= 30) {
            fullscreenContent.windowInsetsController?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            fullscreenContent.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }
        isFullscreen = true

        // Schedule a runnable to display UI elements after a delay
        hideHandler.removeCallbacks(hidePart2Runnable)
        hideHandler.postDelayed(showPart2Runnable, UI_ANIMATION_DELAY.toLong())

    }


    @SuppressLint("InlinedApi")
    val hidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar
        if (Build.VERSION.SDK_INT >= 30) {
            fullscreenContent.windowInsetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            fullscreenContent.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
    }
    private val showPart2Runnable = Runnable {
        // Delayed display of UI elements
        supportActionBar?.show()
    }
    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private const val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private const val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private const val UI_ANIMATION_DELAY = 300
    }
}
package com.huyduc1108.clock

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    var seconds: Long = 0
    var isStop: Boolean = true
    var isRefresh: Boolean = false
    lateinit var thread: Thread
    private val handler: Handler by lazy {
        object: Handler(Looper.getMainLooper()){
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when(msg.what){
                    UPDATE_UI -> {
                        Log.e( "handleMessage: ", "$seconds")
                        setText((msg.obj as Long)*10)
                    }
                }

            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setText(seconds)
        btn_start.setOnClickListener {
            if (isRefresh) {
                refresh()
            } else {
                isStop = false
                btn_stop.visibility = View.VISIBLE
                btn_start.text = "Làm mới"
                isRefresh = true;
//                Handler().postDelayed({
                    run().start()
//                }, 10)

            }
        }

        btn_stop.setOnClickListener {
            isStop = !isStop
            if (!isStop) {
                btn_stop.text = "Dừng lại"
                thread.start()
            } else {
                btn_stop.text = "Tiếp tục"
            }
        }
    }

    private fun setText(milliseconds: Long) {
        val hms = String.format(
            "%02d:%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(milliseconds),
            TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(
                    milliseconds
                )
            ),
            TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(
                    milliseconds
                )
            ),
            (milliseconds - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(milliseconds)))/10
        )
        tv_time.text = hms
    }

    private fun run() : Thread {
        thread = Thread{
            while (!isStop){
                seconds++
                val message = Message()
                message.what = UPDATE_UI
                message.obj = seconds
                handler.sendMessage(message)
                Thread.sleep(10)
            }
        }
        return thread
    }

    private fun refresh() {
        btn_stop.visibility = View.GONE
        btn_start.text = "Bắt đầu"
        isRefresh = false
        isStop = true
        seconds = 0
        val message = Message()
        message.what = UPDATE_UI
        message.obj = seconds
        handler.sendMessage(message)
    }
    companion object{
        const val UPDATE_UI = 1
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        thread.interrupt()
        super.onDestroy()
    }
}
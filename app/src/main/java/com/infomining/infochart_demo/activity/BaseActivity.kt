package com.infomining.infochart_demo.activity

import androidx.appcompat.app.AppCompatActivity
import com.infomining.infochart_demo.R

abstract class BaseActivity: AppCompatActivity() {

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity)
    }

}
package com.stripedemo.utils

import android.app.Application
import com.stripe.android.PaymentConfiguration
import com.stripedemo.R

class StripeDemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        PaymentConfiguration.init(applicationContext,resources.getString(R.string.stripe_publishable_key))

    }
}
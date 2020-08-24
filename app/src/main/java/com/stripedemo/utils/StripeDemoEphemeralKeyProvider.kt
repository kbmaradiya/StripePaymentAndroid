package com.stripedemo.utils

import androidx.annotation.Size
import com.stripe.android.EphemeralKeyProvider
import com.stripe.android.EphemeralKeyUpdateListener


class StripeDemoEphemeralKeyProvider(private val ephemeralKeyRawJson: String) : EphemeralKeyProvider {

    override fun createEphemeralKey(
        @Size(min = 4) apiVersion: String,
        keyUpdateListener: EphemeralKeyUpdateListener
    ) {

        keyUpdateListener.onKeyUpdate(ephemeralKeyRawJson)

    }
}

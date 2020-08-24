package com.stripedemo.utils

import android.content.Context
import com.stripe.Stripe
import com.stripe.android.CustomerSession
import com.stripe.model.Customer
import com.stripe.model.EphemeralKey
import com.stripe.net.RequestOptions
import com.stripe.param.CustomerCreateParams





fun initializeCustomerSession(context: Context?, EphemeralKeyRawJson:String){

    context?.let {context->

        CustomerSession.initCustomerSession(
            context,
            StripeDemoEphemeralKeyProvider(EphemeralKeyRawJson)
        )
    }

}


fun endCustomerSession(){
    CustomerSession.endCustomerSession()
}



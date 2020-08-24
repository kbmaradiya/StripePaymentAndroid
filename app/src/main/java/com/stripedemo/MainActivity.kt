package com.stripedemo


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.stripe.android.*
import com.stripe.android.model.ConfirmPaymentIntentParams
import com.stripe.android.model.StripeIntent
import com.stripe.android.view.PaymentMethodsActivityStarter
import com.stripedemo.utils.endCustomerSession
import com.stripedemo.utils.initializeCustomerSession


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private  var stripe: Stripe?=null
    private val TAG:String="MainActivity"
    private  var paymentMethodId:String?=null
    private  val clientSecret:String?=null
    private  var paymentSession: PaymentSession?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnAddCard).setOnClickListener(this)
        findViewById<Button>(R.id.btnMakePayment).setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnAddCard->{

                initializeCustomerSession(this,"")
                PaymentMethodsActivityStarter(this)
                    .startForResult(
                        PaymentMethodsActivityStarter.Args.Builder()
                            .build()
                    )
            }
            R.id.btnMakePayment->{

                makePayment()

            }
        }
    }

    private fun makePayment() {
        // need to call api to get client secret key (you need to pass amount, currency, customer id to get client secret key)
        initializeCustomerSession(this,"")
        paymentSession = PaymentSession(
            this,
            createPaymentSessionConfig()
        )

        paymentSession?.init(createPaymentSessionListener())

        PaymentMethodsActivityStarter(this)
            .startForResult(
                PaymentMethodsActivityStarter.Args.Builder()
                    .build()
            )
    }

    private fun createPaymentSessionListener(): PaymentSession.PaymentSessionListener {
        return object : PaymentSession.PaymentSessionListener {
            override fun onCommunicatingStateChanged(isCommunicating: Boolean) {
                Log.e(TAG,"onCommunicatingStateChanged "+isCommunicating)
            }

            override fun onError(errorCode: Int, errorMessage: String) {
                Log.e(TAG,"onError "+errorCode+"  "+errorMessage)
            }

            override fun onPaymentSessionDataChanged(data: PaymentSessionData) {
                Log.e(TAG,"onPaymentSessionDataChanged "+data.isPaymentReadyToCharge+"  "+data)

                if (data.isPaymentReadyToCharge){
                    paymentMethodId?.let {
                        clientSecret?.let { it1 ->
                            confirmPayment(
                                it1, it
                            )
                        }
                    }
                }
            }
        }
    }

    private fun createPaymentSessionConfig(): PaymentSessionConfig {
        return PaymentSessionConfig.Builder()
            .setShippingMethodsRequired(false)
            .setShippingInfoRequired(false)
            .build()
    }

    private fun confirmPayment(
        clientSecret: String,
        paymentMethodId: String
    ) {

        stripe = Stripe(
            applicationContext,
            PaymentConfiguration.getInstance(this).publishableKey)

        stripe?.confirmPayment(
            this,
            ConfirmPaymentIntentParams.createWithPaymentMethodId(
                paymentMethodId,
                clientSecret
            )
        )

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PaymentMethodsActivityStarter.REQUEST_CODE) {

            data?.let {
                val result = PaymentMethodsActivityStarter.Result.fromIntent(data)

                val paymentMethod = result?.paymentMethod
                Log.e(TAG, "paymentMethodId : " + paymentMethod?.id)
                paymentMethodId = paymentMethod?.id.toString()
                paymentSession?.handlePaymentData(requestCode, resultCode, it)
            }
        }else {
            stripe?.onPaymentResult(
                requestCode,
                data,
                object : ApiResultCallback<PaymentIntentResult> {
                    override fun onSuccess(result: PaymentIntentResult) {
                        val paymentIntent = result.intent
                        when (paymentIntent.status) {
                            StripeIntent.Status.Succeeded -> {
                                Log.e(TAG, "Payment Success")
                            }
                            StripeIntent.Status.RequiresPaymentMethod -> {
                                Log.e(
                                    TAG,
                                    "Payment Failed " + paymentIntent.lastPaymentError?.message
                                )
                            }
                            else -> {
                                Log.e(TAG, "Payment status unknown " + paymentIntent.status)

                            }
                        }
                    }

                    override fun onError(e: Exception) {
                        Log.e(TAG, "Payment Error " + e.localizedMessage)
                    }
                })
        }
    }
}

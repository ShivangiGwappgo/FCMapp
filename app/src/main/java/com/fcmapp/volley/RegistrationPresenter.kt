package com.example.mvpwithvolley.presentationlayer.registration

import com.fcmapp.MyRequest
import com.fcmapp.interfaces.OperationalCallBack
import com.fcmapp.volley.VolleyHandler


class RegistrationPresenter(
    private val volleyHandler: VolleyHandler,
    private val registrationView: RegistrationMVP.RegistrationView
) : RegistrationMVP.RegistrationPresenter {

    override fun base64Upload(user: MyRequest): String {

        registrationView.onLoad(true)
        val message = volleyHandler.base64Upload(user, object : OperationalCallBack {
            override fun onSuccess(message: String) {
                registrationView.onLoad(false)
                registrationView.setResult(message)
            }

            override fun onFailure(message: String) {
                registrationView.onLoad(false)
                registrationView.setResult(message)
            }
        })
        return message ?: DEFAULT_MESSAGE
    }

    companion object {
        const val DEFAULT_MESSAGE = "default message"
    }
}
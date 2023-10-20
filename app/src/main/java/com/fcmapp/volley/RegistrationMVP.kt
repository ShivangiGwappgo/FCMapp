package com.example.mvpwithvolley.presentationlayer.registration

import com.fcmapp.MyRequest


class RegistrationMVP {

    interface RegistrationView {
        fun setResult(message: String)
        fun onLoad(isLoading: Boolean)
    }

    interface RegistrationPresenter {
        fun base64Upload(user: MyRequest): String
    }
}
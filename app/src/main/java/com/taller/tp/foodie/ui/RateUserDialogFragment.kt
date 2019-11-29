package com.taller.tp.foodie.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.User
import kotlinx.android.synthetic.main.fragment_rate_user.*


interface RateUserListener {
    fun onFinishRateUser(userRated: User, rating: Int)
}

class RateUserDialogFragment : DialogFragment() {

    private lateinit var listener: RateUserListener
    private lateinit var userRated: User

    fun RateUserDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    companion object {
        fun newInstance(userRated: User, listener: RateUserListener): RateUserDialogFragment? {
            val frag = RateUserDialogFragment()
            frag.userRated = userRated
            frag.listener = listener
            return frag
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_rate_user, container)
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_rate_user.setOnClickListener {
            this.listener.onFinishRateUser(userRated, rating.rating.toInt())
            dismiss()
        }
    }
}
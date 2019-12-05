package com.taller.tp.foodie.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.UserProfile
import com.taller.tp.foodie.model.common.UserBackendDataHandler
import com.taller.tp.foodie.model.requestHandlers.CleanFcmTokenRequestHandler
import com.taller.tp.foodie.model.requestHandlers.GetUserProfileRequestHandler
import com.taller.tp.foodie.services.ProfileService
import com.taller.tp.foodie.services.UserService
import kotlinx.android.synthetic.main.fragment_profile.*
import java.lang.ref.WeakReference

class ProfileFragment : Fragment() {

    companion object {
        fun newInstance(): ProfileFragment = ProfileFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        registerForContextMenu(profile_settings)
        profile_settings.setOnClickListener {
            activity?.openContextMenu(it)
        }

        getUserProfile()
    }

    private fun getUserProfile() {
        ProfileService(
            GetUserProfileRequestHandler(WeakReference(this))
        ).getUserProfile()
    }

    @SuppressLint("SetTextI18n")
    fun fillProfile(data: UserProfile) {
        if (full_name != null) {
            full_name.text = data.name + " " + data.last_name
            profile_image.setImageURI(data.profile_image)
            reputation.rating = data.reputation

            email.text = data.email
            phone.text = data.phone

            deliveries.text = data.deliveries_completed.toString()
            sent_msg.text = data.messages_sent.toString()
            profile_gratitude_points.text = data.gratitude_points.toString()
        }
    }

    fun onCleanFcmTokenSuccess() {
        // clean user backend data
        UserBackendDataHandler.getInstance().deleteUserBackendData()

        // go to login and clear task
        val intent = Intent(activity?.applicationContext, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = activity?.menuInflater
        inflater?.inflate(R.menu.profile_settings_menu, menu)
        onPrepareOptionsMenu(menu)
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.edit_profile_option -> {
                startActivity(
                    Intent(
                        activity?.applicationContext,
                        ChangeProfileActivity::class.java
                    )
                )
            }
            R.id.logout_option -> {
                // clean fcm token
                UserService(CleanFcmTokenRequestHandler(WeakReference(this)))
                    .updateUserFcmToken("")
            }
        }

        return true
    }
}


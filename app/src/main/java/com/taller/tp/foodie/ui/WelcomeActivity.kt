package com.taller.tp.foodie.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.requestHandlers.FinishRegisterRequestHandler
import com.taller.tp.foodie.services.UserService
import com.taller.tp.foodie.ui.welcome_fragments.FirstWelcomeSlideFragment
import com.taller.tp.foodie.ui.welcome_fragments.SecondWelcomeSlideFragment
import com.taller.tp.foodie.ui.welcome_fragments.ThirdWelcomeSlideFragment
import kotlinx.android.synthetic.main.activity_welcome.*
import kotlinx.android.synthetic.main.fragment_second_welcome_slide.*
import kotlinx.android.synthetic.main.fragment_third_welcome_slide.*
import java.lang.ref.WeakReference

class WelcomeActivity : AppCompatActivity() {

    companion object {
        const val CUSTOMER_TYPE = "CUSTOMER"
        const val DELIVERY_TYPE = "DELIVERY"

        const val FLAT_SUBSCRIPTION = "FLAT"
        const val PREMIUM_SUBSCRIPTION = "PREMIUM"
    }

    private var currentSliderPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        slide_view_pager.adapter = MyPagerAdapter(supportFragmentManager)
        slide_view_pager.offscreenPageLimit = 2

        // init
        btn_right.text = "siguiente"

        slide_view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                // Check if this is the page you want.
                val oldPosition = currentSliderPosition
                currentSliderPosition = position

                if (position == 2) {
                    btn_right.text = "listo"
                } else if (position == 0) {
                    btn_left.text = ""
                    btn_right.text = "siguiente"
                } else if (position == 1 && oldPosition == 0) {
                    btn_left.text = "anterior"
                } else if (position == 1 && oldPosition == 2) {
                    btn_right.text = "siguiente"
                }
            }
        })

        setupClickListeners()
    }

    private fun setupClickListeners() {
        btn_left.setOnClickListener {
            if (currentSliderPosition != 0) {
                slide_view_pager.currentItem = currentSliderPosition - 1
            }
        }

        btn_right.setOnClickListener {
            if (currentSliderPosition == 2) {
                // get user type from second slide
                val userType = if (group_type.checkedChipId == type_customer.id) {
                    CUSTOMER_TYPE
                } else {
                    DELIVERY_TYPE
                }

                // get subscription from third slide
                val subscription = if (btn_subscription.isChecked) {
                    PREMIUM_SUBSCRIPTION
                } else {
                    FLAT_SUBSCRIPTION
                }

                // finish register user in backend
                val requestHandler = FinishRegisterRequestHandler(WeakReference(this))
                UserService(this.applicationContext, requestHandler).finishRegister(
                    userType,
                    subscription
                )
            } else {
                slide_view_pager.currentItem = currentSliderPosition + 1
            }
        }
    }

    class MyPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
        // Returns total number of pages
        override fun getCount(): Int {
            return 3
        }

        // Returns the fragment to display for that page
        override fun getItem(position: Int): Fragment? {
            return when (position) {
                0 -> {
                    FirstWelcomeSlideFragment()
                }
                1 -> {
                    SecondWelcomeSlideFragment()
                }
                2 -> {
                    ThirdWelcomeSlideFragment()
                }
                else -> null
            }
        }
    }
}

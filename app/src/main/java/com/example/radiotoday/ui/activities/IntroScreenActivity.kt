package com.example.radiotoday.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.example.radiotoday.R
import com.example.radiotoday.data.models.slider.SliderData
import com.example.radiotoday.databinding.ActivityIntroScreenBinding
import com.example.radiotoday.ui.adapters.SliderAdapter
import com.example.radiotoday.utils.AppUtils
import com.example.radiotoday.utils.SharedPreferencesUtil

class IntroScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIntroScreenBinding
    private lateinit var viewPager: ViewPager

    private lateinit var sliderAdapter: SliderAdapter
    private lateinit var sliderList: ArrayList<SliderData>


    private lateinit var nextBtn: Button

    lateinit var indicatorSlideOneTV: TextView
    lateinit var indicatorSlideTwoTV: TextView
    lateinit var indicatorSlideThreeTV: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewPager = binding.idViewPager

        indicatorSlideOneTV = binding.idTVSlideOne
        indicatorSlideTwoTV = binding.idTVSlideTwo
        indicatorSlideThreeTV = binding.idTVSlideThree
        nextBtn = binding.idBtnNext

        binding.idBtnSkip.setOnClickListener {
            val i = Intent(this@IntroScreenActivity, MainActivity::class.java)
            startActivity(i)
            SharedPreferencesUtil.saveData(this@IntroScreenActivity, AppUtils.IntroScreenStatus, true)
            finish()
        }

        sliderList = ArrayList()

        sliderList.add(
            SliderData(
                "Lorem ipsum dolor sit amet consectetur. Feugiat dis diam quam urna lectus. ",
                "Radio Today",
                R.drawable.slider_img_1
            )
        )

        sliderList.add(
            SliderData(
                "Discover endless music on the go! Tune in to your favorite stations with our intuitive FM radio app. \n",
                "Start Stream at your flexible time",
                R.drawable.slider_img_2
            )
        )

        sliderList.add(
            SliderData(
                "Discover endless music on the go! Tune in to your favorite stations with our intuitive FM radio app. \n",
                "Listen to New Releases!",
                R.drawable.slider_img_3
            )
        )

        sliderAdapter = SliderAdapter(this, sliderList)
        viewPager.adapter = sliderAdapter
        viewPager.addOnPageChangeListener(viewListener)

        nextBtn.setOnClickListener {
            val currentItem = viewPager.currentItem
            val nextItem = currentItem + 1

            if (nextItem < sliderList.size) {
                viewPager.currentItem = nextItem
            } else {
                val i = Intent(this@IntroScreenActivity, MainActivity::class.java)
                SharedPreferencesUtil.saveData(this@IntroScreenActivity, AppUtils.IntroScreenStatus, true)
                startActivity(i)
                finish()
            }
        }


    }


    private var viewListener: ViewPager.OnPageChangeListener = object : ViewPager.OnPageChangeListener {

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        override fun onPageSelected(position: Int) {
            when (position) {
                0 -> {
                    indicatorSlideTwoTV.setTextColor(resources.getColor(R.color.grey))
                    indicatorSlideThreeTV.setTextColor(resources.getColor(R.color.grey))
                    indicatorSlideOneTV.setTextColor(resources.getColor(R.color.red))

                    nextBtn.text = "Next"

                }
                1 -> {
                    indicatorSlideTwoTV.setTextColor(resources.getColor(R.color.red))
                    indicatorSlideThreeTV.setTextColor(resources.getColor(R.color.grey))
                    indicatorSlideOneTV.setTextColor(resources.getColor(R.color.grey))

                    nextBtn.text = "Next"
                }
                else -> {
                    indicatorSlideTwoTV.setTextColor(resources.getColor(R.color.grey))
                    indicatorSlideThreeTV.setTextColor(resources.getColor(R.color.red))
                    indicatorSlideOneTV.setTextColor(resources.getColor(R.color.grey))

                    nextBtn.text = "  Let's Go"


                }
            }
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }
}
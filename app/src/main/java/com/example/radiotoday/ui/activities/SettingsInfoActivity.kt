package com.example.radiotoday.ui.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.example.radiotoday.databinding.ActivitySettingsInfoBinding
import com.example.radiotoday.ui.viewmodels.SettingsViewModel
import com.example.radiotoday.utils.ResultType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsInfoBinding
    private val settingsViewModel by viewModels<SettingsViewModel>()
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolBarBackIcon.setOnClickListener {
            onBackPressed()
        }

        val news = intent.getStringExtra("newsType")
        if (news == "News Detail"){
            binding.tvToolBarTitle.text = news

            val contentUrl = intent.getStringExtra("news_url") ?: ""

            binding.infoDataWv.loadUrl(contentUrl)
            binding.infoDataWv.setOnLongClickListener { true }
            binding.infoDataWv.isLongClickable = false
            binding.progressbar.visibility = View.GONE
        }else{
            val type = intent.getStringExtra("type")

            settingsViewModel.fetchSettingsData(type!!)
            settingsViewModel.settingsData.observe(this){
                when(it){
                    is ResultType.Loading -> {
                        binding.progressbar.visibility = View.VISIBLE
                    }

                    is ResultType.Success -> {
                        val content = it.data.content.value
                        binding.tvToolBarTitle.text = it.data.content.type

                        if (content.isNotEmpty()){
                            /*binding.tvInfoData.text = Html.fromHtml("<h2>Title</h2><br><p>$content</p>", Html.FROM_HTML_MODE_COMPACT)*/
                            binding.infoDataWv.loadDataWithBaseURL(null, content, "text/html", "utf-8", null);
                            binding.infoDataWv.setOnLongClickListener { true }
                            binding.infoDataWv.isLongClickable = false
                            binding.progressbar.visibility = View.GONE

                        }
                    }

                    is ResultType.Error -> {
                        Toast.makeText(this, "Something Went Wrong!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


    }
}
package com.example.radiotoday.ui.fragments

import android.content.Intent
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.radiotoday.data.models.SongList
import com.example.radiotoday.data.models.SubContent
import com.example.radiotoday.databinding.FragmentHomeBinding
import com.example.radiotoday.ui.activities.ShowDetailsActivity
import com.example.radiotoday.ui.adapters.ParentHomeAdapter
import com.example.radiotoday.ui.interfaces.HomeItemClickListener
import com.example.radiotoday.ui.viewmodels.HomeViewModel
import com.example.radiotoday.utils.ResultType
import com.example.radiotoday.ui.interfaces.PlayerClickListener
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class HomeFragment : Fragment(), HomeItemClickListener {
    private lateinit var parentHomeAdapter: ParentHomeAdapter
    private val homeViewModel by viewModels<HomeViewModel>()
    private lateinit var binding: FragmentHomeBinding

    var playerClickListener: PlayerClickListener? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater,container,false)


        val greeting = showGreetingsMsg()
        Log.i("Greeting", "onCreateView: $greeting")
        binding.tvGreeting.text = greeting

        parentHomeAdapter = ParentHomeAdapter(this)
        binding.rvHomeMain.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvHomeMain.adapter = parentHomeAdapter

        homeViewModel.homeData.observe(requireActivity()){
            when(it){
                is ResultType.Loading -> {
                    binding.shimmerFrameLayoutHome.visibility = View.VISIBLE
                }
                is ResultType.Success -> {
                    binding.shimmerFrameLayoutHome.visibility = View.GONE
                    val homeData= it.data.content
                    parentHomeAdapter.homeData = ArrayList(homeData + homeData )
                    this.parentHomeAdapter.notifyDataSetChanged()


                }

                else -> {}
            }
        }

        return binding.root
    }

    override fun onResume() {

        homeViewModel.fetchHomeData("home", "android")
        super.onResume()
    }

    override fun onHomeItemClickListener(position: Int, currentItem: SubContent, currentSection: String) {

        if (currentSection == "songs"){
            playerClickListener?.onPlayerClickListener()

        }else{

            val intent = Intent(activity, ShowDetailsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtra("section_code", currentSection)
            intent.putExtra("id", currentItem.id)
            startActivity(intent)

        }

    }

    private fun showGreetingsMsg() : String {
        val cal = Calendar.getInstance()

        return when(cal.get(Calendar.HOUR_OF_DAY)){
            in 5..11 -> "Good Morning"
            in 12..14 -> "Good Noon"
            in 15..17 -> "Good Afternoon"
            in 18..20 -> "Good Evening"
            in 21..23 -> "Good Night"
            in 1..4 -> "Good Night"
            else -> "Hello"
        }
    }


}
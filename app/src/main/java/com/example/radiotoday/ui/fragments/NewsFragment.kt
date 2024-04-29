package com.example.radiotoday.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.radiotoday.data.models.SubContent
import com.example.radiotoday.data.models.seeAll.SeeAllResponse
import com.example.radiotoday.databinding.FragmentNewsBinding
import com.example.radiotoday.ui.adapters.NewsAdapter
import com.example.radiotoday.ui.viewmodels.NewsViewModel
import com.example.radiotoday.ui.interfaces.ItemClickListener
import com.example.radiotoday.ui.interfaces.OnBackAction
import com.example.radiotoday.utils.ResultType
import com.example.radiotoday.ui.interfaces.PlayerClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewsFragment : Fragment(), ItemClickListener {
    private lateinit var binding: FragmentNewsBinding

    private lateinit var newsAdapter: NewsAdapter
    private val newsViewModel by viewModels<NewsViewModel>()

    private lateinit var layoutManager: GridLayoutManager
    private lateinit var playlistData: SeeAllResponse

    var playerClickListener: PlayerClickListener? = null

    private var isLoading = false
    private var isLastpage = false
    private var currentPage = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewsBinding.inflate(layoutInflater,container,false)

        binding.toolBarBackIcon.setOnClickListener {
            onBackAction.onBackListener()
        }

        newsAdapter = NewsAdapter(requireContext(), this)
        binding.rvPlaylist.layoutManager = customGridLayoutManager(1)
        binding.rvPlaylist.adapter = newsAdapter

        observeNewsData()

        return binding.root
    }

    override fun onResume() {
        isLoading = false
        isLastpage = false
        currentPage = 1

        showSeeAll()

        super.onResume()
    }

    private fun loadSeeAllData() {
        newsViewModel.fetchNewsData(currentPage.toString())
    }

    private fun showSeeAll() {
        loadSeeAllData()  //Initial data load

        binding.rvPlaylist.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && !isLastpage) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                        isLoading = true
                        //currentPage++
                        loadSeeAllData()
                    }
                }
            }
        })
    }

    private fun observeNewsData() {
        newsViewModel.newsData.observe(requireActivity()) {
            when (it) {
                is ResultType.Loading -> {
                    //binding.shimmerFrameLayout.visibility = View.VISIBLE
                }

                is ResultType.Success -> {
                    playlistData = it.data
                    val playlistContent= playlistData.content.content

                    if (currentPage == 1) {
                        newsAdapter.newsData = playlistContent


                    } else {
                        if (!newsAdapter.newsData.containsAll(playlistContent)) {

                            newsAdapter.newsData =
                                newsAdapter.newsData.plus(playlistContent) as ArrayList<SubContent>


                        }
                    }

                    isLoading = false
                    //checking last page
                    isLastpage = playlistContent.isEmpty()
                    currentPage++
                    newsAdapter.notifyDataSetChanged()
                    //binding.shimmerFrameLayout.visibility = View.GONE
                }

                else -> {}
            }
        }
    }

    private fun customGridLayoutManager(span: Int): GridLayoutManager {
        layoutManager = GridLayoutManager(requireActivity(), span)
        return layoutManager
    }

    companion object{

        lateinit var onBackAction: OnBackAction
        fun onBackAction(setBackAction: OnBackAction){
            this.onBackAction = setBackAction
        }

    }

    override fun onItemClickListener(position: Int) {
        Log.d("VideoFragment", "Clicked on position: $position")

        if (position >= 0 && position < newsAdapter.itemCount) {
            playerClickListener?.onPlayerClickListener()
        } else {
            Log.e("VideoFragment", "Invalid position: $position")
        }

        newsAdapter.notifyDataSetChanged()
    }


}
package com.awesome.news_app.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.awesome.news_app.R
import com.awesome.news_app.adapter.NewsAdapter
import com.awesome.news_app.databinding.FragmentNewsBinding
import com.awesome.news_app.ViewModels.NewsViewModel

class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NewsViewModel by viewModels()
    private lateinit var newsAdapter: NewsAdapter

    private var selectedSortOptions = mutableSetOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeNewsArticles()

        viewModel.loadNextPage()

        binding.buttonSort.setOnClickListener {
            val dialog = SortBottomSheetDialog.newInstance(
                selectedSortOptions
            ) { selectedSorts ->
                selectedSortOptions = selectedSorts.toMutableSet()
                viewModel.setSelectedSortOptions(selectedSortOptions)
            }

            val fragmentManager = parentFragmentManager
            dialog.show(fragmentManager, getString(R.string.sortdialog))
        }
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter { article ->
            findNavController().navigate(
                NewsFragmentDirections.actionNewsFragmentToArticleFragment(
                    title = article.title ?: "No Title",
                    description = article.description ?: "No Description",
                    author = article.author ?: "No Author",
                    content = article.content ?: "No Content",
                    imageUrl = article.urlToImage ?: "",
                    whichfrag = "news",
                    sourcename = article.source.name,
                    publishedAt = article.publishedAt
                )
            )
        }

        val layoutManager = LinearLayoutManager(context)

        binding.recyclerHeadlines.apply {
            adapter = newsAdapter
            this.layoutManager = layoutManager

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    if (dy > 0 && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
                        viewModel.loadNextPage()
                    }
                }
            })
        }
    }

    private fun observeNewsArticles() {
        viewModel.articlesData.observe(viewLifecycleOwner) { articles ->
            if (!articles.isNullOrEmpty()) {
                newsAdapter.setArticles(articles)
            } else {
                Toast.makeText(context,
                    getString(R.string.no_articles_available), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

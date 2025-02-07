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
import com.awesome.news_app.R
import com.awesome.news_app.adapter.NewsAdapter
import com.awesome.news_app.databinding.FragmentSavedBinding
import com.awesome.news_app.model.Article
import com.awesome.news_app.ViewModels.ArticleViewModel
import com.awesome.news_app.ViewModels.NewsViewModel
import com.awesome.news_app.model.SavedArticle
import com.awesome.news_app.model.Source

class SavedFragment : Fragment() {

    private var _binding: FragmentSavedBinding? = null
    private val binding get() = _binding!!
    private val articleViewModel: ArticleViewModel by viewModels()
    private val newsViewModel: NewsViewModel by viewModels()

    private lateinit var savedAdapter: NewsAdapter
    private var selectedSortOptions = mutableSetOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSavedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeSavedArticles()

        binding.buttonSort.setOnClickListener {
            val dialog = SortBottomSheetDialog.newInstance(
                selectedSortOptions
            ) { selectedSorts ->
                selectedSortOptions = selectedSorts.toMutableSet()
                newsViewModel.setSelectedSortOptions(selectedSortOptions)
            }
            dialog.show(parentFragmentManager, getString(R.string.sort_dialog))
        }

        newsViewModel.articlesData.observe(viewLifecycleOwner) { sortedArticles ->
            if (sortedArticles.isNullOrEmpty()) {
                Toast.makeText(context, getString(R.string.no_articlesavailable), Toast.LENGTH_SHORT).show()
            } else {
                savedAdapter.setArticles(sortedArticles)
            }
        }
    }

    private fun setupRecyclerView() {
        savedAdapter = NewsAdapter { article ->
            findNavController().navigate(
                SavedFragmentDirections.actionSavedFragmentToArticleFragment(
                    title = article.title ?: "No Title",
                    description = article.description ?: "No Description",
                    author = article.author ?: "No Author",
                    content = article.content ?: "No Content",
                    imageUrl = article.urlToImage ?: "",
                    whichfrag = "save",
                    sourcename = article.source.name,
                    publishedAt = article.publishedAt
                )
            )
        }

        binding.recyclerHeadlines.apply {
            adapter = savedAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeSavedArticles() {
        articleViewModel.allArticles.observe(viewLifecycleOwner) { savedArticles ->
            val articles = savedArticles.map { it.toArticle() }
            newsViewModel.updateArticles(articles)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun SavedArticle.toArticle() = Article(
        title = this.title,
        description = this.description,
        author = this.author.toString(),
        urlToImage = this.imagePath.toString(),
        content = this.content.toString(),
        publishedAt = this.publishedAt.toString(),
        source = Source("", this.sourceName.toString()),
        url = ""
    )
}

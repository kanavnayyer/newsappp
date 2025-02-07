package com.awesome.news_app.ui.view

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.awesome.news_app.R
import com.awesome.news_app.databinding.FragmentArticleBinding
import com.awesome.news_app.model.SavedArticle
import com.awesome.news_app.ViewModels.ArticleViewModel
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
class ArticleFragment : Fragment() {

    private var _binding: FragmentArticleBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ArticleViewModel by viewModels()

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentArticleBinding.inflate(inflater, container, false)

        val args = ArticleFragmentArgs.fromBundle(requireArguments())
        binding.textViewTitle.text = args.title
        binding.textViewDescription.text = args.description
        binding.textViewAuthor.text = "Author: ${args.author}"
        binding.textViewSourceName.text = "Source: ${args.sourcename}"
     binding.textViewPublishDate.text = viewModel.formatDate(args.publishedAt)

        binding.textViewContent.text = args.content

        val frag = args.whichfrag
        if (frag == "news") {
            binding.buttonSave.text = "Save"
            binding.buttonSave.icon = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_save_24)
            binding.buttonSave.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), R.color.green)
            )
        } else {
            binding.buttonSave.text = "Delete"
            binding.buttonSave.icon = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_delete_24)
            binding.buttonSave.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), R.color.red)
            )
        }


        Glide.with(this)
            .load(args.imageUrl)
            .into(binding.imageView)

        binding.buttonSave.setOnClickListener {
            if (frag == "news") {
                viewModel.checkIfArticleExists(args.title) { exists ->
                    requireActivity().runOnUiThread {
                        if (exists) {
                            Toast.makeText(context, "Article already saved!", Toast.LENGTH_SHORT).show()
                        } else {
                            saveArticleToDb(args)
                        }
                    }
                }
            } else {
                viewModel.deleteArticleByTitle(args.title)
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Article deleted", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }


            }
        }

        return binding.root
    }

    private fun saveArticleToDb(args: ArticleFragmentArgs) {
        lifecycleScope.launch {
            val savedImagePath = saveImageLocally(args.imageUrl)
            val savedArticle = SavedArticle(
                title = args.title,
                description = args.description,
                author = args.author,
                imagePath = savedImagePath,
                content = args.content,
                sourceName = args.sourcename,
                publishedAt = args.publishedAt
            )
            viewModel.insertArticle(savedArticle)
            Toast.makeText(context, "Article saved!", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun saveImageLocally(imageUrl: String?): String? {
        if (imageUrl.isNullOrEmpty()) return null

        return withContext(Dispatchers.IO) {
            try {
                val bitmap = Glide.with(requireContext())
                    .asBitmap()
                    .load(imageUrl)
                    .submit()
                    .get()

                val file = File(requireContext().filesDir, "article_image_${System.currentTimeMillis()}.png")
                val outputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.flush()
                outputStream.close()

                file.absolutePath
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

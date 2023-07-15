package com.supenovapro.nextlevelera.ui.bookmark

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import com.supenovapro.nextlevelera.R
import com.supenovapro.nextlevelera.data.TrendNewsBookmark
import com.supenovapro.nextlevelera.databinding.FragmentBookmarksBinding
import com.supenovapro.nextlevelera.ui.browser.News
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BookmarkFragment : Fragment(R.layout.fragment_bookmarks),
    BookmarkAdapter.OnItemClickListener {

    private val bookmarkViewModel: BookmarkViewModel by viewModels()
    private var _binding: FragmentBookmarksBinding? = null
    private val binding get() = _binding!!

    private var bookmarkSize = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBookmarksBinding.bind(view)

        val bookAdapter = BookmarkAdapter(this)
        binding.apply {
            val toolbar = bookmarkFrgToolbar
            toolbar.inflateMenu(R.menu.bookmark_menu)
            setBookmarkMenu(toolbar)
            bookmarkRecycler.apply {
                adapter = bookAdapter
                layoutManager = LinearLayoutManager(view.context)
                setHasFixedSize(true)
            }

            ItemTouchHelper(object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START or ItemTouchHelper.END) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val bookmarkArticle = bookAdapter.currentList[viewHolder.bindingAdapterPosition]
                    bookmarkViewModel.deleteBookmarkArticle(bookmarkArticle)
                }

            }).attachToRecyclerView(bookmarkRecycler)

        }

        bookmarkViewModel.bookmarkNews.observe(viewLifecycleOwner) { bookmarks ->
            if (bookmarks.isNotEmpty()) {
                bookmarkSize = bookmarks.size
                binding.textViewNoBookmarks.visibility = View.GONE

            } else binding.textViewNoBookmarks.visibility = View.VISIBLE
            bookAdapter.submitList(bookmarks)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onOpenBookmarkClick(bookmark: TrendNewsBookmark) {
        val intent = Intent(context, News::class.java)
        intent.putExtra("url", bookmark.url)
        startActivity(intent)
    }

    override fun onDeleteClick(bookmark: TrendNewsBookmark) {
        bookmarkViewModel.deleteBookmarkArticle(bookmark)
    }

    private fun setBookmarkMenu(toolbar: MaterialToolbar) {
        toolbar.setOnMenuItemClickListener { topItem ->
            when (topItem.itemId) {
                R.id.all_bookmark_delete -> {
                    if (bookmarkSize > 0)
                        deleteBookmarkDialog().show()
                    else Snackbar.make(binding.root, "Bookmark List Empty", Snackbar.LENGTH_LONG)
                        .show()

                    true
                }

                else -> {
                    return@setOnMenuItemClickListener true
                }
            }
        }
    }

    private fun deleteBookmarkDialog(): AlertDialog {
        val builder = AlertDialog.Builder(requireContext().applicationContext)
        builder.setTitle("Delete")
        builder.setMessage("Are Sure You want \n Delete Your All Bookmarked News.")
        builder.setPositiveButton("YES") { dialog, _ ->
            bookmarkViewModel.deleteAllBookmarkArticles()
            dialog.cancel()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.cancel()
        }
        return builder.create()
    }

    //send data back main activity
    interface BookmarkFragmentListener {
    }

    private var bookmarkListener: BookmarkFragmentListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        bookmarkListener = if (context is BookmarkFragmentListener) {
            context
        } else {
            throw RuntimeException(context.toString() + "must impl BOOKMARK listener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        bookmarkListener = null
    }

}
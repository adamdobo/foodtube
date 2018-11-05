package hu.doboadam.howtube.ui.content.categories

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hu.doboadam.howtube.R
import hu.doboadam.howtube.extensions.createDialog
import hu.doboadam.howtube.model.Category
import hu.doboadam.howtube.ui.BaseViewModelFragment
import kotlinx.android.synthetic.main.dialog_add_new_video.view.*
import kotlinx.android.synthetic.main.fragment_category_list.*

class CategoryListFragment : BaseViewModelFragment() {

    override val TAG: String = "CategoryListFragment"
    private lateinit var adapter: CategoryAdapter
    private lateinit var viewModel: CategoryListViewModel
    private lateinit var listener: OnCategoryClickedListener
    private var dialog: AlertDialog? = null

    companion object {
        fun newInstance(): BaseViewModelFragment = CategoryListFragment()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_category_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CategoryListViewModel::class.java)
        observeViewModel()
        setUpRecyclerView()
        addVideo.setOnClickListener {
            dialog?.show()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnCategoryClickedListener) {
            listener = context
        } else {
            throw RuntimeException("${context.toString()} must implement interface OnCategoryClickedListener")
        }
    }


    private fun setUpRecyclerView() {
        categoryList.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                when {
                    dy > 0 -> addVideo.hide()
                    else -> addVideo.show()
                }
            }
        })
        adapter = CategoryAdapter(emptyList<Category>().toMutableList()) {
            listener.onCategoryClicked(it.id)
        }
        categoryList.layoutManager = GridLayoutManager(context, 2)
        categoryList.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.getCategoryListLiveData().observe(this, Observer { value ->
            value?.let {
                if(dialog == null){
                    initDialog(it)
                }
                adapter.setItems(it)
                addVideo.show()
            }
        })
        viewModel.uploadSucceeded.observe(this, Observer {
            Snackbar.make(rootLayout, "Video successfully uploaded!", Snackbar.LENGTH_SHORT).show()
        })
    }

    private fun initDialog(it: List<Category>) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_new_video, null)
        dialog = createDialog {
            setTitle(getString(R.string.add_new_video))
            setUpSpinner(it, dialogView)
            setNegativeButton(getString(R.string.cancel), null)
            setPositiveButton(getString(R.string.ok)) { _, _ ->
                with(dialogView) {
                    val category = categorySpinner.selectedItem as Category
                    viewModel.checkAndUploadVideo(videoUrlText.text.toString(), category.id)
                }
            }
            setView(dialogView)
        }
    }

    private fun setUpSpinner(it: List<Category>, dialogView: View) = with(dialogView) {
        val adapter = SpinnerAdapter(context, android.R.layout.simple_spinner_item, it.toTypedArray())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter
    }

    override fun startListeningToDb() {
        viewModel.startListeningToDbChanges()
    }

    override fun stopListeningToDb() {
        viewModel.stopListeningToDbChanges()
    }

    interface OnCategoryClickedListener {
        fun onCategoryClicked(id: Int)
    }
}
package hu.doboadam.szakdoga.ui.content

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hu.doboadam.szakdoga.R
import hu.doboadam.szakdoga.extensions.replaceFragment
import hu.doboadam.szakdoga.ui.content.categories.CategoryListFragment

class ContentFragment : Fragment() {

    private lateinit var listener: OnBackPressedListener
    companion object {
        fun newInstance(): ContentFragment = ContentFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        replaceFragment(CategoryListFragment.newInstance(), R.id.fragmentContainer, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnBackPressedListener) {
            listener = context
        } else {
            throw RuntimeException("${context.toString()} must implement in" +
                    "terface OnBackPressedListener")
        }
    }

    fun onBackPressed() {
        if (childFragmentManager.backStackEntryCount > 0) {
            childFragmentManager.popBackStack()
        } else {
            listener.defaultBack()
        }
    }

    interface OnBackPressedListener {
        fun defaultBack()
    }
}
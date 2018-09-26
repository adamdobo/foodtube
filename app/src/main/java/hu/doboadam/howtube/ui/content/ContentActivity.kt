package hu.doboadam.howtube.ui.content

import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import hu.doboadam.howtube.R
import hu.doboadam.howtube.extensions.createDialog
import kotlinx.android.synthetic.main.activity_content.*

class ContentActivity : AppCompatActivity() {

    private lateinit var viewModel: ContentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content)
        viewModel = ViewModelProviders.of(this).get(ContentViewModel::class.java)
        addVideo.setOnClickListener {
            createDialog {
                setTitle("Add new video")
                setView(R.layout.dialog_add_new_video)
                setNegativeButton("Cancel", null)
                setPositiveButton("OK", DialogInterface.OnClickListener { dialog, _ ->
                    //TODO: query youtube
                })
            }
        }
    }

}

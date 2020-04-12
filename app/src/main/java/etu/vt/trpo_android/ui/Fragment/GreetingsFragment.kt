package etu.vt.trpo_android.ui.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import etu.vt.trpo_android.R
import etu.vt.trpo_android.present.presenter.GreetingsPresenter
import etu.vt.trpo_android.present.view.GreetingsView
import etu.vt.trpo_android.ui.Activity.MainActivity

class GreetingsFragment: MvpAppCompatFragment(), GreetingsView {

    @InjectPresenter
    lateinit var mGreetingsPresenter :GreetingsPresenter

    companion object {
        fun newInstance(): GreetingsFragment = GreetingsFragment()
    }

    override fun showGreetings() {
        TODO("Not yet implemented")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.retainInstance = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val button = view?.findViewById<Button>(R.id.btn)
        button?.setOnClickListener {
            findNavController().navigate(R.id.pictureFragment, null)
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?  ): View? {
        this.retainInstance = true
        return inflater.inflate(R.layout.greetings_fragment, container, false)
    }

}
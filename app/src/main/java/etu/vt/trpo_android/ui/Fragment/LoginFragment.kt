package etu.vt.trpo_android.ui.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import etu.vt.trpo_android.R
import etu.vt.trpo_android.present.presenter.LoginPresenter
//import com.arellomobile.mvp.MvpFragment
import etu.vt.trpo_android.present.view.LoginView
import kotlinx.android.synthetic.*


class LoginFragment: MvpAppCompatFragment(), LoginView {

    @InjectPresenter
    lateinit var mLoginPresenter : LoginPresenter

    companion object {
        fun newInstance(): LoginFragment = LoginFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.retainInstance = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val button = view.findViewById<Button>(R.id.login)
//        button?.setOnClickListener {
//            findNavController().navigate(R.id.greetingsFragment2, null)
//        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?  ): View? {
        this.retainInstance = true
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

}
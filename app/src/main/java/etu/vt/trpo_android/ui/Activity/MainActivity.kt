package etu.vt.trpo_android.ui.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import etu.vt.trpo_android.R
import etu.vt.trpo_android.ui.Fragment.GreetingsFragment
import etu.vt.trpo_android.ui.Fragment.LoginFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.greetings_fragment.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //val navContr = Navigation.findNavController(this@MainActivity, R.id.navHostMain)
//        var fm: FragmentManager = supportFragmentManager
//        var fragment: Fragment? = fm.findFragmentById(R.id.fragment_container)
//
//        if (fragment != null) {
//           // fragment = LoginFragment()
//            fm.beginTransaction()
//                .replace(R.id.fragment_container, GreetingsFragment.newInstance())
//                .commit()
//        }

    }




    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.navHostMain).navigateUp()
    }

}

package etu.vt.trpo_android.ui.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.navigation.NavigationView
import etu.vt.trpo_android.R
import etu.vt.trpo_android.ui.Fragment.GreetingsFragment
import etu.vt.trpo_android.ui.Fragment.LoginFragment
import etu.vt.trpo_android.ui.Fragment.PictureFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.greetings_fragment.*
import java.util.zip.Inflater

class MainActivity : AppCompatActivity() {

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navCon : NavController = findNavController(R.id.navHostMain)
        var bundleNav: Bundle? = null
        when(item.itemId){
            R.id.action_main_page ->{
                navCon.navigate(R.id.pictureFragment, null)
                return true
            }

            R.id.action_instruction ->{
                return if (PictureFragment.SingleBitmap.mbitmap != null) {
                    bundleNav?.putParcelable("mbitmap", PictureFragment.SingleBitmap.mbitmap)
                    navCon.navigate(R.id.instructionsFragment, bundleNav)
                    true
                }else {
                    navCon.navigate(R.id.instructionsFragment, bundleNav)
                    true
                }
            }

            R.id.action_about ->{
                return if (PictureFragment.SingleBitmap.mbitmap != null) {
                    bundleNav?.putParcelable("mbitmap", PictureFragment.SingleBitmap.mbitmap)
                    navCon.navigate(R.id.instructionsFragment, bundleNav)
                    true
                }else {
                    navCon.navigate(R.id.instructionsFragment, bundleNav)
                    true
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

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

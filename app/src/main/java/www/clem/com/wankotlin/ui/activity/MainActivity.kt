package www.clem.com.wankotlin.ui.activity

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import toast
import www.clem.com.wankotlin.R
import www.clem.com.wankotlin.base.BaseActivity
import www.clem.com.wankotlin.ui.fragment.HomeFragment

class MainActivity : BaseActivity() {
    private var lastTime: Long = 0
    private var currentIndex = 0
    private var homeFragment: HomeFragment? = null

    //    lazy{} 只能用在val类型, lateinit 只能用在var类型
    private val fragmentManager by lazy {
        supportFragmentManager
    }

    override fun setLayoutId(): Int = R.layout.activity_main


    override fun cancelRequest() {
    }

    override fun initImmersionBar() {
        super.initImmersionBar()
        immersionBar.titleBar(R.id.toolbar).init()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar.run {
            title = getString(R.string.app_name)
            setSupportActionBar(this)
        }

        drawerLayout.run {
            val toggle = ActionBarDrawerToggle(
                    this@MainActivity,
                    this,
                    toolbar,
                    R.string.navigation_drawer_open,
                    R.string.navigation_drawer_close
            )
            addDrawerListener(toggle)
            toggle.syncState()
        }

        navigationView.run {
            setNavigationItemSelectedListener(onDrawerNavigationItemSelectedListener)
        }

        fragmentManager.beginTransaction().apply {
            homeFragment ?: let {
                HomeFragment().let {
                    homeFragment = it
                    add(R.id.content, it)
                }
            }
        }.commit()
    }


    /**
     * 退出
     */
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
            return
        }
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastTime < 2 * 1000) {
            super.onBackPressed()
            finish()
        } else {
            toast(getString(R.string.double_click_exit))
            lastTime = currentTime
        }
    }

    private val onDrawerNavigationItemSelectedListener =
            NavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.nav_camera -> {
                        // Handle the camera action
                    }
                    R.id.nav_gallery -> {

                    }
                    R.id.nav_slideshow -> {

                    }
                    R.id.nav_manage -> {

                    }
                    R.id.nav_share -> {

                    }
                    R.id.nav_send -> {

                    }
                }
                drawerLayout.closeDrawer(GravityCompat.START)
                true
            }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
        when (fragment) {
            is HomeFragment -> homeFragment ?: let { homeFragment = fragment }
//            is TypeFragment -> typeFragment ?: let { typeFragment = fragment }
//            is CommonUseFragment -> commonUseFragment ?: let { commonUseFragment = fragment }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }
}

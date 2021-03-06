package www.clem.com.wankotlin.ui.activity

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import toast
import www.clem.com.wankotlin.R
import www.clem.com.wankotlin.base.BaseActivity
import www.clem.com.wankotlin.ui.fragment.HomeFragment
import www.clem.com.wankotlin.ui.fragment.TypeFragment

class MainActivity : BaseActivity() {
    private var lastTime: Long = 0
    private var currentIndex = 0
    private var homeFragment: HomeFragment? = null
    private var typeFragment: TypeFragment? = null

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

        bottomNavigation.run {
            setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
            selectedItemId = R.id.navigation_home
        }

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

    /**
     * NavigationItemSelect监听
     */
    private val onNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                setFragment(item.itemId)
                return@OnNavigationItemSelectedListener when (item.itemId) {
                    R.id.navigation_home -> {
                        if (currentIndex == R.id.navigation_home) {
                            homeFragment?.smoothScrollToPosition()
                        }
                        currentIndex = R.id.navigation_home
                        true
                    }
                    R.id.navigation_type -> {
                        if (currentIndex == R.id.navigation_type) {
                            typeFragment?.smoothScrollToPosition()
                        }
                        currentIndex = R.id.navigation_type
                        true
                    }
                    else -> {
                        false
                    }
                }
            }

    /**
     * 显示对应Fragment
     */
    private fun setFragment(index: Int) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        fragmentManager.beginTransaction().apply {
            homeFragment ?: let {
                HomeFragment().let {
                    homeFragment = it
                    add(R.id.content, it)
                }
            }
            typeFragment ?: let {
                TypeFragment().let {
                    typeFragment = it
                    add(R.id.content, it)
                }
            }
//            commonUseFragment ?: let {
//                CommonUseFragment().let {
//                    commonUseFragment = it
//                    add(R.id.content, it)
//                }
//            }
            hideFragment(this)
            when (index) {
                R.id.navigation_home -> {
                    toolbar.title = getString(R.string.app_name)
                    homeFragment?.let {
                        this.show(it)
                    }
                }
                R.id.navigation_type -> {
                    toolbar.title = getString(R.string.title_dashboard)
                    typeFragment?.let {
                        this.show(it)
                    }
                }
//                R.id.menuHot -> {
//                    toolbar.title = getString(R.string.hot_title)
//                    commonUseFragment?.let {
//                        this.show(it)
//                    }
//                }
            }
        }.commit()
    }

    /**
     * 隐藏所有fragment
     */
    private fun hideFragment(transaction: FragmentTransaction) {
        homeFragment?.let {
            transaction.hide(it)
        }
        typeFragment?.let {
            transaction.hide(it)
        }
//        commonUseFragment?.let {
//            transaction.hide(it)
//        }
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
        when (fragment) {
            is HomeFragment -> homeFragment ?: let { homeFragment = fragment }
            is TypeFragment -> typeFragment ?: let { typeFragment = fragment }
//            is CommonUseFragment -> commonUseFragment ?: let { commonUseFragment = fragment }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}

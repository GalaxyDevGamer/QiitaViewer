package galaxy.qiitaviewer.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import galaxy.qiitaviewer.R
import galaxy.qiitaviewer.adapter.ViewPageAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initLayout()
    }

    private fun initLayout() {
        setSupportActionBar(toolbar)
        viewPager.adapter = ViewPageAdapter(supportFragmentManager)
        tablayout.setupWithViewPager(viewPager)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        startActivity(Intent(this, SearchActivity::class.java))
        return super.onOptionsItemSelected(item)
    }
}
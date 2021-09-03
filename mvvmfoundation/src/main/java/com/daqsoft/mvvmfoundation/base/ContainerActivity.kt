package com.daqsoft.mvvmfoundation.base

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.daqsoft.mvvmfoundation.R
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference

/**
 * @package name：com.daqsoft.mvvmfoundation.base
 * @date 10/11/2020 下午 1:48
 * @author zp
 * @describe
 */
@AndroidEntryPoint
class ContainerActivity : RxAppCompatActivity() {

    companion object{
        private const val FRAGMENT_TAG = "content_fragment_tag"
        const val FRAGMENT = "fragment"
        const val BUNDLE = "bundle"
    }

    protected var mFragment: WeakReference<Fragment>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)
        val fm: FragmentManager = supportFragmentManager
        var fragment: Fragment? = null
        if (savedInstanceState != null) {
            fragment = fm.getFragment(savedInstanceState, FRAGMENT_TAG)
        }
        if (fragment == null) {
            fragment = initFromIntent(intent)
        }
        val trans: FragmentTransaction = supportFragmentManager.beginTransaction()
        trans.replace(R.id.content, fragment!!)
        trans.commitAllowingStateLoss()
        mFragment = WeakReference<Fragment>(fragment)
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        supportFragmentManager.putFragment(
            outState, FRAGMENT_TAG,
            mFragment!!.get()!!
        )
    }

    protected fun initFromIntent(data: Intent?): Fragment? {
        if (data == null) {
            throw RuntimeException("you must provide a page info to display")
        }
        try {
            val fragmentName = data.getStringExtra(FRAGMENT)
            require(!(fragmentName == null || "" == fragmentName)) { "can not find page fragmentName" }
            val fragmentClass = Class.forName(fragmentName)
            val fragment = fragmentClass.newInstance() as Fragment
            val args = data.getBundleExtra(BUNDLE)
            if (args != null) {
                fragment.arguments = args
            }
            return fragment
        } catch (e: Exception) {
            e.printStackTrace()
        }
        throw RuntimeException("fragment initialization failed!")
    }


    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.content)
        if (fragment is BaseFragment<*,*>) {
            if (!fragment.isBackPressed()) {
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }
}
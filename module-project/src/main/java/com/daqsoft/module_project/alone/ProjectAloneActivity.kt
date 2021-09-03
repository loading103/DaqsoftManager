package com.daqsoft.module_project.alone

import android.content.Intent
import android.os.Bundle
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.daqsoft.module_project.fragment.ProjectFragment
import com.daqsoft.mvvmfoundation.base.ContainerActivity

/**
 * @package name：com.daqsoft.module_task.alone
 * @date 10/11/2020 下午 1:46
 * @author zp
 * @describe
 */
class ProjectAloneActivity : AppCompatActivity() {
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, ContainerActivity::class.java)
        intent.putExtra(ContainerActivity.FRAGMENT, ProjectFragment::class.java.canonicalName)
        this.startActivity(intent)
        finish()
    }
}
package com.daqsoft.module_workbench.alone

import android.content.Intent
import android.os.Bundle
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.daqsoft.module_workbench.fragment.WorkBenchFragment
import com.daqsoft.mvvmfoundation.base.ContainerActivity

/**
 * @package name：com.daqsoft.module_task.alone
 * @date 10/11/2020 下午 1:46
 * @author zp
 * @describe
 */
class WorkbenchAloneActivity : AppCompatActivity() {
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, ContainerActivity::class.java)
        intent.putExtra(ContainerActivity.FRAGMENT, WorkBenchFragment::class.java.canonicalName)
        this.startActivity(intent)
        finish()
    }
}

package com.daqsoft.module_home.viewmodel.itemviewmodel

import android.app.Application
import androidx.databinding.ObservableField
import com.daqsoft.module_home.R
import com.daqsoft.module_home.viewmodel.HomeViewModel
import com.daqsoft.module_home.viewmodel.MessageViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideApplicationFactory
import kotlinx.android.synthetic.main.recyclerview_message_title.view.*
import timber.log.Timber

/**
 * @package name：com.daqsoft.module_mine.viewmodel
 * @date 6/11/2020 下午 2:11
 * @author zp
 * @describe 消息 title viewModel
 */
class MessageTitleViewModel (
    private val messageViewModel : MessageViewModel,
    val title:String
) : MultiItemViewModel<MessageViewModel>(messageViewModel){

    val pairObservable = ObservableField<Pair<String,String>>()

    init {
        val  data = title.split("-")
        pairObservable.set(Pair(data[2],messageViewModel.getApplication<Application>().resources.getString(R.string.month,data[1])))
    }

}
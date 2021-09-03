package com.daqsoft.module_home.repository

import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_common.pojo.vo.NextMessage
import com.daqsoft.module_home.repository.pojo.vo.Message
import com.daqsoft.module_home.repository.pojo.vo.NewsBrief
import com.daqsoft.mvvmfoundation.base.BaseModel
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

/**
 * @package name：com.daqsoft.module_mine.repository
 * @date 6/11/2020 下午 2:11
 * @author zp
 * @describe
 */
class HomeRepository  @Inject constructor(private val homeApiService: HomeApiService) : BaseModel(), HomeApiService {

    override fun getAllNewsList(): Observable<AppResponse<NewsBrief>> {
        return homeApiService.getAllNewsList()
    }

    override fun markAllAsRead(state : Int): Observable<AppResponse<Any>> {
        return homeApiService.markAllAsRead(state)
    }

    override fun readSingle(id: String): Observable<AppResponse<Any>> {
        return homeApiService.readSingle(id)
    }

    override fun getMessage(state: Int?, page: Int, size: Int): Observable<AppResponse<Message>> {
        return homeApiService.getMessage(state, page, size)
    }

    override fun top(id: String,top:Boolean): Observable<AppResponse<Any>> {
        return homeApiService.top(id, top)
    }


    override fun saveScanTime(body: Map<String, String>): Observable<AppResponse<String>> {
        return homeApiService.saveScanTime(body)
    }

    override fun getNextDetail(id: Int): Observable<AppResponse<NextMessage>> {
        return homeApiService.getNextDetail(id)
    }
}
package www.clem.com.wankotlin.view

import top.jowanxu.wanandroidclient.bean.TreeListResponse

/**
 * Created by laileon on 2018/4/2.
 */
interface TypeFragmentView {
    /**
     * get Type list Success
     */
    fun getTypeListSuccess(result: TreeListResponse)

    /**
     * get Type list Failed
     */
    fun getTypeListFailed(errorMessage: String?)

    /**
     * get Type list data size equal zero
     */
    fun getTypeListZero()
}
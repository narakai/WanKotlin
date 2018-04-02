package www.clem.com.wankotlin.presenter

import top.jowanxu.wanandroidclient.bean.TreeListResponse
import top.jowanxu.wanandroidclient.model.HomeModel
import top.jowanxu.wanandroidclient.model.HomeModelImpl
import top.jowanxu.wanandroidclient.presenter.HomePresenter
import www.clem.com.wankotlin.view.TypeFragmentView

/**
 * Created by laileon on 2018/4/2.
 */
class TypeFragmentPresenterImpl(private val typeFragmentView: TypeFragmentView) :
        HomePresenter.OnTypeTreeListListener {

    private val homeModel: HomeModel = HomeModelImpl()

    //传给model取值
    override fun getTypeTreeList() {
        homeModel.getTypeTreeList(this)
    }

    //model取值回调presenter
    override fun getTypeTreeListSuccess(result: TreeListResponse) {
        if (result.errorCode != 0) {
            typeFragmentView.getTypeListFailed(result.errorMsg)
            return
        }
        if (result.data.isEmpty()) {
            typeFragmentView.getTypeListZero()
            return
        }
        typeFragmentView.getTypeListSuccess(result)
    }

    //model取值回调presenter
    override fun getTypeTreeListFailed(errorMessage: String?) {
        typeFragmentView.getTypeListFailed(errorMessage)
    }

    /**
     * cancel request
     */
    fun cancelRequest() {
        homeModel.cancelTypeTreeRequest()
    }

}
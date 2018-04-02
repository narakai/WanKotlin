package www.clem.com.wankotlin.ui.fragment

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.fragment_type.*
import toast
import top.jowanxu.wanandroidclient.adapter.TypeAdapter
import top.jowanxu.wanandroidclient.bean.TreeListResponse
import www.clem.com.wankotlin.R
import www.clem.com.wankotlin.base.BaseFragment
import www.clem.com.wankotlin.presenter.TypeFragmentPresenterImpl
import www.clem.com.wankotlin.view.TypeFragmentView

/**
 * Created by laileon on 2018/4/2.
 */
class TypeFragment : BaseFragment(), TypeFragmentView {
    /**
     * mainView
     */
    private var mainView: View? = null
    private val datas = mutableListOf<TreeListResponse.Data>()
    private val typeFragmentPresenter: TypeFragmentPresenterImpl by lazy {
        TypeFragmentPresenterImpl(this)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        mainView ?: let {
            mainView = inflater.inflate(R.layout.fragment_type, container, false)
        }
        return mainView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        typeSwipeRefreshLayout.run {
            isRefreshing = true
            setOnRefreshListener(onRefreshListener)
        }
        typeRecyclerView.run {
            layoutManager = LinearLayoutManager(activity)
            adapter = typeAdapter
        }
        typeAdapter.run {
            bindToRecyclerView(typeRecyclerView)
            setEmptyView(R.layout.fragment_home_empty)
            onItemClickListener = this@TypeFragment.onItemClickListener
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden && isFirst) {
            typeFragmentPresenter.getTypeTreeList()
            isFirst = false
        }
    }

    /**
     * RefreshListener
     */
    private val onRefreshListener = SwipeRefreshLayout.OnRefreshListener {
        refreshData()
    }

    /**
     * refresh
     */
    fun refreshData() {
        typeSwipeRefreshLayout.isRefreshing = true
        typeFragmentPresenter.getTypeTreeList()
    }

    fun smoothScrollToPosition() = typeRecyclerView.scrollToPosition(0)

    /**
     * ItemClickListener
     */
    private val onItemClickListener = BaseQuickAdapter.OnItemClickListener { _, _, position ->
        //        if (datas.size != 0) {
//            Intent(activity, TypeContentActivity::class.java).run {
//                putExtra(Constant.CONTENT_TITLE_KEY, datas[position].name)
//                putExtra(Constant.CONTENT_CHILDREN_DATA_KEY, datas[position])
//                startActivity(this)
//            }
//        }
    }

    private val typeAdapter: TypeAdapter by lazy {
        TypeAdapter(activity, datas)
    }

    override fun getTypeListSuccess(result: TreeListResponse) {
        result.data.let {
            if (typeSwipeRefreshLayout.isRefreshing) {
                typeAdapter.replaceData(it)
            } else {
                typeAdapter.addData(it)
            }
        }
        typeSwipeRefreshLayout.isRefreshing = false
    }

    override fun cancelRequest() {
        typeFragmentPresenter.cancelRequest()
    }

    override fun getTypeListFailed(errorMessage: String?) {
//        通过let语句，在?.let之后，如果为空不会有任何操作，只有在非空的时候才会执行let之后的操作
//        ?:符号会在对于空的情况才会进行下面的处理，跟?.let正好相反
        errorMessage?.let {
            activity.toast(it)
        } ?: let {
            activity.toast(getString(R.string.get_data_error))
        }
        typeSwipeRefreshLayout.isRefreshing = false
    }

    override fun getTypeListZero() {
        activity.toast(getString(R.string.get_data_zero))
        typeSwipeRefreshLayout.isRefreshing = false
    }
}
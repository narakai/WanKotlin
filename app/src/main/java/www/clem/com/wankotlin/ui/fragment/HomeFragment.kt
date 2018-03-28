package www.clem.com.wankotlin.ui.fragment

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import inflater
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import toast
import top.jowanxu.wanandroidclient.adapter.BannerAdapter
import top.jowanxu.wanandroidclient.adapter.HomeAdapter
import top.jowanxu.wanandroidclient.bean.BannerResponse
import top.jowanxu.wanandroidclient.bean.Datas
import top.jowanxu.wanandroidclient.bean.HomeListResponse
import top.jowanxu.wanandroidclient.presenter.HomeFragmentPresenterImpl
import top.jowanxu.wanandroidclient.view.CollectArticleView
import top.jowanxu.wanandroidclient.view.HomeFragmentView
import www.clem.com.wankotlin.R
import www.clem.com.wankotlin.base.BaseFragment
import www.clem.com.wankotlin.ui.view.HorizontalRecyclerView

/**
 * Created by laileon on 2018/3/28.
 */
class HomeFragment : BaseFragment(), HomeFragmentView, CollectArticleView {
    companion object {
        private const val BANNER_TIME = 5000L
    }

    /**
     * mainView
     */
    private var mainView: View? = null

    /**
     * Data List
     */
    private val datas = mutableListOf<Datas>()

    /**
     * adapter
     */
    private val homeAdapter: HomeAdapter by lazy {
        HomeAdapter(activity, datas)
    }

    /**
     * Banner
     */
    private lateinit var bannerRecyclerView: HorizontalRecyclerView

    /**
     * Banner data
     */
    private val bannerDatas = mutableListOf<BannerResponse.Data>()
    /**
     * Banner RecyclerView adapter
     */
    private val bannerAdapter: BannerAdapter by lazy {
        BannerAdapter(activity, bannerDatas)
    }
    /**
     * Banner PagerSnapHelper
     */
    private val bannerPagerSnap: PagerSnapHelper by lazy {
        PagerSnapHelper()
    }
    /**
     * LinearLayoutManager
     */
    private val linearLayoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
    }
    /**
     * Banner switch job
     */
    private var bannerSwitchJob: Job? = null
    /**
     * save current index
     */
    private var currentIndex = 0

    /**
     * presenter
     */
    private val homeFragmentPresenter: HomeFragmentPresenterImpl by lazy {
        HomeFragmentPresenterImpl(this, this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mainView ?: let {
            mainView = inflater.inflate(R.layout.fragment_home, container, false)
            bannerRecyclerView = activity.inflater(R.layout.home_banner) as HorizontalRecyclerView
        }
        return mainView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        swipeRefreshLayout.run {
            isRefreshing = true
            setOnRefreshListener(onRefreshListener)
        }
        recyclerView.run {
            layoutManager = LinearLayoutManager(activity)
            adapter = homeAdapter
        }
        bannerRecyclerView.run {
            layoutManager = linearLayoutManager
            bannerPagerSnap.attachToRecyclerView(this)
            requestDisallowInterceptTouchEvent(true)
            setOnTouchListener(onTouchListener)
            addOnScrollListener(onScrollListener)
        }
        bannerAdapter.run {
            bindToRecyclerView(bannerRecyclerView)
//            onItemClickListener = this@HomeFragment.onBannerItemClickListener
        }
        homeAdapter.run {
            bindToRecyclerView(recyclerView)
            setOnLoadMoreListener(onRequestLoadMoreListener, recyclerView)
//            onItemClickListener = this@HomeFragment.onItemClickListener
//            onItemChildClickListener = this@HomeFragment.onItemChildClickListener
            addHeaderView(bannerRecyclerView)
            setEmptyView(R.layout.fragment_home_empty)
        }
        homeFragmentPresenter.getBanner()
        homeFragmentPresenter.getHomeList()
    }

    /**
     * pause banner switch
     */
    override fun onPause() {
        super.onPause()
        cancelSwitchJob()
    }

    /**
     * resume banner switch
     */
    override fun onResume() {
        super.onResume()
        startSwitchJob()
    }

    /**
     * if hidden to cancel, else to start
     */
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            cancelSwitchJob()
        } else {
            startSwitchJob()
        }
    }

    /**
     * cancel request
     */
    override fun cancelRequest() {
        homeFragmentPresenter.cancelRequest()
    }

    /**
     * scroll to top
     */
    fun smoothScrollToPosition() = recyclerView.scrollToPosition(0)

    /**
     * refresh
     */
    fun refreshData() {
        swipeRefreshLayout.isRefreshing = true
        homeAdapter.setEnableLoadMore(false)
        cancelSwitchJob()
        homeFragmentPresenter.getBanner()
        homeFragmentPresenter.getHomeList()
    }

    /**
     * get Banner Success
     * @param result BannerResponse
     */
    override fun getBannerSuccess(result: BannerResponse) {
        result.data?.let {
            bannerAdapter.replaceData(it)
            startSwitchJob()
        }
    }

    /**
     * get Banner Failed
     * @param errorMessage error message
     */
    override fun getBannerFailed(errorMessage: String?) {
        errorMessage?.let {
            activity.toast(it)
        } ?: let {
            activity.toast(getString(R.string.get_data_error))
        }
    }

    /**
     * get Banner data size equal zero
     */
    override fun getBannerZero() {
        activity.toast(getString(R.string.get_data_zero))
    }

    override fun getHomeListZero() {
        activity.toast(getString(R.string.get_data_zero))
        swipeRefreshLayout.isRefreshing = false
    }

    override fun getHomeListSmall(result: HomeListResponse) {
        result.data.datas?.let {
            homeAdapter.run {
                replaceData(it)
                loadMoreComplete()
                loadMoreEnd()
                setEnableLoadMore(false)
            }
        }
        swipeRefreshLayout.isRefreshing = false
    }

    override fun getHomeListSuccess(result: HomeListResponse) {
        result.data.datas?.let {
            homeAdapter.run {
                // 列表总数
                val total = result.data.total
                // 当前总数
                if (result.data.offset >= total || data.size >= total) {
                    loadMoreEnd()
                    return@let
                }
                if (swipeRefreshLayout.isRefreshing) {
                    replaceData(it)
                } else {
                    addData(it)
                }
                loadMoreComplete()
                setEnableLoadMore(true)
            }
        }
        swipeRefreshLayout.isRefreshing = false
    }

    override fun getHomeListFailed(errorMessage: String?) {
        homeAdapter.setEnableLoadMore(false)
        homeAdapter.loadMoreFail()
        errorMessage?.let {
            activity.toast(it)
        } ?: let {
            activity.toast(getString(R.string.get_data_error))
        }
        swipeRefreshLayout.isRefreshing = false
    }

    /**
     * add article success
     * @param result HomeListResponse
     * @param isAdd true add, false remove
     */
    override fun collectArticleSuccess(result: HomeListResponse, isAdd: Boolean) {
        activity.toast(
                if (isAdd) activity.getString(R.string.bookmark_success) else activity.getString(
                        R.string.bookmark_cancel_success
                )
        )
    }

    /**
     * add article false
     * @param errorMessage error message
     * @param isAdd true add, false remove
     */
    override fun collectArticleFailed(errorMessage: String?, isAdd: Boolean) {
        activity.toast(
                if (isAdd) activity.getString(
                        R.string.bookmark_failed,
                        errorMessage
                ) else activity.getString(R.string.bookmark_cancel_failed, errorMessage)
        )
    }

    /**
     * RefreshListener
     */
    private val onRefreshListener = SwipeRefreshLayout.OnRefreshListener {
        refreshData()
    }

    /**
     * LoadMoreListener
     */
    private val onRequestLoadMoreListener = BaseQuickAdapter.RequestLoadMoreListener {
        val page = homeAdapter.data.size / 20 + 1
        homeFragmentPresenter.getHomeList(page)
    }

    /**
     * SCROLL_STATE_IDLE to start job
     */
    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            when (newState) {
                RecyclerView.SCROLL_STATE_IDLE -> {
                    currentIndex = linearLayoutManager.findFirstVisibleItemPosition()
                    startSwitchJob()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cancelSwitchJob()
    }

    /**
     * ACTION_MOVE to cancel job
     */
    private val onTouchListener = View.OnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                cancelSwitchJob()
            }
        }
        false
    }

    /**
     * get Banner switch job
     */
    private fun getBannerSwitchJob() = launch {
        repeat(Int.MAX_VALUE) {
            if (bannerDatas.size == 0) {
                return@launch
            }
            delay(BANNER_TIME)
            currentIndex++
            val index = currentIndex % bannerDatas.size
            bannerRecyclerView.smoothScrollToPosition(index)
            currentIndex = index
        }
    }

    /**
     * resume banner switch
     */
    private fun startSwitchJob() = bannerSwitchJob?.run {
        if (!isActive) {
            bannerSwitchJob = getBannerSwitchJob().apply { start() }
        }
    } ?: let {
        bannerSwitchJob = getBannerSwitchJob().apply { start() }
    }

    /**
     * cancel banner switch
     */
    private fun cancelSwitchJob() = bannerSwitchJob?.run {
        if (isActive) {
            cancel()
        }
    }

}
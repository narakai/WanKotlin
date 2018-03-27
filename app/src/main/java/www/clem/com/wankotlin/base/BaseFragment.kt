package www.clem.com.wankotlin.base

import android.support.v4.app.Fragment

/**
 * Created by laileon on 2018/3/27.
 */
abstract class BaseFragment : Fragment() {

    protected var isFirst: Boolean = true

    /**
     * cancel request
     */
    protected abstract fun cancelRequest()

    override fun onDestroyView() {
        super.onDestroyView()
        cancelRequest()
    }
}
package top.jowanxu.wanandroidclient.adapter

import android.content.Context
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import top.jowanxu.wanandroidclient.bean.FriendListResponse
import www.clem.com.wankotlin.R

class CommonAdapter(val context: Context, datas: MutableList<FriendListResponse.Data>) :
    BaseQuickAdapter<FriendListResponse.Data, BaseViewHolder>(R.layout.common_list_item, datas) {
    override fun convert(helper: BaseViewHolder, item: FriendListResponse.Data?) {
        item ?: return
        helper.setText(R.id.commonItemTitle, item.name.trim())
    }
}
package com.android04.capstonedesign.ui.insight

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android04.capstonedesign.R
import com.android04.capstonedesign.data.dto.SearchCategorySetting
import com.android04.capstonedesign.databinding.ItemInsightLocationCategorySettingBinding

// 인사이트 화면 상세 쿼리 리사이클러뷰 어댑터

class InsightCategorySettingAdapter: ListAdapter<SearchCategorySetting, InsightCategorySettingAdapter.ViewHolder>(diffUtil) {
    private var listener : OnItemCheckListener? = null
    private var checkedStatus = Array<Boolean>(50){false}
    fun setOnItemCheckListener(listener : OnItemCheckListener) {
        this.listener = listener
    }

    fun setCheckedStatus(list: MutableList<SearchCategorySetting>) {
        checkedStatus = Array<Boolean>(list.size){false}
        for (i in list.indices) {
            if (list[i].isChecked) {
                checkedStatus[i] = true
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemInsightLocationCategorySettingBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_insight_location_category_setting,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InsightCategorySettingAdapter.ViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
        holder.binding.apply {
            cbCheck.setOnClickListener {
                checkedStatus[position] = !checkedStatus[position]
                data.isChecked = checkedStatus[position]
                if(data.isExpandable) {
                    for (i in data.expandableList.indices) {
                        data.expandableList[i] = checkedStatus[position]
                    }
                    this.item = data
                    executePendingBindings()
                }
                listener?.onItemClick(it, data, position, checkedStatus[position])
            }
//            cbCheck.setOnCheckedChangeListener { buttonView, isChecked ->
//                data.isChecked = isChecked
//
//            }
            setOnOffClickListener(this, data)
            setListener(this, data, position)
        }
    }

    inner class ViewHolder(val binding: ItemInsightLocationCategorySettingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SearchCategorySetting) {
            binding.apply {
                item.isChecked = checkedStatus[adapterPosition]
                this.item = item
                startNum = item.name.substring(0,1)
                executePendingBindings()
            }
        }
    }

    private fun setOnOffClickListener(binding: ItemInsightLocationCategorySettingBinding, item: SearchCategorySetting){
        binding.apply {
            ivOpen.setOnClickListener {
                if (item.isExpanded) {
                    item.isExpanded = false
                    clSubCb.visibility = View.GONE
                    ivOpen.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
                } else {
                    item.isExpanded = true
                    clSubCb.visibility = View.VISIBLE
                    ivOpen.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
                }

            }
        }
    }

    private fun setListener(binding: ItemInsightLocationCategorySettingBinding, item: SearchCategorySetting, pos: Int) {
        binding.apply {
            customListener(cbCheck0, item, pos, this)
            customListener(cbCheck1, item, pos, this)
            customListener(cbCheck2, item, pos, this)
            customListener(cbCheck3, item, pos, this)
            customListener(cbCheck4, item, pos, this)
            customListener(cbCheck5, item, pos, this)
            customListener(cbCheck6, item, pos, this)
            customListener(cbCheck7, item, pos, this)
            customListener(cbCheck8, item, pos, this)
            customListener(cbCheck9, item, pos, this)
        }
    }

    private fun customListener(v: CheckBox, item: SearchCategorySetting, pos: Int, binding: ItemInsightLocationCategorySettingBinding) {
        v.setOnCheckedChangeListener { buttonView, isChecked ->
            val num = buttonView.text.last().toString().toInt()
            item.expandableList[num] = isChecked
            listener?.onItemClick(buttonView, item, pos, isChecked)
            if (!isChecked) {
                binding.cbCheck.isChecked = false
                checkedStatus[pos] = false
                item.isChecked = checkedStatus[pos]
                listener?.onItemClick(v, item, pos, checkedStatus[pos])
            } else {
                if(!item.expandableList.contains(false)) {
                    binding.cbCheck.isChecked = true
                    checkedStatus[pos] = true
                    item.isChecked = checkedStatus[pos]
                    listener?.onItemClick(v, item, pos, checkedStatus[pos])
                }
            }
        }
    }

    fun clearCheck() {
        checkedStatus.forEachIndexed { index, _ -> checkedStatus[index] = false  }
    }


    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<SearchCategorySetting>() {
            override fun areContentsTheSame(oldItem: SearchCategorySetting, newItem: SearchCategorySetting) =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: SearchCategorySetting, newItem: SearchCategorySetting) =
                oldItem.name == newItem.name
        }
    }

    interface OnItemCheckListener {
        fun onItemClick(v: View, data: SearchCategorySetting, pos: Int, isChecked: Boolean)
    }

}
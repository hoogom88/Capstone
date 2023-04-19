package com.android04.capstonedesign.ui.insight

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.android04.capstonedesign.R
import com.android04.capstonedesign.common.TAB_APP
import com.android04.capstonedesign.common.TAB_LOCATION
import com.android04.capstonedesign.data.dto.ProductStatusDTO
import com.android04.capstonedesign.data.dto.SearchCategory
import com.android04.capstonedesign.data.dto.SearchCategorySetting
import com.android04.capstonedesign.databinding.ActivityInsightMainBinding
import com.android04.capstonedesign.ui.base.BaseActivity
import com.android04.capstonedesign.ui.insight.app.InsightAppFragment
import com.android04.capstonedesign.ui.insight.location.InsightLocationFragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

// 인사이트 화면 액티비티

@AndroidEntryPoint
class InsightMainActivity :
    BaseActivity<ActivityInsightMainBinding, InsightMainViewModel>(R.layout.activity_insight_main) {
    override val viewModel: InsightMainViewModel by viewModels()
    private val tabNameList = mutableListOf<String>(
        TAB_LOCATION,
        TAB_APP
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel
        initCategory()
        initCategorySetting()
        setObserver()
        initBinding()
    }

    private fun initBinding() {
        viewModel.loadProductStatus()
        binding.apply {
            btnSearch.setOnClickListener {
                closeRV()
                this@InsightMainActivity.viewModel.fetchInsightData(tabNameList[tbInsight.selectedTabPosition])
            }
            btnAddTag.setOnClickListener {
                if (this@InsightMainActivity.viewModel.queryTags.value!!.size < 6) {
                    if (this@InsightMainActivity.viewModel.checkTagDuplicate()) {
                        Toast.makeText(this@InsightMainActivity, "Same query already exists", Toast.LENGTH_SHORT).show()
                    } else {
                        this@InsightMainActivity.viewModel.addQueryTag()
                        closeRV()
                        clearRV()
                    }
                } else {
                    Toast.makeText(this@InsightMainActivity, "You can add up to six queries", Toast.LENGTH_SHORT).show()
                }

            }
            rvTag.adapter = InsightQueryTagRecyclerAdapter()
            (rvTag.adapter as InsightQueryTagRecyclerAdapter).setOnItemCheckListener(object :
                InsightQueryTagRecyclerAdapter.OnItemCheckListener {
                override fun onItemClick(pos: Int) {
                    this@InsightMainActivity.viewModel.removeTag(pos)
                }
            })
        }
    }

    private fun setObserver() {
        viewModel.settingOn.observe(this) {
            categorySettingOnOff(it)
        }
        viewModel.queryTags.observe(this) {
            it.forEach { Log.d(TAG, "queryTags observed: $it") }
            (binding.rvTag.adapter as InsightQueryTagRecyclerAdapter).submitList(it.toMutableList())
        }
        viewModel.productStatus.observe(this) {
            initViewPager(it)
        }
    }

    private fun closeRV() {
        for(i in 0..binding.rvCategory.childCount) {
            binding.rvCategory.layoutManager?.findViewByPosition(i)?.findViewById<ImageView>(R.id.iv_open)?.visibility = View.VISIBLE
            binding.rvCategory.layoutManager?.findViewByPosition(i)?.findViewById<ImageView>(R.id.iv_close)?.visibility = View.GONE
        }
        viewModel.settingRVOnOff(false, true)
    }

    private fun clearRV() {
        (binding.rvCategorySetting.adapter as InsightCategorySettingAdapter).clearCheck()
        viewModel.resetRV()
    }

    private fun categorySettingOnOff(isShow: Boolean) {
        val openAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.setting_on)
        val closeAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.setting_off)
        binding.apply {
            rvCategorySetting.bringToFront()
            rvCategory.bringToFront()
            view1.bringToFront()
            rvCategorySetting.visibility = if(isShow) View.VISIBLE else View.GONE
            if (isShow) {
                view2.startAnimation(openAnimation)
                rvCategorySetting.startAnimation(openAnimation)
            } else {
                view2.startAnimation(closeAnimation)
                rvCategorySetting.startAnimation(closeAnimation)
            }
            view2.bringToFront()
            view2. visibility  = if(isShow) View.VISIBLE else View.GONE
        }

    }

    private fun initCategory() {
        binding.view1.bringToFront()
        binding.rvCategory.adapter = InsightCategoryAdapter()
        (binding.rvCategory.adapter as InsightCategoryAdapter).submitList(viewModel.initCategoryList())
        (binding.rvCategory.adapter as InsightCategoryAdapter).setOnOpenClickListener(object :
            InsightCategoryAdapter.OnItemClickListener {
            override fun onItemClick(v: View, data: SearchCategory, pos: Int) {
                changeCategorySetting(data)
                viewModel.settingRVOnOff(true)
                resetArrow(pos)
            }
        })
        (binding.rvCategory.adapter as InsightCategoryAdapter).setOnCloseCheckListener(object :
            InsightCategoryAdapter.OnItemClickListener {
            override fun onItemClick(v: View, data: SearchCategory, pos: Int) {
                viewModel.settingRVOnOff(false)
            }
        })
    }

    private fun resetArrow(pos: Int){
        for(i in 0..binding.rvCategory.childCount) {
            binding.rvCategory.layoutManager?.findViewByPosition(i)?.findViewById<ImageView>(R.id.iv_open)?.visibility = View.VISIBLE
            binding.rvCategory.layoutManager?.findViewByPosition(i)?.findViewById<ImageView>(R.id.iv_close)?.visibility = View.GONE
        }
        binding.rvCategory.layoutManager?.findViewByPosition(pos)?.findViewById<ImageView>(R.id.iv_open)?.visibility = View.GONE
        binding.rvCategory.layoutManager?.findViewByPosition(pos)?.findViewById<ImageView>(R.id.iv_close)?.visibility = View.VISIBLE
    }

    private fun initCategorySetting() {
        binding.rvCategorySetting.adapter = InsightCategorySettingAdapter()
        (binding.rvCategorySetting.adapter as InsightCategorySettingAdapter).setOnItemCheckListener(object :
            InsightCategorySettingAdapter.OnItemCheckListener {
            override fun onItemClick(v: View, data: SearchCategorySetting, pos: Int, isChecked: Boolean) {
                viewModel.checkSetting(data, isChecked, pos)
            }
        })
    }

    private fun changeCategorySetting(data: SearchCategory) {
        initCategorySetting()
        val newData = viewModel.getSettingList(data.name)
        (binding.rvCategorySetting.adapter as InsightCategorySettingAdapter).submitList(newData)
        (binding.rvCategorySetting.adapter as InsightCategorySettingAdapter).setCheckedStatus(
            newData as MutableList<SearchCategorySetting>
        )
    }

    private fun initViewPager(status: ProductStatusDTO) {
        binding.vpInsight.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.vpInsight.isUserInputEnabled = false
        binding.vpInsight.adapter = InsightPagerAdapter(this)
        val fragmentList = mutableListOf<Fragment>()
        if (status.locationInsight) {
            fragmentList.add(InsightLocationFragment())
            if (status.appInfoInsight) {
                fragmentList.add(InsightAppFragment())
            } else {
                tabNameList.removeAt(1)
            }
        } else {
            tabNameList.removeAt(0)
            if (status.appInfoInsight) {
                fragmentList.add(InsightAppFragment())
            } else {
                Toast.makeText(this, "There are no available insight.\n" +
                        "Subscribe product first", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        (binding.vpInsight.adapter as InsightPagerAdapter).setFragment(fragmentList)
        TabLayoutMediator(binding.tbInsight, binding.vpInsight) { tab, position ->
            tab.text = tabNameList[position]
        }.attach()
    }

    override fun onBackPressed() {
        clearRV()
        finish()
    }

    companion object {
        const val TAG = "InsightMainActivity"
    }
}
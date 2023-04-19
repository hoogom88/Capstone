package com.android04.capstonedesign.common

import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import com.airbnb.lottie.LottieAnimationView
import com.android04.capstonedesign.data.dto.ProductDTO
import com.android04.capstonedesign.data.dto.SearchCategorySetting
import com.android04.capstonedesign.data.room.entity.LocationLog

object BindingAdapter { // 바인딩 어댑터
    @JvmStatic
    @BindingAdapter("setTime")
    fun setTime(view: TextView, time:Long) {
        view.text = time.toDate()
    }

    @JvmStatic
    @BindingAdapter("setLocation")
    fun setLocation(view: TextView, item: LocationLog) {
        view.text = "(${item.latitude.toString().substring(0,6)}, ${item.longitude.toString().substring(0,7)})"
    }

    @JvmStatic
    @BindingAdapter("setVisibility")
    fun setVisibility(view: View, isSubscribed: LiveData<Boolean>?) {
        view.visibility = if (isSubscribed?.value == true) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("setDescription")
    fun setDescription(view: TextView, item: ProductDTO?) {
        if (item != null) view.text = item.description.replace("@n", "\n")
    }

    @JvmStatic
    @BindingAdapter("setLottieLoading")
    fun setLottieLoading(view: LottieAnimationView, isLoading: Boolean?) {
        if (isLoading == true) {
            view.playAnimation()
            view.visibility = View.VISIBLE
        } else {
            view.pauseAnimation()
            view.visibility = View.GONE
        }
    }

    @JvmStatic
    @BindingAdapter("setLottie")
    fun setLottie(view: LottieAnimationView, isLoading: Boolean?) {
        if (isLoading == true) {
            view.playAnimation()
            view.visibility = View.VISIBLE
        } else {
            view.pauseAnimation()
            view.visibility = View.VISIBLE
        }
    }

    @JvmStatic
    @BindingAdapter("setLoadingStatus")
    fun setLoadingStatus(view: Button, isLoading: Boolean?) {
        view.isEnabled = isLoading != true
    }

    @JvmStatic
    @BindingAdapter("setSubOpener")
    fun setSubOpener(view: ImageView, item: SearchCategorySetting) {
        if (item.isExpandable) view.visibility = View.VISIBLE else view.visibility = View.INVISIBLE
        if(item.name.last() == '+') {
            view.visibility = View.INVISIBLE
        }
    }

    @JvmStatic
    @BindingAdapter("setSubCheck", "setNumber")
    fun setSubCheck(view: CheckBox, item: SearchCategorySetting, num: Char) {
        view.isChecked = item.expandableList[num.toString().toInt()]
    }


}
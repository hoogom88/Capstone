package com.android04.capstonedesign.ui.insight.location

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.activityViewModels
import com.android04.capstonedesign.R
import com.android04.capstonedesign.data.dto.LocationQueryDTO
import com.android04.capstonedesign.databinding.FragmentInsightLocationBinding
import com.android04.capstonedesign.ui.base.BaseFragment
import com.android04.capstonedesign.ui.insight.InsightMainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class InsightLocationFragment : BaseFragment<FragmentInsightLocationBinding, InsightMainViewModel>(R.layout.fragment_insight_location), OnMapReadyCallback,
    GoogleMap.OnCameraIdleListener {
    override val viewModel: InsightMainViewModel by activityViewModels<InsightMainViewModel>()
    lateinit var myMap: GoogleMap
    private val colorList = mutableListOf<Int>(Color.rgb(255,0,0), Color.rgb(255,0,171), Color.rgb(0,64,255), Color.rgb(255,107,0), Color.rgb(255, 187, 0), Color.rgb(68, 255, 0))
    private var lastZoomScale = 0.0f
    private var resizedData = mutableListOf<Int>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBinding()
        initMap()
        setObserver()
        initListener()
    }

    private fun resizeMarker(zoomScale: Float) {
        if (viewModel.locationData.value == null || viewModel.locationData.value?.isNullOrEmpty() == true) return
        val locationData = viewModel.locationData.value!!
        myMap.clear()
        for (data in locationData){
            for (idx in data.data.indices) {
                val dt = data.data[idx]
                val location = LatLng(dt.latitude.toDouble(), dt.longitude.toDouble())
                val colorIdx = colorList.indexOf(data.color)
                val count = resizedData[idx]
                myMap.addMarker(
                    MarkerOptions()
                        .position(location)
                        .icon(BitmapDescriptorFactory.fromBitmap(resizeMarker(count, colorIdx)))
                        .title("${dt.sex}, ${dt.age}, ${count}s")
                )
            }
        }
    }

    private fun initListener() {
        binding.apply {

        }
    }

    private fun setObserver() {
        viewModel.locationData.observe(viewLifecycleOwner) {
            setMapMarker(it)
        }
    }

    private fun initBinding() {
        binding.viewModel = viewModel
    }

    private fun initMap() {
        val mapFragment = this.childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        Log.d("setMap", "myMap = map")
        myMap = map
//        myMap.setOnCameraIdleListener(this)
    }

    private fun setMapMarker(data_: MutableList<LocationQueryDTO>) {
        myMap.clear()
        resizedData.clear()
        if (data_.isEmpty() || data_[0].data.isEmpty()) return
        var avgLocation = LatLng(data_[0].data[0].latitude.toDouble(), data_[0].data[0].longitude.toDouble())
        for (idx in data_.indices){
            val data = data_[idx]
            for (dt in data.data) {
                Log.d(TAG, "setMapMarker: $dt")
                val location = LatLng(dt.latitude.toDouble(), dt.longitude.toDouble())
                avgLocation = LatLng(
                    (avgLocation.latitude + location.latitude) / 2,
                    (avgLocation.longitude + location.longitude) / 2
                )
                val colorIdx = colorList.indexOf(data.color)
                val count = getSize(dt.count.split(".")[0].toInt())
                resizedData.add(count)
                myMap.addMarker(
                    MarkerOptions()
                        .position(location)
                        .icon(BitmapDescriptorFactory.fromBitmap(resizeMarker(count, colorIdx)))
                        .title("${dt.sex}, ${dt.age}, ${count}s")
                )
            }

        }
        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(avgLocation, 14f))
        myMap.setOnCameraIdleListener(this)
    }

    private fun getSize(count_: Int): Int {
        val count = count_ * 40 + Random().nextInt(50)
        return (count * (myMap.cameraPosition.zoom / 15)).toInt()
    }

    private fun resizeMarker(width: Int, colorIdx: Int): Bitmap {
        val drawable = when(colorIdx) {
            0 -> R.drawable.ic_marker0
            1 -> R.drawable.ic_marker1
            2 -> R.drawable.ic_marker2
            3 -> R.drawable.ic_marker3
            4 -> R.drawable.ic_marker4
            else -> R.drawable.ic_marker5
        }
        val bitmap = AppCompatResources.getDrawable(requireContext(), drawable)!!.toBitmap()
        return Bitmap.createScaledBitmap(bitmap, width, width, false)

    }

    override fun onCameraIdle() {
        if(!::myMap.isInitialized) return
        val newScale = myMap.cameraPosition.zoom
        if (Math.abs(newScale - lastZoomScale) > 0.5) {
            resizeMarker(newScale)
            lastZoomScale = newScale
        }
    }

    companion object {
        const val TAG = "InsightLocationFragment"
    }

}
package com.android04.capstonedesign.data.dto

data class SearchCategory(
    val name: String,
    var open: Boolean
)

data class SearchCategorySetting(
    val name: String,
    var isChecked: Boolean = false,
    val isExpandable: Boolean = false,
    val expandableList: Array<Boolean> = Array<Boolean>(10){false},
    var isExpanded: Boolean = false
)

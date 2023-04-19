package com.android04.capstonedesign.common

import com.android04.capstonedesign.data.dto.SearchCategory
import com.android04.capstonedesign.data.dto.SearchCategorySetting

object CategorySetting { // 인사이트 화면 쿼리 옵션 데이터
    val category = mutableListOf<SearchCategory>(SearchCategory("Gender", false), SearchCategory("Age", false), SearchCategory("Time", false),SearchCategory("Day", false),SearchCategory("Month", false))
    val sexList = mutableListOf<SearchCategorySetting>(SearchCategorySetting("male"), SearchCategorySetting("female"))
    val ageList = mutableListOf<SearchCategorySetting>(
        SearchCategorySetting("10's", isExpandable = true),
        SearchCategorySetting("20's", isExpandable = true),
        SearchCategorySetting("30's", isExpandable = true),
        SearchCategorySetting("40's", isExpandable = true),
        SearchCategorySetting("50's", isExpandable = true),
        SearchCategorySetting("60+", isExpandable = true))
    val timeList = mutableListOf<SearchCategorySetting>(
        SearchCategorySetting("00 ~ 01"),
        SearchCategorySetting("01 ~ 02"),
        SearchCategorySetting("02 ~ 03"),
        SearchCategorySetting("03 ~ 04"),
        SearchCategorySetting("04 ~ 05"),
        SearchCategorySetting("05 ~ 06"),
        SearchCategorySetting("06 ~ 07"),
        SearchCategorySetting("07 ~ 08"),
        SearchCategorySetting("08 ~ 09"),
        SearchCategorySetting("09 ~ 10"),
        SearchCategorySetting("10 ~ 11"),
        SearchCategorySetting("11 ~ 12"),
        SearchCategorySetting("12 ~ 13"),
        SearchCategorySetting("13 ~ 14"),
        SearchCategorySetting("14 ~ 15"),
        SearchCategorySetting("15 ~ 16"),
        SearchCategorySetting("16 ~ 17"),
        SearchCategorySetting("17 ~ 18"),
        SearchCategorySetting("18 ~ 19"),
        SearchCategorySetting("19 ~ 20"),
        SearchCategorySetting("20 ~ 21"),
        SearchCategorySetting("21 ~ 22"),
        SearchCategorySetting("22 ~ 23"),
        SearchCategorySetting("23 ~ 24"),
        )
    val monthList = MutableList(12, ) {SearchCategorySetting((it+1).toString())}
    val dayList = mutableListOf<SearchCategorySetting>(
        SearchCategorySetting("Mon"),
        SearchCategorySetting("Tue"),
        SearchCategorySetting("Wed"),
        SearchCategorySetting("Thu"),
        SearchCategorySetting("Fri"),
        SearchCategorySetting("Sat"),
        SearchCategorySetting("Sun"))
}
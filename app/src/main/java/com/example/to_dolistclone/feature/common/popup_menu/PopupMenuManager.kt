package com.example.to_dolistclone.feature.common.popup_menu

import android.content.Context
import android.view.MenuItem
import android.view.View
import javax.inject.Inject

class PopupMenuManager @Inject constructor() {

    fun showCategoryPopupMenu(
        context: Context,
        v: View,
        setOfTodoCategories: Set<String>,
        selectedCategory: String?,
        onClick: (MenuItem) -> Boolean
    ) {
        val categoryPopupMenu = CategoryPopupMenu(context)
        categoryPopupMenu.build(v, setOfTodoCategories, selectedCategory, onClick)
    }

    fun showPieChartPopupMenu(
        context: Context, v: View, onClick: (MenuItem) -> Boolean
    ) {
        val pieGraphOptionPopupMenu = PieGraphOptionPopupMenu(context)
        pieGraphOptionPopupMenu.build(v, onClick)

    }

}
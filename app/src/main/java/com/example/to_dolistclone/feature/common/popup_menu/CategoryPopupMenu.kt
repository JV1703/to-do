package com.example.to_dolistclone.feature.common.popup_menu

import android.content.Context
import android.os.Build
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.example.to_dolistclone.R
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class CategoryPopupMenu @Inject constructor(@ActivityContext private val context: Context) {

    private lateinit var popup: PopupMenu

    fun showCategoryPopupMenu(){
        popup.show()
    }

    fun build(
        v: View,
        setOfTodoCategories: Set<String>,
        selectedCategory: String?,
        onClick: (MenuItem) -> Boolean
    ) {
        popup = PopupMenu(context, v)
        setupMenuOptions(popup.menu, setOfTodoCategories, selectedCategory)

        val inflater = popup.menuInflater
        inflater.inflate(R.menu.details_todo_menu, popup.menu)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popup.setForceShowIcon(true)
        }

        popup.setOnMenuItemClickListener {
            onClick(it)
        }
    }

    private fun generateOptions(set: Set<String>): MutableSet<String> {
        val categories = mutableSetOf<String>()
        categories.add("No Category")
        set.forEach { category -> categories.add(category) }
        categories.add("Create New")
        return categories
    }

    private fun setupMenuOptions(menu: Menu, categoryList: Set<String>, selectedCategory: String?) {
        generateOptions(categoryList).forEachIndexed { index, title ->
            menu.add(Menu.NONE, index, index, title)
        }
        menu.getItem(categoryList.size + 1).icon =
            ContextCompat.getDrawable(context, R.drawable.ic_add)
        if (categoryList.isNotEmpty()) {
            if (selectedCategory == null) {
                setStringSpanColor(menu.getItem(0), R.color.teal_200)
            } else {
                setStringSpanColor(
                    menu.getItem(categoryList.indexOf(selectedCategory) + 1), R.color.teal_200
                )
            }
        }
    }

    private fun setStringSpanColor(menuItem: MenuItem, @ColorRes color: Int) {
        val title = SpannableString(menuItem.title)
        title.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, color)), 0, title.length, 0
        )
        menuItem.title = title
    }
}
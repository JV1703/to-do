package com.example.to_dolistclone.feature.common.popup_menu

import android.content.Context
import android.os.Build
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import com.example.to_dolistclone.R

class PieGraphOptionPopupMenu(private val context: Context) {

    fun build(
        v: View,
        onClick: (MenuItem) -> Boolean
    ) {
        val popup = PopupMenu(context, v)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.pie_graph_menu, popup.menu)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popup.setForceShowIcon(true)
        }

        popup.setOnMenuItemClickListener {
            onClick(it)
        }

        popup.show()
    }

}
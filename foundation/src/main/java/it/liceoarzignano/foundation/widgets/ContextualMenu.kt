package it.liceoarzignano.foundation.widgets

import android.annotation.SuppressLint
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.View
import androidx.annotation.MenuRes
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.PopupMenu
import it.liceoarzignano.foundation.R

@SuppressLint("RestrictedApi")
class ContextualMenu(anchor: View, wrapper: ContextThemeWrapper, @MenuRes menu: Int) {

    private val mPopupMenu: PopupMenu = PopupMenu(wrapper, anchor,
            Gravity.NO_GRAVITY, R.attr.actionOverflowMenuStyle, 0)
    private val mHelper: MenuPopupHelper

    lateinit var onClickListener: (Int) -> Boolean

    init {
        mPopupMenu.inflate(menu)
        mPopupMenu.setOnMenuItemClickListener { item ->
            if (::onClickListener.isInitialized) {
                onClickListener(item.itemId)
            } else {
                false
            }
        }

        mHelper = MenuPopupHelper(wrapper, mPopupMenu.menu as MenuBuilder, anchor)
        mHelper.setForceShowIcon(true)
    }

    fun show() {
        mHelper.show()
    }
}
package com.example.a20190418test

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import github.hellocsl.cursorwheel.CursorWheelLayout

public class WheelAdapter : CursorWheelLayout.CycleWheelAdapter {
    lateinit var mContext: Context
    lateinit var menuItems: List<MenuItemData>
    lateinit var inflater: LayoutInflater
    var gravity: Int = 0

    constructor(mContext: Context, menuItems: List<MenuItemData>){
        this.mContext = mContext
        this.menuItems = menuItems
        gravity = Gravity.CENTER
        inflater = LayoutInflater.from(mContext)
    }

    constructor(mContext: Context, menuItems: List<MenuItemData>, gravity: Int){
        this.mContext = mContext
        this.menuItems = menuItems
        this.gravity = gravity
    }


    override fun getView(parent: View?, position: Int): View {
        var itemData:MenuItemData = getItem(position) as MenuItemData
        var root :View = inflater.inflate(R.layout.wheel_test_layout,null,false)
        var textView:TextView = root.findViewById(R.id.wheel_menu_item_tv)
        textView.visibility = View.VISIBLE
        textView.textSize = 18.toFloat()

        textView.text = itemData.mTitle
        if(textView.layoutParams is FrameLayout.LayoutParams)
            ((textView.layoutParams)as FrameLayout.LayoutParams).gravity = gravity
        return root
    }

    override fun getItem(position: Int): Any {
        return menuItems.get(position)
    }

    override fun getCount(): Int {
        return menuItems.size
    }


}
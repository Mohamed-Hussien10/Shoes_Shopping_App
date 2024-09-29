package com.mm.shopping.Helper

import android.content.Context
import android.widget.Toast
import com.example.project1762.Helper.ChangeNumberItemsListener
import com.mm.shopping.Model.ItemsModel


class ManagmentWishlist(val context: Context) {

    private val tinyDB = TinyDB(context)

    fun insertFood(item: ItemsModel) {
        var listFood = getListCart()
        val existAlready = listFood.any { it.title == item.title }
        val index = listFood.indexOfFirst { it.title == item.title }

        if (existAlready) {
            listFood[index].numberInWishlist = item.numberInWishlist
        } else {
            listFood.add(item)
        }
        tinyDB.putListObject("WishlistList", listFood)
        Toast.makeText(context, "Added to your Wishlist", Toast.LENGTH_SHORT).show()
    }

    fun getListCart(): ArrayList<ItemsModel> {
        return tinyDB.getListObject("WishlistList") ?: arrayListOf()
    }

    fun minusItem(listFood: ArrayList<ItemsModel>, position: Int, listener: ChangeNumberItemsListener) {
        if (listFood[position].numberInWishlist == 1) {
            listFood.removeAt(position)
        } else {
            listFood[position].numberInWishlist--
        }
        tinyDB.putListObject("WishlistList", listFood)
        listener.onChanged()
    }

    fun plusItem(listFood: ArrayList<ItemsModel>, position: Int, listener: ChangeNumberItemsListener) {
        listFood[position].numberInWishlist++
        tinyDB.putListObject("WishlistList", listFood)
        listener.onChanged()
    }

    fun getTotalFee(): Double {
        val listFood = getListCart()
        var fee = 0.0
        for (item in listFood) {
            fee += item.price * item.numberInWishlist
        }
        return fee
    }
}
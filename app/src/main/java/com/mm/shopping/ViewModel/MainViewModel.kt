package com.mm.shopping.ViewModel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mm.shopping.Model.BrandModel
import com.mm.shopping.Model.ItemsModel
import com.mm.shopping.Model.SliderModel

class MainViewModel() : ViewModel() {
    private var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val _banner = MutableLiveData<List<SliderModel>>()
    private val _brand = MutableLiveData<List<BrandModel>>()
    private val _item = MutableLiveData<List<ItemsModel>>()
    val banners: LiveData<List<SliderModel>> = _banner
    val brands: LiveData<List<BrandModel>> = _brand
    val item: LiveData<List<ItemsModel>> = _item

    fun loadBanner() {
        val bannerRef = firebaseDatabase.getReference("Banner")
        bannerRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<SliderModel>()
                for (childSnapshot in snapshot.children) {
                    val list = childSnapshot.getValue(SliderModel::class.java)
                    if (list != null) {
                        lists.add(list)
                    }
                }
                _banner.value = lists
            }

            override fun onCancelled(error: DatabaseError) {
                println("Database Error: ${error.message}")
            }
        })
    }

    fun loadBrand() {
        val brandRef = firebaseDatabase.getReference("Category")
        brandRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<BrandModel>()
                for (childSnapshot in snapshot.children) {
                    val list = childSnapshot.getValue(BrandModel::class.java)
                    if (list != null) {
                        lists.add(list)
                    }
                }
                _brand.value = lists
            }

            override fun onCancelled(error: DatabaseError) {
                println("Database Error: ${error.message}")
            }

        })
    }

    fun loadItem(){
        val itemRef = firebaseDatabase.getReference("Items")
        itemRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var lists = mutableListOf<ItemsModel>()
                for (snapshot in snapshot.children) {
                    val list = snapshot.getValue(ItemsModel::class.java)
                    if (list != null) {
                        lists.add(list)
                    }
                }
                _item.value = lists
            }

            override fun onCancelled(error: DatabaseError) {
                println("Database Error: ${error.message}")
            }

        })
    }
}

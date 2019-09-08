package com.taller.tp.foodie.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.Place
import kotlinx.android.synthetic.main.activity_place_choice.*

class PlaceChoiceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_choice)

        loadPlaces()
    }

    fun loadPlaces(){
        Place.listPlaces(this){ populatePlaces(it) }
    }

    fun populatePlaces(places: ArrayList<Place>) {
        // Creates a vertical Layout Manager
        rv_place_list.layoutManager = LinearLayoutManager(this)

        // Access the RecyclerView Adapter and load the data into it
        rv_place_list.adapter = PlaceAdapter(places, this,
            { item: Place -> placeClicked(item) })
    }

    private fun placeClicked(item : Place) {
        Place.choosePlace(this,item){}
    }

}

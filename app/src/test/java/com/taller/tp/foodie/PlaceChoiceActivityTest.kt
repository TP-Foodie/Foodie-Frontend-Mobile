package com.taller.tp.foodie

import androidx.recyclerview.widget.RecyclerView
import com.taller.tp.foodie.model.Coordinate
import com.taller.tp.foodie.model.Place
import com.taller.tp.foodie.ui.PlaceAdapter
import com.taller.tp.foodie.ui.PlaceChoiceActivity
import kotlinx.android.synthetic.main.activity_place_choice.view.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric.buildActivity
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class PlaceChoiceActivityTest {

    @Test
    fun should_show_list_places() {
        val places : ArrayList<Place> = ArrayList()
        val coordinate = Coordinate(2.0, 2.0)
        val place = Place("placeId", "placeName", coordinate)
        places.add(place)

        val placeChoice= buildActivity<PlaceChoiceActivity>(PlaceChoiceActivity::class.java).setup().get()
        placeChoice.populatePlaces(places)
        val view = placeChoice.findViewById<RecyclerView>(R.id.rv_place_list)
        val placeList = view.rv_place_list
        assertNotNull(placeList)
        val adapter = placeList.adapter as PlaceAdapter
        assertEquals(adapter.places.size,1)
        val responsePlace = adapter.places.get(0)
        assertEquals(place, responsePlace)
    }
}
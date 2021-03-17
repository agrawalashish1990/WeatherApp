package com.dev.ashish.weather.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev.ashish.weather.R
import com.dev.ashish.weather.adapter.FavouriteListAdapter
import com.dev.ashish.weather.storage.prefs.SessionPref
import com.dev.ashish.weather.ui.HomeActivity
import kotlinx.android.synthetic.main.fragment_favourite.*

//
// Created by Ashish on 16/03/21.
//

class MyFavFragment : Fragment() {

    private var adapter: FavouriteListAdapter? = null

    companion object {
        fun getInstance(): MyFavFragment {
            return MyFavFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favourite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        setUpAdapter()
    }

    private fun initUI() {
        (activity as HomeActivity).setupToolbar(getString(R.string.my_favourite),true)
    }

    private fun setUpAdapter(){
        rvLocations.layoutManager = LinearLayoutManager(activity)
        rvLocations.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))

        val listFav = SessionPref.getSharedPref(requireContext()).getMyFavouriteWeathersList()

        if (listFav.isEmpty()) {
            showEmptyView()
        } else {
            adapter = FavouriteListAdapter(requireContext(), listFav)
            rvLocations.adapter = adapter
            adapter?.notifyDataSetChanged()
        }
    }

    private fun showEmptyView(){
        tvEmptyData.visibility = View.VISIBLE
    }
}
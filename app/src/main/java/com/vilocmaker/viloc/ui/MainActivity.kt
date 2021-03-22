package com.vilocmaker.viloc.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.vilocmaker.viloc.R
import com.vilocmaker.viloc.adapter.ChooseBuildingAdapter
import com.vilocmaker.viloc.repository.Repository
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    private val chooseBuildingAdapter by lazy { ChooseBuildingAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupChooseBuildingRecyclerView()

        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)

        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        viewModel.retrieveBuildingItemList("building", null, null)
        viewModel.myBuildingItemListResponse.observe(this, { response ->
            if (response.isSuccessful) {
                for (eachBuilding in response.body()!!.data) {
                    Log.d("Main", eachBuilding.buildingName + " from Main Activity")
                    Log.d("Main", eachBuilding._id.toString().substring(6, 30) + " from Main Activity")
                    Log.d("Main", response.code().toString() + " from Main Activity")
                    Log.d("Main", response.message() + " from Main Activity")

                    chooseBuildingAdapter.setData(response.body()!!.data)
                }
            } else {
                Toast.makeText(this, response.code(), Toast.LENGTH_SHORT).show()
            }
        })

        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.account_menu -> {
                    val intent = Intent(this, AccountActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun setupChooseBuildingRecyclerView() {
        chooseBuilding_recyclerView.adapter = chooseBuildingAdapter
        chooseBuilding_recyclerView.layoutManager = LinearLayoutManager(this)
    }
}
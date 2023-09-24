package com.syracuse.caloriemanager

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.syracuse.caloriemanager.databinding.ActivityFoodBinding
import com.syracuse.caloriemanager.models.FoodItem
import com.syracuse.caloriemanager.models.MealItem
import com.syracuse.caloriemanager.views.FoodAdapter
import java.text.SimpleDateFormat
import java.util.*


class FoodActivity : AppCompatActivity(), FoodAdapter.ItemClickListener {
    private lateinit var binding: ActivityFoodBinding
    private lateinit var mFoodAdapter: FoodAdapter
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var fUser: FirebaseUser
    private lateinit var todayDate: String
    private var mealItems: MutableList<MealItem> = arrayListOf()

    private val foods = ArrayList<FoodItem>()
    private var MEAL_TYPE = "breakfast"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodBinding.inflate(layoutInflater)
        this.title = ""
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        fUser = firebaseAuth.currentUser!!

        val sdf = SimpleDateFormat("yyyy-MM-dd")
        todayDate = sdf.format(Date())

        val extras = intent.extras
        if (extras != null) {
            when(extras.getString("type")){
                resources.getString(R.string.meal_breakfast) -> {
                    binding.title.text = resources.getString(R.string.meal_breakfast)
                    MEAL_TYPE = "breakfast"
                }
                resources.getString(R.string.meal_lunch) -> {
                    binding.title.text = resources.getString(R.string.meal_lunch)
                    MEAL_TYPE = "lunch"
                }
                resources.getString(R.string.meal_dinner) -> {
                    binding.title.text = resources.getString(R.string.meal_dinner)
                    MEAL_TYPE = "dinner"
                }
            }
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        firebaseDatabase.reference
            .child("meals")
            .child(fUser.uid)
            .child(todayDate)
            .child(MEAL_TYPE)
            .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                for (sampleSnapshot in dataSnapshot.getChildren()) {
                    Log.wtf(TAG,
                        "onDataChange: sampleSnapshot " + sampleSnapshot.getKey()
                            .toString() + " = " + sampleSnapshot.getValue()
                    )
                }
            }

            override fun onCancelled(@NonNull databaseError: DatabaseError) {
                throw databaseError.toException() // don't ignore errors
            }
        })

        foods.add(FoodItem("Chicken Sandwich", "1 slice", 210, 14, 12, 12))
        foods.add(FoodItem("Veggie Sandwich", "1 slice", 210, 12, 13, 14))
        foods.add(FoodItem("Chicken Burger", "1 slice", 210, 13, 12, 16))
        foods.add(FoodItem("Veggie Burger", "1 slice", 210, 9, 13, 17))
        foods.add(FoodItem("Aloo Tikki Burger", "1 slice", 9, 23, 12, 18))
        foods.add(FoodItem("Tuna Salad", "1 bowl", 176, 12, 14, 12))
        foods.add(FoodItem("Veggie Salad", "1 bowl", 176, 14, 13, 14))
        foods.add(FoodItem("Caesar Salad", "1 bowl", 176, 13, 17, 13))
        foods.add(FoodItem("Honey Oats", "20 Grams", 83, 15, 19, 14))
        foods.add(FoodItem("Mango Smoothie", "20 Grams", 11, 34, 12, 13))
        foods.add(FoodItem("Pancakes", "20 Grams", 83, 12, 13, 14))
        foods.add(FoodItem("Chicken Tacos", "20 Grams", 83, 12, 14, 12))
        foods.add(FoodItem("Veggie Tacos", "20 Grams", 83, 14, 16, 19))

        binding.foodItems.layoutManager = LinearLayoutManager(this)
        mFoodAdapter = FoodAdapter(foods)
        mFoodAdapter.setItemClickListener(this)
        binding.foodItems.adapter = mFoodAdapter
        setUpSearchView()
    }

    private fun setUpSearchView() {
        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                mFoodAdapter.getFilter().filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                mFoodAdapter.getFilter().filter(newText);
                return true
            }
        })
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home->{
                finish()
                return true
            }
        }
        super.onOptionsItemSelected(item)
        return false
    }

    override fun onFoodAdd(view: View, position: Int) {
        Log.wtf(TAG, "Added Food Item : ${foods[position]}")
        val meal = MealItem()

        meal.name = foods[position].name
        meal.unit = foods[position].unit
        meal.count = 1
        meal.calories = foods[position].calories
        meal.carbohydrates = foods[position].carbohydrates
        meal.proteins = foods[position].proteins
        meal.fats = foods[position].fats

        val dataRef = firebaseDatabase.reference.child("meals")
            .child(fUser.uid)
            .child(todayDate)
            .child(MEAL_TYPE)
            .push()

        val key: String? = dataRef.key
        val map: MutableMap<String, Any> = HashMap()
        map[key!!] = meal

        firebaseDatabase.reference.child("meals")
            .child(fUser.uid)
            .child(todayDate)
            .child(MEAL_TYPE).updateChildren(map)

        val button = view as MaterialButton
        button.icon = getDrawable(R.drawable.ic_baseline_check_24)
        button.setBackgroundColor(getColor(R.color.md_theme_light_primary))
        button.setIconTintResource(R.color.ic_launcher_background)
    }

    companion object {
        @JvmStatic val TAG = "MealLoggerActivity"
    }

}
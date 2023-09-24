package com.syracuse.caloriemanager

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.syracuse.caloriemanager.databinding.FragmentDiaryBinding
import com.syracuse.caloriemanager.models.MealItem
import com.syracuse.caloriemanager.views.MealAdapter
import jp.wasabeef.recyclerview.animators.LandingAnimator
import java.text.SimpleDateFormat
import java.util.*

class DiaryFragment : Fragment(), View.OnClickListener, MealAdapter.ItemClickListener {
    private var _binding: FragmentDiaryBinding? = null
    private val binding get() = _binding!!
    private lateinit var mMealBreakfastAdapter: MealAdapter
    private lateinit var mMealLunchAdapter: MealAdapter
    private lateinit var mMealDinnerAdapter: MealAdapter
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var todayDate: String
    private lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        Log.v(TAG, "Diary Fragment Loaded")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.breakfastMeals.layoutManager = WrapContentLinearLayoutManager(activity, LinearLayoutManager.VERTICAL,false)
        binding.lunchMeals.layoutManager = WrapContentLinearLayoutManager(activity, LinearLayoutManager.VERTICAL,false)
        binding.dinnerMeals.layoutManager = WrapContentLinearLayoutManager(activity, LinearLayoutManager.VERTICAL,false)

        binding.addItemBreakfast.setOnClickListener(this)
        binding.addItemLunch.setOnClickListener(this)
        binding.addItemDinner.setOnClickListener(this)

        binding.removeItemBreakfast.setOnClickListener(this)
        binding.removeItemLunch.setOnClickListener(this)
        binding.removeItemDinner.setOnClickListener(this)

        user = firebaseAuth.currentUser!!
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        todayDate = sdf.format(Date())

        Log.wtf(DashboardFragment.TAG, "Loading diary data for date $todayDate")

        try {
            initBreakfastAdapter()
            initLunchAdapter()
            initDinnerAdapter()
        } catch (error: java.lang.NullPointerException){
            Log.wtf(TAG, "SILENT FAIL -  Cannot fetch dairy")
        }

        mMealBreakfastAdapter.setItemClickListener(this)
        mMealLunchAdapter.setItemClickListener(this)
        mMealDinnerAdapter.setItemClickListener(this)

        binding.breakfastMeals.apply {
            itemAnimator = LandingAnimator()
            adapter = this@DiaryFragment.mMealBreakfastAdapter
        }

        binding.lunchMeals.apply {
            itemAnimator = LandingAnimator()
            adapter = this@DiaryFragment.mMealLunchAdapter
        }

        binding.dinnerMeals.apply {
            itemAnimator = LandingAnimator()
            adapter = this@DiaryFragment.mMealDinnerAdapter
        }
    }

    override fun onStart() {
        super.onStart()
        mMealBreakfastAdapter.startListening()
        mMealLunchAdapter.startListening()
        mMealDinnerAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        mMealBreakfastAdapter.stopListening()
        mMealLunchAdapter.stopListening()
        mMealDinnerAdapter.stopListening()
    }

    override fun onClick(view: View) {
        when(view.id){
            R.id.add_item_breakfast->{
                val intent = Intent(activity, FoodActivity::class.java)
                intent.putExtra("type",resources.getString(R.string.meal_breakfast));
                this.startActivity(intent)
            }
            R.id.remove_item_breakfast->{
                if(mMealBreakfastAdapter.getRemoveState()){
                    binding.removeItemBreakfast.text = "Remove"
                    mMealBreakfastAdapter.setRemoveState(false)
                } else {
                    binding.removeItemBreakfast.text = "Cancel"
                    mMealBreakfastAdapter.setRemoveState(true)
                }
            }

            R.id.add_item_lunch->{
                val intent = Intent(activity, FoodActivity::class.java)
                intent.putExtra("type",resources.getString(R.string.meal_lunch));
                this.startActivity(intent)
            }
            R.id.remove_item_lunch-> {
                if(mMealLunchAdapter.getRemoveState()){
                    binding.removeItemLunch.text = "Remove"
                    mMealLunchAdapter.setRemoveState(false)
                } else {
                    binding.removeItemLunch.text = "Cancel"
                    mMealLunchAdapter.setRemoveState(true)
                }
            }

            R.id.add_item_dinner->{
                val intent = Intent(activity, FoodActivity::class.java)
                intent.putExtra("type",resources.getString(R.string.meal_dinner));
                this.startActivity(intent)
            }
            R.id.remove_item_dinner-> {
                if(mMealDinnerAdapter.getRemoveState()){
                    binding.removeItemDinner.text = "Remove"
                    mMealDinnerAdapter.setRemoveState(false)
                } else {
                    binding.removeItemDinner.text = "Cancel"
                    mMealDinnerAdapter.setRemoveState(true)
                }
            }
        }
    }

    override fun onMealItemRemove(view: View, position: Int, mealType: String) {
        Log.e(MealAdapter.TAG, "Item Removed")
        view.visibility = View.GONE
        binding.removeItemBreakfast.text = "Remove"
        binding.removeItemLunch.text = "Remove"
        binding.removeItemDinner.text = "Remove"

        mMealBreakfastAdapter.setRemoveState(false)
        mMealLunchAdapter.setRemoveState(false)
        mMealDinnerAdapter.setRemoveState(false)

        if (mealType == "breakfast"){
            mMealBreakfastAdapter.getRef(position).removeValue()
        } else if (mealType == "lunch") {
            mMealLunchAdapter.getRef(position).removeValue()
        } else if (mealType == "dinner") {
            mMealDinnerAdapter.getRef(position).removeValue()
        }
    }

    private fun initBreakfastAdapter(){
        val breakfastMealsQuery = FirebaseDatabase.getInstance()
            .reference
            .child("meals")
            .child(user?.uid.toString())
            .child(todayDate)
            .child("breakfast")
            .limitToFirst(10)
        Log.wtf(TAG, "Querying Breakfast Data..")
        breakfastMealsQuery
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var tCalories = 0L
                    var tFats = 0L
                    var tCarbohydrates = 0L
                    var tProteins = 0L
                    for (data in dataSnapshot.children) {
                        Log.wtf(TAG, "Breakfast Query item calories: $tCalories")
                        val calories: Long = data.child("calories").getValue(Long::class.java)!!
                        val fats: Long = data.child("fats").getValue(Long::class.java)!!
                        val carbohydrates: Long = data.child("carbohydrates").getValue(Long::class.java)!!
                        val proteins: Long = data.child("proteins").getValue(Long::class.java)!!

                        tCalories += calories
                        tFats += fats
                        tCarbohydrates += carbohydrates
                        tProteins += proteins
                    }
                    binding.breakfastCalories.text = tCalories.toString()
                    binding.breakfastDescription.text = "Carbs $tCarbohydrates • Fats $tFats • Protein $tProteins"
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        mMealBreakfastAdapter = MealAdapter(MealItem::class.java, breakfastMealsQuery,"breakfast")
    }

    private fun initLunchAdapter(){
        val lunchMealsQuery = FirebaseDatabase.getInstance()
            .reference
            .child("meals")
            .child(user?.uid.toString())
            .child(todayDate)
            .child("lunch")
            .limitToFirst(10)

        lunchMealsQuery
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var tCalories = 0L
                    var tFats = 0L
                    var tCarbohydrates = 0L
                    var tProteins = 0L
                    for (data in dataSnapshot.children) {
                        val calories: Long = data.child("calories").getValue(Long::class.java)!!
                        val fats: Long = data.child("fats").getValue(Long::class.java)!!
                        val carbohydrates: Long = data.child("carbohydrates").getValue(Long::class.java)!!
                        val proteins: Long = data.child("proteins").getValue(Long::class.java)!!

                        tCalories += calories
                        tFats += fats
                        tCarbohydrates += carbohydrates
                        tProteins += proteins
                    }
                    binding.lunchCalories.text = tCalories.toString()
                    binding.lunchDescription.text = "Carbs $tCarbohydrates • Fats $tFats • Protein $tProteins"
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        mMealLunchAdapter = MealAdapter(MealItem::class.java, lunchMealsQuery, "lunch")
    }
    private fun initDinnerAdapter(){
        val dinnerMealsQuery = FirebaseDatabase.getInstance()
            .reference
            .child("meals")
            .child(user?.uid.toString())
            .child(todayDate)
            .child("dinner")
            .limitToFirst(10)


        dinnerMealsQuery
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var tCalories = 0L
                    var tFats = 0L
                    var tCarbohydrates = 0L
                    var tProteins = 0L
                    for (data in dataSnapshot.children) {
                        val calories: Long = data.child("calories").getValue(Long::class.java)!!
                        val fats: Long = data.child("fats").getValue(Long::class.java)!!
                        val carbohydrates: Long = data.child("carbohydrates").getValue(Long::class.java)!!
                        val proteins: Long = data.child("proteins").getValue(Long::class.java)!!

                        tCalories += calories
                        tFats += fats
                        tCarbohydrates += carbohydrates
                        tProteins += proteins
                    }
                    binding.dinnerCalories.text = tCalories.toString()
                    binding.dinnerDescription.text = "Carbs $tCarbohydrates • Fats $tFats • Protein $tProteins"
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        mMealDinnerAdapter = MealAdapter(MealItem::class.java, dinnerMealsQuery, "dinner")
    }

    companion object {
        @JvmStatic val TAG = "DiaryFragment"
    }


}
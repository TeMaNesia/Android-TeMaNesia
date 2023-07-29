package com.inovego.temanesia.ui.home.lowongan

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.inovego.temanesia.data.model.FeatureItem
import com.inovego.temanesia.databinding.ActivityLowonganBinding
import com.inovego.temanesia.helper.ViewModelFactory
import com.inovego.temanesia.ui.adapter.HomeAdapter
import com.inovego.temanesia.ui.detail.DetailFeatureActivity
import com.inovego.temanesia.ui.home.HomeViewModel
import com.inovego.temanesia.utils.FIREBASE_LOWONGAN
import com.inovego.temanesia.utils.FIREBASE_USER_MOBILE

class LowonganActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLowonganBinding
    private lateinit var adapter: HomeAdapter
    private val homeViewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(Firebase.auth, Firebase.firestore)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLowonganBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.shimmerLayout.startShimmer()
        homeViewModel.shimmer.observe(this) {
            if (it == false) {
                binding.shimmerLayout.stopShimmer()
                binding.shimmerLayout.visibility = View.GONE

            }
        }
        adapter = HomeAdapter(this) { item ->
            Intent(this, DetailFeatureActivity::class.java).also {
                it.putExtra(FeatureItem::class.java.simpleName, item)
                startActivity(it)
            }
        }

        homeViewModel.getUserData(FIREBASE_USER_MOBILE).observe(this) { jurusan ->
            setRecycleView(jurusan, "")
            binding.search.setOnQueryTextListener(object :
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    setRecycleView(jurusan, query.lowercase())
                    hideKeyboard()
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    if (newText.isEmpty()) setRecycleView(jurusan, "")
                    return true
                }
            })
        }

    }


    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        currentFocus?.let { imm?.hideSoftInputFromWindow(it.windowToken, 0) }
    }

    private fun setRecycleView(jurusan: String, query: String) {
        homeViewModel.getListItemByJurusan(FIREBASE_LOWONGAN, jurusan)
        homeViewModel.lowongan.observe(this) {
            it?.let { data ->
                val filteredList = data.filter { list ->
                    list.nama.lowercase().contains(query)
                }
                adapter.submitList(filteredList)
            }
        }
        binding.rvLowongan.apply {
            adapter = this@LowonganActivity.adapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

}
package com.inovego.temanesia.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.inovego.temanesia.R
import com.inovego.temanesia.data.model.FeatureItem
import com.inovego.temanesia.databinding.FragmentHomeBinding
import com.inovego.temanesia.helper.ViewModelFactory
import com.inovego.temanesia.ui.adapter.HomeAdapter
import com.inovego.temanesia.utils.FIREBASE_BEASISWA
import com.inovego.temanesia.utils.FIREBASE_LOMBA
import com.inovego.temanesia.utils.FIREBASE_LOWONGAN
import com.inovego.temanesia.utils.FIREBASE_SERTIFIKASI
import com.inovego.temanesia.utils.FIREBASE_USER_MOBILE

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by activityViewModels {
        ViewModelFactory.getInstance(Firebase.auth, Firebase.firestore)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        homeViewModel.apply {
            text.observe(viewLifecycleOwner) {
                binding.actionBarCustom.tvUsername.text = it
            }

            text2.observe(viewLifecycleOwner) {
                binding.actionBarCustom.tvJurusan.text = it
            }

            imgProfile.observe(viewLifecycleOwner) {
                Glide.with(requireContext())
                    .load(it)
                    .placeholder(R.drawable.avatar_placeholder)
                    .into(binding.actionBarCustom.ivAvatar)
            }

            binding.actionBarCustom.ivAvatar.setOnClickListener {
                findNavController().navigate(R.id.action_navigation_home_to_navigation_profile)
            }
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            shimmerLayout.startShimmer()
            shimmerLowongan.startShimmer()
            shimmerBeasiswa.startShimmer()
            shimmerSertifikat.startShimmer()
        }

        homeViewModel.getUserData(FIREBASE_USER_MOBILE).observe(viewLifecycleOwner) { jurusan ->
            if (!jurusan.isNullOrEmpty()) {
                setRecycleView(jurusan, "")
                binding.actionBarCustom.apply {
                    search.setOnQueryTextListener(object :
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
        }

        binding.cardFeatures.apply {
            cardBeasiswa.setOnClickListener {
                findNavController().navigate(R.id.action_navigation_home_to_navigation_beasiswaActivity)
            }
            cardLowongan.setOnClickListener {
                findNavController().navigate(R.id.action_navigation_home_to_navigation_lowonganActivity)
            }
            cardLomba.setOnClickListener {
                findNavController().navigate(R.id.action_navigation_home_to_navigation_lombaActivity)
            }
            cardSertifikat.setOnClickListener {
                findNavController().navigate(R.id.action_navigation_home_to_navigation_sertifikatActivity)
            }
        }
    }

    private fun setRecycleView(jurusan: String, query: String) {
        val adapterLomba = HomeAdapter(requireContext()) { item ->
            findNavController().navigate(
                R.id.action_navigation_home_to_navigation_detailFeatureActivity,
                bundleOf("FeatureItem" to item)
            )
        }

        val adapterLowongan = HomeAdapter(requireContext()) { item ->
            findNavController().navigate(
                R.id.action_navigation_home_to_navigation_detailFeatureActivity,
                bundleOf("FeatureItem" to item)
            )
        }

        val adapterBeasiswa = HomeAdapter(requireContext()) { item ->
            findNavController().navigate(
                R.id.action_navigation_home_to_navigation_detailFeatureActivity,
                bundleOf("FeatureItem" to item)
            )
        }

        val adapterSertifikat = HomeAdapter(requireContext()) { item ->
            findNavController().navigate(
                R.id.action_navigation_home_to_navigation_detailFeatureActivity,
                bundleOf("FeatureItem" to item)
            )
        }

        getRecycleViewData(FIREBASE_LOMBA, jurusan)
        homeViewModel.lomba.observe(viewLifecycleOwner) {
            adapterLomba.submitList(emptyList())
            val filteredList = it?.filter { data ->
                data.nama.lowercase().contains(query)
            }
            adapterLomba.submitList(filteredList)
            binding.titleLomba.checkData(it)
        }

        getRecycleViewData(FIREBASE_LOWONGAN, jurusan)
        homeViewModel.lowongan.observe(viewLifecycleOwner) {
            val filteredList = it?.filter { data ->
                data.nama.lowercase().contains(query)
            }
            adapterLowongan.submitList(filteredList)
            binding.titleLowongan.checkData(it)
        }

        getRecycleViewData(FIREBASE_BEASISWA, jurusan)
        homeViewModel.beasiswa.observe(viewLifecycleOwner) {
            val filteredList = it?.filter { data ->
                data.nama.lowercase().contains(query)
            }
            adapterBeasiswa.submitList(filteredList)
            binding.titleBeasiswa.checkData(it)
        }

        getRecycleViewData(FIREBASE_SERTIFIKASI, jurusan)
        homeViewModel.sertifikasi.observe(viewLifecycleOwner) {
            val filteredList = it?.filter { data ->
                data.nama.lowercase().contains(query)
            }
            adapterSertifikat.submitList(filteredList)
            binding.titleSertifikasi.checkData(it)
        }

        binding.rvLomba.apply {
            adapter = adapterLomba
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        binding.rvLowongan.apply {
            adapter = adapterLowongan
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        binding.rvBeasiswa.apply {
            adapter = adapterBeasiswa
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        binding.rvSertifikat.apply {
            adapter = adapterSertifikat
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        homeViewModel.shimmer.observe(viewLifecycleOwner) {
            if (it == false) {
                binding.apply {
                    shimmerLayout.stopShimmer()
                    shimmerLowongan.stopShimmer()
                    shimmerBeasiswa.stopShimmer()
                    shimmerSertifikat.stopShimmer()

                    shimmerLayout.visibility = View.GONE
                    shimmerLowongan.visibility = View.GONE
                    shimmerBeasiswa.visibility = View.GONE
                    shimmerSertifikat.visibility = View.GONE
                }
            }
        }
    }

    private fun LinearLayout.checkData(it: List<FeatureItem>?) {
        if (it.isNullOrEmpty()) this.visibility = View.GONE
    }

    private fun getRecycleViewData(collection: String, jurusan: String) {
        homeViewModel.getListItemByJurusan(collection, jurusan)
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(InputMethodManager::class.java)
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
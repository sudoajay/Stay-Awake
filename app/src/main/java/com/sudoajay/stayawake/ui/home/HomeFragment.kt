package com.sudoajay.stayawake.ui.home


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sudoajay.stayawake.R
import com.sudoajay.stayawake.databinding.FragmentHomeBinding
import com.sudoajay.stayawake.ui.BaseFragment
import com.sudoajay.stayawake.ui.home.repository.HomeAdapter
import com.sudoajay.stayawake.utill.HelperClass
import com.sudoajay.stayawake.utill.HelperClass.Companion.isDarkMode
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()


    @Inject
    lateinit var homeAdapter: HomeAdapter
    private var isDarkTheme: Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        isDarkTheme = isDarkMode(requireContext())
        requireActivity().changeStatusBarColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.statusBarColor
            ),  false
        )
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && HelperClass.isFirstTimeNotify(requireContext())) {
            openNotifyMe()
        }

        setUpLanguageRecyclerView()


        return binding.root
    }

    private fun openNotifyMe(){
        findNavController().navigate(R.id.action_homeFragment_to_firebasePushNotificationFragment)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onBack()
                }
            })
    }


    private fun setUpLanguageRecyclerView() {
        binding.recyclerView.apply {
            this.layoutManager = LinearLayoutManager(this.context)
            this.setHasFixedSize(true)
            adapter = homeAdapter
        }
        homeAdapter.homeOptionList =
            homeViewModel.homeOptionList

        homeAdapter.notifyItemRangeChanged(
            0,
            homeViewModel.homeOptionList.size
        )
    }



}
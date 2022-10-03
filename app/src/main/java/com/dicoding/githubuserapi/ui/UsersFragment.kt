package com.dicoding.githubuserapi.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.githubuserapi.R
import com.dicoding.githubuserapi.data.local.entity.UserEntity
import com.dicoding.githubuserapi.ui.adapter.ListUserAdapter
import com.dicoding.githubuserapi.databinding.FragmentUsersBinding
import com.dicoding.githubuserapi.di.UserViewModel

class UsersFragment : Fragment() {
   private var tabName : String? = null

   private var _binding : FragmentUsersBinding? = null
   private val binding get() = _binding

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View? {
      // Inflate the layout for this fragment
      _binding = FragmentUsersBinding.inflate(inflater, container, false)

      return binding?.root
   }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      tabName = arguments?.getString(ARG_TAB)

      val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
      val viewModel: UserViewModel by viewModels { factory }

      val listAdapter = ListUserAdapter(false)

      if (tabName == TAB_FOLLOWER) {
         viewModel.listFollower.observe(viewLifecycleOwner) { follower ->
            binding?.tvEmptyUsers?.text = resources.getString(R.string.empty_follower)
            setUsersData(follower)
            listAdapter.submitList(follower)
         }
      } else if (tabName == TAB_FOLLOWING) {
         viewModel.listFollowing.observe(viewLifecycleOwner) { following ->
            binding?.tvEmptyUsers?.text = resources.getString(R.string.empty_following)
            listAdapter.submitList(following)
            setUsersData(following)
         }
      }

      binding?.rvUsersDetail?.apply {
         layoutManager = LinearLayoutManager(context)
         setHasFixedSize(true)
         adapter = listAdapter
      }
   }

   override fun onDestroyView() {
      super.onDestroyView()
      _binding = null
   }

   private fun setUsersData(users: List<UserEntity>) {
      if (users.isNotEmpty()) {
         binding?.tvEmptyUsers?.visibility = View.GONE
         binding?.rvUsersDetail?.visibility = View.VISIBLE


      } else {
         binding?.tvEmptyUsers?.visibility = View.VISIBLE
         binding?.rvUsersDetail?.visibility = View.GONE
      }
   }

   companion object {
      const val ARG_TAB = "tab_name"
      const val TAB_FOLLOWER = "follower"
      const val TAB_FOLLOWING = "following"
   }
}
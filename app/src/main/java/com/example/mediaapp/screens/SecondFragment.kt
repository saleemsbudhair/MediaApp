package com.example.mediaapp.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.mediaapp.R
import com.example.mediaapp.databinding.FragmentSecondBinding
import com.example.mediaapp.viewmodel.ViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SecondFragment : Fragment() {

    private val binding get() = _binding!!
    private var _binding: FragmentSecondBinding? = null
    private val viewModel: ViewModel by viewModels({ requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.selectedSong.observe(viewLifecycleOwner) { selectedSong ->
            binding.songTitle.text = selectedSong?.songTitle
            binding.songSubTitle.text = selectedSong?.songSubTitle
            Glide.with(binding.songImageView.context)
                .load(selectedSong?.songImage)
                .into(binding.songImageView)
        }

        viewModel.isPlaying.observe(viewLifecycleOwner) { isPlaying ->
            updateUI(isPlaying)
        }
        binding.playPauseButton.setOnClickListener {
            viewModel.onPlayPauseButtonClick()
        }

        viewModel.appVersion.observe(viewLifecycleOwner) { appVersion ->
            binding.versionName.text = getString(R.string.App_Version, appVersion)
        }
    }

    private fun updateUI(isPlaying: Boolean) {
        if (isPlaying) {
            binding.playPauseButton.setImageResource(R.drawable.pause)
        } else {
            binding.playPauseButton.setImageResource(R.drawable.play)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
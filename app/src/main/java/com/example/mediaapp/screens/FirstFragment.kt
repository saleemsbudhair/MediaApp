package com.example.mediaapp.screens

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.mediaapp.R
import com.example.mediaapp.adapter.OnItemClickListener
import com.example.mediaapp.adapter.SongsAdapter
import com.example.mediaapp.databinding.FragmentFirstBinding
import com.example.mediaapp.viewmodel.ViewModel
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FirstFragment : Fragment() , Player.Listener {

    private var _binding: FragmentFirstBinding? = null
    private lateinit var adapter: SongsAdapter
    private val viewModel: ViewModel by viewModels({ requireActivity() })
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.songImageView.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        viewModel.isDataLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        adapter = SongsAdapter(emptyList(), object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                val selectedSong = adapter.getItem(position)
                viewModel.updateSelectedSong(selectedSong)
                val mediaItem = MediaItem.fromUri(Uri.parse(selectedSong.songLink))
                viewModel.playMediaItem(mediaItem)
            }
        })

        viewModel.selectedSong.observe(viewLifecycleOwner) { selectedSong ->
            selectedSong?.let {
                binding.songTitle.text = it.songTitle
                binding.songSubTitle.text = it.songSubTitle
                Glide.with(binding.songImageView.context)
                    .load(it.songImage)
                    .into(binding.songImageView)
            }
        }

        binding.playPauseButton.setOnClickListener {
            viewModel.onPlayPauseButtonClick()
        }

        binding.nextButton.setOnClickListener {
            viewModel.playNextSong()
        }
        binding.prevButton.setOnClickListener {
            viewModel.playPreviousSong()
        }
        viewModel.isPlaying.observe(viewLifecycleOwner) { isPlaying ->
            updatePlayPauseButton(isPlaying)
        }

        binding.recycleView.adapter = adapter
        binding.recycleView.layoutManager = LinearLayoutManager(context)

        viewModel.songList.observe(this) { albums ->
            adapter.setData(albums)
        }
    }

    private fun updatePlayPauseButton(isPlaying: Boolean) {
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

    override fun onDestroy() {
        super.onDestroy()

    }


}
package com.alamiya.weatherapptask.presentation.weatherDetails

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.alamiya.weatherapptask.R
import com.alamiya.weatherapptask.data.repository.RepositoryImpl
import com.alamiya.weatherapptask.databinding.FragmentWeatherDetailsBinding
import com.alamiya.weatherapptask.domain.models.WeatherContentModel
import com.alamiya.weatherapptask.domain.usecase.GetRegionsName
import com.alamiya.weatherapptask.domain.usecase.GetWeatherDetailsUseCase
import com.alamiya.weatherapptask.domain.usecase.UseCases
import com.alamiya.weatherapptask.domain.utils.DataResponseState
import kotlinx.coroutines.launch


class WeatherDetailsFragment : Fragment() {
    private lateinit var binding: FragmentWeatherDetailsBinding


    private val viewModel:WeatherDetailsViewModel by lazy {
        val repository = RepositoryImpl.getInstance(requireActivity().application)
        val getWeatherDetailsUseCase = GetWeatherDetailsUseCase(repository)
        val getRegionsName = GetRegionsName(repository)
        val userCase = UseCases(
            getWeatherDetailsUseCase,
            getRegionsName
        )
        ViewModelProvider(
            requireActivity(),
            WeatherDetailsViewModelFactory(userCase)
        )[WeatherDetailsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_weather_details,container,false)
        binding.lifecycleOwner = this

        binding.autoCompleteTextView.addTextChangedListener(viewModel.textWatcher)
        val adapter = WeatherAdapter(WeatherAdapter.ItemOnCLickListener(::ItemClicked))
        binding.mAdapter = adapter


        regionsAutoComplete()
        lifecycleScope.launch{
            viewModel.state.collect{state->
                if (viewModel.firstOpen){
                    binding.stateFirstOpen.visibility = View.VISIBLE
                    binding.stateEmptyDataHolder.visibility = View.GONE
                    binding.stateLoadingHolder.visibility = View.GONE
                    binding.stateErrorHolder.visibility = View.GONE
                    binding.rvFavorite.visibility = View.GONE
                }
                else
                {
                    when(state){
                        is DataResponseState.OnSuccess -> {
                            binding.stateFirstOpen.visibility = View.GONE
                            binding.stateEmptyDataHolder.visibility = View.GONE
                            binding.stateLoadingHolder.visibility = View.GONE
                            binding.stateErrorHolder.visibility = View.GONE
                            binding.rvFavorite.visibility = View.VISIBLE
                            adapter.submitList(state.data.weatherList)
                        }
                        is DataResponseState.OnError -> {
                            binding.stateFirstOpen.visibility = View.GONE
                            binding.stateEmptyDataHolder.visibility = View.GONE
                            binding.stateLoadingHolder.visibility = View.GONE
                            binding.stateErrorHolder.visibility = View.VISIBLE
                            binding.rvFavorite.visibility = View.GONE
                        }
                        is DataResponseState.OnLoading -> {
                            binding.stateFirstOpen.visibility = View.GONE
                            binding.stateEmptyDataHolder.visibility = View.GONE
                            binding.stateLoadingHolder.visibility = View.VISIBLE
                            binding.stateErrorHolder.visibility = View.GONE
                            binding.rvFavorite.visibility = View.GONE
                        }
                        is DataResponseState.OnNothingData -> {
                            binding.stateFirstOpen.visibility = View.GONE
                            binding.stateEmptyDataHolder.visibility = View.VISIBLE
                            binding.stateLoadingHolder.visibility = View.GONE
                            binding.stateErrorHolder.visibility = View.GONE
                            binding.rvFavorite.visibility = View.GONE
                        }
                    }
                }

            }
        }
        return binding.root
    }

    private fun ItemClicked(model: WeatherContentModel) {

    }

    private fun regionsAutoComplete() {
        viewModel.getRegions(R.raw.countries)
        lifecycleScope.launch {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, viewModel.regions.value)
            binding.autoCompleteTextView.setAdapter(adapter)
        }
    }

}
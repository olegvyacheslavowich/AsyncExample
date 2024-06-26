package ru.elipson.asyncexample

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.elipson.asyncexample.databinding.FragmentFirstBinding
import kotlin.concurrent.thread

class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    private val binding get() = _binding!!

    private val handler = MessageHandler {
        Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)


        return binding.root

    }

    private fun downloadWithoutCoroutines(step: Int, obj: Any) {
        when (step) {
            0 -> {
                binding.progess.isVisible = true
                loadCityWithoutCoroutine {
                    downloadWithoutCoroutines(1, it)
                }
            }

            1 -> {
                binding.textviewFirst.text = obj as String
                loadDegreeWithoutCoroutine(obj) {
                    downloadWithoutCoroutines(2, it)
                }
            }

            2 -> {
                binding.textviewSecond.text = (obj as Int).toString()
                binding.progess.isVisible = false
            }
        }
    }

    private fun loadCityWithoutCoroutine(callback: (String) -> Unit) {
        Handler().postDelayed({
            callback("Moscow")
        }, 1000)

    }

    private fun loadDegreeWithoutCoroutine(city: String, callback: (Int) -> Unit) {
        Handler().postDelayed({
            Log.d("test", city)
            callback(25)
        }, 1000)
    }

    private suspend fun download() {


    }

    private suspend fun loadCity(): String {
        delay(1000)
        return "Moscow"
    }

    private suspend fun loadDegree(): Int {
        delay(5000)
        // Log.d("test", city)
        return 25
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState) 

        binding.buttonFirst.setOnClickListener {
            // downloadWithoutCoroutines(0, "")
            binding.progess.isVisible = true

            val deferredCity = lifecycleScope.async {
                val city = loadCity()
                binding.textviewFirst.text = city
                city
            }

            val deferredTemp = lifecycleScope.async {
                val degree = loadDegree()
                binding.textviewSecond.text = degree.toString()
                binding.progess.isVisible = false
                degree
            }

            lifecycleScope.launch {
                val city = deferredCity.await()
                val temp = deferredTemp.await()

                binding.progess.isVisible = false

                Toast.makeText(requireContext(), "City: $city; temp: $temp", Toast.LENGTH_LONG)
                    .show()

            }


        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class MessageHandler(private val callback: (String) -> Unit) : Handler() {
    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        callback.invoke(msg.obj.toString())
    }
}
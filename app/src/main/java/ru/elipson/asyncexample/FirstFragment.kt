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
import androidx.navigation.fragment.findNavController
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

    private fun download() {
        binding.progess.isVisible = true
        loadCity {
            binding.buttonFirst.text = it
            loadDegree(it) { degree ->
                binding.textviewSecond.text = degree.toString()
            }
        }
    }

    private fun loadCity(callback: (String) -> Unit) {
        thread {
            Thread.sleep(1000)
            requireActivity().runOnUiThread {
                callback("Moscow")
            }
            handler.sendMessage(Message.obtain(handler, 0, "Hello!"))
        }


    }

    private fun loadDegree(city: String, callback: (Int) -> Unit) {
        thread {
            Thread.sleep(1000)
            Log.d("test", city)
            Handler(Looper.getMainLooper()).post {
                callback.invoke(25)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            download()
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
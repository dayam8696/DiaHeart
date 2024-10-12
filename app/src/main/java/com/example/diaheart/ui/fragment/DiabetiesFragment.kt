package com.example.diaheart.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.diaheart.databinding.DiabetiesBinding
import com.example.diaheart.ml.Model
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

class DiabetiesFragment : BaseFragment() {
    private var _binding: DiabetiesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DiabetiesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up button click to predict
        binding.btnPredict.setOnClickListener {
            Log.d("DiabetiesFragment", "Button clicked") // Debug Log
            inputTaken() // Call inputTaken to predict
        }

        // Gender Spinner setup
        val genderSpinner = binding.genderSpinner
        val genderOptions = arrayOf("Male", "Female")
        val genderAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, genderOptions)
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genderSpinner.adapter = genderAdapter

        // Smoking History Spinner setup
        val smokingHistorySpinner = binding.smokingHistorySpinner
        val smokingOptions = arrayOf("Never", "Current", "Former")
        val smokingAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, smokingOptions)
        smokingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        smokingHistorySpinner.adapter = smokingAdapter
    }

    fun inputTaken() {
        try {
            // Validation for empty fields
            if (binding.age.text.toString().isEmpty() ||
                binding.hypertension.text.toString().isEmpty() ||
                binding.heartDisease.text.toString().isEmpty() ||
                binding.bmi.text.toString().isEmpty() ||
                binding.HbA1cLevel.text.toString().isEmpty() ||
                binding.bloodGlucoseLevel.text.toString().isEmpty()) {
                showToast("Please enter all the values")
                return // Return early if validation fails
            }

            // Collect inputs from user
            val genderString = binding.genderSpinner.selectedItem.toString()  // Collect gender as a string
            val genderNumeric = if (genderString == "Male") 1 else 0  // Convert gender to numeric value: 1 for Male, 0 for Female

            val age = binding.age.text.toString().toInt()  // Age as Integer
            val hypertension = binding.hypertension.text.toString().toInt()  // Hypertension as Integer
            val heartDisease = binding.heartDisease.text.toString().toInt()  // Heart Disease as Integer

            val smokingHistoryString = binding.smokingHistorySpinner.selectedItem.toString()  // Collect smoking history as a string
            val smokingHistoryNumeric = when (smokingHistoryString) {  // Convert smoking history to numeric value
                "Never" -> 0
                "Current" -> 1
                "Former" -> 2
                else -> 0
            }

            val bmi = binding.bmi.text.toString().toFloat()  // BMI as Float
            val HbA1cLevel = binding.HbA1cLevel.text.toString().toFloat()  // HbA1c Level as Float
            val bloodGlucoseLevel = binding.bloodGlucoseLevel.text.toString().toInt()  // Glucose Level as Integer

            // Debugging values before passing to the model
            Log.d("DiabetiesFragment", "Gender: $genderString, Age: $age, Hypertension: $hypertension, Heart Disease: $heartDisease, Smoking: $smokingHistoryString, BMI: $bmi, HbA1c: $HbA1cLevel, Glucose: $bloodGlucoseLevel")

            // Correct ByteBuffer allocation (7 features * 4 bytes for each float)
            val bufferSize = 7 * 4
            Log.d("DiabetiesFragment", "Allocating ByteBuffer of size: $bufferSize bytes")

            val byteBuffer = ByteBuffer.allocateDirect(bufferSize) // Ensure buffer is large enough
            byteBuffer.order(ByteOrder.nativeOrder())

            // Pack data into ByteBuffer
            byteBuffer.putFloat(genderNumeric.toFloat())  // Gender as float (numeric representation)
            byteBuffer.putFloat(age.toFloat())  // Age as float (convert from int to float)
            byteBuffer.putFloat(hypertension.toFloat())  // Hypertension as float (convert from int to float)
            byteBuffer.putFloat(heartDisease.toFloat())  // Heart Disease as float (convert from int to float)
            byteBuffer.putFloat(smokingHistoryNumeric.toFloat())  // Smoking history as float (numeric representation)
            byteBuffer.putFloat(bmi)  // BMI as float
            byteBuffer.putFloat(HbA1cLevel)  // HbA1c Level as float
            byteBuffer.putFloat(bloodGlucoseLevel.toFloat())  // Glucose level as float (convert from int to float)

            Log.d("DiabetiesFragment", "ByteBuffer prepared successfully. Remaining capacity: ${byteBuffer.remaining()} bytes")

            // Run the TensorFlow Lite model
            val model = Model.newInstance(requireContext())
            Log.d("DiabetiesFragment", "Model loaded successfully")

            // Create input buffer
            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 7), DataType.FLOAT32)
            inputFeature0.loadBuffer(byteBuffer)
            Log.d("DiabetiesFragment", "Input feature created successfully")

            // Run model inference
            val outputs = model.process(inputFeature0)
            Log.d("DiabetiesFragment", "Model processed successfully")

            val outputFeature0 = outputs.outputFeature0AsTensorBuffer

            // Get prediction result (assuming the output is a single float value 0 or 1)
            val prediction = outputFeature0.floatArray[0]
            Log.d("DiabetiesFragment", "Prediction received: $prediction")

            // Interpret the result and show a toast
            val predictionText = if (prediction >= 0.5f) {
                "Yes, you may have diabetes"
            } else {
                "No, you do not have diabetes"
            }

            // Show the prediction result in a toast
            showToast(predictionText)
            Log.d("DiabetiesFragment", "Prediction displayed")

            // Release the model resources
            model.close()
        } catch (e: Exception) {
            Log.e("DiabetiesFragment", "Error during model inference", e)
            showToast("Error running prediction")
        }
    }

    // Function to display toast messages
//    fun showToast(message: String) {
//        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
//    }
}

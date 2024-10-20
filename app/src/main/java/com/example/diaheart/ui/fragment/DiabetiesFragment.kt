package com.example.diaheart.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

import androidx.navigation.fragment.findNavController
import com.example.diaheart.R
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
            findNavController().navigate(R.id.action_diabeties_to_diabetiesResultScreen)
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

            // Collect string inputs from user
            val genderString = binding.genderSpinner.selectedItem.toString()  // Collect gender as a string
            val smokingHistoryString = binding.smokingHistorySpinner.selectedItem.toString()  // Collect smoking history as a string

            // One-Hot Encoding for gender (2 categories)
            val genderOneHot = when (genderString) {
                "Male" -> floatArrayOf(1.0f, 0.0f)
                "Female" -> floatArrayOf(0.0f, 1.0f)
                else -> floatArrayOf(0.0f, 0.0f)  // Handle unknown case if needed
            }

            // One-Hot Encoding for smoking history (3 categories)
            val smokingHistoryOneHot = when (smokingHistoryString) {
                "Never" -> floatArrayOf(1.0f, 0.0f, 0.0f)
                "Current" -> floatArrayOf(0.0f, 1.0f, 0.0f)
                "Former" -> floatArrayOf(0.0f, 0.0f, 1.0f)
                else -> floatArrayOf(0.0f, 0.0f, 0.0f)  // Handle unknown case if needed
            }

            // Collect numeric inputs
            val age = binding.age.text.toString().toFloat()  // Age as Float
            val hypertension = binding.hypertension.text.toString().toFloat()  // Hypertension as Float
            val heartDisease = binding.heartDisease.text.toString().toFloat()  // Heart Disease as Float
            val bmi = binding.bmi.text.toString().toFloat()  // BMI as Float
            val HbA1cLevel = binding.HbA1cLevel.text.toString().toFloat()  // HbA1c Level as Float
            val bloodGlucoseLevel = binding.bloodGlucoseLevel.text.toString().toFloat()  // Glucose Level as Float

            // Log the collected values, including glucose
            Log.d("DiabetiesFragment", "Gender One-Hot: ${genderOneHot.contentToString()}")
            Log.d("DiabetiesFragment", "Smoking History One-Hot: ${smokingHistoryOneHot.contentToString()}")
            Log.d("DiabetiesFragment", "Age: $age, Hypertension: $hypertension, Heart Disease: $heartDisease")
            Log.d("DiabetiesFragment", "BMI: $bmi, HbA1c Level: $HbA1cLevel, Glucose Level: $bloodGlucoseLevel")

            // The model expects 8 features in total:
            // 2 for gender, 3 for smoking history, and 3 other numeric inputs
            val bufferSize = 8 * 4  // Each float takes 4 bytes
            val byteBuffer = ByteBuffer.allocateDirect(bufferSize)
            byteBuffer.order(ByteOrder.nativeOrder())

            // Pack One-Hot Encoded Gender
            byteBuffer.putFloat(genderOneHot[0])
            byteBuffer.putFloat(genderOneHot[1])

            // Pack One-Hot Encoded Smoking History
            byteBuffer.putFloat(smokingHistoryOneHot[0])
            byteBuffer.putFloat(smokingHistoryOneHot[1])
            byteBuffer.putFloat(smokingHistoryOneHot[2])

            // Pack other features (age, hypertension, heart disease, BMI, HbA1c Level, glucose level)
            byteBuffer.putFloat(age)
            byteBuffer.putFloat(bmi)  // BMI
            byteBuffer.putFloat(bloodGlucoseLevel)  // Glucose Level

            // Debugging: log buffer size and remaining capacity
            Log.d("DiabetiesFragment", "ByteBuffer size: $bufferSize bytes. Remaining capacity: ${byteBuffer.remaining()}")

            // Load the TensorFlow Lite model
            val model = Model.newInstance(requireContext())

            // Prepare input feature with shape (1, 8) for the model
            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 8), DataType.FLOAT32)
            inputFeature0.loadBuffer(byteBuffer)

            // Run model inference
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer

            // Get prediction result (assuming the output is a single float value)
            val prediction = outputFeature0.floatArray[0]

            // Log the prediction value
            Log.d("DiabetiesFragment", "Prediction result: $prediction")

// Convert the prediction to a percentage
            val predictionPercentage = (prediction * 100).toInt()

// Show the prediction result as a percentage in a toast
            val predictionText = "Your diabetes risk is $predictionPercentage%"
            diaResult.percentage = predictionText

// Display the percentage as a toast message
            showToast(predictionText)


            // Close model resources
            model.close()

        } catch (e: Exception) {
            Log.e("DiabetiesFragment", "Error during model inference", e)
            showToast("Error running prediction")
        }
    }

    object diaResult {
        var percentage = " "
    }

}

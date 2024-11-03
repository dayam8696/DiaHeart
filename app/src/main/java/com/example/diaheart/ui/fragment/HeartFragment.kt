package com.example.diaheart.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.diaheart.R
import com.example.diaheart.databinding.HeartAttackBinding
import com.example.diaheart.ml.HeartAttackPredictionModel
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

class HeartFragment : BaseFragment() {

    private val binding by lazy { HeartAttackBinding.inflate(layoutInflater) }
    private val TAG = "HeartFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.submitButton.setOnClickListener {
            try {
                val inputs = getInputs() // Collect inputs from the user
                logByteBuffer(inputs)    // Log ByteBuffer contents
                val prediction = runModel(inputs) // Run the TFLite model
                showPrediction(prediction) // Display the prediction result
                findNavController().navigate(R.id.action_heartFragment_to_heartResultFragment)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Please enter valid inputs", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Error: ${e.message}")
            }
        }
    }

    // Collect inputs from user and convert them to ByteBuffer
    private fun getInputs(): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4 * 13) // 13 features, 4 bytes each
        byteBuffer.order(ByteOrder.nativeOrder())

        try {
            // Collect all 13 required inputs as floats
            val age = binding.age.text.toString().toFloat()
            val sex = binding.sex.text.toString().toFloat()
            val cp = binding.cp.text.toString().toFloat()
            val trtbps = binding.trtbps.text.toString().toFloat()
            val chol = binding.chol.text.toString().toFloat()
            val fbs = binding.fbs.text.toString().toFloat()
            val restecg = binding.restecg.text.toString().toFloat()
            val thalachh = binding.thalachh.text.toString().toFloat()
            val exng = binding.exng.text.toString().toFloat()
            val oldpeak = binding.oldpeak.text.toString().toFloat()
            val slp = binding.slp.text.toString().toFloat()
            val caa = binding.caa.text.toString().toFloat()
            val thall = binding.thall.text.toString().toFloat()

            Log.d(TAG, "User Input Values:")
            Log.d(TAG, "Age: $age")
            Log.d(TAG, "Sex: $sex")
            Log.d(TAG, "Chest Pain (cp): $cp")
            Log.d(TAG, "Resting Blood Pressure (trtbps): $trtbps")
            Log.d(TAG, "Cholesterol (chol): $chol")
            Log.d(TAG, "Fasting Blood Sugar (fbs): $fbs")
            Log.d(TAG, "Resting ECG (restecg): $restecg")
            Log.d(TAG, "Max Heart Rate Achieved (thalachh): $thalachh")
            Log.d(TAG, "Exercise Induced Angina (exng): $exng")
            Log.d(TAG, "Oldpeak: $oldpeak")
            Log.d(TAG, "Slope (slp): $slp")
            Log.d(TAG, "Number of Major Vessels (caa): $caa")
            Log.d(TAG, "Thallium Stress Test (thall): $thall")

            // Add inputs to ByteBuffer in the correct order
            byteBuffer.putFloat(age)
            byteBuffer.putFloat(sex)
            byteBuffer.putFloat(cp)
            byteBuffer.putFloat(trtbps)
            byteBuffer.putFloat(chol)
            byteBuffer.putFloat(fbs)
            byteBuffer.putFloat(restecg)
            byteBuffer.putFloat(thalachh)
            byteBuffer.putFloat(exng)
            byteBuffer.putFloat(oldpeak)
            byteBuffer.putFloat(slp)
            byteBuffer.putFloat(caa)
            byteBuffer.putFloat(thall)

        } catch (e: NumberFormatException) {
            Log.e(TAG, "Invalid input: ${e.message}")
            throw Exception("Invalid input")
        }

        return byteBuffer
    }

    // Log ByteBuffer contents for debugging
    private fun logByteBuffer(buffer: ByteBuffer) {
        val floatBuffer = buffer.asFloatBuffer()
        val array = FloatArray(floatBuffer.capacity())
        floatBuffer.get(array)
        Log.d(TAG, "ByteBuffer Contents: ${array.joinToString(", ")}")
    }

    // Run the TFLite model and return the prediction result
    private fun runModel(byteBuffer: ByteBuffer): Float {
        val model = HeartAttackPredictionModel.newInstance(requireContext())

        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 13), DataType.FLOAT32)
        inputFeature0.loadBuffer(byteBuffer)

        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        val prediction = outputFeature0.floatArray[0]
        Log.d(TAG, "Prediction Raw Output: $prediction")

        model.close()
        return prediction
    }

    // Display the prediction result
    // Display the prediction result as a percentage
    private fun showPrediction(prediction: Float) {
        // Convert the prediction to a percentage (0-100)
        val percentage = prediction * 100
        val result = String.format("Heart attack risk: %.2f%%", percentage)
        HeartResult.percentage = result

        Log.d(TAG, "Prediction Percentage: $result")

        // Display the percentage result in a Toast
        Toast.makeText(requireContext(), result, Toast.LENGTH_LONG).show()
    }

    object HeartResult {
        var percentage = " "
    }
}

package com.example.diaheart.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.diaheart.databinding.HeartAttackBinding
import com.example.diaheart.ml.HeartAttackPredictionModel
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

class HeartFragment : BaseFragment() {

    private val binding by lazy { HeartAttackBinding.inflate(layoutInflater) }
    private val TAG = "HeartFragment" // Tag for logging

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set an onClickListener for the Predict button
        binding.btnPredict.setOnClickListener {
            try {
                val inputs = getInputs() // Collect inputs from the user
                logByteBuffer(inputs)    // Log ByteBuffer contents
                val prediction = runModel(inputs) // Run the TFLite model
                showPrediction(prediction) // Display the prediction result
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Please enter valid inputs", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Error: ${e.message}")
            }
        }
    }

    // Collect inputs, log them, and convert them to ByteBuffer
    private fun getInputs(): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4 * 22) // 22 * 4 bytes each
        byteBuffer.order(ByteOrder.nativeOrder())

        try {
            // Collect continuous features
            val age = binding.age.text.toString().toFloat()
            val trtbps = binding.trtbps.text.toString().toFloat()
            val chol = binding.chol.text.toString().toFloat()
            val thalachh = binding.thalachh.text.toString().toFloat()
            val oldpeak = binding.oldpeak.text.toString().toFloat()

            // Collect categorical features
            val sex_1 = if (binding.sex.text.toString().toInt() == 1) 1.0f else 0.0f
            val exng_1 = if (binding.exng.text.toString().toInt() == 1) 1.0f else 0.0f
            val fbs_1 = if (binding.fbs.text.toString().toInt() == 1) 1.0f else 0.0f

            val cp_2 = if (binding.cp.text.toString().toInt() == 2) 1.0f else 0.0f
            val cp_3 = if (binding.cp.text.toString().toInt() == 3) 1.0f else 0.0f

            val restecg_1 = if (binding.restecg.text.toString().toInt() == 1) 1.0f else 0.0f
            val restecg_2 = if (binding.restecg.text.toString().toInt() == 2) 1.0f else 0.0f

            val slp_1 = if (binding.slp.text.toString().toInt() == 1) 1.0f else 0.0f
            val slp_2 = if (binding.slp.text.toString().toInt() == 2) 1.0f else 0.0f

            val caa_1 = if (binding.caa.text.toString().toInt() == 1) 1.0f else 0.0f
            val caa_2 = if (binding.caa.text.toString().toInt() == 2) 1.0f else 0.0f
            val caa_3 = if (binding.caa.text.toString().toInt() == 3) 1.0f else 0.0f

            val thall_1 = if (binding.thall.text.toString().toInt() == 1) 1.0f else 0.0f
            val thall_2 = if (binding.thall.text.toString().toInt() == 2) 1.0f else 0.0f
            val thall_3 = if (binding.thall.text.toString().toInt() == 3) 1.0f else 0.0f

            // Add inputs to ByteBuffer in the correct order
            byteBuffer.putFloat(age)
            byteBuffer.putFloat(trtbps)
            byteBuffer.putFloat(chol)
            byteBuffer.putFloat(thalachh)
            byteBuffer.putFloat(oldpeak)
            byteBuffer.putFloat(sex_1)
            byteBuffer.putFloat(exng_1)
            byteBuffer.putFloat(fbs_1)
            byteBuffer.putFloat(cp_2)
            byteBuffer.putFloat(cp_3)
            byteBuffer.putFloat(restecg_1)
            byteBuffer.putFloat(restecg_2)
            byteBuffer.putFloat(slp_1)
            byteBuffer.putFloat(slp_2)
            byteBuffer.putFloat(caa_1)
            byteBuffer.putFloat(caa_2)
            byteBuffer.putFloat(caa_3)
            byteBuffer.putFloat(thall_1)
            byteBuffer.putFloat(thall_2)
            byteBuffer.putFloat(thall_3)

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

        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 22), DataType.FLOAT32)
        inputFeature0.loadBuffer(byteBuffer)

        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        val prediction = outputFeature0.floatArray[0]
        Log.d(TAG, "Prediction Raw Output: $prediction")

        model.close()
        return prediction
    }

    // Display the prediction result
    private fun showPrediction(prediction: Float) {
        val result = if (prediction >= 0.5) "High risk of heart attack" else "Low risk of heart attack"
        Log.d(TAG, "Prediction Value: $prediction")
        Toast.makeText(requireContext(), result, Toast.LENGTH_LONG).show()
    }
}

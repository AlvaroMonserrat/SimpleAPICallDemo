package com.rrat.simpleapicalldemo

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.rrat.simpleapicalldemo.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CallAPILoginAsyncTask().onExecuteCalled()
    }


    private inner class CallAPILoginAsyncTask(){

        private lateinit var customProgressDialog: Dialog

        fun onExecuteCalled(){
            showProgressDialog()

            CoroutineScope(Dispatchers.IO).launch {
                //In Background
                var result: String

                var connection : HttpURLConnection? = null

                try {
                    val url = URL("https://run.mocky.io/v3/730bca7c-e933-49df-9026-0bb615a5b3f5")
                    connection = url.openConnection() as HttpURLConnection
                    connection.doInput = true
                    connection.doOutput = true

                    val httpResult: Int = connection.responseCode

                    if(httpResult == HttpURLConnection.HTTP_OK){
                        val inputStream = connection.inputStream

                        val reader = BufferedReader(InputStreamReader(inputStream))

                        val stringBuilder = StringBuilder()
                        var line: String?
                        try {
                            while (reader.readLine().also { line = it } != null){
                                stringBuilder.append(line + "\n")
                            }
                        }catch (e: IOException){
                            e.printStackTrace()
                        }finally {
                            try {
                                inputStream.close()
                            }catch (e: IOException){
                                e.printStackTrace()
                            }
                        }

                        result = stringBuilder.toString()

                    }else{
                        result = connection.responseMessage
                    }
                }catch (e: SocketTimeoutException){
                    result = "Connection Timeout"
                }catch (e: Exception){
                    result = "Error: " + e.message
                }finally {
                    connection?.disconnect()
                }

                withContext(Dispatchers.Main){
                    //On Post Execute
                    binding.mainText.text = result
                    cancelProgressDialog()

                    Log.i("JSON RESPONSE RESULT", result)
                }
            }

        }

        private fun showProgressDialog(){
            customProgressDialog = Dialog(this@MainActivity)
            customProgressDialog.setContentView(R.layout.dialog_custom_progress)
            customProgressDialog.show()
        }

        private fun cancelProgressDialog(){
            customProgressDialog.dismiss()
        }

    }

}
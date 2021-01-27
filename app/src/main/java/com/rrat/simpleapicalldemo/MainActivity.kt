package com.rrat.simpleapicalldemo

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.rrat.simpleapicalldemo.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
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

        CallAPILoginAsyncTask("Alvaro", "12351").onExecuteCalled()
    }


    private inner class CallAPILoginAsyncTask(val username: String, val password: String){

        private lateinit var customProgressDialog: Dialog

        fun onExecuteCalled(){
            showProgressDialog()

            CoroutineScope(Dispatchers.IO).launch {
                //In Background
                kotlin.runCatching {
                    var result: String
                    var connection: HttpURLConnection? = null
                    try {
                        val url = URL("http://www.mocky.io/v2/5e3826143100006a00d37ffa")
                        connection = url.openConnection() as HttpURLConnection
                        connection.doInput = true
                        connection.doOutput = true

                        /*
                        connection.instanceFollowRedirects = false

                        connection.requestMethod = "POST"
                        connection.setRequestProperty("Content-Type", "application/json")
                        connection.setRequestProperty("charset", "utf-8")
                        connection.setRequestProperty("Accept", "application/json")

                        connection.useCaches = false

                        val writeDataOutputStream = DataOutputStream(connection.outputStream)
                        val jsonRequest = JSONObject()
                        jsonRequest.put("username", username)
                        jsonRequest.put("password", password)


                        writeDataOutputStream.writeBytes(jsonRequest.toString())
                        writeDataOutputStream.flush()
                        writeDataOutputStream.close()
                        */
                        val httpResult: Int = connection.responseCode

                        if (httpResult == HttpURLConnection.HTTP_OK) {
                            val inputStream = connection.inputStream

                            val reader = BufferedReader(InputStreamReader(inputStream))

                            val stringBuilder = StringBuilder()
                            var line: String?
                            try {
                                while (reader.readLine().also { line = it } != null) {
                                    stringBuilder.append(line + "\n")
                                }
                            } catch (e: IOException) {
                                e.printStackTrace()
                            } finally {
                                try {
                                    inputStream.close()
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                            }

                            result = stringBuilder.toString()

                        } else {
                            result = connection.responseMessage
                        }
                    } catch (e: SocketTimeoutException) {
                        result = "Connection Timeout"
                    } catch (e: Exception) {
                        result = "Error: " + e.message
                    } finally {
                        connection?.disconnect()
                    }

                    withContext(Dispatchers.Main) {
                        //On Post Execute

                        cancelProgressDialog()

                        Log.i("JSON RESPONSE RESULT", result)

                        val responseData = Gson().fromJson(result, ResponseData::class.java)

                        //val jsonObject = JSONObject(result)
                        //val message = jsonObject.optString("name")
                        binding.mainText.text = responseData.message
                    }
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
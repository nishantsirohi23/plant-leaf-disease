package com.example.leafdetection


import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class LOGIN : Fragment() {
    private var nameValue:EditText?=null
    private var passwordValue:EditText?=null
    private var buttonLogIn:Button?=null
    private val url:String="https://madhavarijit000web.000webhostapp.com/Leaf%20Detection/login.php"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view= inflater.inflate(R.layout.fragment_l_o_g_i_n, container, false)
        initialization(view)
        checkExistingRecord()
        buttonLogIn!!.setOnClickListener(){
            if (nameValue!!.text.toString().trim()!="" && passwordValue!!.text.toString().trim()!="" ) {
                checkLogInDetailsInServer()
            }
            else{
                Toast.makeText(requireContext(),"Username or Password must not be empty",Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
    private fun initialization(view:View){
        nameValue=view.findViewById(R.id.nameValue)
        passwordValue=view.findViewById(R.id.passwordValue)
        buttonLogIn=view.findViewById(R.id.buttonLogIn)
    }

    private fun checkLogInDetailsInServer(){
        val stringRequest= @SuppressLint("CommitPrefEdits")
        object : StringRequest(Request.Method.POST,url, Response.Listener { response->
            if(response.toString().trim()=="found"){
                val newIntent=Intent(requireContext(),MainActivity::class.java)
                startActivity(newIntent)

                val sharedPreferences:SharedPreferences = requireContext().getSharedPreferences("credentials", MODE_PRIVATE)
                val editor:SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString("email",nameValue!!.text.toString().trim())
                editor.putString("password",passwordValue!!.text.toString().trim())
                editor.apply()

                activity?.finish()
            }
            else {
                Toast.makeText(requireContext(), response.toString(), Toast.LENGTH_SHORT).show()
            }

        },
        Response.ErrorListener {error: VolleyError? ->
            Toast.makeText(requireContext(),error.toString(),Toast.LENGTH_SHORT).show()
        }){
            override fun getParams(): MutableMap<String, String>? {
                val hashMap:HashMap<String,String> = HashMap<String,String>()
                hashMap.put("username",nameValue!!.text.toString().trim())
                hashMap.put("password",passwordValue!!.text.toString().trim())
                return hashMap
            }
        }
        val requestQueue:RequestQueue = Volley.newRequestQueue(requireContext())
        requestQueue.add(stringRequest)


    }
    private fun checkExistingRecord(){
        val sp :SharedPreferences = requireContext().getSharedPreferences("credentials", MODE_PRIVATE)
        if(sp.contains("email")){
            nameValue!!.setText(sp.getString("email",""))
            passwordValue!!.setText(sp.getString("password",""))
        }
    }


}
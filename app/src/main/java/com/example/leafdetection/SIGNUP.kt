package com.example.leafdetection

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley


class SIGNUP : Fragment() {

    private var nameValue:EditText?=null
    private var emailAddress:EditText?=null
    private var password:EditText?=null
    private var contactNo:EditText?=null
    private var signUp:Button?=null
    private val url:String="https://madhavarijit000web.000webhostapp.com/Leaf%20Detection/signup.php"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_s_i_g_n_u_p, container, false)
        initialization(view)
        signUp!!.setOnClickListener() {
            signUpInServer()
        }
        return view
    }
    private fun initialization(view:View){
        nameValue=view.findViewById(R.id.nameValue)
        emailAddress=view.findViewById(R.id.emailAddress)
        password=view.findViewById(R.id.password)
        contactNo=view.findViewById(R.id.contactNo)
        signUp=view.findViewById(R.id.signUp)

    }
    private fun signUpInServer(){
        val stringRequest:StringRequest=object : StringRequest(Request.Method.POST,url,Response.Listener {response ->
            Toast.makeText(requireContext(),response.toString().trim(),Toast.LENGTH_SHORT).show()
            if(response.toString().trim()=="New User Created"){
                val newIntent=Intent(requireContext(),MainActivity::class.java)
                startActivity(newIntent)
                activity?.finish()
            }
        },
        Response.ErrorListener {error: VolleyError? ->
            Toast.makeText(requireContext(),error.toString(),Toast.LENGTH_SHORT).show()
        }){
            override fun getParams(): MutableMap<String, String>? {
                var hashMap:HashMap<String,String>  = HashMap<String,String>()
                hashMap.put("name",nameValue!!.text.toString().trim())
                hashMap.put("email",emailAddress!!.text.toString().trim())
                hashMap.put("password",password!!.text.toString().trim())
                hashMap.put("contact",contactNo!!.text.toString().trim())
                return hashMap
            }
        }
        val requestQueue:RequestQueue= Volley.newRequestQueue(requireContext())
        requestQueue.add(stringRequest)
    }

}
package com.example.leafdetection

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.example.leafdetection.ml.Model
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import kotlin.math.log

class MainActivity : AppCompatActivity() {
    private var imageView: ImageView?=null

    private var result: TextView?=null
    private var bitmap: Bitmap?=null
    private var globalUri:Uri?=null
    private var cameraIcon:ImageView?=null
    private var predictIV:ImageView?=null
    private val cameraRequestCode:Int=100
    private var logOut:ImageView?=null



    private val imageProcessor = ImageProcessor.Builder()
        .add(ResizeOp(256,256,ResizeOp.ResizeMethod.BILINEAR))
        .build()

    val permissionResultLauncher: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.RequestPermission()){ response->
        if(response){
            galleryActivityResultLauncher.launch("image/*")
        }
        else{
            showAlertDialog("Storage Permission Denied","We can not access the images from the storage")
        }
    }





    val galleryActivityResultLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.GetContent()){ uri->
            if(uri!=null){
                bitmap= MediaStore.Images.Media.getBitmap(this.contentResolver,uri)
                imageView!!.setImageBitmap(bitmap)
                globalUri=uri
                result!!.text="Result"
            }
        }

    private val takePictureLauncher:ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode== RESULT_OK){
            bitmap=it?.data?.extras?.get("data") as Bitmap
            imageView!!.setImageBitmap(bitmap)
        }
    }

    private val cameraResultLauncher:ActivityResultLauncher<String> =registerForActivityResult(
        ActivityResultContracts.RequestPermission()){ response->
        if(response){
            val intent:Intent=Intent()
            intent.action=MediaStore.ACTION_IMAGE_CAPTURE
            takePictureLauncher.launch(intent)
        }
        else{
            showAlertDialog("Camera Access Denied","We can not access the camera app")
        }
    }




    private fun showAlertDialog(title: String, message: String) {
        val alertDialog= AlertDialog.Builder(this)
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.setPositiveButton("Cancel"){dialog,_->
            dialog.dismiss()
        }
        alertDialog.create().show()
    }



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.navigationBarColor = resources.getColor(R.color.black);
        window.statusBarColor=resources.getColor(R.color.black)

        predictIV=findViewById(R.id.predictIV)
        cameraIcon=findViewById(R.id.cameraIcon)
        imageView=findViewById(R.id.imageView)
        logOut=findViewById(R.id.logOut)

        logOut!!.setOnClickListener(){
            openAlertDialog()

        }

        result=findViewById(R.id.result)

        cameraIcon!!.setOnClickListener(){
            openDialog()
        }

        val labels=application.assets.open("Labels.txt").bufferedReader().readLines()

        predictIV!!.setOnClickListener(){
            prediction(labels)
        }


    }
    private fun openDialog(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.camera_gallery_dialog)
        val camera:ImageView=dialog.findViewById(R.id.camera)
        val gallery:ImageView=dialog.findViewById(R.id.gallery)

        camera.setOnClickListener(){
            cameraResultLauncher.launch(android.Manifest.permission.CAMERA)
            dialog.dismiss()
        }
        gallery.setOnClickListener(){
            permissionResultLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            dialog.dismiss()
        }
        dialog.create()
        dialog.show()
    }

    private fun prediction(labels:List<String>){
        if(globalUri!=null || bitmap!=null){
            var tensorImage=TensorImage(DataType.FLOAT32)
            tensorImage.load(bitmap)
            tensorImage=imageProcessor.process(tensorImage)

            val model = Model.newInstance(this)
            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 256, 256, 3), DataType.FLOAT32)
            inputFeature0.loadBuffer(tensorImage.buffer)

            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray
            var maxIdx:Int=0
            outputFeature0.forEachIndexed{index, fl ->
                if(outputFeature0[maxIdx]<fl){
                    maxIdx=index
                }
            }
            result!!.text=labels[maxIdx]

            model.close()
        }
    }
/*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== RESULT_OK){
            if(requestCode==cameraRequestCode){
                bitmap=data!!.extras!!.get("data") as Bitmap
                imageView!!.setImageBitmap(bitmap)
            }
        }
    }
*/
    private fun openAlertDialog(){
        val alertDialog:AlertDialog.Builder=AlertDialog.Builder(this)
        alertDialog.setTitle("Are you want to log out ?")
        alertDialog.setMessage("This will clear your auto fill details from login page")
        alertDialog.setPositiveButton("Yes"){dialog,_->
            dialog.dismiss()
            val sp:SharedPreferences=this.getSharedPreferences("credentials", MODE_PRIVATE)
            val editor:SharedPreferences.Editor=sp.edit()
            editor.clear()
            editor.apply()
            startActivity(Intent(this,LoginSignup::class.java))
            finish()
        }
        alertDialog.setNegativeButton("Cancel"){dialog,_->
            dialog.dismiss()
        }
        alertDialog.create()
        alertDialog.show()




    }


    
}
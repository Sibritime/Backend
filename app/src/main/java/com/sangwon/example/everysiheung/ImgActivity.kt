package com.sangwon.example.everysiheung

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage


class ImgActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var progressBar: ProgressBar
    private val root = FirebaseFirestore.getInstance().collection("Image")
    val db = Firebase.firestore
    //공식 문서 드가자
    val storage = Firebase.storage
    val storageRef = storage.reference
    val mountainsRef = storageRef.child("mountains.jpg")
    val mountainImagesRef = storageRef.child("images/mountains.jpg")

    // val reference = FirebaseStorage.getInstance().getReference()를 사용하여 Firebase Storage의 루트 참조를 얻습니다.
    private val reference = FirebaseStorage.getInstance().getReference()


    private lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_img)

        // Get the image view and progress bar
        imageView = findViewById(R.id.image_view)
        progressBar = findViewById(R.id.progress_View)

        // 프로그레스 처음에는 안보이게 설정
        progressBar.visibility = View.INVISIBLE

        // Set an onclick listener for the image view
        imageView.setOnClickListener {

            // 갤러리 접근 암시적 인텐트? 그거인듯
            val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
            //이게 뭐야?
            //galleryIntent.type = "image/*"를 사용하여 암시적 인텐트의 타입을 "image/*"로 설정합니다.
            // 이는 선택 가능한 컨텐츠의 타입을 이미지로 제한하는 역할을 합니다. 즉, 갤러리 앱에서는 이미지 파일만 선택할 수 있게 됩니다.
            galleryIntent.type = "image/*"

            // Launch the gallery intent
            // onActivityResult 메서드에서 이 요청 코드를 통해 결과를 처리할 수 있습니다.
            startActivityForResult(galleryIntent, 1)
        }

        // Set an onclick listener for the upload button
        findViewById<Button>(R.id.upload_btn).setOnClickListener {

            // Check if an image is selected
            if (imageUri != null) {

                // Upload the image to Firestore
                // 밑에 함수 정의함
                uploadToFirestore(imageUri)

            } else {
                Toast.makeText(this, "Please select an image.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 갤러리에서 사진 받으면 실행하는 함수
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Check if the request code is the same as the one we sent
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {

            // Get the image Uri
            imageUri = data.data!!

            // Set the image Uri to the image view
            imageView.setImageURI(imageUri)
            //아주 뭔 이상한 값을 가지고 있네
            //content://com.google.android.apps.photos.contentprovider/-1/1/content%3A%2F%2Fmedia%2Fexternal%2Fimages%2Fmedia%2F13/ORIGINAL/NONE/image%2Fjpeg/273006428
            //Log.e("img","${imageUri}")
        }
    }

    // Upload the image to Firestore
    private fun uploadToFirestore(imageUri: Uri) {

        // Get the storage reference
        // Firebase Storage에서 사용할 파일 참조를 생성합니다. 파일 참조의 경로는 현재 시간과 이미지 파일의 확장자를 조합하여 생성됩니다.
        val fileRef = reference.child(System.currentTimeMillis().toString() + "." + getFileExtension(imageUri))

        val ref = storageRef.child("images/mountains.jpg")
        var uploadTask = ref.putFile(imageUri)
        uploadTask.continueWithTask {
            if (!it.isSuccessful) {
                it.exception?.let {
                    throw it
                }
            }
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
            } else {
                // Handle failures
                // ...
            }
        }










        // Upload the image
        fileRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->

                // Get the download Uri
                val downloadUri = taskSnapshot.storage.downloadUrl
                Log.e("uri","${downloadUri}")

//                // Create a document
//                val document = DocumentReference.create(root, downloadUri.toString())
//
//                // Set the data
//                document.set(mapOf("imageUri" to downloadUri.toString()))

                db.collection("Images")
                    .add(mapOf("imageUri" to downloadUri.toString()))
                    .addOnSuccessListener { documentReference ->
                        Log.d("db", "DocumentSnapshot added with ID: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.w("db", "Error adding document", e)
                    }

                // Hide the progress bar
                progressBar.visibility = View.INVISIBLE

                // Show a toast message
                Toast.makeText(this, "Upload success", Toast.LENGTH_SHORT).show()

                // Set the image resource to the image view
                imageView.setImageResource(R.drawable.ic_add_photo)
            }
            .addOnProgressListener { snapshot ->

                // Show the progress bar
                progressBar.visibility = View.VISIBLE
            }
            .addOnFailureListener { exception ->

                // Hide the progress bar
                progressBar.visibility = View.INVISIBLE

                // Show a toast message
                Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getFileExtension(imageUri: Uri): String {

        // Get the content resolver
        val contentResolver = contentResolver

        // Get the mime type
        val mimeType = contentResolver.getType(imageUri)

        // Get the file extension
        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)

        return extension.toString()
    }
}
package com.inkquoir.instareelsdownloader

import android.Manifest
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.hcr2bot.instagramvideosdownloader.InstaVideo
import com.inkquoir.instareelsdownloader.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val STORAGE_PERMISSION_CODE = 102
    private lateinit var binding: ActivityMainBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpWebView();
        checkPermission(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            STORAGE_PERMISSION_CODE)

        val uri = intent.data
        if (uri != null) {
            var reel: String = uri.path.toString()
            reel = "https://www.instagram.com$reel"
            binding.intagramUrlEditText.setText(reel)
            loadUrl()
        }

        when {
            intent?.action == Intent.ACTION_SEND ->{
                if("text/plain" == intent.type){
                    var reel : String? = intent.getStringExtra(Intent.EXTRA_TEXT)
                    binding.intagramUrlEditText.setText(reel)
                    loadUrl()
                }
            }

        }



        binding.pasteVideoLinkButton.setOnClickListener {

            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData: ClipData? = clipboard.primaryClip

            clipData?.apply {
                val textToPaste:String = this.getItemAt(0).text.toString().trim()
                binding.intagramUrlEditText.setText(textToPaste)
            }

        }
        binding.retrieveVideoButton.setOnClickListener {
            loadUrl()
        }
        binding.downloadVideoBtn.setOnClickListener(View.OnClickListener {

            if(isStoragePermissionGiven(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                if(!checkIfEmpty(binding.intagramUrlEditText)) {
                    var url: String = binding.intagramUrlEditText.text.toString().trim()
                    InstaVideo.downloadVideo(
                        this,
                        url
                    )
                }
                else {
                    Toast.makeText(this, "Please enter the url first...", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Toast.makeText(
                        this,
                        "Give the storage permission first...",
                        Toast.LENGTH_LONG
                    ).show()
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + this.packageName)
                    )
                    startActivity(intent)
                }
            }


        })

    }

    private fun loadUrl(){
        if(!checkIfEmpty(binding.intagramUrlEditText)) {
            var url: String = binding.intagramUrlEditText.text.toString().trim()
            binding.instagramVideoWebView.loadUrl(url)
        }
        else {
            Toast.makeText(this, "Please enter the url first...", Toast.LENGTH_SHORT).show();
        }

    }


    private fun setUpWebView()
    {
        binding.instagramVideoWebView.settings.javaScriptEnabled = true
        binding.instagramVideoWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url != null) {
                    view?.loadUrl(url)
                }
                return true
            }
        }

    }

    private fun checkIfEmpty(editText : EditText) : Boolean{
        return TextUtils.isEmpty(editText.text.toString().trim())
    }


    // Function to check and request permission.
    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this@MainActivity, permission)
            == PackageManager.PERMISSION_DENIED
        ) {

            // Requesting the permission
            ActivityCompat.requestPermissions(
                this@MainActivity, arrayOf(permission),
                requestCode
            )
        } else {
            // Permission Already granted
        }
    }

    // This function is called when the user accepts or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when the user is prompt for permission.

    // This function is called when the user accepts or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when the user is prompt for permission.
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(
                    this@MainActivity,
                    "Storage Permission Granted",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                Toast.makeText(
                    this@MainActivity,
                    "Storage Permission Denied, you will not be able to download the videos...",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }


    fun isStoragePermissionGiven(permission: String?): Boolean {
        return ContextCompat.checkSelfPermission(this, permission!!) != PackageManager.PERMISSION_DENIED
    }


}
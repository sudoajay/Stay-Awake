package com.sudoajay.stayawake.ui.feedbackAndHelp

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import com.sudoajay.stayawake.R
import com.sudoajay.stayawake.databinding.ActivitySendFeedbackBinding
import com.sudoajay.stayawake.model.MessageType
import com.sudoajay.stayawake.ui.BaseActivity
import com.sudoajay.stayawake.utill.HelperClass.Companion.isDarkMode


class SendFeedbackAndHelp : BaseActivity() {
    private var arrayImageUri: ArrayList<Uri> = arrayListOf()
    private lateinit var binding: ActivitySendFeedbackBinding
    private lateinit var startForResult: ActivityResultLauncher<Intent>
    private var isDarkTheme: Boolean = false
    private var emailSubject:String=""
    private var errorMessage :String = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isDarkTheme = isDarkMode(applicationContext)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isDarkTheme) {
                WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars =
                    true
            }

        }

        changeStatusBarColor(
            ContextCompat.getColor(
                applicationContext,
                R.color.bgBoxColor
            ), !isDarkTheme
        )

        binding = ActivitySendFeedbackBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.activity = this
        setUpView()
        reference()

    }

    private fun reference() {

        binding.feedbackEditText.editText?.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrBlank() )
                binding.feedbackEditText.isErrorEnabled = false

        }


        startForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data
                    if (result.data != null) {
                        arrayImageUri.clear()
                        if (null != intent?.clipData) {
                            for (i in 0 until intent.clipData!!.itemCount) {
                                val uri = intent.clipData!!.getItemAt(i).uri
                                arrayImageUri.add(uri!!)
                            }
                        } else {
                            val uri = intent?.data
                            arrayImageUri.add(uri!!)
                        }
                        binding.addScreenshotTextView.visibility = View.GONE
                        binding.addScreenshotSmallImageView.visibility = View.GONE
                        binding.addScreenshotLargeImageView.visibility = View.VISIBLE
                       throwToaster(getString(R.string.touch_again_to_change_image_text))

                    }
                }
            }
        val text = getText(R.string.systemInfo_text)
        val ss = SpannableString(text)

        val clickableSpan1: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                onClickCustomSystemInfo()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(
                    applicationContext,
                    R.color.colorAccent
                )
                ds.isUnderlineText = true

            }
        }

        ss.setSpan(clickableSpan1, 5, 16, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.systemInfoTextView.text = ss
        binding.systemInfoTextView.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun setUpView(){

        val type = intent.getStringExtra(messageType)
        if ((MessageType.Support).toString() == type){
            binding.feedbackEditText.hint = getString(R.string.describe_your_issue_text)
            binding.sendFeedbackButton.text = getString(R.string.send_issue_text)
            emailSubject = getString(R.string.issue_in_app_text, getString(R.string.app_name))
            errorMessage = getString(R.string.supportEditTextError)
        }else{
            errorMessage = getString(R.string.feedbackEditTextError)
            emailSubject = getString(R.string.feedback_about_app_text, getString(R.string.app_name))
        }
    }


    fun onClickImageManager() {
        startForResult.launch(getFileChooserIntent())
    }

    private fun getFileChooserIntent(): Intent {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_OPEN_DOCUMENT
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        return intent
    }


    fun onClickSendFeedBack(){
        if (binding.feedbackEditText.editText!!.text.isEmpty()) {
            Toast.makeText(applicationContext, errorMessage,Toast.LENGTH_LONG).show()
            binding.feedbackEditText.error = errorMessage
        } else {
            openEmail()
        }
    }

    private fun openEmail() {
        try {
            val systemInfo = SystemInfo(this)

            val emailIntent = Intent(Intent.ACTION_SEND)

            if (arrayImageUri.isNotEmpty()) {
                emailIntent.action = Intent.ACTION_SEND_MULTIPLE
                emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, arrayImageUri)
            } else emailIntent.action = Intent.ACTION_SEND

            emailIntent.type = "image/*"
            val to = arrayOf("devsudoajay@gmail.com")
            emailIntent.putExtra(Intent.EXTRA_EMAIL, to)
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject)
            emailIntent.putExtra(
                Intent.EXTRA_TEXT,
                "${binding.feedbackEditText.editText!!.text} ${systemInfo.createTextForEmail()}"
            )
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(Intent.createChooser(emailIntent, "Send email..."))
        } catch (e: Exception) {
            Log.e("ErrorTag", "emailIntent ${e.localizedMessage}")

        }

    }
    private fun onClickCustomSystemInfo() {
        val ft = supportFragmentManager.beginTransaction()
        val systemInfoDialog = SystemInfoDialog()
        systemInfoDialog.show(ft, "dialog")
    }



}

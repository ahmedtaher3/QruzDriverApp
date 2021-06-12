package qruz.t.qruzdriverapp.ui.main.fragments.profile.update

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.fragment_map.view.*
import qruz.t.qruzdriverapp.R
import qruz.t.qruzdriverapp.Utilities.CommonUtilities
import qruz.t.qruzdriverapp.Utilities.CommonUtilities.DecodeFile
import qruz.t.qruzdriverapp.Utilities.CommonUtilities.getCustomImagePath
import qruz.t.qruzdriverapp.base.BaseFragment
import qruz.t.qruzdriverapp.databinding.FragmentChangePasswordBinding
import qruz.t.qruzdriverapp.databinding.FragmentUpdateProfileBinding
import qruz.t.qruzdriverapp.model.User
import java.io.File

/**
 * A simple [Fragment] subclass.
 */
class UpdateProfileFragment : BaseFragment<FragmentUpdateProfileBinding>() {

    lateinit var binding: FragmentUpdateProfileBinding
    lateinit var viewModel: UpdateProfileViewModel
    var mCapturedImageUrl: String? = null
    var file: File? = null

    override fun getLayoutId(): Int {
        return R.layout.fragment_update_profile
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = viewDataBinding
        viewModel = ViewModelProviders.of(this).get(UpdateProfileViewModel::class.java)
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        Glide.with(baseActivity).load(viewModel?.dataManager?.user?.avatar).placeholder(R.drawable.com_facebook_profile_picture_blank_portrait)
            .into(binding?.image!!)

        binding.currentName.setText(viewModel.dataManager.user.name.toString())
        try {
            binding.currentEmail.setText(viewModel.dataManager.user.email.toString())
        } catch (e: Exception) {

        }
        binding.currentPhone.setText(viewModel.dataManager.user.phone.toString())



        binding.editImage.setOnClickListener {

            if (ContextCompat.checkSelfPermission(
                    baseActivity,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    baseActivity,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    baseActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                pickImage()

            } else {
                requestCameraPermission()

            }

        }


        binding.edit.setOnClickListener(View.OnClickListener {


            viewModel.updateDriver(
                binding.currentName.text.toString(),
                binding.currentEmail.text.toString(),
                binding.currentPhone.text.toString()
            )

        })




        subscribeObservers()
    }

    private fun subscribeObservers() {

        viewModel.responseLive.observe(viewLifecycleOwner, Observer {


            if (!it.hasErrors())
            {
                Logger.d(" aaaaa "+it.data()?.updateDriver().toString())


                var user = viewModel.dataManager.user
                user.id = it.data()?.updateDriver()?.id().toString()
                user.name = it.data()?.updateDriver()?.name()
                user.email = it.data()?.updateDriver()?.email()
                user.phone = it.data()?.updateDriver()?.phone()


                val gson = Gson()
                val json = gson.toJson(user)
                viewModel?.dataManager?.saveUser(json)
            }
            else
            {
                Toast.makeText(
                    baseActivity,
                   "Error",
                    Toast.LENGTH_SHORT
                ).show()

            }




        })
        viewModel.responseLiveDriver.observe(viewLifecycleOwner, Observer {


            var user = viewModel.dataManager.user
            user.avatar = it?.avatar
            val gson = Gson()
            val json = gson.toJson(user)
            viewModel?.dataManager?.saveUser(json)


        })

        viewModel?.progress?.observe(viewLifecycleOwner, Observer { progress ->

            when (progress) {
                0 -> {
                    CommonUtilities.hideDialog()
                }
                1 -> {
                    CommonUtilities.showStaticDialog(baseActivity)
                }
            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 30) {

                file = File(DecodeFile("QRUZ", mCapturedImageUrl, 600, 600))
                binding.image?.setImageResource(android.R.color.transparent);
                binding.image?.setImageURI(Uri.fromFile(file))

                viewModel.updateImage(file!!)


            } else if (requestCode == 20) {


                file = File(
                    DecodeFile(
                        "QRUZ",
                        CommonUtilities.GetPathFromUri(baseActivity, data?.data),
                        600,
                        600
                    )
                )

                binding.image?.setImageResource(android.R.color.transparent);
                binding.image?.setImageURI(Uri.fromFile(file))

                viewModel.updateImage(file!!)
            }
        }


    }


    fun requestCameraPermission() {


        if (ActivityCompat.shouldShowRequestPermissionRationale(
                baseActivity,
                Manifest.permission.CAMERA
            )
        ) {

            AlertDialog.Builder(baseActivity)
                .setTitle("permission denied")
                .setMessage("ask for permission again")
                .setPositiveButton("ok") { dialog, which ->
                    ActivityCompat.requestPermissions(
                        baseActivity,
                        arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ),
                        22
                    )
                }
                .setNegativeButton("cancel") { dialog, which -> dialog.dismiss() }
                .create().show()
        } else {

            ActivityCompat.requestPermissions(
                baseActivity,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                22
            )
        }
    }


    fun pickImage() {

        val dialogBuilder = AlertDialog.Builder(baseActivity)
        val dialogView: View = layoutInflater.inflate(R.layout.pick_image, null)
        dialogBuilder.setView(dialogView)


        val camera = dialogView.findViewById<LinearLayout>(R.id.camera)
        val gallery = dialogView.findViewById<LinearLayout>(R.id.gallery)
        val cancel = dialogView.findViewById   <TextView
>(R.id.cancel)

        val alertDialog = dialogBuilder.create()

        alertDialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.getWindow()
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);




        camera.setOnClickListener(View.OnClickListener {

            val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val file: File =
                getCustomImagePath(baseActivity, System.currentTimeMillis().toString() + "")
            mCapturedImageUrl = file.absolutePath
            takePicture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file))
            startActivityForResult(takePicture, 30)
            alertDialog?.dismiss()
        })
        gallery.setOnClickListener(View.OnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 20)
            alertDialog?.dismiss()

        }
        )

        cancel.setOnClickListener(
            View.OnClickListener { alertDialog?.dismiss() }
        )

        alertDialog?.show()
    }
}

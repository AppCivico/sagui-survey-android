package com.eokoe.sagui.features.complaints.report

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import android.view.Menu
import android.view.MenuItem
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Category
import com.eokoe.sagui.data.entities.Complaint
import com.eokoe.sagui.data.entities.Enterprise
import com.eokoe.sagui.data.model.impl.SaguiModelImpl
import com.eokoe.sagui.extensions.ErrorType
import com.eokoe.sagui.extensions.errorType
import com.eokoe.sagui.features.base.view.BaseActivity
import com.eokoe.sagui.features.base.view.ViewPresenter
import com.eokoe.sagui.features.complaints.report.ReportAdapter.ItemType
import com.eokoe.sagui.features.complaints.report.pin.PinActivity
import com.eokoe.sagui.utils.LogUtil
import com.eokoe.sagui.widgets.dialog.AlertDialogFragment
import com.eokoe.sagui.widgets.dialog.LoadingDialog
import kotlinx.android.synthetic.main.activity_report.*
import java.io.File


/**
 * @author Pedro Silva
 * @since 25/09/17
 */
class ReportActivity : BaseActivity(), ReportAdapter.OnItemClickListener,
        ReportContract.View, ViewPresenter<ReportContract.Presenter> {

    override lateinit var presenter: ReportContract.Presenter
    private lateinit var reportAdapter: ReportAdapter
    private lateinit var progressDialog: LoadingDialog
    private val complaint = Complaint()
    private var enterprise: Enterprise? = null
    private var picture: Uri? = null
    private var pictureFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
    }

    override fun setUp(savedInstanceState: Bundle?) {
        showBackButton()
        presenter = ReportPresenter(SaguiModelImpl())
        progressDialog = LoadingDialog.newInstance("Reportando problema")

        enterprise = intent.extras?.getParcelable(EXTRA_ENTERPRISE)
        complaint.enterpriseId = enterprise?.id
        complaint.categoryId = intent.extras?.getParcelable<Category>(EXTRA_CATEGORY)?.id
    }

    override fun init(savedInstanceState: Bundle?) {
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        rvReport.setHasFixedSize(false)
        reportAdapter = ReportAdapter()
        rvReport.adapter = reportAdapter
        reportAdapter.onItemClickListener = this
        reportAdapter.titleChangeSubject.subscribe {
            complaint.title = it
        }
        reportAdapter.descriptionChangeSubject.subscribe {
            complaint.description = it
        }
    }

    override fun onResume() {
        super.onResume()
        reportAdapter.setComplaint(complaint)
    }

    override fun onItemClick(itemType: ItemType) {
        when (itemType) {
            ItemType.LOCATION -> {
                val intent = PinActivity.getIntent(this@ReportActivity, enterprise!!,
                        complaint.location, complaint.address)
                startActivityForResult(intent, REQUEST_CODE_LOCATION)
            }
            ItemType.CAMERA -> {
                if (hasCameraPermission()) {
                    openCamera()
                } else {
                    requestCameraPermission()
                }
            }
        }
    }

    private fun openCamera() {
        // https://developer.android.com/training/camera/photobasics.html
        val filename = resources.getString(R.string.app_name)
        pictureFile = File(
                getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                filename + "_complaint_" + System.currentTimeMillis() + ".jpg"
        )
        val authority = "com.eokoe.sagui.fileprovider"
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val file = FileProvider.getUriForFile(this, authority, pictureFile)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, file)
//        takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.save_check, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_ok) {
            presenter.saveComplaint(complaint)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun showError(error: Throwable) {
        hideLoading()
        AlertDialogFragment
                .create(this) {
                    title = "Falha ao reportar problema"
                    message = if (error.errorType == ErrorType.CONNECTION)
                        "Por favor verifique sua internet e tente novamente"
                    else "Ocorreu um erro inexperado.\nTente novamente mais tarde"
                }
                .show(supportFragmentManager)
    }

    override fun onSaveSuccess(complaint: Complaint) {
        hideKeyboard()
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun showLoading() {
        progressDialog.show(supportFragmentManager)
    }

    override fun hideLoading() {
        progressDialog.dismiss()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_LOCATION) {
            if (resultCode == Activity.RESULT_OK) {
                complaint.location = data?.getParcelableExtra(PinActivity.RESULT_LOCATION)
                complaint.address = data?.getStringExtra(PinActivity.RESULT_ADDRESS)
            }
        } else if (requestCode == REQUEST_CODE_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                picture = Uri.fromFile(pictureFile)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (hasCameraPermission()) {
                openCamera()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun hasCameraPermission(): Boolean {
        return hasPermission(Manifest.permission.CAMERA)
    }

    private fun requestCameraPermission() {
        // TODO handle permission not granted
        if (!hasCameraPermission()) {
            requestPermission(R.string.title_request_camera_permission, R.string.message_request_camera_permission, REQUEST_CAMERA_PERMISSION, Manifest.permission.CAMERA)
        }
    }

    companion object {
        private val REQUEST_CODE_LOCATION = 1
        private val REQUEST_CODE_CAMERA = 2
        private val EXTRA_ENTERPRISE = "EXTRA_ENTERPRISE"
        private val EXTRA_CATEGORY = "EXTRA_CATEGORY"
        private val REQUEST_CAMERA_PERMISSION = 1

        fun getIntent(context: Context, enterprise: Enterprise, category: Category?): Intent {
            val intent = Intent(context, ReportActivity::class.java)
            intent.putExtra(EXTRA_ENTERPRISE, enterprise)
            intent.putExtra(EXTRA_CATEGORY, category)
            return intent
        }
    }
}
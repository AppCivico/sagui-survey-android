package com.eokoe.sagui.features.show_asset

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Asset
import com.eokoe.sagui.extensions.show
import com.eokoe.sagui.extensions.toAuthority
import com.eokoe.sagui.features.base.view.BaseActivity
import kotlinx.android.synthetic.main.activity_view_asset.*

/**
 * @author Pedro Silva
 */
class ShowAssetActivity : BaseActivity() {

    private lateinit var assets: List<Asset>
    private var showSendButton: Boolean = false
    private var currentPosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_asset)
        setSupportActionBar(toolbar)
    }

    override fun setUp(savedInstanceState: Bundle?) {
        showBackButton()
        assets = intent.extras.getParcelableArrayList(EXTRA_ASSETS)
        showSendButton = intent.extras.getBoolean(EXTRA_SHOW_SEND_BUTTON)
        if (currentPosition == -1) {
            currentPosition = intent.extras.getInt(EXTRA_CURRENT_POSITION)
        }
    }

    override fun init(savedInstanceState: Bundle?) {
        if (showSendButton) {
            fabSend.show()
        }
        val asset = assets[currentPosition]
        when {
            asset.isImage -> ivImage.setImageURI(asset.uri.toString())
            asset.isVideo -> {
                if (asset.isLocal) {
                    val videoThumbnail = ThumbnailUtils.createVideoThumbnail(asset.uri.toString(),
                            MediaStore.Images.Thumbnails.FULL_SCREEN_KIND)
                    ivImage.setImageBitmap(videoThumbnail)
                } else {
                    ivImage.setImageURI(asset.thumbnail)
                }
                ivPlay.setOnClickListener(OpenMediaClickListener(asset))
                ivPlay.show()
            }
            else -> {
                ivAudio.setOnClickListener(OpenMediaClickListener(asset))
                ivAudio.show()
            }
        }
        fabSend.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    override fun saveInstanceState(outState: Bundle) {
        outState.putInt(STATE_CURRENT_POSITION, currentPosition)
    }

    override fun restoreInstanceState(savedInstanceState: Bundle) {
        currentPosition = savedInstanceState.getInt(STATE_CURRENT_POSITION)
    }

    companion object {
        private val EXTRA_ASSETS = "EXTRA_ASSETS"
        private val EXTRA_CURRENT_POSITION = "EXTRA_CURRENT_POSITION"
        private val EXTRA_SHOW_SEND_BUTTON = "EXTRA_SHOW_SEND_BUTTON"

        private val STATE_CURRENT_POSITION = "STATE_CURRENT_POSITION"

        fun getIntent(context: Context, assets: List<Asset>, currentPosition: Int = 0, showSendButton: Boolean = false): Intent =
                Intent(context, ShowAssetActivity::class.java)
                        .putExtra(EXTRA_ASSETS, ArrayList<Asset>(assets))
                        .putExtra(EXTRA_SHOW_SEND_BUTTON, showSendButton)
                        .putExtra(EXTRA_CURRENT_POSITION, currentPosition)

        fun getIntent(context: Context, asset: Asset, showSendButton: Boolean = false): Intent {
            val assets = ArrayList<Asset>()
            assets.add(asset)
            return getIntent(context, assets, 0, showSendButton)
        }
    }

    inner class OpenMediaClickListener(val asset: Asset) : View.OnClickListener {
        override fun onClick(view: View) {
            val uri: Uri?
            val intent = Intent(Intent.ACTION_VIEW)
            if (asset.isLocal) {
                uri = asset.uri.toAuthority(this@ShowAssetActivity)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } else {
                uri = asset.uri
            }
            intent.setDataAndType(uri, asset.type ?: contentResolver.getType(uri))
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this@ShowAssetActivity, "Falha ao tentar reproduzir a mídia", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
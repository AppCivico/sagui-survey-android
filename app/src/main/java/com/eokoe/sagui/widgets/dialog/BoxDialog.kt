package com.eokoe.sagui.widgets.dialog

import android.animation.Animator
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.view.*
import com.eokoe.sagui.extensions.height
import com.eokoe.sagui.extensions.hideSlidingBottom
import com.eokoe.sagui.extensions.showSlidingTop
import com.eokoe.sagui.extensions.statusBarHeight
import com.eokoe.sagui.widgets.listeners.VisibilityAnimatorListener

abstract class BoxDialog : DialogFragment() {

    abstract val closeButton: View?
    abstract val boxView: View
    private var onDismissListener: OnDismissListener? = null

    var isShow = false
        private set

    override fun onStart() {
        super.onStart()
        if (dialog?.window != null) {
            dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    dialog.window!!.height - resources.statusBarHeight)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        boxView.showSlidingTop()
        isShow = true
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dismiss()
            }
            true
        }
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        setStyle(DialogFragment.STYLE_NO_TITLE, 0)

        closeButton?.setOnClickListener {
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        if (onDismissListener != null) {
            onDismissListener!!.onDismiss()
        }
        super.onDismiss(dialog)
    }

    override fun dismiss() {
        isShow = false
        boxView.hideSlidingBottom(object : VisibilityAnimatorListener(boxView, View.GONE) {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                super@BoxDialog.dismiss()
            }
        })
    }

    fun setOnDismissListener(onDismissListener: OnDismissListener): BoxDialog {
        this.onDismissListener = onDismissListener
        return this
    }

    fun show(fragmentManager: FragmentManager) {
        show(fragmentManager, this::class.simpleName)
    }

    interface OnDismissListener {
        fun onDismiss()
    }
}
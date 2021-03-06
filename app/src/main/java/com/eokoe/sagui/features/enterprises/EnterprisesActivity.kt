package com.eokoe.sagui.features.enterprises

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Asset
import com.eokoe.sagui.data.entities.Enterprise
import com.eokoe.sagui.extensions.friendlyMessage
import com.eokoe.sagui.features.asset.ViewAssetActivity
import com.eokoe.sagui.features.base.view.BaseActivityNavDrawer
import com.eokoe.sagui.features.base.view.RecyclerViewAdapter
import com.eokoe.sagui.features.base.view.ViewPresenter
import com.eokoe.sagui.features.categories.CategoriesActivity
import com.eokoe.sagui.features.enterprises.filter.EnterprisesFilterActivity
import kotlinx.android.synthetic.main.content_enterprises.*
import org.koin.android.ext.android.inject

class EnterprisesActivity : BaseActivityNavDrawer(),
        ViewPresenter<EnterprisesContract.Presenter>, EnterprisesContract.View {

    override val presenter by inject<EnterprisesContract.Presenter>()
    private val enterprisesAdapter by inject<EnterprisesAdapter>()

    private var enterprises: List<Enterprise>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enterprises)
    }

    // TODO create filter layout
    /*override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }*/

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_filter -> {
                openFilter()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openFilter() {
        startActivityForResult(EnterprisesFilterActivity.getIntent(this), REQUEST_FILTER)
    }

    override fun setUp(savedInstanceState: Bundle?) {
        super.setUp(savedInstanceState)
        enterprisesAdapter.isShowLoading = enterprises == null || enterprises!!.isEmpty()
    }

    override fun init(savedInstanceState: Bundle?) {
        setupRecyclerView()
        if (enterprises == null || enterprises!!.isEmpty()) {
            presenter.list()
        } else {
            load(enterprises!!)
        }
    }

    private fun setupRecyclerView() {
        rvEnterprises.adapter = enterprisesAdapter
        rvEnterprises.setHasFixedSize(true)
        enterprisesAdapter.onItemClickListener = object : EnterprisesAdapter.OnItemClickListener {
            override fun onClick(enterprise: Enterprise) {
                presenter.setEnterprise(enterprise)
            }
        }
        enterprisesAdapter.onImageClickListener = object : ImageAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, assets: List<Asset>) {
                val intent = ViewAssetActivity.getIntent(this@EnterprisesActivity,
                        assets, position)
                startActivity(intent)
            }
        }
    }

    override fun load(enterprises: List<Enterprise>) {
        this.enterprises = enterprises
        enterprisesAdapter.items = enterprises
    }

    override fun showLoading() {
        enterprisesAdapter.isShowLoading = true
    }

    override fun hideLoading() {
        enterprisesAdapter.isShowLoading = false
    }

    override fun showError(error: Throwable) {
        enterprisesAdapter.showError(error.friendlyMessage, object : RecyclerViewAdapter.OnRetryClickListener {
            override fun retry() {
                enterprisesAdapter.clearError()
                presenter.list()
            }
        })
    }

    override fun navigateToDashboard(enterprise: Enterprise) {
        startActivity(CategoriesActivity.getIntent(this, enterprise))
    }

    override fun saveInstanceState(outState: Bundle) {
        if (enterprises != null) {
            outState.putParcelableArrayList(STATE_ENTERPRISES, ArrayList<Enterprise>(enterprises))
        }
    }

    override fun restoreInstanceState(savedInstanceState: Bundle) {
        enterprises = savedInstanceState.getParcelableArrayList(STATE_ENTERPRISES)
    }

    companion object {
        val TAG = EnterprisesActivity::class.simpleName!!
        val REQUEST_FILTER = 1

        private val STATE_ENTERPRISES = "STATE_ENTERPRISES"

        fun getIntent(context: Context) = Intent(context, EnterprisesActivity::class.java)
    }
}

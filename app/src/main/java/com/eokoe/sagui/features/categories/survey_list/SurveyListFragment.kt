package com.eokoe.sagui.features.categories.survey_list

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Category
import com.eokoe.sagui.data.entities.Survey
import com.eokoe.sagui.data.model.impl.SurveyModelImpl
import com.eokoe.sagui.features.base.view.BaseFragment
import com.eokoe.sagui.features.base.view.ViewPresenter
import com.eokoe.sagui.features.survey.SurveyActivity
import kotlinx.android.synthetic.main.fragment_survey_list.*

/**
 * @author Pedro Silva
 * @since 23/08/17
 */
class SurveyListFragment: BaseFragment(),
        SurveyListContract.View, ViewPresenter<SurveyListContract.Presenter> {

    private lateinit var surveyListAdapter: SurveyListAdapter
    override lateinit var presenter: SurveyListContract.Presenter
    var category: Category? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_survey_list, container, false)
    }

    override fun setUp(view: View?, savedInstanceState: Bundle?) {
        super.setUp(view, savedInstanceState)
        presenter = SurveyListPresenter(SurveyModelImpl())
        surveyListAdapter = SurveyListAdapter()
    }

    override fun init(view: View?, savedInstanceState: Bundle?) {
        category = arguments[EXTRA_CATEGORY] as Category
        presenter.list(category!!)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        rvSurveys.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvSurveys.adapter = surveyListAdapter
        rvSurveys.setHasFixedSize(true)
        surveyListAdapter.onItemClickListener = object : SurveyListAdapter.OnItemClickListener {
            override fun onClick(survey: Survey) {
                startActivity(SurveyActivity.getIntent(activity, category!!, survey))
            }
        }
    }

    override fun load(items: List<Survey>) {
        surveyListAdapter.items = items
    }

    companion object {
        val TAG = "SurveyListFragment"

        private val EXTRA_CATEGORY = "EXTRA_CATEGORY"

        fun newInstance(category: Category): SurveyListFragment {
            val fragment = SurveyListFragment()
            fragment.arguments = Bundle()
            fragment.arguments.putParcelable(EXTRA_CATEGORY, category)
            return fragment
        }

    }
}
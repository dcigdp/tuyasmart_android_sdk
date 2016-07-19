package com.nextapp.tuyatest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;

import com.alibaba.fastjson.JSONObject;
import com.nextapp.tuyatest.R;
import com.nextapp.tuyatest.adapter.SharedThirdAdapter;
import com.nextapp.tuyatest.presenter.SharedEditReceivedMemberPresenter;
import com.nextapp.tuyatest.utils.ProgressUtil;
import com.nextapp.tuyatest.view.ISharedEditReceivedMemberView;
import com.tuya.smart.android.device.bean.GwWrapperBean;
import com.tuya.smart.android.user.bean.PersonBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leaf on 15/12/22.
 * 别人的共享
 */
public class SharedEditReceivedMemberActivity extends BaseActivity implements ISharedEditReceivedMemberView, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "SharedOtherActivity";

    public static final String EXTRA_DEVICE_LIST = "extra_device_list";

    public static final String EXTRA_PERSON = "extra_person";

    public static final String EXTRA_POSITION = "extra_position";

    ListView mListView;
    EditText mNumber;
    EditText mName;

    private PersonBean mPerson;

    protected ArrayList<String> mDeviceList;

    private int mPosition;

    private SharedThirdAdapter mAdapter;

    private SharedEditReceivedMemberPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_edit_received_member);
        initToolbar();
        initMenu();
        initData();
        initView();
        initPresenter();
        initAdapter();
    }

    private void initMenu() {
        setTitle(getString(R.string.friend));
        setMenu(R.menu.toolbar_edit_profile, new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_done) {
                    updateNickname();
                }

                return false;
            }
        });
        setDisplayHomeAsUpEnabled();
    }

    private void initPresenter() {
        mPresenter = new SharedEditReceivedMemberPresenter(this, this);
    }

    private void initData() {
        Intent intent = getIntent();
        mDeviceList = intent.getStringArrayListExtra(EXTRA_DEVICE_LIST);
        mPerson = JSONObject.parseObject(intent.getExtras().getString(EXTRA_PERSON), PersonBean.class);

        mPosition = intent.getIntExtra(EXTRA_POSITION, 0);
    }

    private void initAdapter() {
        mAdapter = getSharedThirdAdapter();
        mListView.setAdapter(mAdapter);
        ProgressUtil.showLoading(this, R.string.loading);
        mPresenter.list();
    }

    protected SharedThirdAdapter getSharedThirdAdapter() {
        return new SharedThirdAdapter(this);
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.lv_device_list);
        mNumber = (EditText) findViewById(R.id.et_phone_number);
        mName = (EditText) findViewById(R.id.et_name);

        if (null != mPerson) {
            String name = mPerson.getMobile();
            if(TextUtils.isEmpty(name)){
                name = mPerson.getUsername();
            }

            mNumber.setText(name);
            mNumber.setEnabled(false);
            mName.setText(TextUtils.isEmpty(mPerson.getMname()) ?
                    String.format(getString(R.string.ty_add_share_tab2_name), mPerson.getMobile()) :
                    mPerson.getMname());
        }
    }

    @Override
    public void onRefresh() {
        mPresenter.list();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void updateList(List<GwWrapperBean> list) {
        if (mAdapter != null && 0 < mDeviceList.size()) {
            mAdapter.setData(getCurrentList(list));
            mAdapter.notifyDataSetChanged();
        }
    }

    //给子类复写
    protected List<Object> getCurrentList(List<GwWrapperBean> list) {
        List<Object> currentList = new ArrayList<>();

        for (Object o : list) {
            if (currentList.size() == mDeviceList.size()) {
                break;
            }

            if (o instanceof GwWrapperBean) {
                if (mDeviceList.contains(((GwWrapperBean) o).getGwId()))
                    currentList.add(o);
            }
        }

        return currentList;
    }

    private void updateNickname() {
        mPresenter.updateNickname(mPerson, mName.getText().toString(), mPosition);
    }
}
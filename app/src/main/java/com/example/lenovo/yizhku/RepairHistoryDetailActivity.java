package com.example.lenovo.yizhku;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.xutils.x;

import java.util.List;

import base.BaseActivity;
import bean.RepairBean;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import database.manager.RepairManager;
import global.Constants;

public class RepairHistoryDetailActivity extends BaseActivity {
    @BindView(R.id.tv_repair_state)
    TextView tv_repair_state;
    @BindView(R.id.tv_repair_type)
    TextView tvRepairType;
    @BindView(R.id.tv_repair_address)
    TextView tvRepairAddress;
    @BindView(R.id.tv_repair_person)
    TextView tvRepairPerson;
    @BindView(R.id.tv_repair_phone)
    TextView tvRepairPhone;
    @BindView(R.id.tv_repair_time)
    TextView tvRepairTime;
    @BindView(R.id.tv_describe)
    TextView tvDescribe;
    @BindView(R.id.iv_repair_photo)
    ImageView ivRepairPhoto;

    RepairManager dbManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_repair_history_detail);
        ButterKnife.bind(this);
        dbManager = RepairManager.getInstance(mCtx);
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        RepairBean repairBean = (RepairBean) intent.getSerializableExtra(Constants.REPAIR_INFO);
        if (repairBean == null) {
            return;
        }
        //状态为已提交或  已受理 都需要询问是否已经 完成
        if (repairBean.getState() == Constants.SUBMISSION) {
            tv_repair_state.setText("已提交");
            requestState(repairBean);
        } else if (repairBean.getState() == Constants.ADMISSIBLE) {
            tv_repair_state.setText("已受理");
            requestState(repairBean);
        } else if (repairBean.getState() == Constants.COMPLETED) {
            tv_repair_state.setText("已完成");
        }
        tvRepairType.setText(repairBean.getRepairType());
        tvRepairAddress.setText(repairBean.getAddress());
        tvRepairPerson.setText(repairBean.getName());
        tvRepairPhone.setText(repairBean.getPhone());
        tvRepairTime.setText(repairBean.getService_time());
        tvDescribe.setText(repairBean.getDescribe());
        x.image().bind(ivRepairPhoto, repairBean.getPhotoUrl());
    }

    public void requestState(RepairBean repairBean) {
        BmobQuery<RepairBean> query = new BmobQuery<RepairBean>();
        query.setLimit(1);
        query.addWhereEqualTo("objectId", repairBean.getObjectId());
        query.findObjects(new FindListener<RepairBean>() {
            public void done(List<RepairBean> object, BmobException e) {
                if (e == null) {

                    if (object.get(0).getState() == Constants.SUBMISSION) {
                        tv_repair_state.setText("已提交");
                    } else if (object.get(0).getState() == Constants.ADMISSIBLE) {
                        tv_repair_state.setText("已受理");
                        dbManager.upOrderState(object.get(0).getObjectId(), Constants.ADMISSIBLE);
                    } else if (object.get(0).getState() == Constants.COMPLETED) {
                        tv_repair_state.setText("已完成");
                        dbManager.upOrderState(object.get(0).getObjectId(), Constants.COMPLETED);
                    }

                } else {

                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        dbManager.closeDB();
        super.onDestroy();
    }
}

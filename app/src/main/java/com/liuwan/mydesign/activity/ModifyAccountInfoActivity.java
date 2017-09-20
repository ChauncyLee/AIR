package com.liuwan.mydesign.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.liuwan.mydesign.R;
import com.liuwan.mydesign.fragment.modifyaccountinfo.ModifyMobileNumberFragment;
import com.liuwan.mydesign.fragment.modifyaccountinfo.ModifyRealNameFragment;

/**
 * Created by liuwan on 2016/11/8.
 * 修改账户信息界面
 */
public class ModifyAccountInfoActivity extends Activity implements View.OnClickListener {

    private LinearLayout back;
    private Fragment modifyRealNameFragment, modifyMobileNumberFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifyaccountinfo);

        back = (LinearLayout) findViewById(R.id.back);
        back.setOnClickListener(this);

        initLayout();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                this.finish();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initLayout() {
        Intent intent = getIntent();
        String type = intent.getStringExtra("Type");
        Bundle bundle = new Bundle();
        bundle.putString(type, intent.getStringExtra(type));
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        if ("RealName".equals(type)) {
            if (modifyRealNameFragment == null) {
                modifyRealNameFragment = new ModifyRealNameFragment();
            }
            if (!modifyRealNameFragment.isAdded()) {
                modifyRealNameFragment.setArguments(bundle);
                transaction.remove(modifyRealNameFragment);
                transaction.replace(R.id.fragment_modifyAccountInfo, modifyRealNameFragment).commit();
            }
        } else if ("MobileNumber".equals(type)) {
            if (modifyMobileNumberFragment == null) {
                modifyMobileNumberFragment = new ModifyMobileNumberFragment();
            }
            if (!modifyMobileNumberFragment.isAdded()) {
                modifyMobileNumberFragment.setArguments(bundle);
                transaction.remove(modifyMobileNumberFragment);
                transaction.replace(R.id.fragment_modifyAccountInfo, modifyMobileNumberFragment).commit();
            }
        }
    }

}

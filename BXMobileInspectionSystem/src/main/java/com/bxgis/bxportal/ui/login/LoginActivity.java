package com.bxgis.bxportal.ui.login;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bxgis.bxportal.R;
import com.bxgis.bxportal.base.BaseActivity;
import com.bxgis.bxportal.bean.UserBean;
import com.bxgis.bxportal.ui.home.MainActivity;
import com.bxgis.bxportal.ui.login.contract.LoginContract;
import com.bxgis.bxportal.ui.login.model.LoginModel;
import com.bxgis.bxportal.ui.login.presenter.LoginPresenter;
import com.bxgis.bxportal.utils.NoDoubleClickListener;
import com.bxgis.bxportal.widget.CustomEditText;

import butterknife.Bind;
import me.leefeng.promptlibrary.PromptDialog;

public class LoginActivity extends BaseActivity<LoginPresenter> implements LoginContract.View {

    @Bind(R.id.btn_login)
    Button login;
    @Bind(R.id.et_name)
    CustomEditText loginName;
    @Bind(R.id.et_password)
    CustomEditText passWord;
    private String name="";
    private String pw="";
    private PromptDialog promptDialog;  //自定义对话框


    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        //创建对象
        promptDialog = new PromptDialog(this);
        //设置自定义属性
        promptDialog.getDefaultBuilder().touchAble(true).round(3).loadingDuration(3000);
        login.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                name=loginName.getText().toString().trim();
                pw= passWord.getText().toString().trim();
                promptDialog.showLoading("正在登录");
                mPresenter.login(name,pw);
            }
        });

    }

    @Override
    protected LoginPresenter initPresenter() {
        return new LoginPresenter(this,new LoginModel());
    }


    @Override
    public void showData(UserBean userBean) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("USER_INFO",userBean);
        startActivity(MainActivity.class,bundle);
        finish();

    }

    /**
     * 当用户合法就马上获取其信息
     */
    @Override
    public void toRequsetUserInfo(String token) {
          mPresenter.getUserInfo(token);
    }


    @Override
    public void onLoginSucceed() {
        promptDialog.showSuccess("登陆成功");
    }

    @Override
    public void onLoginFaild(String msg) {
        promptDialog.showError(msg);

    }

    @Override
    public void loginAgain() {
        name=loginName.getText().toString().trim();
        pw= passWord.getText().toString().trim();
        promptDialog.showLoading("正在登录");
        mPresenter.login(name,pw);
    }


}

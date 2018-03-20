package com.bxgis.yczw.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.bxgis.yczw.R;
import com.bxgis.yczw.base.BaseActivity;
import com.bxgis.yczw.bean.UserBean;
import com.bxgis.yczw.ui.login.contract.LoginContract;
import com.bxgis.yczw.ui.login.model.LoginModel;
import com.bxgis.yczw.ui.login.presenter.LoginPresenter;
import com.bxgis.yczw.ui.main.MainActivity;
import com.bxgis.yczw.utils.NoDoubleClickListener;
import com.bxgis.yczw.utils.SPUtils;
import com.bxgis.yczw.widget.CustomEditText;

import me.leefeng.promptlibrary.PromptDialog;

public class LoginActivity extends BaseActivity<LoginPresenter> implements LoginContract.View {


    Button login;
    CustomEditText loginName;
    CustomEditText passWord;
    CheckBox keepPassword;
    private String name = "";
    private String pw = "";
    private PromptDialog promptDialog;  //自定义对话框


    @Override
    public void doBeforeSetContentView() {
        boolean isKeepP = (boolean) SPUtils.get(this, "KEEPPASSWORD", false);
        if (isKeepP) {
            Intent toMain = new Intent(LoginActivity.this, MainActivity.class);
            toMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(toMain);
            finish();
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        login = (Button) findViewById(R.id.btn_login);
        loginName = (CustomEditText) findViewById(R.id.et_name);
        passWord = (CustomEditText) findViewById(R.id.et_password);
        keepPassword = (CheckBox) findViewById(R.id.cb_login_keeppassword);
        //创建对象
        promptDialog = new PromptDialog(this);
        //设置自定义属性
        promptDialog.getDefaultBuilder().touchAble(true).round(3).loadingDuration(3000);
        login.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                name = loginName.getText().toString().trim();
                pw = passWord.getText().toString().trim();
                promptDialog.showLoading("正在登录");
                mPresenter.login(name, pw);
//                if(keepPassword.isSelected()){
//                    SPUtils.put(LoginActivity.this,"KEEPPASSWORD",true);
//                }else{
//                    SPUtils.put(LoginActivity.this,"KEEPPASSWORD",false);
//                }
//                Bundle bundle = new Bundle();
////                bundle.putSerializable("USER_INFO",userBean);
//                startActivity(MainActivity.class,bundle);
//                finish();
            }
        });

    }

    @Override
    protected LoginPresenter initPresenter() {
        return new LoginPresenter(this, new LoginModel());
    }


    @Override
    public void showData(UserBean userBean) {
        if (keepPassword.isSelected()) {
            SPUtils.put(this, "KEEPPASSWORD", true);
        } else {
            SPUtils.put(this, "KEEPPASSWORD", false);
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable("USER_INFO", userBean);
        startActivity(MainActivity.class, bundle);
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
        if (keepPassword.isSelected()) {
            SPUtils.put(LoginActivity.this, "KEEPPASSWORD", true);
        } else {
            SPUtils.put(LoginActivity.this, "KEEPPASSWORD", false);
        }
        promptDialog.showSuccess("登陆成功");
        Bundle bundle = new Bundle();
//                bundle.putSerializable("USER_INFO",userBean);
        startActivity(MainActivity.class, bundle);
        finish();


    }

    @Override
    public void onLoginFaild(String msg) {
        promptDialog.showError(msg);

    }

    @Override
    public void loginAgain() {
        name = loginName.getText().toString().trim();
        pw = passWord.getText().toString().trim();
        promptDialog.showLoading("正在登录");
        mPresenter.login(name, pw);
    }


}

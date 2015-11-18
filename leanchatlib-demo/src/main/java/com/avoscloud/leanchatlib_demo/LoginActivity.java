package com.avoscloud.leanchatlib_demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avoscloud.leanchatlib.controller.ChatManager;
import com.avoscloud.leanchatlib.utils.LogUtils;


public class LoginActivity extends Activity implements View.OnClickListener {
  private EditText selfIdEditText;
  private Button loginButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    selfIdEditText = (EditText) findViewById(R.id.selfIdEditText);
    loginButton = (Button) findViewById(R.id.login);
    loginButton.setOnClickListener(this);
  }

  @Override
  public void onClick(View view) {
    String selfId = selfIdEditText.getText().toString();
    if (!TextUtils.isEmpty(selfId)) {
      ChatManager chatManager = ChatManager.getInstance();
      chatManager.setupManagerWithUserId(selfId);
      chatManager.openClient(new AVIMClientCallback() {
        @Override
        public void done(AVIMClient avimClient, AVIMException e) {
          if (e != null) {
            LogUtils.logException(e);
          }
          Intent intent = new Intent(LoginActivity.this, MainActivity.class);
          startActivity(intent);
          finish();
        }
      });
    }
  }
}

package com.superkeychain.keychain.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.superkeychain.keychain.R;
import com.superkeychain.keychain.repository.UserRepository;
import com.superkeychain.keychain.view.ContentView;
import com.superkeychain.keychain.view.Drawl;

/**
 * 
 * @author yinqiaoyin
 * 
 */
public class GraphicPasswordActivity extends Activity {

	public static final int SUCCEED = 11;
	public static final int FAILED = 12;
	public static final int CHECK = 21;
	public static final int SAVE = 22;
	public static final int TRY = 5;
	public static final String MODE = "MODE";
	public static final String PASSWORD = "PASSWORD";


	private FrameLayout body_layout;
	private ContentView content;

	private int mode;
    private int triedTimes=0;

    private int setTimes=0;

    private String tempPassword="";

	private UserRepository userRepository;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graphic_password);
		body_layout = (FrameLayout) findViewById(R.id.body_layout);

		Intent intent = getIntent();
		mode = intent.getIntExtra(MODE, CHECK);

		userRepository = new UserRepository(GraphicPasswordActivity.this);

		// 初始化一个显示各个点的viewGroup
		content = new ContentView(this, new Drawl.GestureCallBack() {

			@Override
			public void doWithPassword(String password) {
				switch (mode){
					case CHECK:
						if(userRepository.isPasswordValid(password)){
							Intent i = new Intent();
							i.putExtra(PASSWORD, password);
							setResult(SUCCEED, i);
                            finish();
                        }else{
                            Toast.makeText(GraphicPasswordActivity.this, "Pattern Wrong!", Toast.LENGTH_SHORT).show();
                            triedTimes++;
                            if(triedTimes>TRY){
                                setResult(FAILED);
                                finish();
                            }
                        }
						break;
					case SAVE:
							if(password !=null && !"".equals(password)){
                                if(setTimes==0){
                                    setTimes++;
                                    tempPassword = password;
                                }else if(setTimes==1){
                                    if(tempPassword.equals(password)){
                                        userRepository.savePassword(password);
                                        Toast.makeText(GraphicPasswordActivity.this, "Succeed!", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent();
                                        i.putExtra(PASSWORD, password);
                                        setResult(SUCCEED, i);
                                        finish();
                                    }else{
                                        Toast.makeText(GraphicPasswordActivity.this, "Inconsistent Pattern!", Toast.LENGTH_SHORT).show();
                                        setTimes=0;
                                        tempPassword="";
                                    }
                                }
							}else{
								Toast.makeText(GraphicPasswordActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                setResult(FAILED);
                                finish();
							}
						break;
					default:
						break;
				}

			}
		});
		//设置手势解锁显示到哪个布局里面
		content.setParentView(body_layout);
	}


}

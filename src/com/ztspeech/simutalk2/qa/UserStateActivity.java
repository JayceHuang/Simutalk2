package com.ztspeech.simutalk2.qa;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import cn.ac.ia.directtrans.json.AskTask;
import cn.ac.ia.directtrans.json.Json;
import cn.ac.ia.directtrans.json.JsonData;
import cn.ac.ia.directtrans.json.JsonFunction;
import cn.ac.ia.directtrans.json.JsonLogin;
import cn.ac.ia.directtrans.json.JsonQuestion;
import cn.ac.ia.directtrans.json.JsonRequest;
import cn.ac.ia.directtrans.json.JsonRequestResult;
import cn.ac.ia.directtrans.json.QuestionInfo;
import cn.ac.ia.directtrans.json.UserState;

import com.ztspeech.recognizer.PhoneInfo;
import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.data.AskTaskList;
import com.ztspeech.simutalk2.data.GlobalData;
import com.ztspeech.simutalk2.data.TextPlayer;
import com.ztspeech.simutalk2.data.UserInfo;
import com.ztspeech.simutalk2.dictionary.util.PublicArithmetic;
import com.ztspeech.simutalk2.dictionary.util.Util;
import com.ztspeech.simutalk2.net.PostPackage;
import com.ztspeech.simutalk2.net.PostPackage.IHttpPostListener;
import com.ztspeech.simutalk2.net.PostPackageEngine;
import com.ztspeech.simutalk2.net.ResultPackage;
import com.ztspeech.simutalk2.qa.message.ProcessMessage;

public class UserStateActivity extends UpdateBaseActivity implements OnClickListener, IHttpPostListener {

	private static final int ACTIVITY_ASK = 100;

	private static final int ACTIVITY_SOLVE = 200;

	public static final String PARAM_NAME = "name";
	public static final String PARAM_TEXT = "text";
	public static final String PARAM_TIME = "time";
	public static final String PARAM_TYPE = "type";

	public static UserStateActivity mInstance;
	public static boolean isTaskListUpdate = true;
	public static QuestionInfo mQuestionInfo;
	private UserInfo mUser = UserInfo.getInstanse();
	private AskTaskList mTaskList = GlobalData.getAskInstance();
	private AskTaskList mFishList = GlobalData.getFishInstance();
	private AskTaskList mTempTaskList = new AskTaskList();
	private JsonRequest mLoginRequest = new JsonRequest();
	private JsonLogin mJsonLogin = new JsonLogin();
	private JsonQuestion mJsonMessage = new JsonQuestion();
	// private PostPackage mPostPackage;
	private Button mBtnAsk;
	private Button mBtnAsk1;
	private Button mBtnSolve;
	/**
	 * 捞问题
	 */
	private ImageButton mBtnFish;
	private RadioButton mrbNetState;

	private ImageView mivBackground;
	private TextView mtvTaskCount;
	private TextView mTvUserName;
	private TextView mTvAskCount;
	private TextView mTvSolveCount;
	private Button mBtnSolve2;

	private ImageView ivBirdLeft;// 鸟提问
	private AnimationDrawable rocketAnimationLeft;// 鸟提问扇翅膀

	private FrameLayout mflSolve;
	private FrameLayout mflFish;
	private TextView tvFishCount;
	/**
	 * 当前问题
	 */
	private AskTask mCurrentTask;

	// 鸟的初始数据
	private float mSolvePostionX = 0;
	private float mSolvePostionY = 0;
	private float mAskPostionX = 0;
	private float mAskPostionY = 0;
	private boolean mIsInitView = false;
	private Context context;
	private PostPackageEngine mPostPackageEngine;
	private int postPackageType = 0;
	private Handler mUpdateBirdPostionLoop = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 200:
				postPackageCallBack((ResultPackage) msg.obj, postPackageType);
				break;
			default:
				Toast.makeText(context, (String) msg.obj, Toast.LENGTH_LONG).show();
				mBtnAsk.setVisibility(View.VISIBLE);
				mBtnAsk1.setVisibility(View.VISIBLE);

				break;
			}

		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFormat(PixelFormat.RGBA_8888);
		setContentView(R.layout.activity_user_state);
		mIsInitView = false;
		mInstance = this;
		context = this;
		mBtnAsk = (Button) findViewById(R.id.btnAsk);
		mBtnAsk1 = (Button) findViewById(R.id.btnAsk1);
		mBtnSolve = (Button) findViewById(R.id.btnSolve);
		mBtnFish = (ImageButton) findViewById(R.id.btnFish);
		

		mrbNetState = (RadioButton) findViewById(R.id.rbNetState);
		mflSolve = (FrameLayout) findViewById(R.id.flSolve);
		mflFish= (FrameLayout) findViewById(R.id.fishQuestion);
		mivBackground = (ImageView) findViewById(R.id.ivBackground);

		mTvUserName = (TextView) findViewById(R.id.tvUserName);
		mTvAskCount = (TextView) findViewById(R.id.tvAskCount);
		mTvSolveCount = (TextView) findViewById(R.id.tvSolveCount);
		mBtnSolve2 = (Button) findViewById(R.id.btnSolve2);
		mtvTaskCount = (TextView) findViewById(R.id.tvTaskCount);

		tvFishCount = (TextView) findViewById(R.id.tvFishCount);

		mBtnAsk.setOnClickListener(this);
		mBtnAsk1.setOnClickListener(this);
		mBtnSolve.setOnClickListener(this);
		mBtnSolve2.setOnClickListener(this);
		mBtnFish.setOnClickListener(this);

		mrbNetState.setEnabled(false);
		mrbNetState.setVisibility(View.INVISIBLE);
		mflSolve.setVisibility(View.INVISIBLE);
		mflFish.setVisibility(View.INVISIBLE);
		mBtnSolve2.setVisibility(View.INVISIBLE);

		mBtnAsk.setVisibility(View.VISIBLE);
		mBtnAsk1.setVisibility(View.VISIBLE);

		/**
		 * 鸟
		 */
		ivBirdLeft = (ImageView) findViewById(R.id.ivBirdLeft);
		ivBirdLeft.setBackgroundResource(R.anim.bird_rock_left);
//		ivBirdRight = (ImageView) findViewById(R.id.ivBirdRight);
//		ivBirdRight.setBackgroundResource(R.anim.bird_rock_right);
//		ivBirdRight.setVisibility(View.INVISIBLE);
		ivBirdLeft.setVisibility(View.INVISIBLE);

		mUpdateBirdPostionLoop.postDelayed(mUpdateRunnable, 300);
		updateUserState();

		setCtrlsPostoin();
	}

	private Runnable mUpdateRunnable = new Runnable() {

		public void run() {

			if (false == setCtrlsPostoin()) {
				mUpdateBirdPostionLoop.postDelayed(mUpdateRunnable, 300);
			}
		}
	};

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {

		super.onWindowFocusChanged(hasFocus);

		setCtrlsPostoin();

	}

	@Override
	protected void onResume() {

		super.onResume();
		setCtrlsPostoin();
		isTaskListUpdate = true;
		updateMesage();

	}

	@Override
	protected void onRestart() {

		super.onRestart();
		setCtrlsPostoin();
	}

	private boolean setCtrlsPostoin() {

		if (mIsInitView) {
			return true;
		}

		int h = mivBackground.getMeasuredHeight();
		int w = mivBackground.getMeasuredWidth();
		if (h == 0) {
			return false;
		}

		mIsInitView = true;

		mSolvePostionX = (int) Math.round(w * 0.382);
		mSolvePostionY = (int) Math.round(h * 0.255);

		mAskPostionX = (int) Math.round(w * 0.45);
		mAskPostionY = (int) Math.round(h * 0.85);

		ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mflSolve.getLayoutParams();
		if (params != null) {

			params.leftMargin = (int) (mSolvePostionX - mflSolve.getMeasuredWidth() * 0.5);
			params.topMargin = (int) (mSolvePostionY - mflSolve.getMeasuredHeight() * 0.5);
			mflSolve.setLayoutParams(params);
		}

		params = (ViewGroup.MarginLayoutParams) mflFish.getLayoutParams();
		if (params != null) {

			params.leftMargin = (int) (mSolvePostionX - mflFish.getMeasuredWidth() * 0.5);
			params.topMargin = (int) (mSolvePostionY - mflFish.getMeasuredHeight() * 0.7);
			mflFish.setLayoutParams(params);
		}

		params = (ViewGroup.MarginLayoutParams) mBtnAsk1.getLayoutParams();
		if (params != null) {

			params.leftMargin = (int) (mAskPostionX);
			params.topMargin = (int) (mAskPostionY - mBtnAsk1.getMeasuredHeight());
			mBtnAsk1.setLayoutParams(params);
		}

		params = (ViewGroup.MarginLayoutParams) ivBirdLeft.getLayoutParams();
		if (params != null) {
			params.leftMargin = (int) (mAskPostionX - ivBirdLeft.getMeasuredWidth() * 0.5);
			params.topMargin = (int) (mAskPostionY - ivBirdLeft.getMeasuredHeight());
			ivBirdLeft.setLayoutParams(params);
		}

		return true;
	}

	@Override
	protected void onStart() {

		super.onStart();
		setCtrlsPostoin();
	}

	@Override
	public void setVisible(boolean visible) {

		super.setVisible(visible);
		if (visible) {
			setCtrlsPostoin();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 1, "刷新");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case 1:
			updateUserInfo();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void updateUserState() {
		mTvUserName.setText(UserInfo.state.name + "");
		mTvAskCount.setText(UserInfo.state.ask + "");
		mTvSolveCount.setText(UserInfo.state.solve + "");
		mrbNetState.setChecked(mUser.isLogin());
		mUser.isChange = false;
	}

	public void askBirdFly() {
		ivBirdLeft.setBackgroundResource(R.anim.bird_rock_left);
		rocketAnimationLeft = (AnimationDrawable) ivBirdLeft.getBackground();
		rocketAnimationLeft.start();

		ivBirdLeft.setVisibility(View.VISIBLE);
		mBtnAsk.setVisibility(View.INVISIBLE);
		mBtnAsk1.setVisibility(View.INVISIBLE);

		TranslateAnimation tAnimation = new TranslateAnimation(Animation.ABSOLUTE, -470, Animation.ABSOLUTE, -220);
		tAnimation.setDuration(3000);
		// tAnimation.setFillAfter(true);
		ivBirdLeft.startAnimation(tAnimation);
		new Handler().postDelayed(new Runnable() {

			public void run() {
				ivBirdLeft.setVisibility(View.GONE);
				mBtnAsk.setVisibility(View.VISIBLE);
				mBtnAsk1.setVisibility(View.VISIBLE);

				rocketAnimationLeft.stop();

			}

		}, 3000);
	}

	public static void askSend() {
		new Handler().postDelayed(new Runnable() {

			public void run() {
				mInstance.askBirdFly();
			}
		}, 700);

	}

	public void onClick(View arg0) {

		if (arg0 == mBtnAsk) {
			PublicArithmetic.buttonClickOnlyOneTime(mBtnAsk);
			askQusetion();
		} else if (arg0 == mBtnAsk1) {
			PublicArithmetic.buttonClickOnlyOneTime(mBtnAsk1);
			askQusetion();
		} else if (mBtnFish == arg0) {
			fishQusetion();
		} else if (arg0 == mBtnSolve || arg0 == mBtnSolve2) {
			solveBirdFly1();
		} else if (arg0 == mBtnFish) {
			fishQuestionBirdFly1();
		}
	}

	/**
	 * 捞一个问题
	 */
	private void fishQusetion() {

		mJsonMessage.cmd = JsonQuestion.OBTAIN;
		mJsonMessage.type = "";

		// 验证握手
		// 验证握手
		 Util.handkey++;
		mJsonMessage.handkey = Util.handkey;

		postPackageType = 3;
		mPostPackageEngine = new PostPackageEngine(this.getParent(), mJsonMessage, mUpdateBirdPostionLoop);
		mPostPackageEngine.post();		
		

	}

	/**
	 * 获取问题小鸟飞行
	 */
	private void fishQuestionBirdFly1() {

		ivBirdLeft.setBackgroundResource(R.anim.bird_rock_left_b);
		rocketAnimationLeft = (AnimationDrawable) ivBirdLeft.getBackground();
		rocketAnimationLeft.start();

		ivBirdLeft.setVisibility(View.VISIBLE);
		mBtnAsk.setVisibility(View.INVISIBLE);
		mBtnAsk1.setVisibility(View.INVISIBLE);

		float xTo = mSolvePostionX - mAskPostionX + ivBirdLeft.getMeasuredWidth() * 0.5f;
		TranslateAnimation tAnimation = new TranslateAnimation(0, xTo, 0, mSolvePostionY - mAskPostionY);
		tAnimation.setDuration(2000);
		// tAnimation.setFillAfter(true);
		ivBirdLeft.startAnimation(tAnimation);
		new Handler().postDelayed(new Runnable() {

			public void run() {
				fishQusetion();
				ivBirdLeft.setVisibility(View.INVISIBLE);
				rocketAnimationLeft.stop();
				isTaskListUpdate = true;
				updateMesage();
			}

		}, 2000);
	}

	/**
	 * 解答问题小鸟飞行
	 */
	private void solveBirdFly1() {

		ivBirdLeft.setBackgroundResource(R.anim.bird_rock_left_b);
		rocketAnimationLeft = (AnimationDrawable) ivBirdLeft.getBackground();
		rocketAnimationLeft.start();

		ivBirdLeft.setVisibility(View.VISIBLE);
		mBtnAsk.setVisibility(View.INVISIBLE);
		mBtnAsk1.setVisibility(View.INVISIBLE);

		float xTo = mSolvePostionX - mAskPostionX + ivBirdLeft.getMeasuredWidth() * 0.5f;
		float yTo = mSolvePostionY - (mAskPostionY - mBtnAsk1.getMeasuredHeight()); // mSolvePostionX
																					// -
																					// mAskPostionX
																					// +
																					// ivBirdLeft.getMeasuredWidth()
																					// *
																					// 0.5f;
		TranslateAnimation tAnimation = new TranslateAnimation(0, xTo, 0, yTo);
		tAnimation.setDuration(2000);
		// tAnimation.setFillAfter(true);
		ivBirdLeft.startAnimation(tAnimation);
		new Handler().postDelayed(new Runnable() {

			public void run() {
				solveQuestion();
				ivBirdLeft.setVisibility(View.INVISIBLE);
				rocketAnimationLeft.stop();
				isTaskListUpdate = true;
				updateMesage();
			}

		}, 2000);

		// new Handler().postDelayed(new Runnable(){

		// public void run() {
		// mllAsk.setVisibility(View.VISIBLE);
		// }

		// }, 5500);
	}

	private void updateUserInfo() {

		// mPostPackage = new PostPackage(this.getParent(), this);
		mLoginRequest.function = JsonFunction.LOGIN;

		mJsonLogin.imei = PhoneInfo.getInstance().getIMEI();
		mJsonLogin.name = mUser.getUserName();
		mJsonLogin.version = UserInfo.version;
		mLoginRequest.json = Json.toJson(mJsonLogin);
		Util.handkey++;
		mJsonMessage.handkey = Util.handkey;		

		postPackageType = 0;
		mPostPackageEngine = new PostPackageEngine(this.getParent(), mLoginRequest, mUpdateBirdPostionLoop);
		mPostPackageEngine.post();

		// if (mPostPackage.post(mLoginRequest, getString(R.string.host_ip),
		// true)) {
		//
		// // mLogin.setEnabled(false);
		// WaitingActivity.waiting(this, 0);
		// }
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {

			if (requestCode == ACTIVITY_ASK) {

			} else if (requestCode == ACTIVITY_SOLVE) {
				if (resultCode == requestCode) {
					// Toast.makeText(UserStateActivity.this, "222222",
					// Toast.LENGTH_SHORT).show();
					askBirdFly();
				}
			}
		}
		isTaskListUpdate = true;
		updateMesage();
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void askQusetion() {
		Intent intent = new Intent(this, AskQuestionActivity.class);
		intent.putExtra(PARAM_TYPE, AskQuestionActivity.TYPE_NEW);
		startActivityForResult(intent, ACTIVITY_ASK);

	}

	private void solveQuestion() {

		// mPostPackage = new PostPackage(this.getParent(), mGetSolveListener);
		mCurrentTask = mTaskList.putTaskId();

		if (mCurrentTask == null) {
			mJsonMessage.cmd = JsonQuestion.OBTAIN;
		} else {
			mJsonMessage.id = mCurrentTask.id;
			mJsonMessage.cmd = JsonQuestion.GET_ASK;
		}
		mJsonMessage.type = "";

		// 验证握手
		 Util.handkey++;
		mJsonMessage.handkey = Util.handkey;

		updateMesage();

		postPackageType = 1;
		mPostPackageEngine = new PostPackageEngine(this.getParent(), mJsonMessage, mUpdateBirdPostionLoop);
		mPostPackageEngine.post();

	}

	private void postPackageCallBack(ResultPackage result, int postPackageType) {
		if (result.isNetSucceed()) {
			JsonRequestResult ret = JsonRequestResult.fromJson(result.getJson());
			if (postPackageType == 0) {
				if (ret != null) {
					if (ret.succeed == true) {
						UserState state = Json.fromJson(ret.json, UserState.class);
						mUser.setInfo(state);
						updateUserState();
						ProcessMessage pro = ProcessMessage.getInstance();
						pro.process(ret, false);
					} else {
						new AlertDialog.Builder(this.getParent()).setTitle("提示").setMessage(ret.explain)
								.setPositiveButton("确定", null).show();
					}
				}
			} else if (postPackageType == 1) {
				if (ret != null) {
					if (ret.succeed == true) {
						Util.handkey++;// 更新握手值
						mQuestionInfo = Json.fromJson(ret.json, QuestionInfo.class);
						if (mQuestionInfo != null) {
							Intent intent = new Intent(UserStateActivity.this, SolveQuestionActivity.class);
							startActivityForResult(intent, ACTIVITY_SOLVE);

							// Intent intent = new
							// Intent(UserStateActivity.this,
							// GetQuestionActivity.class);
							// startActivityForResult(intent,ACTIVITY_SOLVE);
						}
						mTempTaskList.clear();
						int nCount = ret.items.size();
						for (int i = 0; i < nCount; i++) {
							JsonData data = ret.items.get(i);
							AskTask task = Json.fromJson(data.json, AskTask.class);
							if (task != null) {
								mTempTaskList.AddTask(task);
							}
						}
						mTaskList.setTaskList(mTempTaskList);
						updateMesage();
					} else {
						new AlertDialog.Builder(UserStateActivity.this.getParent()).setTitle("提示")
								.setMessage(ret.explain).setPositiveButton("确定", null).show();

					}
				}
			} else if (postPackageType == 3) {
				if (ret != null) {
					if (ret.succeed == true) {
						Util.handkey++;// 更新握手值
						mQuestionInfo = Json.fromJson(ret.json, QuestionInfo.class);
						if (mQuestionInfo != null) {
							Intent intent = new Intent(UserStateActivity.this, SolveQuestionActivity.class);
							startActivityForResult(intent, ACTIVITY_SOLVE);
						}
						mTempTaskList.clear();
						int nCount = ret.items.size();
						for (int i = 0; i < nCount; i++) {
							JsonData data = ret.items.get(i);
							AskTask task = Json.fromJson(data.json, AskTask.class);
							if (task != null) {
								mTempTaskList.AddTask(task);
							}
						}
						mFishList.setTaskList(mTempTaskList);
						showFishCount();
					} else {
						new AlertDialog.Builder(UserStateActivity.this.getParent()).setTitle("提示")
								.setMessage(ret.explain).setPositiveButton("确定", null).show();
						mFishList.deleteTopTask();
						isTaskListUpdate = true;
						updateMesage();
					}
				}
			}
		}

		mBtnAsk.setVisibility(View.VISIBLE);
		mBtnAsk1.setVisibility(View.VISIBLE);
	}


	@Override
	public void onNetPostResult(PostPackage owner, ResultPackage result) {

		if (result.isNetSucceed()) {

			JsonRequestResult ret = JsonRequestResult.fromJson(result.getJson());
			if (ret != null) {
				if (ret.succeed == true) {
					UserState state = Json.fromJson(ret.json, UserState.class);
					mUser.setInfo(state);

					updateUserState();

					ProcessMessage pro = ProcessMessage.getInstance();
					pro.process(ret, false);
				} else {
					new AlertDialog.Builder(this.getParent()).setTitle("提示").setMessage(ret.explain)
							.setPositiveButton("确定", null).show();
				}
			}
		}

		// mLogin.setEnabled(true);
		WaitingActivity.stop();
	}

	public void showTaskCount(int count) {
		showFishCount();
		if (count <= 0) {
			mtvTaskCount.setText("");
			mflSolve.setVisibility(View.INVISIBLE);
			mBtnSolve2.setVisibility(View.INVISIBLE);
			if (mFishList.getTaskCount() > 0) {
				mflFish.setVisibility(View.VISIBLE);
			} else {
				mflFish.setVisibility(View.INVISIBLE);
			}
		} else {
			mBtnSolve.setEnabled(true);
			mtvTaskCount.setText(count + "");
			// mtvTaskCount.setVisibility(View.VISIBLE);
			mflSolve.setVisibility(View.VISIBLE);
			mBtnSolve2.setVisibility(View.VISIBLE);
			mflFish.setVisibility(View.INVISIBLE);
		}
	}

	private MainTabActivity mMainTabActivity = MainTabActivity.getInstance();
	private InteractionActivity mInteractionActivity = InteractionActivity.getInstance();

	@Override
	public void updateMesage() {

		if (mUser.isChange) {
			updateUserState();
		}

		if (isTaskListUpdate) {
			isTaskListUpdate = false;

			int nCount = mTaskList.getTaskCount();
			showTaskCount(nCount);
			mMainTabActivity.updateTaskCount();
			mInteractionActivity.showTaskCount(nCount);
		}
	}

	@Override
	public void isShowTipDialog(String msg) {


	}

	public void showFishCount() {
		if (mFishList != null) {
			tvFishCount.setText(mFishList.getTaskCount() + "");
		}

	}
	
	@Override
	protected void onStop() {
		if(TextPlayer.getInstance().isPlaying()){
			TextPlayer.getInstance().stop();
		}
		super.onStop();
	}
}

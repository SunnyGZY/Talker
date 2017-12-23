package net.sunny.talker.push.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.EditText;

import net.sunny.talker.common.app.ToolbarActivity;
import net.sunny.talker.factory.model.api.RspModel;
import net.sunny.talker.factory.model.api.feedback.FeedbackModel;
import net.sunny.talker.factory.net.Network;
import net.sunny.talker.factory.net.RemoteService;
import net.sunny.talker.push.App;
import net.sunny.talker.push.R;

import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 用户反馈
 */
public class FeedbackActivity extends ToolbarActivity {

    @BindView(R.id.et_feedback_content)
    EditText mContent;
    @BindView(R.id.et_phone_num)
    EditText mPhoneNum;

    public static void show(Context context) {
        Intent intent = new Intent(context, FeedbackActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_feedback;
    }

    @OnClick(R.id.bt_submit)
    public void submit() {
        String feedbackId = UUID.randomUUID().toString();
        String content = mContent.getText().toString().trim();
        String phoneNum = mPhoneNum.getText().toString().trim();
        int versionCode = 0;
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = getPackageManager().
                    getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String appVersion = String.valueOf(versionCode);

        if (content.equals("")) {
            App.showToast(R.string.feedback_content_not_null);
            return;
        } else if (phoneNum.equals("")) {
            App.showToast(R.string.feedback_phone_not_null);
            return;
        }

        FeedbackModel model = new FeedbackModel(feedbackId, content, phoneNum, appVersion);
        putFeedback(model);
        finish();
    }

    /**
     * 将反馈信息上传至服务器
     *
     * @param model FeedbackModel
     */
    public static void putFeedback(FeedbackModel model) {

        RemoteService service = Network.remote();
        Call<RspModel<String>> call = service.putFeedback(model);
        call.enqueue(new Callback<RspModel<String>>() {
            @Override
            public void onResponse(Call<RspModel<String>> call, Response<RspModel<String>> response) {
                RspModel<String> rspModel = response.body();
                String result = rspModel.getResult();
                if (result.equals("OK")) {
                    App.showToast(R.string.thanks_for_your_feedback);

                } else {
                    // 反馈失败暂不做处理
                }
            }

            @Override
            public void onFailure(Call<RspModel<String>> call, Throwable t) {

            }
        });
    }
}

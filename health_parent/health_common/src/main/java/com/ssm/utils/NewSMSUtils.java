package com.ssm.utils;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;

/**
 * 短信发送工具类
 */
public class NewSMSUtils {

    /*
     * 阿里云 用户登录名称/显示名称
     *
     * 用户登录名称 SSM_Health@1323394258661704.onaliyun.com
     * AccessKey ID LTAI4G3GTT8tbNj8mi7WnH3i
     * AccessKey Secret neO1rHHijKgDKKtNwG6rKtc73Btsog
     * */

    public static final String VALIDATE_CODE = "SMS_198915774";//发送短信验证码  阿里云申请模版CODE,每个人都不一样
    public static final String ORDER_NOTICE = "SMS_198915780";//体检预约成功通知  阿里云申请模版CODE,每个人都不一样

    /*
     * 发送短信 新版本封装测试工具类
     *
     * 参数signName:短信签名
     * 参数templateCode:短信模板
     * 参数phoneNumbers:发送给哪个手机号
     * 参数param:发送短信里面的动态数据,如验证码
     */
    public static void sendShortMessage(String signName, String templateCode, String phoneNumbers, String param) throws ClientException {

        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAI4G3GTT8tbNj8mi7WnH3i", "neO1rHHijKgDKKtNwG6rKtc73Btsog");
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", phoneNumbers);
        request.putQueryParameter("SignName", signName);
        request.putQueryParameter("TemplateCode", templateCode);
        request.putQueryParameter("TemplateParam", "{\"code\":\"" + param + "\"}");
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }

    }


    // 测试方法
    public static void main(String[] args) {
        try {
            NewSMSUtils.sendShortMessage("传值健康", NewSMSUtils.VALIDATE_CODE, "18395557858", "1234");
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

}

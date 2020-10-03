package com.ssm.test;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.junit.Test;

/*
 * 测试七牛云服务
 *
 * 使用七牛云提供的SDK实现将本地图片上传存储到七牛云服务器
 * */
public class QiNiuTest {

    /*
     * 测试代码七牛云官网摘抄
     *
     * 测试在七牛云上上传文件
     *
     * 新版参数使用Region.region0(),导包不行,因此还是你使用旧版参数Zone.zone0()
     * */
    @Test
    public void testQiNiuUpload() {

        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Zone.zone0());  //新版参数使用Region.region0()
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //...生成上传凭证，然后准备上传
        String accessKey = "GrNI8EJP2hgKEhY8fnvXMwMRV-_0Es-vYKt30UA9";  //七牛云的AK
        String secretKey = "Nz-8O8UmexLB6kgF5pvcWHj7DMBNkJlauQ46sIgW";  //七牛云的SK
        String bucket = "ssmhealth";  //自定义,存储空间名字
        //如果是Windows情况下，格式是 D:\\qiniu\\test.png
        String localFilePath = "F:\\Google浏览器 下载\\刀剑神域.jpg";
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = "abc.jpg";  //自定义文件名
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        try {
            Response response = uploadManager.put(localFilePath, key, upToken);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            System.out.println(putRet.key);  //自定义上传文件的文件名
            System.out.println(putRet.hash);  //上传文件随机生成的哈希值
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
            }
        }
    }

    /*
     * 测试代码七牛云官网摘抄
     *
     * 测试在七牛云上删除文件
     *
     * 新版参数使用Region.region0(),导包不行,因此还是你使用旧版参数Zone.zone0()
     * */
    @Test
    public void testQiNiuDelete() {

        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Zone.zone0());
        //...其他参数参考类注释
        String accessKey = "GrNI8EJP2hgKEhY8fnvXMwMRV-_0Es-vYKt30UA9";  //七牛云的AK
        String secretKey = "Nz-8O8UmexLB6kgF5pvcWHj7DMBNkJlauQ46sIgW";  //七牛云的SK
        String bucket = "ssmhealth";  //自定义,存储空间名字
        String key = "abc.jpg";
        Auth auth = Auth.create(accessKey, secretKey);
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            bucketManager.delete(bucket, key);
        } catch (QiniuException ex) {
            //如果遇到异常，说明删除失败
            System.err.println(ex.code());
            System.err.println(ex.response.toString());
        }
    }

}

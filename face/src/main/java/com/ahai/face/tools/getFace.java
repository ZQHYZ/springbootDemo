package com.ahai.face.tools;

import com.ahai.face.dao.photoInfoDao;
import com.ahai.face.utils.SpringUtil;
import com.arcsoft.face.*;
import com.arcsoft.face.enums.*;
import com.arcsoft.face.toolkit.ImageInfo;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.arcsoft.face.toolkit.ImageFactory.getRGBData;
public class getFace {

    public static String faceconfig="E:\\lian";
    public static String face1="E:\\image\\tmp\\facetest.png";
    public static String face2="d:\\face.png";
    public  int i = 2;


    private ApplicationContext applicationContext = SpringUtil.getApplicationContext();
    private photoInfoDao photoInfoDao = applicationContext.getBean(com.ahai.face.dao.photoInfoDao.class);

    public boolean errorCode1() {
        //从官网获取
        String appId = "HRLhUBDv9Rqdehnet1H1rzzYMNQTHZmYEimJurpECwaM";
        String sdkKey = "Ca85Vht4kH447V7AQqgNRWNj6YUCTnfKmQWnbc6JUaZd";
        FaceEngine faceEngine = new FaceEngine(faceconfig);
        //激活引擎
        int errorCode = faceEngine.activeOnline(appId, sdkKey);
        if (errorCode != ErrorInfo.MOK.getValue() && errorCode != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED.getValue()) {
            System.out.println("引擎激活失败");
        }
        ActiveFileInfo activeFileInfo = new ActiveFileInfo();
        errorCode = faceEngine.getActiveFileInfo(activeFileInfo);
        if (errorCode != ErrorInfo.MOK.getValue() && errorCode != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED.getValue()) {
            System.out.println("获取激活文件信息失败");
        }
        //引擎配置
        EngineConfiguration engineConfiguration = new EngineConfiguration();
        engineConfiguration.setDetectMode(DetectMode.ASF_DETECT_MODE_IMAGE);
        engineConfiguration.setDetectFaceOrientPriority(DetectOrient.ASF_OP_ALL_OUT);
        engineConfiguration.setDetectFaceMaxNum(10);
        engineConfiguration.setDetectFaceScaleVal(16);
        //功能配置
        FunctionConfiguration functionConfiguration = new FunctionConfiguration();
        functionConfiguration.setSupportAge(true);
        functionConfiguration.setSupportFace3dAngle(true);
        functionConfiguration.setSupportFaceDetect(true);
        functionConfiguration.setSupportFaceRecognition(true);
        functionConfiguration.setSupportGender(true);
        functionConfiguration.setSupportLiveness(true);
        functionConfiguration.setSupportIRLiveness(true);
        engineConfiguration.setFunctionConfiguration(functionConfiguration);
        //初始化引擎
        errorCode = faceEngine.init(engineConfiguration);

        if (errorCode != ErrorInfo.MOK.getValue()) {
            System.out.println("初始化引擎失败");
        }

            //人脸检测
            ImageInfo imageInfo = getRGBData(new File(face1));
            List<FaceInfo> faceInfoList = new ArrayList<FaceInfo>();
            errorCode = faceEngine.detectFaces(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList);
            System.out.println(faceInfoList);

            //特征提取
            FaceFeature faceFeature = new FaceFeature();
            errorCode = faceEngine.extractFaceFeature(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList.get(0), faceFeature);
            System.out.println("特征值大小：" + faceFeature.getFeatureData().length);
        while (i<=photoInfoDao.count()) {
            face2 = "E:\\image\\" + photoInfoDao.selectByPrimaryKey(i).getName();
            //人脸检测2
            ImageInfo imageInfo2 = getRGBData(new File(face2));
            List<FaceInfo> faceInfoList2 = new ArrayList<FaceInfo>();
            errorCode = faceEngine.detectFaces(imageInfo2.getImageData(), imageInfo2.getWidth(), imageInfo2.getHeight(), imageInfo.getImageFormat(), faceInfoList2);
            System.out.println(faceInfoList);

            //特征提取2
            FaceFeature faceFeature2 = new FaceFeature();
            errorCode = faceEngine.extractFaceFeature(imageInfo2.getImageData(), imageInfo2.getWidth(), imageInfo2.getHeight(), imageInfo.getImageFormat(), faceInfoList2.get(0), faceFeature2);
            System.out.println("特征值大小：" + faceFeature.getFeatureData().length);

            //特征比对
            FaceFeature targetFaceFeature = new FaceFeature();
            targetFaceFeature.setFeatureData(faceFeature.getFeatureData());
            FaceFeature sourceFaceFeature = new FaceFeature();
            sourceFaceFeature.setFeatureData(faceFeature2.getFeatureData());
            FaceSimilar faceSimilar = new FaceSimilar();

            errorCode = faceEngine.compareFaceFeature(targetFaceFeature, sourceFaceFeature, faceSimilar);

            System.out.println("相似度：" + faceSimilar.getScore());
            if (faceSimilar.getScore() > 0.9) {
                return true;
            }
            i++;
        }
            return false;
    }
}

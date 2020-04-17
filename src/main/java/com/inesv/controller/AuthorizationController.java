package com.inesv.controller;

import com.google.common.collect.Maps;
import com.inesv.annotation.Login;
import com.inesv.annotation.LoginUser;
import com.inesv.annotation.UnLogin;
import com.inesv.common.constant.RErrorEnum;
import com.inesv.common.exception.RRException;
import com.inesv.model.User;
import com.inesv.service.AuthorizeService;
import com.inesv.util.BaseResponse;
import com.inesv.util.RspUtil;
import com.inesv.util.ValidataUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/authorize")
public class AuthorizationController {

    @Autowired
    private AuthorizeService authorizeService;

    @Value("${headPic.path}")
    public String headPic;
    @Value("${mapping.pic.path}")
    public String mappingPicPath;

    //判断当前用户是否授权
    @RequestMapping("/isAuthorize")
    @Login
    @UnLogin
    public BaseResponse isAuthorize(@LoginUser User user, @RequestParam("programId") String programId) {
        Integer count=authorizeService.isAuthorize(user.getId(),programId);
        HashMap<String, Object> map = Maps.newHashMap();
        if (count==0){
            map.put("isAuthorize",false);
        }else {
            map.put("isAuthorize",true);
        }
        map.put("isAuthorize",false);
        return RspUtil.success(map);
    }

    //程序注册
    @RequestMapping("/register")
    @UnLogin
    public BaseResponse register(@RequestParam("programId") String programId,@RequestParam("programName") String programName,@RequestParam("picture") String picture){
        authorizeService.register(programId,programName,picture);
        return RspUtil.success();
    }

    //图片上传
    @RequestMapping("/upload")
    @UnLogin
    public BaseResponse upload(MultipartFile picture){
        //图片上传
        pictureUpload(picture);

        return RspUtil.success();
    }

    public String pictureUpload(MultipartFile picture){
        String suffix = picture.getOriginalFilename().substring(picture.getOriginalFilename().lastIndexOf("."));
        String filename = UUID.randomUUID() + suffix;
        String filePath = headPic + filename;//本地存放路径
        String urlPath = mappingPicPath + filename;//url访问路径
        try {
            picture.transferTo(new File(filePath));
        } catch (IOException e) {
            log.error("保存文件到本地失败", e);
            throw new RRException(RErrorEnum.UPLOAD_FAILURE);
        }
        return urlPath;
    }



    //生成程序id
    @RequestMapping("/createProgramId")
    @UnLogin
    public BaseResponse createProgramId(){
        String uuid = ValidataUtil.generateUUID();
        Map<String,String> map=new HashMap<>(1);
        map.put("programId",uuid);
        return RspUtil.success(map);
    }

    //获取授权信息
    @RequestMapping("/authorizeInfo")
    @Login
    @UnLogin
    public BaseResponse info(@LoginUser User user, @RequestParam("programId") String programId){
        Map<String,String> map=authorizeService.info(programId);
        map.put("userPhoto",user.getPhoto());
        map.put("username",user.getUsername());
        return RspUtil.success(map);
    }

    //授权接口
    @RequestMapping("/authorize")
    @Login
    @UnLogin
    public BaseResponse authorize(@LoginUser User user, @RequestParam("programId") String programId){
        String programToken;
        synchronized (this){
            Integer count=authorizeService.isAuthorize(user.getId(),programId);
            if (count==0){
                programToken= ValidataUtil.generateUUID();
                //插入授权用户记录表
                authorizeService.insertRecord(user.getId(),programToken,programId);

            }
        }
        /*Map<String,String> map=new HashMap<>(1);
        map.put("programToken",programToken);*/
        Map<String,String> map=new HashMap<>(3);
        map.put("userPhoto",user.getPhoto());
        map.put("username",user.getUsername());
        map.put("userId",user.getId().toString());
        return RspUtil.success(map);
    }

    //授权接口
    @RequestMapping("/userInfo")
    @Login
    @UnLogin
    public BaseResponse userInfo(@LoginUser User user){
        Map<String,String> map=new HashMap<>(3);
        map.put("userPhoto",user.getPhoto());
        map.put("username",user.getUsername());
        map.put("userId",user.getId().toString());
        return RspUtil.success(map);
    }

}

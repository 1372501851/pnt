package com.inesv.service.impl;

import com.inesv.mapper.IDCardMapper;
import com.inesv.mapper.UserMapper;
import com.inesv.model.IDCard;
import com.inesv.model.User;
import com.inesv.service.IDCardService;
import com.inesv.util.ResponseParamsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * @author: xujianfeng
 * @create: 2018-06-29 11:29
 **/
@Service
public class IDCardServiceImpl implements IDCardService {
    private static final Logger log = LoggerFactory
            .getLogger(IDCardServiceImpl.class);

    @Value("${idpic.path}")
    public String idpicPath;
    @Value("${mapping.pic.path}")
    public String mappingPicPath;

    @Autowired
    private IDCardMapper idCardMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void uploadIDCard(String name, String idCardNumber, MultipartFile[] file, ResponseParamsDto responseParamsDto, String token) throws RuntimeException {
        User user = userMapper.getUserInfoByToken(token);
        if (user == null){
            throw new RuntimeException(responseParamsDto.ACCOUNT_NULL_DESC);
        }

        //一个身份证最多关联1个用户
        IDCard iCard = new IDCard();
        iCard.setState(1);
        iCard.setName(name);
        iCard.setIdCardNumber(idCardNumber);
        List<IDCard> idCardList = idCardMapper.getByConditions(iCard);
        if(idCardList.size() >= 1){
            throw new RuntimeException(responseParamsDto.ID_CARD_BINDTOOMUCH);
        }

        IDCard idCard = new IDCard();
        idCard.setName(name);
        idCard.setIdCardNumber(idCardNumber);
        idCard.setUserId(user.getId().intValue());

        //处理上传的多个文件
        for (int i=0;i<file.length;i++) {
            MultipartFile multipartFile = file[i];
            executeUpload(i, multipartFile, responseParamsDto, idCard);
        }

        int count = idCardMapper.add(idCard);
        if(count <=0){
            throw new RuntimeException(responseParamsDto.FAIL);
        }
    }

    @Override
    public IDCard getIDCard(String token, ResponseParamsDto responseParamsDto) throws RuntimeException  {
        User user = userMapper.getUserInfoByToken(token);
        if (user == null){
            throw new RuntimeException(responseParamsDto.ACCOUNT_NULL_DESC);
        }
        IDCard idCard = new IDCard();
        idCard.setUserId(user.getId().intValue());
        idCard.setLimitNum(1);
        idCard.setOrderByCondition("ORDER BY id DESC");
        List<IDCard> idCardList = idCardMapper.getByConditionsWithLimit(idCard);

        IDCard iCard = new IDCard();
        if(idCardList.size() <= 0){
            iCard.setState(3);//未提审核申请
            return iCard;
        }

        IDCard card = idCardList.get(0);
        BeanUtils.copyProperties(card, iCard);
        return iCard;
    }

    /**
     * 单个文件服务器上传
     * @param index
     * @param file
     * @param responseParamsDto
     * @param idCard
     */
    private void executeUpload(int index, MultipartFile file, ResponseParamsDto responseParamsDto, IDCard idCard) {
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        if(!".jpg".equals(suffix) && !".png".equals(suffix)){
            throw new RuntimeException(responseParamsDto.ID_PIC_INVALID);
        }

        String filename = UUID.randomUUID() + suffix;
        String filePath = idpicPath + filename;//本地存放路径
        String urlPath = mappingPicPath + filename;//url访问路径

        try {
            file.transferTo(new File(filePath));
        } catch (IOException e) {
            log.error("保存文件到本地失败", e);
            throw new RuntimeException(responseParamsDto.FAIL);
        }

        //保存到数据库
        if(index == 0){//身份证正面
            idCard.setSide("front");
            idCard.setFrontFilePath(filePath);
            idCard.setFrontUrl(urlPath);
        }else if(index == 1){//身份证反面
            idCard.setSide("back");
            idCard.setBackFilePath(filePath);
            idCard.setBackUrl(urlPath);
        }else{
            idCard.setHandUrl(urlPath);
        }
    }
}

package com.inesv.util;

public class LanguageUtil {

    public static ResponseParamsDto proving(String languageType){
        ResponseParamsDto responseParamsDto = null;
        if (languageType == null){  //默认中文
            responseParamsDto = ResponseParamsDto.responseParamsCNDto;
        }else if (languageType.equalsIgnoreCase("0")){ //中文
            responseParamsDto = ResponseParamsDto.responseParamsCNDto;
        }else if (languageType.equalsIgnoreCase("1")){ //英文
            responseParamsDto = ResponseParamsDto.responseParamsENDto;
        }else if (languageType.equalsIgnoreCase("2")){ //KR
            responseParamsDto = ResponseParamsDto.responseParamsKRDto;
        }else{  //默认中文
            responseParamsDto = ResponseParamsDto.responseParamsCNDto;
        }
        return responseParamsDto;
    }

}

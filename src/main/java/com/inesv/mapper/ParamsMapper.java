package com.inesv.mapper;

import com.inesv.model.Params;

public interface ParamsMapper {

    Params queryByKey(String paramKey);

	Params getParams(String paramsKey);
	
	Long updateParams(Params param);
   
}


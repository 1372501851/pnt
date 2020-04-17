package com.inesv.mapper;


import com.inesv.model.SpotDispute;
import java.util.List;

public interface SpotDisputeMapper {
    /**
     * 新增纠纷记录
     * @param spotDisputeDto
     * @return
     */
    int insertSpotDispute(SpotDispute spotDisputeDto) throws Exception;

    /**
     * 修改纠纷记录
     * @param spotDisputeDto
     * @return
     * @throws Exception
     */
    int updateSpotDispute(SpotDispute spotDisputeDto) throws Exception;

    /**
     * 获取纠纷记录数
     * @param spotDisputeDto
     * @return
     * @throws Exception
     */
    int getSpotDisputeByCount(SpotDispute spotDisputeDto) throws Exception;

    /**
     * 条件查找纠纷记录
     * @param spotDisputeDto
     * @return
     */
    SpotDispute getSpotDisputeByCondition(SpotDispute spotDisputeDto) throws Exception;

    /**
     * 条件查找纠纷记录
     * @param spotDisputeDto
     * @return
     */
    List<SpotDispute> getSpotDisputeByConditions(SpotDispute spotDisputeDto) throws Exception;

    /**
     * 查询用户纠纷记录
     * */
    List<SpotDispute> queryByUser(Integer userNo);

    /**
     * 查询纠纷记录详情
     * */
    SpotDispute queryById(Long id);



    int updateSpotDisputeOther(SpotDispute spotDisputeDto) throws Exception;
}

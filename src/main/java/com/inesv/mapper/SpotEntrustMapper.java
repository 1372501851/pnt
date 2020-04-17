package com.inesv.mapper;


import com.inesv.model.SpotEntrust;
import java.util.List;

public interface SpotEntrustMapper {

    /**
     * 新增币币广告委托记录
     * @param spotEntrustDto
     * @return
     */
    int insertSpotEntrust(SpotEntrust spotEntrustDto) throws Exception;

    /**
     * 修改广告委托记录
     * @param spotEntrustDto
     * @return
     * @throws Exception
     */
    int updateSpotEntrust(SpotEntrust spotEntrustDto) throws Exception;

    /**
     * 批量修改广告委托记录
     * @param spotEntrustDtos
     * @return
     * @throws Exception
     */
    int updateSpotEntrusts(List<SpotEntrust> spotEntrustDtos) throws Exception;

    /**
     * 主键修改
     * @param spotEntrustDto
     * @return
     * @throws Exception
     */
    int updateByPrimaryKey(SpotEntrust spotEntrustDto) throws Exception;

    /**
     * 条件查询广告交易委托记录
     * @param spotEntrustDto
     * @return
     * @throws Exception
     */
    SpotEntrust selectSpotEntrustByCondition(SpotEntrust spotEntrustDto) throws Exception;

    /**
     * 条件查询广告交易委托记录
     * @param spotEntrustDto
     * @return
     * @throws Exception
     */
    List<SpotEntrust> selectSpotEntrustByConditions(SpotEntrust spotEntrustDto) throws Exception;

    /**
     * 条件查询广告交易委托记录（总记录数）
     * @param spotEntrustDto
     * @return
     * @throws Exception
     */
    int selectCountByCondition(SpotEntrust spotEntrustDto) throws Exception;

    /**
     * 条件查询广告交易委托记录
     * @param spotEntrustDto
     * @return
     * @throws Exception
     */
    List<SpotEntrust> selectSpotEntrustByConditionsAndMultiTable(SpotEntrust spotEntrustDto) throws Exception;

    /**
     * 条件查询广告交易委托记录
     * @param spotEntrustDto
     * @return
     * @throws Exception
     */
    List<SpotEntrust> selectSpotEntrustByConditionsAndFeatured(SpotEntrust spotEntrustDto) throws Exception;

    /**
     * 条件查询C2C交易委托记录（行级锁,不是处理事务时不要使用）
     * @param spotEntrustDto
     * @return
     * @throws Exception
     */
    SpotEntrust selectSpotEntrustByConditionForUpdate(SpotEntrust spotEntrustDto) throws Exception;

    /**
     * 条件查询匹配买（卖）C2C交易委托记录
     * @param spotEntrustDto
     * @return
     * @throws Exception
     */
    List<SpotEntrust> selectSpotEntrustByMatching(SpotEntrust spotEntrustDto) throws Exception;

    /**
     * 条件查询匹配买（卖）C2C交易委托记录（行级锁,不是处理事务时不要使用）
     * @param spotEntrustDto
     * @return
     * @throws Exception
     */
    List<SpotEntrust> selectSpotEntrustByMatchingForUpdate(SpotEntrust spotEntrustDto) throws Exception;

    /**
     * 主键获取广告（行级锁）
     * @param spotEntrstNo
     * @return
     * @throws Exception
     */
    SpotEntrust selectSpotEntrustByPrimaryKey(Long spotEntrstNo) throws Exception;
    
    /**
     * 查询不重复用户
     * @param spotEntrustDto
     * @return
     * @throws Exception
     */
    List<SpotEntrust> selectDistinctSpotEntrustByConditionsAndFeatured(SpotEntrust spotEntrustDto) throws Exception;

}
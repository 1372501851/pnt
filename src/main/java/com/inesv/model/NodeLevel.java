package com.inesv.model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
/**
*
*  @author author
*/
public class NodeLevel implements Serializable {

    private static final long serialVersionUID = 1564564244381L;


    /**
    * 主键
    * 
    * isNullAble:0
    */
    private Long id;

    /**
    * 等级
    * isNullAble:0
    */
    private Integer level;

    /**
    * 等级名称
    * isNullAble:0
    */
    private String name;

    /**
    * 最小金额>=

    * isNullAble:0
    */
    private java.math.BigDecimal amountMin;

    /**
    * 最大金额<
    * isNullAble:0
    */
    private java.math.BigDecimal amountMax;

    /**
    * 静态收益比例
    * isNullAble:0
    */
    private Double staticProfit;

    /**
    * 备注
    * isNullAble:0
    */
    private String remark;

    /**
    * 创建时间
    * isNullAble:0,defaultVal:CURRENT_TIMESTAMP
    */
    private java.time.LocalDateTime createtime;

    /**
    * 更新时间
    * isNullAble:0,defaultVal:CURRENT_TIMESTAMP
    */
    private java.time.LocalDateTime updatetime;


    public void setId(Long id){this.id = id;}

    public Long getId(){return this.id;}

    public void setLevel(Integer level){this.level = level;}

    public Integer getLevel(){return this.level;}

    public void setName(String name){this.name = name;}

    public String getName(){return this.name;}

    public void setAmountMin(java.math.BigDecimal amountMin){this.amountMin = amountMin;}

    public java.math.BigDecimal getAmountMin(){return this.amountMin;}

    public void setAmountMax(java.math.BigDecimal amountMax){this.amountMax = amountMax;}

    public java.math.BigDecimal getAmountMax(){return this.amountMax;}

    public void setStaticProfit(Double staticProfit){this.staticProfit = staticProfit;}

    public Double getStaticProfit(){return this.staticProfit;}

    public void setRemark(String remark){this.remark = remark;}

    public String getRemark(){return this.remark;}

    public void setCreatetime(java.time.LocalDateTime createtime){this.createtime = createtime;}

    public java.time.LocalDateTime getCreatetime(){return this.createtime;}

    public void setUpdatetime(java.time.LocalDateTime updatetime){this.updatetime = updatetime;}

    public java.time.LocalDateTime getUpdatetime(){return this.updatetime;}
    @Override
    public String toString() {
        return "NodeLevel{" +
                "id='" + id + '\'' +
                "level='" + level + '\'' +
                "name='" + name + '\'' +
                "amountMin='" + amountMin + '\'' +
                "amountMax='" + amountMax + '\'' +
                "staticProfit='" + staticProfit + '\'' +
                "remark='" + remark + '\'' +
                "createtime='" + createtime + '\'' +
                "updatetime='" + updatetime + '\'' +
            '}';
    }

    public static Builder Build(){return new Builder();}

    public static ConditionBuilder ConditionBuild(){return new ConditionBuilder();}

    public static UpdateBuilder UpdateBuild(){return new UpdateBuilder();}

    public static QueryBuilder QueryBuild(){return new QueryBuilder();}

    public static class UpdateBuilder {

        private NodeLevel set;

        private ConditionBuilder where;

        public UpdateBuilder set(NodeLevel set){
            this.set = set;
            return this;
        }

        public NodeLevel getSet(){
            return this.set;
        }

        public UpdateBuilder where(ConditionBuilder where){
            this.where = where;
            return this;
        }

        public ConditionBuilder getWhere(){
            return this.where;
        }

        public UpdateBuilder build(){
            return this;
        }
    }

    public static class QueryBuilder extends NodeLevel{
        /**
        * 需要返回的列
        */
        private Map<String,Object> fetchFields;

        public Map<String,Object> getFetchFields(){return this.fetchFields;}

        private List<Long> idList;

        public List<Long> getIdList(){return this.idList;}

        private Long idSt;

        private Long idEd;

        public Long getIdSt(){return this.idSt;}

        public Long getIdEd(){return this.idEd;}

        private List<Integer> levelList;

        public List<Integer> getLevelList(){return this.levelList;}

        private Integer levelSt;

        private Integer levelEd;

        public Integer getLevelSt(){return this.levelSt;}

        public Integer getLevelEd(){return this.levelEd;}

        private List<String> nameList;

        public List<String> getNameList(){return this.nameList;}


        private List<String> fuzzyName;

        public List<String> getFuzzyName(){return this.fuzzyName;}

        private List<String> rightFuzzyName;

        public List<String> getRightFuzzyName(){return this.rightFuzzyName;}
        private List<java.math.BigDecimal> amountMinList;

        public List<java.math.BigDecimal> getAmountMinList(){return this.amountMinList;}

        private java.math.BigDecimal amountMinSt;

        private java.math.BigDecimal amountMinEd;

        public java.math.BigDecimal getAmountMinSt(){return this.amountMinSt;}

        public java.math.BigDecimal getAmountMinEd(){return this.amountMinEd;}

        private List<java.math.BigDecimal> amountMaxList;

        public List<java.math.BigDecimal> getAmountMaxList(){return this.amountMaxList;}

        private java.math.BigDecimal amountMaxSt;

        private java.math.BigDecimal amountMaxEd;

        public java.math.BigDecimal getAmountMaxSt(){return this.amountMaxSt;}

        public java.math.BigDecimal getAmountMaxEd(){return this.amountMaxEd;}

        private List<Double> staticProfitList;

        public List<Double> getStaticProfitList(){return this.staticProfitList;}

        private Double staticProfitSt;

        private Double staticProfitEd;

        public Double getStaticProfitSt(){return this.staticProfitSt;}

        public Double getStaticProfitEd(){return this.staticProfitEd;}

        private List<String> remarkList;

        public List<String> getRemarkList(){return this.remarkList;}


        private List<String> fuzzyRemark;

        public List<String> getFuzzyRemark(){return this.fuzzyRemark;}

        private List<String> rightFuzzyRemark;

        public List<String> getRightFuzzyRemark(){return this.rightFuzzyRemark;}
        private List<java.time.LocalDateTime> createtimeList;

        public List<java.time.LocalDateTime> getCreatetimeList(){return this.createtimeList;}

        private java.time.LocalDateTime createtimeSt;

        private java.time.LocalDateTime createtimeEd;

        public java.time.LocalDateTime getCreatetimeSt(){return this.createtimeSt;}

        public java.time.LocalDateTime getCreatetimeEd(){return this.createtimeEd;}

        private List<java.time.LocalDateTime> updatetimeList;

        public List<java.time.LocalDateTime> getUpdatetimeList(){return this.updatetimeList;}

        private java.time.LocalDateTime updatetimeSt;

        private java.time.LocalDateTime updatetimeEd;

        public java.time.LocalDateTime getUpdatetimeSt(){return this.updatetimeSt;}

        public java.time.LocalDateTime getUpdatetimeEd(){return this.updatetimeEd;}

        private QueryBuilder (){
            this.fetchFields = new HashMap<>();
        }

        public QueryBuilder idBetWeen(Long idSt,Long idEd){
            this.idSt = idSt;
            this.idEd = idEd;
            return this;
        }

        public QueryBuilder idGreaterEqThan(Long idSt){
            this.idSt = idSt;
            return this;
        }
        public QueryBuilder idLessEqThan(Long idEd){
            this.idEd = idEd;
            return this;
        }


        public QueryBuilder id(Long id){
            setId(id);
            return this;
        }

        public QueryBuilder idList(Long ... id){
            this.idList = solveNullList(id);
            return this;
        }

        public QueryBuilder idList(List<Long> id){
            this.idList = id;
            return this;
        }

        public QueryBuilder fetchId(){
            setFetchFields("fetchFields","id");
            return this;
        }

        public QueryBuilder excludeId(){
            setFetchFields("excludeFields","id");
            return this;
        }

        public QueryBuilder levelBetWeen(Integer levelSt,Integer levelEd){
            this.levelSt = levelSt;
            this.levelEd = levelEd;
            return this;
        }

        public QueryBuilder levelGreaterEqThan(Integer levelSt){
            this.levelSt = levelSt;
            return this;
        }
        public QueryBuilder levelLessEqThan(Integer levelEd){
            this.levelEd = levelEd;
            return this;
        }


        public QueryBuilder level(Integer level){
            setLevel(level);
            return this;
        }

        public QueryBuilder levelList(Integer ... level){
            this.levelList = solveNullList(level);
            return this;
        }

        public QueryBuilder levelList(List<Integer> level){
            this.levelList = level;
            return this;
        }

        public QueryBuilder fetchLevel(){
            setFetchFields("fetchFields","level");
            return this;
        }

        public QueryBuilder excludeLevel(){
            setFetchFields("excludeFields","level");
            return this;
        }

        public QueryBuilder fuzzyName (List<String> fuzzyName){
            this.fuzzyName = fuzzyName;
            return this;
        }

        public QueryBuilder fuzzyName (String ... fuzzyName){
            this.fuzzyName = solveNullList(fuzzyName);
            return this;
        }

        public QueryBuilder rightFuzzyName (List<String> rightFuzzyName){
            this.rightFuzzyName = rightFuzzyName;
            return this;
        }

        public QueryBuilder rightFuzzyName (String ... rightFuzzyName){
            this.rightFuzzyName = solveNullList(rightFuzzyName);
            return this;
        }

        public QueryBuilder name(String name){
            setName(name);
            return this;
        }

        public QueryBuilder nameList(String ... name){
            this.nameList = solveNullList(name);
            return this;
        }

        public QueryBuilder nameList(List<String> name){
            this.nameList = name;
            return this;
        }

        public QueryBuilder fetchName(){
            setFetchFields("fetchFields","name");
            return this;
        }

        public QueryBuilder excludeName(){
            setFetchFields("excludeFields","name");
            return this;
        }

        public QueryBuilder amountMinBetWeen(java.math.BigDecimal amountMinSt,java.math.BigDecimal amountMinEd){
            this.amountMinSt = amountMinSt;
            this.amountMinEd = amountMinEd;
            return this;
        }

        public QueryBuilder amountMinGreaterEqThan(java.math.BigDecimal amountMinSt){
            this.amountMinSt = amountMinSt;
            return this;
        }
        public QueryBuilder amountMinLessEqThan(java.math.BigDecimal amountMinEd){
            this.amountMinEd = amountMinEd;
            return this;
        }


        public QueryBuilder amountMin(java.math.BigDecimal amountMin){
            setAmountMin(amountMin);
            return this;
        }

        public QueryBuilder amountMinList(java.math.BigDecimal ... amountMin){
            this.amountMinList = solveNullList(amountMin);
            return this;
        }

        public QueryBuilder amountMinList(List<java.math.BigDecimal> amountMin){
            this.amountMinList = amountMin;
            return this;
        }

        public QueryBuilder fetchAmountMin(){
            setFetchFields("fetchFields","amountMin");
            return this;
        }

        public QueryBuilder excludeAmountMin(){
            setFetchFields("excludeFields","amountMin");
            return this;
        }

        public QueryBuilder amountMaxBetWeen(java.math.BigDecimal amountMaxSt,java.math.BigDecimal amountMaxEd){
            this.amountMaxSt = amountMaxSt;
            this.amountMaxEd = amountMaxEd;
            return this;
        }

        public QueryBuilder amountMaxGreaterEqThan(java.math.BigDecimal amountMaxSt){
            this.amountMaxSt = amountMaxSt;
            return this;
        }
        public QueryBuilder amountMaxLessEqThan(java.math.BigDecimal amountMaxEd){
            this.amountMaxEd = amountMaxEd;
            return this;
        }


        public QueryBuilder amountMax(java.math.BigDecimal amountMax){
            setAmountMax(amountMax);
            return this;
        }

        public QueryBuilder amountMaxList(java.math.BigDecimal ... amountMax){
            this.amountMaxList = solveNullList(amountMax);
            return this;
        }

        public QueryBuilder amountMaxList(List<java.math.BigDecimal> amountMax){
            this.amountMaxList = amountMax;
            return this;
        }

        public QueryBuilder fetchAmountMax(){
            setFetchFields("fetchFields","amountMax");
            return this;
        }

        public QueryBuilder excludeAmountMax(){
            setFetchFields("excludeFields","amountMax");
            return this;
        }

        public QueryBuilder staticProfitBetWeen(Double staticProfitSt,Double staticProfitEd){
            this.staticProfitSt = staticProfitSt;
            this.staticProfitEd = staticProfitEd;
            return this;
        }

        public QueryBuilder staticProfitGreaterEqThan(Double staticProfitSt){
            this.staticProfitSt = staticProfitSt;
            return this;
        }
        public QueryBuilder staticProfitLessEqThan(Double staticProfitEd){
            this.staticProfitEd = staticProfitEd;
            return this;
        }


        public QueryBuilder staticProfit(Double staticProfit){
            setStaticProfit(staticProfit);
            return this;
        }

        public QueryBuilder staticProfitList(Double ... staticProfit){
            this.staticProfitList = solveNullList(staticProfit);
            return this;
        }

        public QueryBuilder staticProfitList(List<Double> staticProfit){
            this.staticProfitList = staticProfit;
            return this;
        }

        public QueryBuilder fetchStaticProfit(){
            setFetchFields("fetchFields","staticProfit");
            return this;
        }

        public QueryBuilder excludeStaticProfit(){
            setFetchFields("excludeFields","staticProfit");
            return this;
        }

        public QueryBuilder fuzzyRemark (List<String> fuzzyRemark){
            this.fuzzyRemark = fuzzyRemark;
            return this;
        }

        public QueryBuilder fuzzyRemark (String ... fuzzyRemark){
            this.fuzzyRemark = solveNullList(fuzzyRemark);
            return this;
        }

        public QueryBuilder rightFuzzyRemark (List<String> rightFuzzyRemark){
            this.rightFuzzyRemark = rightFuzzyRemark;
            return this;
        }

        public QueryBuilder rightFuzzyRemark (String ... rightFuzzyRemark){
            this.rightFuzzyRemark = solveNullList(rightFuzzyRemark);
            return this;
        }

        public QueryBuilder remark(String remark){
            setRemark(remark);
            return this;
        }

        public QueryBuilder remarkList(String ... remark){
            this.remarkList = solveNullList(remark);
            return this;
        }

        public QueryBuilder remarkList(List<String> remark){
            this.remarkList = remark;
            return this;
        }

        public QueryBuilder fetchRemark(){
            setFetchFields("fetchFields","remark");
            return this;
        }

        public QueryBuilder excludeRemark(){
            setFetchFields("excludeFields","remark");
            return this;
        }

        public QueryBuilder createtimeBetWeen(java.time.LocalDateTime createtimeSt,java.time.LocalDateTime createtimeEd){
            this.createtimeSt = createtimeSt;
            this.createtimeEd = createtimeEd;
            return this;
        }

        public QueryBuilder createtimeGreaterEqThan(java.time.LocalDateTime createtimeSt){
            this.createtimeSt = createtimeSt;
            return this;
        }
        public QueryBuilder createtimeLessEqThan(java.time.LocalDateTime createtimeEd){
            this.createtimeEd = createtimeEd;
            return this;
        }


        public QueryBuilder createtime(java.time.LocalDateTime createtime){
            setCreatetime(createtime);
            return this;
        }

        public QueryBuilder createtimeList(java.time.LocalDateTime ... createtime){
            this.createtimeList = solveNullList(createtime);
            return this;
        }

        public QueryBuilder createtimeList(List<java.time.LocalDateTime> createtime){
            this.createtimeList = createtime;
            return this;
        }

        public QueryBuilder fetchCreatetime(){
            setFetchFields("fetchFields","createtime");
            return this;
        }

        public QueryBuilder excludeCreatetime(){
            setFetchFields("excludeFields","createtime");
            return this;
        }

        public QueryBuilder updatetimeBetWeen(java.time.LocalDateTime updatetimeSt,java.time.LocalDateTime updatetimeEd){
            this.updatetimeSt = updatetimeSt;
            this.updatetimeEd = updatetimeEd;
            return this;
        }

        public QueryBuilder updatetimeGreaterEqThan(java.time.LocalDateTime updatetimeSt){
            this.updatetimeSt = updatetimeSt;
            return this;
        }
        public QueryBuilder updatetimeLessEqThan(java.time.LocalDateTime updatetimeEd){
            this.updatetimeEd = updatetimeEd;
            return this;
        }


        public QueryBuilder updatetime(java.time.LocalDateTime updatetime){
            setUpdatetime(updatetime);
            return this;
        }

        public QueryBuilder updatetimeList(java.time.LocalDateTime ... updatetime){
            this.updatetimeList = solveNullList(updatetime);
            return this;
        }

        public QueryBuilder updatetimeList(List<java.time.LocalDateTime> updatetime){
            this.updatetimeList = updatetime;
            return this;
        }

        public QueryBuilder fetchUpdatetime(){
            setFetchFields("fetchFields","updatetime");
            return this;
        }

        public QueryBuilder excludeUpdatetime(){
            setFetchFields("excludeFields","updatetime");
            return this;
        }
        private <T>List<T> solveNullList(T ... objs){
            if (objs != null){
            List<T> list = new ArrayList<>();
                for (T item : objs){
                    if (item != null){
                        list.add(item);
                    }
                }
                return list;
            }
            return null;
        }

        public QueryBuilder fetchAll(){
            this.fetchFields.put("AllFields",true);
            return this;
        }

        public QueryBuilder addField(String ... fields){
            List<String> list = new ArrayList<>();
            if (fields != null){
                for (String field : fields){
                    list.add(field);
                }
            }
            this.fetchFields.put("otherFields",list);
            return this;
        }
        @SuppressWarnings("unchecked")
        private void setFetchFields(String key,String val){
            Map<String,Boolean> fields= (Map<String, Boolean>) this.fetchFields.get(key);
            if (fields == null){
                fields = new HashMap<>();
            }
            fields.put(val,true);
            this.fetchFields.put(key,fields);
        }

        public NodeLevel build(){return this;}
    }


    public static class ConditionBuilder{
        private List<Long> idList;

        public List<Long> getIdList(){return this.idList;}

        private Long idSt;

        private Long idEd;

        public Long getIdSt(){return this.idSt;}

        public Long getIdEd(){return this.idEd;}

        private List<Integer> levelList;

        public List<Integer> getLevelList(){return this.levelList;}

        private Integer levelSt;

        private Integer levelEd;

        public Integer getLevelSt(){return this.levelSt;}

        public Integer getLevelEd(){return this.levelEd;}

        private List<String> nameList;

        public List<String> getNameList(){return this.nameList;}


        private List<String> fuzzyName;

        public List<String> getFuzzyName(){return this.fuzzyName;}

        private List<String> rightFuzzyName;

        public List<String> getRightFuzzyName(){return this.rightFuzzyName;}
        private List<java.math.BigDecimal> amountMinList;

        public List<java.math.BigDecimal> getAmountMinList(){return this.amountMinList;}

        private java.math.BigDecimal amountMinSt;

        private java.math.BigDecimal amountMinEd;

        public java.math.BigDecimal getAmountMinSt(){return this.amountMinSt;}

        public java.math.BigDecimal getAmountMinEd(){return this.amountMinEd;}

        private List<java.math.BigDecimal> amountMaxList;

        public List<java.math.BigDecimal> getAmountMaxList(){return this.amountMaxList;}

        private java.math.BigDecimal amountMaxSt;

        private java.math.BigDecimal amountMaxEd;

        public java.math.BigDecimal getAmountMaxSt(){return this.amountMaxSt;}

        public java.math.BigDecimal getAmountMaxEd(){return this.amountMaxEd;}

        private List<Double> staticProfitList;

        public List<Double> getStaticProfitList(){return this.staticProfitList;}

        private Double staticProfitSt;

        private Double staticProfitEd;

        public Double getStaticProfitSt(){return this.staticProfitSt;}

        public Double getStaticProfitEd(){return this.staticProfitEd;}

        private List<String> remarkList;

        public List<String> getRemarkList(){return this.remarkList;}


        private List<String> fuzzyRemark;

        public List<String> getFuzzyRemark(){return this.fuzzyRemark;}

        private List<String> rightFuzzyRemark;

        public List<String> getRightFuzzyRemark(){return this.rightFuzzyRemark;}
        private List<java.time.LocalDateTime> createtimeList;

        public List<java.time.LocalDateTime> getCreatetimeList(){return this.createtimeList;}

        private java.time.LocalDateTime createtimeSt;

        private java.time.LocalDateTime createtimeEd;

        public java.time.LocalDateTime getCreatetimeSt(){return this.createtimeSt;}

        public java.time.LocalDateTime getCreatetimeEd(){return this.createtimeEd;}

        private List<java.time.LocalDateTime> updatetimeList;

        public List<java.time.LocalDateTime> getUpdatetimeList(){return this.updatetimeList;}

        private java.time.LocalDateTime updatetimeSt;

        private java.time.LocalDateTime updatetimeEd;

        public java.time.LocalDateTime getUpdatetimeSt(){return this.updatetimeSt;}

        public java.time.LocalDateTime getUpdatetimeEd(){return this.updatetimeEd;}


        public ConditionBuilder idBetWeen(Long idSt,Long idEd){
            this.idSt = idSt;
            this.idEd = idEd;
            return this;
        }

        public ConditionBuilder idGreaterEqThan(Long idSt){
            this.idSt = idSt;
            return this;
        }
        public ConditionBuilder idLessEqThan(Long idEd){
            this.idEd = idEd;
            return this;
        }


        public ConditionBuilder idList(Long ... id){
            this.idList = solveNullList(id);
            return this;
        }

        public ConditionBuilder idList(List<Long> id){
            this.idList = id;
            return this;
        }

        public ConditionBuilder levelBetWeen(Integer levelSt,Integer levelEd){
            this.levelSt = levelSt;
            this.levelEd = levelEd;
            return this;
        }

        public ConditionBuilder levelGreaterEqThan(Integer levelSt){
            this.levelSt = levelSt;
            return this;
        }
        public ConditionBuilder levelLessEqThan(Integer levelEd){
            this.levelEd = levelEd;
            return this;
        }


        public ConditionBuilder levelList(Integer ... level){
            this.levelList = solveNullList(level);
            return this;
        }

        public ConditionBuilder levelList(List<Integer> level){
            this.levelList = level;
            return this;
        }

        public ConditionBuilder fuzzyName (List<String> fuzzyName){
            this.fuzzyName = fuzzyName;
            return this;
        }

        public ConditionBuilder fuzzyName (String ... fuzzyName){
            this.fuzzyName = solveNullList(fuzzyName);
            return this;
        }

        public ConditionBuilder rightFuzzyName (List<String> rightFuzzyName){
            this.rightFuzzyName = rightFuzzyName;
            return this;
        }

        public ConditionBuilder rightFuzzyName (String ... rightFuzzyName){
            this.rightFuzzyName = solveNullList(rightFuzzyName);
            return this;
        }

        public ConditionBuilder nameList(String ... name){
            this.nameList = solveNullList(name);
            return this;
        }

        public ConditionBuilder nameList(List<String> name){
            this.nameList = name;
            return this;
        }

        public ConditionBuilder amountMinBetWeen(java.math.BigDecimal amountMinSt,java.math.BigDecimal amountMinEd){
            this.amountMinSt = amountMinSt;
            this.amountMinEd = amountMinEd;
            return this;
        }

        public ConditionBuilder amountMinGreaterEqThan(java.math.BigDecimal amountMinSt){
            this.amountMinSt = amountMinSt;
            return this;
        }
        public ConditionBuilder amountMinLessEqThan(java.math.BigDecimal amountMinEd){
            this.amountMinEd = amountMinEd;
            return this;
        }


        public ConditionBuilder amountMinList(java.math.BigDecimal ... amountMin){
            this.amountMinList = solveNullList(amountMin);
            return this;
        }

        public ConditionBuilder amountMinList(List<java.math.BigDecimal> amountMin){
            this.amountMinList = amountMin;
            return this;
        }

        public ConditionBuilder amountMaxBetWeen(java.math.BigDecimal amountMaxSt,java.math.BigDecimal amountMaxEd){
            this.amountMaxSt = amountMaxSt;
            this.amountMaxEd = amountMaxEd;
            return this;
        }

        public ConditionBuilder amountMaxGreaterEqThan(java.math.BigDecimal amountMaxSt){
            this.amountMaxSt = amountMaxSt;
            return this;
        }
        public ConditionBuilder amountMaxLessEqThan(java.math.BigDecimal amountMaxEd){
            this.amountMaxEd = amountMaxEd;
            return this;
        }


        public ConditionBuilder amountMaxList(java.math.BigDecimal ... amountMax){
            this.amountMaxList = solveNullList(amountMax);
            return this;
        }

        public ConditionBuilder amountMaxList(List<java.math.BigDecimal> amountMax){
            this.amountMaxList = amountMax;
            return this;
        }

        public ConditionBuilder staticProfitBetWeen(Double staticProfitSt,Double staticProfitEd){
            this.staticProfitSt = staticProfitSt;
            this.staticProfitEd = staticProfitEd;
            return this;
        }

        public ConditionBuilder staticProfitGreaterEqThan(Double staticProfitSt){
            this.staticProfitSt = staticProfitSt;
            return this;
        }
        public ConditionBuilder staticProfitLessEqThan(Double staticProfitEd){
            this.staticProfitEd = staticProfitEd;
            return this;
        }


        public ConditionBuilder staticProfitList(Double ... staticProfit){
            this.staticProfitList = solveNullList(staticProfit);
            return this;
        }

        public ConditionBuilder staticProfitList(List<Double> staticProfit){
            this.staticProfitList = staticProfit;
            return this;
        }

        public ConditionBuilder fuzzyRemark (List<String> fuzzyRemark){
            this.fuzzyRemark = fuzzyRemark;
            return this;
        }

        public ConditionBuilder fuzzyRemark (String ... fuzzyRemark){
            this.fuzzyRemark = solveNullList(fuzzyRemark);
            return this;
        }

        public ConditionBuilder rightFuzzyRemark (List<String> rightFuzzyRemark){
            this.rightFuzzyRemark = rightFuzzyRemark;
            return this;
        }

        public ConditionBuilder rightFuzzyRemark (String ... rightFuzzyRemark){
            this.rightFuzzyRemark = solveNullList(rightFuzzyRemark);
            return this;
        }

        public ConditionBuilder remarkList(String ... remark){
            this.remarkList = solveNullList(remark);
            return this;
        }

        public ConditionBuilder remarkList(List<String> remark){
            this.remarkList = remark;
            return this;
        }

        public ConditionBuilder createtimeBetWeen(java.time.LocalDateTime createtimeSt,java.time.LocalDateTime createtimeEd){
            this.createtimeSt = createtimeSt;
            this.createtimeEd = createtimeEd;
            return this;
        }

        public ConditionBuilder createtimeGreaterEqThan(java.time.LocalDateTime createtimeSt){
            this.createtimeSt = createtimeSt;
            return this;
        }
        public ConditionBuilder createtimeLessEqThan(java.time.LocalDateTime createtimeEd){
            this.createtimeEd = createtimeEd;
            return this;
        }


        public ConditionBuilder createtimeList(java.time.LocalDateTime ... createtime){
            this.createtimeList = solveNullList(createtime);
            return this;
        }

        public ConditionBuilder createtimeList(List<java.time.LocalDateTime> createtime){
            this.createtimeList = createtime;
            return this;
        }

        public ConditionBuilder updatetimeBetWeen(java.time.LocalDateTime updatetimeSt,java.time.LocalDateTime updatetimeEd){
            this.updatetimeSt = updatetimeSt;
            this.updatetimeEd = updatetimeEd;
            return this;
        }

        public ConditionBuilder updatetimeGreaterEqThan(java.time.LocalDateTime updatetimeSt){
            this.updatetimeSt = updatetimeSt;
            return this;
        }
        public ConditionBuilder updatetimeLessEqThan(java.time.LocalDateTime updatetimeEd){
            this.updatetimeEd = updatetimeEd;
            return this;
        }


        public ConditionBuilder updatetimeList(java.time.LocalDateTime ... updatetime){
            this.updatetimeList = solveNullList(updatetime);
            return this;
        }

        public ConditionBuilder updatetimeList(List<java.time.LocalDateTime> updatetime){
            this.updatetimeList = updatetime;
            return this;
        }

        private <T>List<T> solveNullList(T ... objs){
            if (objs != null){
            List<T> list = new ArrayList<>();
                for (T item : objs){
                    if (item != null){
                        list.add(item);
                    }
                }
                return list;
            }
            return null;
        }

        public ConditionBuilder build(){return this;}
    }

    public static class Builder {

        private NodeLevel obj;

        public Builder(){
            this.obj = new NodeLevel();
        }

        public Builder id(Long id){
            this.obj.setId(id);
            return this;
        }
        public Builder level(Integer level){
            this.obj.setLevel(level);
            return this;
        }
        public Builder name(String name){
            this.obj.setName(name);
            return this;
        }
        public Builder amountMin(java.math.BigDecimal amountMin){
            this.obj.setAmountMin(amountMin);
            return this;
        }
        public Builder amountMax(java.math.BigDecimal amountMax){
            this.obj.setAmountMax(amountMax);
            return this;
        }
        public Builder staticProfit(Double staticProfit){
            this.obj.setStaticProfit(staticProfit);
            return this;
        }
        public Builder remark(String remark){
            this.obj.setRemark(remark);
            return this;
        }
        public Builder createtime(java.time.LocalDateTime createtime){
            this.obj.setCreatetime(createtime);
            return this;
        }
        public Builder updatetime(java.time.LocalDateTime updatetime){
            this.obj.setUpdatetime(updatetime);
            return this;
        }
        public NodeLevel build(){return obj;}
    }

}

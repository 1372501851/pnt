package com.inesv.model;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
/**
*
*  @author author
*/
public class NodeMineProfit implements Serializable {

    private static final long serialVersionUID = 1564564283090L;


    /**
    * 主键
    * 
    * isNullAble:0
    */
    private Long id;

    /**
    * 节点id
    * isNullAble:1
    */
    private Long nodeId;

    /**
    * 金额
    * isNullAble:0
    */
    private java.math.BigDecimal amount;

    /**
    * 收益产出日期
    * isNullAble:0
    */
    @JsonFormat(pattern="yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private java.time.LocalDate createdate;

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

    public void setNodeId(Long nodeId){this.nodeId = nodeId;}

    public Long getNodeId(){return this.nodeId;}

    public void setAmount(java.math.BigDecimal amount){this.amount = amount;}

    public java.math.BigDecimal getAmount(){return this.amount;}

    public void setCreatedate(java.time.LocalDate createdate){this.createdate = createdate;}

    public java.time.LocalDate getCreatedate(){return this.createdate;}

    public void setCreatetime(java.time.LocalDateTime createtime){this.createtime = createtime;}

    public java.time.LocalDateTime getCreatetime(){return this.createtime;}

    public void setUpdatetime(java.time.LocalDateTime updatetime){this.updatetime = updatetime;}

    public java.time.LocalDateTime getUpdatetime(){return this.updatetime;}
    @Override
    public String toString() {
        return "NodeMineProfit{" +
                "id='" + id + '\'' +
                "nodeId='" + nodeId + '\'' +
                "amount='" + amount + '\'' +
                "createdate='" + createdate + '\'' +
                "createtime='" + createtime + '\'' +
                "updatetime='" + updatetime + '\'' +
            '}';
    }

    public static Builder Build(){return new Builder();}

    public static ConditionBuilder ConditionBuild(){return new ConditionBuilder();}

    public static UpdateBuilder UpdateBuild(){return new UpdateBuilder();}

    public static QueryBuilder QueryBuild(){return new QueryBuilder();}

    public static class UpdateBuilder {

        private NodeMineProfit set;

        private ConditionBuilder where;

        public UpdateBuilder set(NodeMineProfit set){
            this.set = set;
            return this;
        }

        public NodeMineProfit getSet(){
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

    public static class QueryBuilder extends NodeMineProfit{
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

        private List<Long> nodeIdList;

        public List<Long> getNodeIdList(){return this.nodeIdList;}

        private Long nodeIdSt;

        private Long nodeIdEd;

        public Long getNodeIdSt(){return this.nodeIdSt;}

        public Long getNodeIdEd(){return this.nodeIdEd;}

        private List<java.math.BigDecimal> amountList;

        public List<java.math.BigDecimal> getAmountList(){return this.amountList;}

        private java.math.BigDecimal amountSt;

        private java.math.BigDecimal amountEd;

        public java.math.BigDecimal getAmountSt(){return this.amountSt;}

        public java.math.BigDecimal getAmountEd(){return this.amountEd;}

        private List<java.time.LocalDate> createdateList;

        public List<java.time.LocalDate> getCreatedateList(){return this.createdateList;}

        private java.time.LocalDate createdateSt;

        private java.time.LocalDate createdateEd;

        public java.time.LocalDate getCreatedateSt(){return this.createdateSt;}

        public java.time.LocalDate getCreatedateEd(){return this.createdateEd;}

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

        public QueryBuilder nodeIdBetWeen(Long nodeIdSt,Long nodeIdEd){
            this.nodeIdSt = nodeIdSt;
            this.nodeIdEd = nodeIdEd;
            return this;
        }

        public QueryBuilder nodeIdGreaterEqThan(Long nodeIdSt){
            this.nodeIdSt = nodeIdSt;
            return this;
        }
        public QueryBuilder nodeIdLessEqThan(Long nodeIdEd){
            this.nodeIdEd = nodeIdEd;
            return this;
        }


        public QueryBuilder nodeId(Long nodeId){
            setNodeId(nodeId);
            return this;
        }

        public QueryBuilder nodeIdList(Long ... nodeId){
            this.nodeIdList = solveNullList(nodeId);
            return this;
        }

        public QueryBuilder nodeIdList(List<Long> nodeId){
            this.nodeIdList = nodeId;
            return this;
        }

        public QueryBuilder fetchNodeId(){
            setFetchFields("fetchFields","nodeId");
            return this;
        }

        public QueryBuilder excludeNodeId(){
            setFetchFields("excludeFields","nodeId");
            return this;
        }

        public QueryBuilder amountBetWeen(java.math.BigDecimal amountSt,java.math.BigDecimal amountEd){
            this.amountSt = amountSt;
            this.amountEd = amountEd;
            return this;
        }

        public QueryBuilder amountGreaterEqThan(java.math.BigDecimal amountSt){
            this.amountSt = amountSt;
            return this;
        }
        public QueryBuilder amountLessEqThan(java.math.BigDecimal amountEd){
            this.amountEd = amountEd;
            return this;
        }


        public QueryBuilder amount(java.math.BigDecimal amount){
            setAmount(amount);
            return this;
        }

        public QueryBuilder amountList(java.math.BigDecimal ... amount){
            this.amountList = solveNullList(amount);
            return this;
        }

        public QueryBuilder amountList(List<java.math.BigDecimal> amount){
            this.amountList = amount;
            return this;
        }

        public QueryBuilder fetchAmount(){
            setFetchFields("fetchFields","amount");
            return this;
        }

        public QueryBuilder excludeAmount(){
            setFetchFields("excludeFields","amount");
            return this;
        }

        public QueryBuilder createdateBetWeen(java.time.LocalDate createdateSt,java.time.LocalDate createdateEd){
            this.createdateSt = createdateSt;
            this.createdateEd = createdateEd;
            return this;
        }

        public QueryBuilder createdateGreaterEqThan(java.time.LocalDate createdateSt){
            this.createdateSt = createdateSt;
            return this;
        }
        public QueryBuilder createdateLessEqThan(java.time.LocalDate createdateEd){
            this.createdateEd = createdateEd;
            return this;
        }


        public QueryBuilder createdate(java.time.LocalDate createdate){
            setCreatedate(createdate);
            return this;
        }

        public QueryBuilder createdateList(java.time.LocalDate ... createdate){
            this.createdateList = solveNullList(createdate);
            return this;
        }

        public QueryBuilder createdateList(List<java.time.LocalDate> createdate){
            this.createdateList = createdate;
            return this;
        }

        public QueryBuilder fetchCreatedate(){
            setFetchFields("fetchFields","createdate");
            return this;
        }

        public QueryBuilder excludeCreatedate(){
            setFetchFields("excludeFields","createdate");
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

        public NodeMineProfit build(){return this;}
    }


    public static class ConditionBuilder{
        private List<Long> idList;

        public List<Long> getIdList(){return this.idList;}

        private Long idSt;

        private Long idEd;

        public Long getIdSt(){return this.idSt;}

        public Long getIdEd(){return this.idEd;}

        private List<Long> nodeIdList;

        public List<Long> getNodeIdList(){return this.nodeIdList;}

        private Long nodeIdSt;

        private Long nodeIdEd;

        public Long getNodeIdSt(){return this.nodeIdSt;}

        public Long getNodeIdEd(){return this.nodeIdEd;}

        private List<java.math.BigDecimal> amountList;

        public List<java.math.BigDecimal> getAmountList(){return this.amountList;}

        private java.math.BigDecimal amountSt;

        private java.math.BigDecimal amountEd;

        public java.math.BigDecimal getAmountSt(){return this.amountSt;}

        public java.math.BigDecimal getAmountEd(){return this.amountEd;}

        private List<java.time.LocalDate> createdateList;

        public List<java.time.LocalDate> getCreatedateList(){return this.createdateList;}

        private java.time.LocalDate createdateSt;

        private java.time.LocalDate createdateEd;

        public java.time.LocalDate getCreatedateSt(){return this.createdateSt;}

        public java.time.LocalDate getCreatedateEd(){return this.createdateEd;}

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

        public ConditionBuilder nodeIdBetWeen(Long nodeIdSt,Long nodeIdEd){
            this.nodeIdSt = nodeIdSt;
            this.nodeIdEd = nodeIdEd;
            return this;
        }

        public ConditionBuilder nodeIdGreaterEqThan(Long nodeIdSt){
            this.nodeIdSt = nodeIdSt;
            return this;
        }
        public ConditionBuilder nodeIdLessEqThan(Long nodeIdEd){
            this.nodeIdEd = nodeIdEd;
            return this;
        }


        public ConditionBuilder nodeIdList(Long ... nodeId){
            this.nodeIdList = solveNullList(nodeId);
            return this;
        }

        public ConditionBuilder nodeIdList(List<Long> nodeId){
            this.nodeIdList = nodeId;
            return this;
        }

        public ConditionBuilder amountBetWeen(java.math.BigDecimal amountSt,java.math.BigDecimal amountEd){
            this.amountSt = amountSt;
            this.amountEd = amountEd;
            return this;
        }

        public ConditionBuilder amountGreaterEqThan(java.math.BigDecimal amountSt){
            this.amountSt = amountSt;
            return this;
        }
        public ConditionBuilder amountLessEqThan(java.math.BigDecimal amountEd){
            this.amountEd = amountEd;
            return this;
        }


        public ConditionBuilder amountList(java.math.BigDecimal ... amount){
            this.amountList = solveNullList(amount);
            return this;
        }

        public ConditionBuilder amountList(List<java.math.BigDecimal> amount){
            this.amountList = amount;
            return this;
        }

        public ConditionBuilder createdateBetWeen(java.time.LocalDate createdateSt,java.time.LocalDate createdateEd){
            this.createdateSt = createdateSt;
            this.createdateEd = createdateEd;
            return this;
        }

        public ConditionBuilder createdateGreaterEqThan(java.time.LocalDate createdateSt){
            this.createdateSt = createdateSt;
            return this;
        }
        public ConditionBuilder createdateLessEqThan(java.time.LocalDate createdateEd){
            this.createdateEd = createdateEd;
            return this;
        }


        public ConditionBuilder createdateList(java.time.LocalDate ... createdate){
            this.createdateList = solveNullList(createdate);
            return this;
        }

        public ConditionBuilder createdateList(List<java.time.LocalDate> createdate){
            this.createdateList = createdate;
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

        private NodeMineProfit obj;

        public Builder(){
            this.obj = new NodeMineProfit();
        }

        public Builder id(Long id){
            this.obj.setId(id);
            return this;
        }
        public Builder nodeId(Long nodeId){
            this.obj.setNodeId(nodeId);
            return this;
        }
        public Builder amount(java.math.BigDecimal amount){
            this.obj.setAmount(amount);
            return this;
        }
        public Builder createdate(java.time.LocalDate createdate){
            this.obj.setCreatedate(createdate);
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
        public NodeMineProfit build(){return obj;}
    }

}

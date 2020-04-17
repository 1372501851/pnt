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
public class NodeUserTotal implements Serializable {

    private static final long serialVersionUID = 1564564387790L;


    /**
    * 主键
    * 
    * isNullAble:0
    */
    private Long id;

    /**
    * 用户id
    * isNullAble:0
    */
    private Long userId;

    /**
    * 节点id
    * isNullAble:0
    */
    private Long nodeId;

    /**
    * 状态（1.正常，2.退出票选）
    * isNullAble:0
    */
    private Integer state;

    /**
    * 用户投入总额
    * isNullAble:0
    */
    private java.math.BigDecimal amount;

    /**
    * 最后一笔投入时间
    * isNullAble:0
    */
    private java.time.LocalDateTime lasttime;

    /**
    * 层级路径
    * isNullAble:0
    */
    private String path;

    /**
    * 层级
    * isNullAble:0
    */
    private Integer treeGrade;

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

    public void setUserId(Long userId){this.userId = userId;}

    public Long getUserId(){return this.userId;}

    public void setNodeId(Long nodeId){this.nodeId = nodeId;}

    public Long getNodeId(){return this.nodeId;}

    public void setState(Integer state){this.state = state;}

    public Integer getState(){return this.state;}

    public void setAmount(java.math.BigDecimal amount){this.amount = amount;}

    public java.math.BigDecimal getAmount(){return this.amount;}

    public void setLasttime(java.time.LocalDateTime lasttime){this.lasttime = lasttime;}

    public java.time.LocalDateTime getLasttime(){return this.lasttime;}

    public void setPath(String path){this.path = path;}

    public String getPath(){return this.path;}

    public void setTreeGrade(Integer treeGrade){this.treeGrade = treeGrade;}

    public Integer getTreeGrade(){return this.treeGrade;}

    public void setCreatetime(java.time.LocalDateTime createtime){this.createtime = createtime;}

    public java.time.LocalDateTime getCreatetime(){return this.createtime;}

    public void setUpdatetime(java.time.LocalDateTime updatetime){this.updatetime = updatetime;}

    public java.time.LocalDateTime getUpdatetime(){return this.updatetime;}
    @Override
    public String toString() {
        return "NodeUserTotal{" +
                "id='" + id + '\'' +
                "userId='" + userId + '\'' +
                "nodeId='" + nodeId + '\'' +
                "state='" + state + '\'' +
                "amount='" + amount + '\'' +
                "lasttime='" + lasttime + '\'' +
                "path='" + path + '\'' +
                "treeGrade='" + treeGrade + '\'' +
                "createtime='" + createtime + '\'' +
                "updatetime='" + updatetime + '\'' +
            '}';
    }

    public static Builder Build(){return new Builder();}

    public static ConditionBuilder ConditionBuild(){return new ConditionBuilder();}

    public static UpdateBuilder UpdateBuild(){return new UpdateBuilder();}

    public static QueryBuilder QueryBuild(){return new QueryBuilder();}

    public static class UpdateBuilder {

        private NodeUserTotal set;

        private ConditionBuilder where;

        public UpdateBuilder set(NodeUserTotal set){
            this.set = set;
            return this;
        }

        public NodeUserTotal getSet(){
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

    public static class QueryBuilder extends NodeUserTotal{
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

        private List<Long> userIdList;

        public List<Long> getUserIdList(){return this.userIdList;}

        private Long userIdSt;

        private Long userIdEd;

        public Long getUserIdSt(){return this.userIdSt;}

        public Long getUserIdEd(){return this.userIdEd;}

        private List<Long> nodeIdList;

        public List<Long> getNodeIdList(){return this.nodeIdList;}

        private Long nodeIdSt;

        private Long nodeIdEd;

        public Long getNodeIdSt(){return this.nodeIdSt;}

        public Long getNodeIdEd(){return this.nodeIdEd;}

        private List<Integer> stateList;

        public List<Integer> getStateList(){return this.stateList;}

        private Integer stateSt;

        private Integer stateEd;

        public Integer getStateSt(){return this.stateSt;}

        public Integer getStateEd(){return this.stateEd;}

        private List<java.math.BigDecimal> amountList;

        public List<java.math.BigDecimal> getAmountList(){return this.amountList;}

        private java.math.BigDecimal amountSt;

        private java.math.BigDecimal amountEd;

        public java.math.BigDecimal getAmountSt(){return this.amountSt;}

        public java.math.BigDecimal getAmountEd(){return this.amountEd;}

        private List<java.time.LocalDateTime> lasttimeList;

        public List<java.time.LocalDateTime> getLasttimeList(){return this.lasttimeList;}

        private java.time.LocalDateTime lasttimeSt;

        private java.time.LocalDateTime lasttimeEd;

        public java.time.LocalDateTime getLasttimeSt(){return this.lasttimeSt;}

        public java.time.LocalDateTime getLasttimeEd(){return this.lasttimeEd;}

        private List<String> pathList;

        public List<String> getPathList(){return this.pathList;}


        private List<String> fuzzyPath;

        public List<String> getFuzzyPath(){return this.fuzzyPath;}

        private List<String> rightFuzzyPath;

        public List<String> getRightFuzzyPath(){return this.rightFuzzyPath;}
        private List<Integer> treeGradeList;

        public List<Integer> getTreeGradeList(){return this.treeGradeList;}

        private Integer treeGradeSt;

        private Integer treeGradeEd;

        public Integer getTreeGradeSt(){return this.treeGradeSt;}

        public Integer getTreeGradeEd(){return this.treeGradeEd;}

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

        public QueryBuilder userIdBetWeen(Long userIdSt,Long userIdEd){
            this.userIdSt = userIdSt;
            this.userIdEd = userIdEd;
            return this;
        }

        public QueryBuilder userIdGreaterEqThan(Long userIdSt){
            this.userIdSt = userIdSt;
            return this;
        }
        public QueryBuilder userIdLessEqThan(Long userIdEd){
            this.userIdEd = userIdEd;
            return this;
        }


        public QueryBuilder userId(Long userId){
            setUserId(userId);
            return this;
        }

        public QueryBuilder userIdList(Long ... userId){
            this.userIdList = solveNullList(userId);
            return this;
        }

        public QueryBuilder userIdList(List<Long> userId){
            this.userIdList = userId;
            return this;
        }

        public QueryBuilder fetchUserId(){
            setFetchFields("fetchFields","userId");
            return this;
        }

        public QueryBuilder excludeUserId(){
            setFetchFields("excludeFields","userId");
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

        public QueryBuilder stateBetWeen(Integer stateSt,Integer stateEd){
            this.stateSt = stateSt;
            this.stateEd = stateEd;
            return this;
        }

        public QueryBuilder stateGreaterEqThan(Integer stateSt){
            this.stateSt = stateSt;
            return this;
        }
        public QueryBuilder stateLessEqThan(Integer stateEd){
            this.stateEd = stateEd;
            return this;
        }


        public QueryBuilder state(Integer state){
            setState(state);
            return this;
        }

        public QueryBuilder stateList(Integer ... state){
            this.stateList = solveNullList(state);
            return this;
        }

        public QueryBuilder stateList(List<Integer> state){
            this.stateList = state;
            return this;
        }

        public QueryBuilder fetchState(){
            setFetchFields("fetchFields","state");
            return this;
        }

        public QueryBuilder excludeState(){
            setFetchFields("excludeFields","state");
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

        public QueryBuilder lasttimeBetWeen(java.time.LocalDateTime lasttimeSt,java.time.LocalDateTime lasttimeEd){
            this.lasttimeSt = lasttimeSt;
            this.lasttimeEd = lasttimeEd;
            return this;
        }

        public QueryBuilder lasttimeGreaterEqThan(java.time.LocalDateTime lasttimeSt){
            this.lasttimeSt = lasttimeSt;
            return this;
        }
        public QueryBuilder lasttimeLessEqThan(java.time.LocalDateTime lasttimeEd){
            this.lasttimeEd = lasttimeEd;
            return this;
        }


        public QueryBuilder lasttime(java.time.LocalDateTime lasttime){
            setLasttime(lasttime);
            return this;
        }

        public QueryBuilder lasttimeList(java.time.LocalDateTime ... lasttime){
            this.lasttimeList = solveNullList(lasttime);
            return this;
        }

        public QueryBuilder lasttimeList(List<java.time.LocalDateTime> lasttime){
            this.lasttimeList = lasttime;
            return this;
        }

        public QueryBuilder fetchLasttime(){
            setFetchFields("fetchFields","lasttime");
            return this;
        }

        public QueryBuilder excludeLasttime(){
            setFetchFields("excludeFields","lasttime");
            return this;
        }

        public QueryBuilder fuzzyPath (List<String> fuzzyPath){
            this.fuzzyPath = fuzzyPath;
            return this;
        }

        public QueryBuilder fuzzyPath (String ... fuzzyPath){
            this.fuzzyPath = solveNullList(fuzzyPath);
            return this;
        }

        public QueryBuilder rightFuzzyPath (List<String> rightFuzzyPath){
            this.rightFuzzyPath = rightFuzzyPath;
            return this;
        }

        public QueryBuilder rightFuzzyPath (String ... rightFuzzyPath){
            this.rightFuzzyPath = solveNullList(rightFuzzyPath);
            return this;
        }

        public QueryBuilder path(String path){
            setPath(path);
            return this;
        }

        public QueryBuilder pathList(String ... path){
            this.pathList = solveNullList(path);
            return this;
        }

        public QueryBuilder pathList(List<String> path){
            this.pathList = path;
            return this;
        }

        public QueryBuilder fetchPath(){
            setFetchFields("fetchFields","path");
            return this;
        }

        public QueryBuilder excludePath(){
            setFetchFields("excludeFields","path");
            return this;
        }

        public QueryBuilder treeGradeBetWeen(Integer treeGradeSt,Integer treeGradeEd){
            this.treeGradeSt = treeGradeSt;
            this.treeGradeEd = treeGradeEd;
            return this;
        }

        public QueryBuilder treeGradeGreaterEqThan(Integer treeGradeSt){
            this.treeGradeSt = treeGradeSt;
            return this;
        }
        public QueryBuilder treeGradeLessEqThan(Integer treeGradeEd){
            this.treeGradeEd = treeGradeEd;
            return this;
        }


        public QueryBuilder treeGrade(Integer treeGrade){
            setTreeGrade(treeGrade);
            return this;
        }

        public QueryBuilder treeGradeList(Integer ... treeGrade){
            this.treeGradeList = solveNullList(treeGrade);
            return this;
        }

        public QueryBuilder treeGradeList(List<Integer> treeGrade){
            this.treeGradeList = treeGrade;
            return this;
        }

        public QueryBuilder fetchTreeGrade(){
            setFetchFields("fetchFields","treeGrade");
            return this;
        }

        public QueryBuilder excludeTreeGrade(){
            setFetchFields("excludeFields","treeGrade");
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

        public NodeUserTotal build(){return this;}
    }


    public static class ConditionBuilder{
        private List<Long> idList;

        public List<Long> getIdList(){return this.idList;}

        private Long idSt;

        private Long idEd;

        public Long getIdSt(){return this.idSt;}

        public Long getIdEd(){return this.idEd;}

        private List<Long> userIdList;

        public List<Long> getUserIdList(){return this.userIdList;}

        private Long userIdSt;

        private Long userIdEd;

        public Long getUserIdSt(){return this.userIdSt;}

        public Long getUserIdEd(){return this.userIdEd;}

        private List<Long> nodeIdList;

        public List<Long> getNodeIdList(){return this.nodeIdList;}

        private Long nodeIdSt;

        private Long nodeIdEd;

        public Long getNodeIdSt(){return this.nodeIdSt;}

        public Long getNodeIdEd(){return this.nodeIdEd;}

        private List<Integer> stateList;

        public List<Integer> getStateList(){return this.stateList;}

        private Integer stateSt;

        private Integer stateEd;

        public Integer getStateSt(){return this.stateSt;}

        public Integer getStateEd(){return this.stateEd;}

        private List<java.math.BigDecimal> amountList;

        public List<java.math.BigDecimal> getAmountList(){return this.amountList;}

        private java.math.BigDecimal amountSt;

        private java.math.BigDecimal amountEd;

        public java.math.BigDecimal getAmountSt(){return this.amountSt;}

        public java.math.BigDecimal getAmountEd(){return this.amountEd;}

        private List<java.time.LocalDateTime> lasttimeList;

        public List<java.time.LocalDateTime> getLasttimeList(){return this.lasttimeList;}

        private java.time.LocalDateTime lasttimeSt;

        private java.time.LocalDateTime lasttimeEd;

        public java.time.LocalDateTime getLasttimeSt(){return this.lasttimeSt;}

        public java.time.LocalDateTime getLasttimeEd(){return this.lasttimeEd;}

        private List<String> pathList;

        public List<String> getPathList(){return this.pathList;}


        private List<String> fuzzyPath;

        public List<String> getFuzzyPath(){return this.fuzzyPath;}

        private List<String> rightFuzzyPath;

        public List<String> getRightFuzzyPath(){return this.rightFuzzyPath;}
        private List<Integer> treeGradeList;

        public List<Integer> getTreeGradeList(){return this.treeGradeList;}

        private Integer treeGradeSt;

        private Integer treeGradeEd;

        public Integer getTreeGradeSt(){return this.treeGradeSt;}

        public Integer getTreeGradeEd(){return this.treeGradeEd;}

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

        public ConditionBuilder userIdBetWeen(Long userIdSt,Long userIdEd){
            this.userIdSt = userIdSt;
            this.userIdEd = userIdEd;
            return this;
        }

        public ConditionBuilder userIdGreaterEqThan(Long userIdSt){
            this.userIdSt = userIdSt;
            return this;
        }
        public ConditionBuilder userIdLessEqThan(Long userIdEd){
            this.userIdEd = userIdEd;
            return this;
        }


        public ConditionBuilder userIdList(Long ... userId){
            this.userIdList = solveNullList(userId);
            return this;
        }

        public ConditionBuilder userIdList(List<Long> userId){
            this.userIdList = userId;
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

        public ConditionBuilder stateBetWeen(Integer stateSt,Integer stateEd){
            this.stateSt = stateSt;
            this.stateEd = stateEd;
            return this;
        }

        public ConditionBuilder stateGreaterEqThan(Integer stateSt){
            this.stateSt = stateSt;
            return this;
        }
        public ConditionBuilder stateLessEqThan(Integer stateEd){
            this.stateEd = stateEd;
            return this;
        }


        public ConditionBuilder stateList(Integer ... state){
            this.stateList = solveNullList(state);
            return this;
        }

        public ConditionBuilder stateList(List<Integer> state){
            this.stateList = state;
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

        public ConditionBuilder lasttimeBetWeen(java.time.LocalDateTime lasttimeSt,java.time.LocalDateTime lasttimeEd){
            this.lasttimeSt = lasttimeSt;
            this.lasttimeEd = lasttimeEd;
            return this;
        }

        public ConditionBuilder lasttimeGreaterEqThan(java.time.LocalDateTime lasttimeSt){
            this.lasttimeSt = lasttimeSt;
            return this;
        }
        public ConditionBuilder lasttimeLessEqThan(java.time.LocalDateTime lasttimeEd){
            this.lasttimeEd = lasttimeEd;
            return this;
        }


        public ConditionBuilder lasttimeList(java.time.LocalDateTime ... lasttime){
            this.lasttimeList = solveNullList(lasttime);
            return this;
        }

        public ConditionBuilder lasttimeList(List<java.time.LocalDateTime> lasttime){
            this.lasttimeList = lasttime;
            return this;
        }

        public ConditionBuilder fuzzyPath (List<String> fuzzyPath){
            this.fuzzyPath = fuzzyPath;
            return this;
        }

        public ConditionBuilder fuzzyPath (String ... fuzzyPath){
            this.fuzzyPath = solveNullList(fuzzyPath);
            return this;
        }

        public ConditionBuilder rightFuzzyPath (List<String> rightFuzzyPath){
            this.rightFuzzyPath = rightFuzzyPath;
            return this;
        }

        public ConditionBuilder rightFuzzyPath (String ... rightFuzzyPath){
            this.rightFuzzyPath = solveNullList(rightFuzzyPath);
            return this;
        }

        public ConditionBuilder pathList(String ... path){
            this.pathList = solveNullList(path);
            return this;
        }

        public ConditionBuilder pathList(List<String> path){
            this.pathList = path;
            return this;
        }

        public ConditionBuilder treeGradeBetWeen(Integer treeGradeSt,Integer treeGradeEd){
            this.treeGradeSt = treeGradeSt;
            this.treeGradeEd = treeGradeEd;
            return this;
        }

        public ConditionBuilder treeGradeGreaterEqThan(Integer treeGradeSt){
            this.treeGradeSt = treeGradeSt;
            return this;
        }
        public ConditionBuilder treeGradeLessEqThan(Integer treeGradeEd){
            this.treeGradeEd = treeGradeEd;
            return this;
        }


        public ConditionBuilder treeGradeList(Integer ... treeGrade){
            this.treeGradeList = solveNullList(treeGrade);
            return this;
        }

        public ConditionBuilder treeGradeList(List<Integer> treeGrade){
            this.treeGradeList = treeGrade;
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

        private NodeUserTotal obj;

        public Builder(){
            this.obj = new NodeUserTotal();
        }

        public Builder id(Long id){
            this.obj.setId(id);
            return this;
        }
        public Builder userId(Long userId){
            this.obj.setUserId(userId);
            return this;
        }
        public Builder nodeId(Long nodeId){
            this.obj.setNodeId(nodeId);
            return this;
        }
        public Builder state(Integer state){
            this.obj.setState(state);
            return this;
        }
        public Builder amount(java.math.BigDecimal amount){
            this.obj.setAmount(amount);
            return this;
        }
        public Builder lasttime(java.time.LocalDateTime lasttime){
            this.obj.setLasttime(lasttime);
            return this;
        }
        public Builder path(String path){
            this.obj.setPath(path);
            return this;
        }
        public Builder treeGrade(Integer treeGrade){
            this.obj.setTreeGrade(treeGrade);
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
        public NodeUserTotal build(){return obj;}
    }

}

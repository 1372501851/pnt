package com.inesv.model;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
/**
*
*  @author author
*/
public class Node implements Serializable {

    private static final long serialVersionUID = 1564729746423L;


    /**
    * 主键
    * 
    * isNullAble:0
    */
    private Long id;

    /**
    * 节点名称
    * isNullAble:0
    */
    private String name;

    /**
    * 节点状态（0.初始状态，1.开启，2.关闭）
    * isNullAble:0
    */
    @JsonIgnore
    private Integer state;

    /**
    * 创建人id
    * isNullAble:0
    */
    @JsonIgnore
    private Long userId;

    /**
    * 创建人邀请码（取t_user的invitation_code）
    * isNullAble:0
    */
    private String invitationCode;

    /**
    * 节点地址
    * isNullAble:0
    */
    private String address;

    /**
    * 地址公钥
    * isNullAble:0,defaultVal:
    */
    @JsonIgnore
    private String pubkey;

    /**
    * 总设定金额
    * isNullAble:0
    */
    private java.math.BigDecimal totalAmount;

    /**
    * 投入量
    * isNullAble:0
    */
    private java.math.BigDecimal intoAmount;

    /**
    * 挖矿地址余额
    * isNullAble:0
    */
    private java.math.BigDecimal addressAmount;

    /**
    * 备注
    * isNullAble:0,defaultVal:
    */
    @JsonIgnore
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

    public void setName(String name){this.name = name;}

    public String getName(){return this.name;}

    public void setState(Integer state){this.state = state;}

    public Integer getState(){return this.state;}

    public void setUserId(Long userId){this.userId = userId;}

    public Long getUserId(){return this.userId;}

    public void setInvitationCode(String invitationCode){this.invitationCode = invitationCode;}

    public String getInvitationCode(){return this.invitationCode;}

    public void setAddress(String address){this.address = address;}

    public String getAddress(){return this.address;}

    public void setPubkey(String pubkey){this.pubkey = pubkey;}

    public String getPubkey(){return this.pubkey;}

    public void setTotalAmount(java.math.BigDecimal totalAmount){this.totalAmount = totalAmount;}

    public java.math.BigDecimal getTotalAmount(){return this.totalAmount;}

    public void setIntoAmount(java.math.BigDecimal intoAmount){this.intoAmount = intoAmount;}

    public java.math.BigDecimal getIntoAmount(){return this.intoAmount;}

    public void setAddressAmount(java.math.BigDecimal addressAmount){this.addressAmount = addressAmount;}

    public java.math.BigDecimal getAddressAmount(){return this.addressAmount;}

    public void setRemark(String remark){this.remark = remark;}

    public String getRemark(){return this.remark;}

    public void setCreatetime(java.time.LocalDateTime createtime){this.createtime = createtime;}

    public java.time.LocalDateTime getCreatetime(){return this.createtime;}

    public void setUpdatetime(java.time.LocalDateTime updatetime){this.updatetime = updatetime;}

    public java.time.LocalDateTime getUpdatetime(){return this.updatetime;}
    @Override
    public String toString() {
        return "Node{" +
                "id='" + id + '\'' +
                "name='" + name + '\'' +
                "state='" + state + '\'' +
                "userId='" + userId + '\'' +
                "invitationCode='" + invitationCode + '\'' +
                "address='" + address + '\'' +
                "pubkey='" + pubkey + '\'' +
                "totalAmount='" + totalAmount + '\'' +
                "intoAmount='" + intoAmount + '\'' +
                "addressAmount='" + addressAmount + '\'' +
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

        private Node set;

        private ConditionBuilder where;

        public UpdateBuilder set(Node set){
            this.set = set;
            return this;
        }

        public Node getSet(){
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

    public static class QueryBuilder extends Node{
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

        private List<String> nameList;

        public List<String> getNameList(){return this.nameList;}


        private List<String> fuzzyName;

        public List<String> getFuzzyName(){return this.fuzzyName;}

        private List<String> rightFuzzyName;

        public List<String> getRightFuzzyName(){return this.rightFuzzyName;}
        private List<Integer> stateList;

        public List<Integer> getStateList(){return this.stateList;}

        private Integer stateSt;

        private Integer stateEd;

        public Integer getStateSt(){return this.stateSt;}

        public Integer getStateEd(){return this.stateEd;}

        private List<Long> userIdList;

        public List<Long> getUserIdList(){return this.userIdList;}

        private Long userIdSt;

        private Long userIdEd;

        public Long getUserIdSt(){return this.userIdSt;}

        public Long getUserIdEd(){return this.userIdEd;}

        private List<String> invitationCodeList;

        public List<String> getInvitationCodeList(){return this.invitationCodeList;}


        private List<String> fuzzyInvitationCode;

        public List<String> getFuzzyInvitationCode(){return this.fuzzyInvitationCode;}

        private List<String> rightFuzzyInvitationCode;

        public List<String> getRightFuzzyInvitationCode(){return this.rightFuzzyInvitationCode;}
        private List<String> addressList;

        public List<String> getAddressList(){return this.addressList;}


        private List<String> fuzzyAddress;

        public List<String> getFuzzyAddress(){return this.fuzzyAddress;}

        private List<String> rightFuzzyAddress;

        public List<String> getRightFuzzyAddress(){return this.rightFuzzyAddress;}
        private List<String> pubkeyList;

        public List<String> getPubkeyList(){return this.pubkeyList;}


        private List<String> fuzzyPubkey;

        public List<String> getFuzzyPubkey(){return this.fuzzyPubkey;}

        private List<String> rightFuzzyPubkey;

        public List<String> getRightFuzzyPubkey(){return this.rightFuzzyPubkey;}
        private List<java.math.BigDecimal> totalAmountList;

        public List<java.math.BigDecimal> getTotalAmountList(){return this.totalAmountList;}

        private java.math.BigDecimal totalAmountSt;

        private java.math.BigDecimal totalAmountEd;

        public java.math.BigDecimal getTotalAmountSt(){return this.totalAmountSt;}

        public java.math.BigDecimal getTotalAmountEd(){return this.totalAmountEd;}

        private List<java.math.BigDecimal> intoAmountList;

        public List<java.math.BigDecimal> getIntoAmountList(){return this.intoAmountList;}

        private java.math.BigDecimal intoAmountSt;

        private java.math.BigDecimal intoAmountEd;

        public java.math.BigDecimal getIntoAmountSt(){return this.intoAmountSt;}

        public java.math.BigDecimal getIntoAmountEd(){return this.intoAmountEd;}

        private List<java.math.BigDecimal> addressAmountList;

        public List<java.math.BigDecimal> getAddressAmountList(){return this.addressAmountList;}

        private java.math.BigDecimal addressAmountSt;

        private java.math.BigDecimal addressAmountEd;

        public java.math.BigDecimal getAddressAmountSt(){return this.addressAmountSt;}

        public java.math.BigDecimal getAddressAmountEd(){return this.addressAmountEd;}

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

        public QueryBuilder fuzzyInvitationCode (List<String> fuzzyInvitationCode){
            this.fuzzyInvitationCode = fuzzyInvitationCode;
            return this;
        }

        public QueryBuilder fuzzyInvitationCode (String ... fuzzyInvitationCode){
            this.fuzzyInvitationCode = solveNullList(fuzzyInvitationCode);
            return this;
        }

        public QueryBuilder rightFuzzyInvitationCode (List<String> rightFuzzyInvitationCode){
            this.rightFuzzyInvitationCode = rightFuzzyInvitationCode;
            return this;
        }

        public QueryBuilder rightFuzzyInvitationCode (String ... rightFuzzyInvitationCode){
            this.rightFuzzyInvitationCode = solveNullList(rightFuzzyInvitationCode);
            return this;
        }

        public QueryBuilder invitationCode(String invitationCode){
            setInvitationCode(invitationCode);
            return this;
        }

        public QueryBuilder invitationCodeList(String ... invitationCode){
            this.invitationCodeList = solveNullList(invitationCode);
            return this;
        }

        public QueryBuilder invitationCodeList(List<String> invitationCode){
            this.invitationCodeList = invitationCode;
            return this;
        }

        public QueryBuilder fetchInvitationCode(){
            setFetchFields("fetchFields","invitationCode");
            return this;
        }

        public QueryBuilder excludeInvitationCode(){
            setFetchFields("excludeFields","invitationCode");
            return this;
        }

        public QueryBuilder fuzzyAddress (List<String> fuzzyAddress){
            this.fuzzyAddress = fuzzyAddress;
            return this;
        }

        public QueryBuilder fuzzyAddress (String ... fuzzyAddress){
            this.fuzzyAddress = solveNullList(fuzzyAddress);
            return this;
        }

        public QueryBuilder rightFuzzyAddress (List<String> rightFuzzyAddress){
            this.rightFuzzyAddress = rightFuzzyAddress;
            return this;
        }

        public QueryBuilder rightFuzzyAddress (String ... rightFuzzyAddress){
            this.rightFuzzyAddress = solveNullList(rightFuzzyAddress);
            return this;
        }

        public QueryBuilder address(String address){
            setAddress(address);
            return this;
        }

        public QueryBuilder addressList(String ... address){
            this.addressList = solveNullList(address);
            return this;
        }

        public QueryBuilder addressList(List<String> address){
            this.addressList = address;
            return this;
        }

        public QueryBuilder fetchAddress(){
            setFetchFields("fetchFields","address");
            return this;
        }

        public QueryBuilder excludeAddress(){
            setFetchFields("excludeFields","address");
            return this;
        }

        public QueryBuilder fuzzyPubkey (List<String> fuzzyPubkey){
            this.fuzzyPubkey = fuzzyPubkey;
            return this;
        }

        public QueryBuilder fuzzyPubkey (String ... fuzzyPubkey){
            this.fuzzyPubkey = solveNullList(fuzzyPubkey);
            return this;
        }

        public QueryBuilder rightFuzzyPubkey (List<String> rightFuzzyPubkey){
            this.rightFuzzyPubkey = rightFuzzyPubkey;
            return this;
        }

        public QueryBuilder rightFuzzyPubkey (String ... rightFuzzyPubkey){
            this.rightFuzzyPubkey = solveNullList(rightFuzzyPubkey);
            return this;
        }

        public QueryBuilder pubkey(String pubkey){
            setPubkey(pubkey);
            return this;
        }

        public QueryBuilder pubkeyList(String ... pubkey){
            this.pubkeyList = solveNullList(pubkey);
            return this;
        }

        public QueryBuilder pubkeyList(List<String> pubkey){
            this.pubkeyList = pubkey;
            return this;
        }

        public QueryBuilder fetchPubkey(){
            setFetchFields("fetchFields","pubkey");
            return this;
        }

        public QueryBuilder excludePubkey(){
            setFetchFields("excludeFields","pubkey");
            return this;
        }

        public QueryBuilder totalAmountBetWeen(java.math.BigDecimal totalAmountSt,java.math.BigDecimal totalAmountEd){
            this.totalAmountSt = totalAmountSt;
            this.totalAmountEd = totalAmountEd;
            return this;
        }

        public QueryBuilder totalAmountGreaterEqThan(java.math.BigDecimal totalAmountSt){
            this.totalAmountSt = totalAmountSt;
            return this;
        }
        public QueryBuilder totalAmountLessEqThan(java.math.BigDecimal totalAmountEd){
            this.totalAmountEd = totalAmountEd;
            return this;
        }


        public QueryBuilder totalAmount(java.math.BigDecimal totalAmount){
            setTotalAmount(totalAmount);
            return this;
        }

        public QueryBuilder totalAmountList(java.math.BigDecimal ... totalAmount){
            this.totalAmountList = solveNullList(totalAmount);
            return this;
        }

        public QueryBuilder totalAmountList(List<java.math.BigDecimal> totalAmount){
            this.totalAmountList = totalAmount;
            return this;
        }

        public QueryBuilder fetchTotalAmount(){
            setFetchFields("fetchFields","totalAmount");
            return this;
        }

        public QueryBuilder excludeTotalAmount(){
            setFetchFields("excludeFields","totalAmount");
            return this;
        }

        public QueryBuilder intoAmountBetWeen(java.math.BigDecimal intoAmountSt,java.math.BigDecimal intoAmountEd){
            this.intoAmountSt = intoAmountSt;
            this.intoAmountEd = intoAmountEd;
            return this;
        }

        public QueryBuilder intoAmountGreaterEqThan(java.math.BigDecimal intoAmountSt){
            this.intoAmountSt = intoAmountSt;
            return this;
        }
        public QueryBuilder intoAmountLessEqThan(java.math.BigDecimal intoAmountEd){
            this.intoAmountEd = intoAmountEd;
            return this;
        }


        public QueryBuilder intoAmount(java.math.BigDecimal intoAmount){
            setIntoAmount(intoAmount);
            return this;
        }

        public QueryBuilder intoAmountList(java.math.BigDecimal ... intoAmount){
            this.intoAmountList = solveNullList(intoAmount);
            return this;
        }

        public QueryBuilder intoAmountList(List<java.math.BigDecimal> intoAmount){
            this.intoAmountList = intoAmount;
            return this;
        }

        public QueryBuilder fetchIntoAmount(){
            setFetchFields("fetchFields","intoAmount");
            return this;
        }

        public QueryBuilder excludeIntoAmount(){
            setFetchFields("excludeFields","intoAmount");
            return this;
        }

        public QueryBuilder addressAmountBetWeen(java.math.BigDecimal addressAmountSt,java.math.BigDecimal addressAmountEd){
            this.addressAmountSt = addressAmountSt;
            this.addressAmountEd = addressAmountEd;
            return this;
        }

        public QueryBuilder addressAmountGreaterEqThan(java.math.BigDecimal addressAmountSt){
            this.addressAmountSt = addressAmountSt;
            return this;
        }
        public QueryBuilder addressAmountLessEqThan(java.math.BigDecimal addressAmountEd){
            this.addressAmountEd = addressAmountEd;
            return this;
        }


        public QueryBuilder addressAmount(java.math.BigDecimal addressAmount){
            setAddressAmount(addressAmount);
            return this;
        }

        public QueryBuilder addressAmountList(java.math.BigDecimal ... addressAmount){
            this.addressAmountList = solveNullList(addressAmount);
            return this;
        }

        public QueryBuilder addressAmountList(List<java.math.BigDecimal> addressAmount){
            this.addressAmountList = addressAmount;
            return this;
        }

        public QueryBuilder fetchAddressAmount(){
            setFetchFields("fetchFields","addressAmount");
            return this;
        }

        public QueryBuilder excludeAddressAmount(){
            setFetchFields("excludeFields","addressAmount");
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

        public Node build(){return this;}
    }


    public static class ConditionBuilder{
        private List<Long> idList;

        public List<Long> getIdList(){return this.idList;}

        private Long idSt;

        private Long idEd;

        public Long getIdSt(){return this.idSt;}

        public Long getIdEd(){return this.idEd;}

        private List<String> nameList;

        public List<String> getNameList(){return this.nameList;}


        private List<String> fuzzyName;

        public List<String> getFuzzyName(){return this.fuzzyName;}

        private List<String> rightFuzzyName;

        public List<String> getRightFuzzyName(){return this.rightFuzzyName;}
        private List<Integer> stateList;

        public List<Integer> getStateList(){return this.stateList;}

        private Integer stateSt;

        private Integer stateEd;

        public Integer getStateSt(){return this.stateSt;}

        public Integer getStateEd(){return this.stateEd;}

        private List<Long> userIdList;

        public List<Long> getUserIdList(){return this.userIdList;}

        private Long userIdSt;

        private Long userIdEd;

        public Long getUserIdSt(){return this.userIdSt;}

        public Long getUserIdEd(){return this.userIdEd;}

        private List<String> invitationCodeList;

        public List<String> getInvitationCodeList(){return this.invitationCodeList;}


        private List<String> fuzzyInvitationCode;

        public List<String> getFuzzyInvitationCode(){return this.fuzzyInvitationCode;}

        private List<String> rightFuzzyInvitationCode;

        public List<String> getRightFuzzyInvitationCode(){return this.rightFuzzyInvitationCode;}
        private List<String> addressList;

        public List<String> getAddressList(){return this.addressList;}


        private List<String> fuzzyAddress;

        public List<String> getFuzzyAddress(){return this.fuzzyAddress;}

        private List<String> rightFuzzyAddress;

        public List<String> getRightFuzzyAddress(){return this.rightFuzzyAddress;}
        private List<String> pubkeyList;

        public List<String> getPubkeyList(){return this.pubkeyList;}


        private List<String> fuzzyPubkey;

        public List<String> getFuzzyPubkey(){return this.fuzzyPubkey;}

        private List<String> rightFuzzyPubkey;

        public List<String> getRightFuzzyPubkey(){return this.rightFuzzyPubkey;}
        private List<java.math.BigDecimal> totalAmountList;

        public List<java.math.BigDecimal> getTotalAmountList(){return this.totalAmountList;}

        private java.math.BigDecimal totalAmountSt;

        private java.math.BigDecimal totalAmountEd;

        public java.math.BigDecimal getTotalAmountSt(){return this.totalAmountSt;}

        public java.math.BigDecimal getTotalAmountEd(){return this.totalAmountEd;}

        private List<java.math.BigDecimal> intoAmountList;

        public List<java.math.BigDecimal> getIntoAmountList(){return this.intoAmountList;}

        private java.math.BigDecimal intoAmountSt;

        private java.math.BigDecimal intoAmountEd;

        public java.math.BigDecimal getIntoAmountSt(){return this.intoAmountSt;}

        public java.math.BigDecimal getIntoAmountEd(){return this.intoAmountEd;}

        private List<java.math.BigDecimal> addressAmountList;

        public List<java.math.BigDecimal> getAddressAmountList(){return this.addressAmountList;}

        private java.math.BigDecimal addressAmountSt;

        private java.math.BigDecimal addressAmountEd;

        public java.math.BigDecimal getAddressAmountSt(){return this.addressAmountSt;}

        public java.math.BigDecimal getAddressAmountEd(){return this.addressAmountEd;}

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

        public ConditionBuilder fuzzyInvitationCode (List<String> fuzzyInvitationCode){
            this.fuzzyInvitationCode = fuzzyInvitationCode;
            return this;
        }

        public ConditionBuilder fuzzyInvitationCode (String ... fuzzyInvitationCode){
            this.fuzzyInvitationCode = solveNullList(fuzzyInvitationCode);
            return this;
        }

        public ConditionBuilder rightFuzzyInvitationCode (List<String> rightFuzzyInvitationCode){
            this.rightFuzzyInvitationCode = rightFuzzyInvitationCode;
            return this;
        }

        public ConditionBuilder rightFuzzyInvitationCode (String ... rightFuzzyInvitationCode){
            this.rightFuzzyInvitationCode = solveNullList(rightFuzzyInvitationCode);
            return this;
        }

        public ConditionBuilder invitationCodeList(String ... invitationCode){
            this.invitationCodeList = solveNullList(invitationCode);
            return this;
        }

        public ConditionBuilder invitationCodeList(List<String> invitationCode){
            this.invitationCodeList = invitationCode;
            return this;
        }

        public ConditionBuilder fuzzyAddress (List<String> fuzzyAddress){
            this.fuzzyAddress = fuzzyAddress;
            return this;
        }

        public ConditionBuilder fuzzyAddress (String ... fuzzyAddress){
            this.fuzzyAddress = solveNullList(fuzzyAddress);
            return this;
        }

        public ConditionBuilder rightFuzzyAddress (List<String> rightFuzzyAddress){
            this.rightFuzzyAddress = rightFuzzyAddress;
            return this;
        }

        public ConditionBuilder rightFuzzyAddress (String ... rightFuzzyAddress){
            this.rightFuzzyAddress = solveNullList(rightFuzzyAddress);
            return this;
        }

        public ConditionBuilder addressList(String ... address){
            this.addressList = solveNullList(address);
            return this;
        }

        public ConditionBuilder addressList(List<String> address){
            this.addressList = address;
            return this;
        }

        public ConditionBuilder fuzzyPubkey (List<String> fuzzyPubkey){
            this.fuzzyPubkey = fuzzyPubkey;
            return this;
        }

        public ConditionBuilder fuzzyPubkey (String ... fuzzyPubkey){
            this.fuzzyPubkey = solveNullList(fuzzyPubkey);
            return this;
        }

        public ConditionBuilder rightFuzzyPubkey (List<String> rightFuzzyPubkey){
            this.rightFuzzyPubkey = rightFuzzyPubkey;
            return this;
        }

        public ConditionBuilder rightFuzzyPubkey (String ... rightFuzzyPubkey){
            this.rightFuzzyPubkey = solveNullList(rightFuzzyPubkey);
            return this;
        }

        public ConditionBuilder pubkeyList(String ... pubkey){
            this.pubkeyList = solveNullList(pubkey);
            return this;
        }

        public ConditionBuilder pubkeyList(List<String> pubkey){
            this.pubkeyList = pubkey;
            return this;
        }

        public ConditionBuilder totalAmountBetWeen(java.math.BigDecimal totalAmountSt,java.math.BigDecimal totalAmountEd){
            this.totalAmountSt = totalAmountSt;
            this.totalAmountEd = totalAmountEd;
            return this;
        }

        public ConditionBuilder totalAmountGreaterEqThan(java.math.BigDecimal totalAmountSt){
            this.totalAmountSt = totalAmountSt;
            return this;
        }
        public ConditionBuilder totalAmountLessEqThan(java.math.BigDecimal totalAmountEd){
            this.totalAmountEd = totalAmountEd;
            return this;
        }


        public ConditionBuilder totalAmountList(java.math.BigDecimal ... totalAmount){
            this.totalAmountList = solveNullList(totalAmount);
            return this;
        }

        public ConditionBuilder totalAmountList(List<java.math.BigDecimal> totalAmount){
            this.totalAmountList = totalAmount;
            return this;
        }

        public ConditionBuilder intoAmountBetWeen(java.math.BigDecimal intoAmountSt,java.math.BigDecimal intoAmountEd){
            this.intoAmountSt = intoAmountSt;
            this.intoAmountEd = intoAmountEd;
            return this;
        }

        public ConditionBuilder intoAmountGreaterEqThan(java.math.BigDecimal intoAmountSt){
            this.intoAmountSt = intoAmountSt;
            return this;
        }
        public ConditionBuilder intoAmountLessEqThan(java.math.BigDecimal intoAmountEd){
            this.intoAmountEd = intoAmountEd;
            return this;
        }


        public ConditionBuilder intoAmountList(java.math.BigDecimal ... intoAmount){
            this.intoAmountList = solveNullList(intoAmount);
            return this;
        }

        public ConditionBuilder intoAmountList(List<java.math.BigDecimal> intoAmount){
            this.intoAmountList = intoAmount;
            return this;
        }

        public ConditionBuilder addressAmountBetWeen(java.math.BigDecimal addressAmountSt,java.math.BigDecimal addressAmountEd){
            this.addressAmountSt = addressAmountSt;
            this.addressAmountEd = addressAmountEd;
            return this;
        }

        public ConditionBuilder addressAmountGreaterEqThan(java.math.BigDecimal addressAmountSt){
            this.addressAmountSt = addressAmountSt;
            return this;
        }
        public ConditionBuilder addressAmountLessEqThan(java.math.BigDecimal addressAmountEd){
            this.addressAmountEd = addressAmountEd;
            return this;
        }


        public ConditionBuilder addressAmountList(java.math.BigDecimal ... addressAmount){
            this.addressAmountList = solveNullList(addressAmount);
            return this;
        }

        public ConditionBuilder addressAmountList(List<java.math.BigDecimal> addressAmount){
            this.addressAmountList = addressAmount;
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

        private Node obj;

        public Builder(){
            this.obj = new Node();
        }

        public Builder id(Long id){
            this.obj.setId(id);
            return this;
        }
        public Builder name(String name){
            this.obj.setName(name);
            return this;
        }
        public Builder state(Integer state){
            this.obj.setState(state);
            return this;
        }
        public Builder userId(Long userId){
            this.obj.setUserId(userId);
            return this;
        }
        public Builder invitationCode(String invitationCode){
            this.obj.setInvitationCode(invitationCode);
            return this;
        }
        public Builder address(String address){
            this.obj.setAddress(address);
            return this;
        }
        public Builder pubkey(String pubkey){
            this.obj.setPubkey(pubkey);
            return this;
        }
        public Builder totalAmount(java.math.BigDecimal totalAmount){
            this.obj.setTotalAmount(totalAmount);
            return this;
        }
        public Builder intoAmount(java.math.BigDecimal intoAmount){
            this.obj.setIntoAmount(intoAmount);
            return this;
        }
        public Builder addressAmount(java.math.BigDecimal addressAmount){
            this.obj.setAddressAmount(addressAmount);
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
        public Node build(){return obj;}
    }

}

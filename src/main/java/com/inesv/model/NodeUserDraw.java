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
public class NodeUserDraw implements Serializable {

    private static final long serialVersionUID = 1564564334845L;


    /**
    * 主键
    * 
    * isNullAble:0
    */
    private Long id;

    /**
    * 用户汇总id
    * isNullAble:0
    */
    private Long totalId;

    /**
    * 提取id
    * isNullAble:0
    */
    private java.math.BigDecimal amount;

    /**
    * 总投入金额
    * isNullAble:0
    */
    private java.math.BigDecimal totalAmount;

    /**
    * 手续费金额
    * isNullAble:0
    */
    private java.math.BigDecimal fee;

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

    /**
    * 转账返回hash
    * isNullAble:0
    */
    private String hash;


    public void setId(Long id){this.id = id;}

    public Long getId(){return this.id;}

    public void setTotalId(Long totalId){this.totalId = totalId;}

    public Long getTotalId(){return this.totalId;}

    public void setAmount(java.math.BigDecimal amount){this.amount = amount;}

    public java.math.BigDecimal getAmount(){return this.amount;}

    public void setTotalAmount(java.math.BigDecimal totalAmount){this.totalAmount = totalAmount;}

    public java.math.BigDecimal getTotalAmount(){return this.totalAmount;}

    public void setFee(java.math.BigDecimal fee){this.fee = fee;}

    public java.math.BigDecimal getFee(){return this.fee;}

    public void setCreatetime(java.time.LocalDateTime createtime){this.createtime = createtime;}

    public java.time.LocalDateTime getCreatetime(){return this.createtime;}

    public void setUpdatetime(java.time.LocalDateTime updatetime){this.updatetime = updatetime;}

    public java.time.LocalDateTime getUpdatetime(){return this.updatetime;}

    public void setHash(String hash){this.hash = hash;}

    public String getHash(){return this.hash;}
    @Override
    public String toString() {
        return "NodeUserDraw{" +
                "id='" + id + '\'' +
                "totalId='" + totalId + '\'' +
                "amount='" + amount + '\'' +
                "totalAmount='" + totalAmount + '\'' +
                "fee='" + fee + '\'' +
                "createtime='" + createtime + '\'' +
                "updatetime='" + updatetime + '\'' +
                "hash='" + hash + '\'' +
            '}';
    }

    public static Builder Build(){return new Builder();}

    public static ConditionBuilder ConditionBuild(){return new ConditionBuilder();}

    public static UpdateBuilder UpdateBuild(){return new UpdateBuilder();}

    public static QueryBuilder QueryBuild(){return new QueryBuilder();}

    public static class UpdateBuilder {

        private NodeUserDraw set;

        private ConditionBuilder where;

        public UpdateBuilder set(NodeUserDraw set){
            this.set = set;
            return this;
        }

        public NodeUserDraw getSet(){
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

    public static class QueryBuilder extends NodeUserDraw{
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

        private List<Long> totalIdList;

        public List<Long> getTotalIdList(){return this.totalIdList;}

        private Long totalIdSt;

        private Long totalIdEd;

        public Long getTotalIdSt(){return this.totalIdSt;}

        public Long getTotalIdEd(){return this.totalIdEd;}

        private List<java.math.BigDecimal> amountList;

        public List<java.math.BigDecimal> getAmountList(){return this.amountList;}

        private java.math.BigDecimal amountSt;

        private java.math.BigDecimal amountEd;

        public java.math.BigDecimal getAmountSt(){return this.amountSt;}

        public java.math.BigDecimal getAmountEd(){return this.amountEd;}

        private List<java.math.BigDecimal> totalAmountList;

        public List<java.math.BigDecimal> getTotalAmountList(){return this.totalAmountList;}

        private java.math.BigDecimal totalAmountSt;

        private java.math.BigDecimal totalAmountEd;

        public java.math.BigDecimal getTotalAmountSt(){return this.totalAmountSt;}

        public java.math.BigDecimal getTotalAmountEd(){return this.totalAmountEd;}

        private List<java.math.BigDecimal> feeList;

        public List<java.math.BigDecimal> getFeeList(){return this.feeList;}

        private java.math.BigDecimal feeSt;

        private java.math.BigDecimal feeEd;

        public java.math.BigDecimal getFeeSt(){return this.feeSt;}

        public java.math.BigDecimal getFeeEd(){return this.feeEd;}

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

        private List<String> hashList;

        public List<String> getHashList(){return this.hashList;}


        private List<String> fuzzyHash;

        public List<String> getFuzzyHash(){return this.fuzzyHash;}

        private List<String> rightFuzzyHash;

        public List<String> getRightFuzzyHash(){return this.rightFuzzyHash;}
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

        public QueryBuilder totalIdBetWeen(Long totalIdSt,Long totalIdEd){
            this.totalIdSt = totalIdSt;
            this.totalIdEd = totalIdEd;
            return this;
        }

        public QueryBuilder totalIdGreaterEqThan(Long totalIdSt){
            this.totalIdSt = totalIdSt;
            return this;
        }
        public QueryBuilder totalIdLessEqThan(Long totalIdEd){
            this.totalIdEd = totalIdEd;
            return this;
        }


        public QueryBuilder totalId(Long totalId){
            setTotalId(totalId);
            return this;
        }

        public QueryBuilder totalIdList(Long ... totalId){
            this.totalIdList = solveNullList(totalId);
            return this;
        }

        public QueryBuilder totalIdList(List<Long> totalId){
            this.totalIdList = totalId;
            return this;
        }

        public QueryBuilder fetchTotalId(){
            setFetchFields("fetchFields","totalId");
            return this;
        }

        public QueryBuilder excludeTotalId(){
            setFetchFields("excludeFields","totalId");
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

        public QueryBuilder feeBetWeen(java.math.BigDecimal feeSt,java.math.BigDecimal feeEd){
            this.feeSt = feeSt;
            this.feeEd = feeEd;
            return this;
        }

        public QueryBuilder feeGreaterEqThan(java.math.BigDecimal feeSt){
            this.feeSt = feeSt;
            return this;
        }
        public QueryBuilder feeLessEqThan(java.math.BigDecimal feeEd){
            this.feeEd = feeEd;
            return this;
        }


        public QueryBuilder fee(java.math.BigDecimal fee){
            setFee(fee);
            return this;
        }

        public QueryBuilder feeList(java.math.BigDecimal ... fee){
            this.feeList = solveNullList(fee);
            return this;
        }

        public QueryBuilder feeList(List<java.math.BigDecimal> fee){
            this.feeList = fee;
            return this;
        }

        public QueryBuilder fetchFee(){
            setFetchFields("fetchFields","fee");
            return this;
        }

        public QueryBuilder excludeFee(){
            setFetchFields("excludeFields","fee");
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

        public QueryBuilder fuzzyHash (List<String> fuzzyHash){
            this.fuzzyHash = fuzzyHash;
            return this;
        }

        public QueryBuilder fuzzyHash (String ... fuzzyHash){
            this.fuzzyHash = solveNullList(fuzzyHash);
            return this;
        }

        public QueryBuilder rightFuzzyHash (List<String> rightFuzzyHash){
            this.rightFuzzyHash = rightFuzzyHash;
            return this;
        }

        public QueryBuilder rightFuzzyHash (String ... rightFuzzyHash){
            this.rightFuzzyHash = solveNullList(rightFuzzyHash);
            return this;
        }

        public QueryBuilder hash(String hash){
            setHash(hash);
            return this;
        }

        public QueryBuilder hashList(String ... hash){
            this.hashList = solveNullList(hash);
            return this;
        }

        public QueryBuilder hashList(List<String> hash){
            this.hashList = hash;
            return this;
        }

        public QueryBuilder fetchHash(){
            setFetchFields("fetchFields","hash");
            return this;
        }

        public QueryBuilder excludeHash(){
            setFetchFields("excludeFields","hash");
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

        public NodeUserDraw build(){return this;}
    }


    public static class ConditionBuilder{
        private List<Long> idList;

        public List<Long> getIdList(){return this.idList;}

        private Long idSt;

        private Long idEd;

        public Long getIdSt(){return this.idSt;}

        public Long getIdEd(){return this.idEd;}

        private List<Long> totalIdList;

        public List<Long> getTotalIdList(){return this.totalIdList;}

        private Long totalIdSt;

        private Long totalIdEd;

        public Long getTotalIdSt(){return this.totalIdSt;}

        public Long getTotalIdEd(){return this.totalIdEd;}

        private List<java.math.BigDecimal> amountList;

        public List<java.math.BigDecimal> getAmountList(){return this.amountList;}

        private java.math.BigDecimal amountSt;

        private java.math.BigDecimal amountEd;

        public java.math.BigDecimal getAmountSt(){return this.amountSt;}

        public java.math.BigDecimal getAmountEd(){return this.amountEd;}

        private List<java.math.BigDecimal> totalAmountList;

        public List<java.math.BigDecimal> getTotalAmountList(){return this.totalAmountList;}

        private java.math.BigDecimal totalAmountSt;

        private java.math.BigDecimal totalAmountEd;

        public java.math.BigDecimal getTotalAmountSt(){return this.totalAmountSt;}

        public java.math.BigDecimal getTotalAmountEd(){return this.totalAmountEd;}

        private List<java.math.BigDecimal> feeList;

        public List<java.math.BigDecimal> getFeeList(){return this.feeList;}

        private java.math.BigDecimal feeSt;

        private java.math.BigDecimal feeEd;

        public java.math.BigDecimal getFeeSt(){return this.feeSt;}

        public java.math.BigDecimal getFeeEd(){return this.feeEd;}

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

        private List<String> hashList;

        public List<String> getHashList(){return this.hashList;}


        private List<String> fuzzyHash;

        public List<String> getFuzzyHash(){return this.fuzzyHash;}

        private List<String> rightFuzzyHash;

        public List<String> getRightFuzzyHash(){return this.rightFuzzyHash;}

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

        public ConditionBuilder totalIdBetWeen(Long totalIdSt,Long totalIdEd){
            this.totalIdSt = totalIdSt;
            this.totalIdEd = totalIdEd;
            return this;
        }

        public ConditionBuilder totalIdGreaterEqThan(Long totalIdSt){
            this.totalIdSt = totalIdSt;
            return this;
        }
        public ConditionBuilder totalIdLessEqThan(Long totalIdEd){
            this.totalIdEd = totalIdEd;
            return this;
        }


        public ConditionBuilder totalIdList(Long ... totalId){
            this.totalIdList = solveNullList(totalId);
            return this;
        }

        public ConditionBuilder totalIdList(List<Long> totalId){
            this.totalIdList = totalId;
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

        public ConditionBuilder feeBetWeen(java.math.BigDecimal feeSt,java.math.BigDecimal feeEd){
            this.feeSt = feeSt;
            this.feeEd = feeEd;
            return this;
        }

        public ConditionBuilder feeGreaterEqThan(java.math.BigDecimal feeSt){
            this.feeSt = feeSt;
            return this;
        }
        public ConditionBuilder feeLessEqThan(java.math.BigDecimal feeEd){
            this.feeEd = feeEd;
            return this;
        }


        public ConditionBuilder feeList(java.math.BigDecimal ... fee){
            this.feeList = solveNullList(fee);
            return this;
        }

        public ConditionBuilder feeList(List<java.math.BigDecimal> fee){
            this.feeList = fee;
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

        public ConditionBuilder fuzzyHash (List<String> fuzzyHash){
            this.fuzzyHash = fuzzyHash;
            return this;
        }

        public ConditionBuilder fuzzyHash (String ... fuzzyHash){
            this.fuzzyHash = solveNullList(fuzzyHash);
            return this;
        }

        public ConditionBuilder rightFuzzyHash (List<String> rightFuzzyHash){
            this.rightFuzzyHash = rightFuzzyHash;
            return this;
        }

        public ConditionBuilder rightFuzzyHash (String ... rightFuzzyHash){
            this.rightFuzzyHash = solveNullList(rightFuzzyHash);
            return this;
        }

        public ConditionBuilder hashList(String ... hash){
            this.hashList = solveNullList(hash);
            return this;
        }

        public ConditionBuilder hashList(List<String> hash){
            this.hashList = hash;
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

        private NodeUserDraw obj;

        public Builder(){
            this.obj = new NodeUserDraw();
        }

        public Builder id(Long id){
            this.obj.setId(id);
            return this;
        }
        public Builder totalId(Long totalId){
            this.obj.setTotalId(totalId);
            return this;
        }
        public Builder amount(java.math.BigDecimal amount){
            this.obj.setAmount(amount);
            return this;
        }
        public Builder totalAmount(java.math.BigDecimal totalAmount){
            this.obj.setTotalAmount(totalAmount);
            return this;
        }
        public Builder fee(java.math.BigDecimal fee){
            this.obj.setFee(fee);
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
        public Builder hash(String hash){
            this.obj.setHash(hash);
            return this;
        }
        public NodeUserDraw build(){return obj;}
    }

}

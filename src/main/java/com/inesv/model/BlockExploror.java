package com.inesv.model;

import org.json.JSONObject;

/**
 * <p>
 * 
 * </p>
 *
 * @author lqh
 * @since 2018-01-18
 */
public class BlockExploror {
	private Integer id;
	private String blockHash;
	private String transactionIndex;
	private String nonce;
	private String input;
	private String r;
	private String s;
	private String v;
	private String timeStamp;
	private String contractAddress;
	private String blockNumber;
	private String tokenName;
	private String tokenSymbol;
	private String tokenDecimal;
	private String gasUsed;
	private String gas;
	private String cumulativeGasUsed;
	private String confirmations;
	private String transFrom;
	private String transTo;
	private String transValue;
	private String transHash;
	private String gasPrice;


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getBlockHash() {
		return blockHash;
	}

	public void setBlockHash(String blockHash) {
		this.blockHash = blockHash;
	}

	public String getTransactionIndex() {
		return transactionIndex;
	}

	public void setTransactionIndex(String transactionIndex) {
		this.transactionIndex = transactionIndex;
	}

	public String getNonce() {
		return nonce;
	}

	public void setNonce(String nonce) {
		this.nonce = nonce;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getR() {
		return r;
	}

	public void setR(String r) {
		this.r = r;
	}

	public String getS() {
		return s;
	}

	public void setS(String s) {
		this.s = s;
	}

	public String getV() {
		return v;
	}

	public void setV(String v) {
		this.v = v;
	}

	public String getBlockNumber() {
		return blockNumber;
	}

	public void setBlockNumber(String blockNumber) {
		this.blockNumber = blockNumber;
	}

	public String getGas() {
		return gas;
	}

	public void setGas(String gas) {
		this.gas = gas;
	}

	public String getTransFrom() {
		return transFrom;
	}

	public void setTransFrom(String transFrom) {
		this.transFrom = transFrom;
	}

	public String getTransTo() {
		return transTo;
	}

	public void setTransTo(String transTo) {
		this.transTo = transTo;
	}

	public String getTransValue() {
		return transValue;
	}

	public void setTransValue(String transValue) {
		this.transValue = transValue;
	}

	public String getTransHash() {
		return transHash;
	}

	public void setTransHash(String transHash) {
		this.transHash = transHash;
	}

	public String getGasPrice() {
		return gasPrice;
	}

	public void setGasPrice(String gasPrice) {
		this.gasPrice = gasPrice;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public String getContractAddress() {
		return contractAddress;
	}

	public void setContractAddress(String contractAddress) {
		this.contractAddress = contractAddress;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getTokenName() {
		return tokenName;
	}

	public void setTokenName(String tokenName) {
		this.tokenName = tokenName;
	}

	public String getTokenSymbol() {
		return tokenSymbol;
	}

	public void setTokenSymbol(String tokenSymbol) {
		this.tokenSymbol = tokenSymbol;
	}

	public String getTokenDecimal() {
		return tokenDecimal;
	}

	public void setTokenDecimal(String tokenDecimal) {
		this.tokenDecimal = tokenDecimal;
	}

	public String getGasUsed() {
		return gasUsed;
	}

	public void setGasUsed(String gasUsed) {
		this.gasUsed = gasUsed;
	}

	public String getCumulativeGasUsed() {
		return cumulativeGasUsed;
	}

	public void setCumulativeGasUsed(String cumulativeGasUsed) {
		this.cumulativeGasUsed = cumulativeGasUsed;
	}

	public String getConfirmations() {
		return confirmations;
	}

	public void setConfirmations(String confirmations) {
		this.confirmations = confirmations;
	}

	@Override
	public String toString() {
		return "BlockExploror{" +
				"id=" + id +
				", blockHash='" + blockHash + '\'' +
				", transactionIndex='" + transactionIndex + '\'' +
				", nonce='" + nonce + '\'' +
				", input='" + input + '\'' +
				", r='" + r + '\'' +
				", s='" + s + '\'' +
				", v='" + v + '\'' +
				", timeStamp='" + timeStamp + '\'' +
				", contractAddress='" + contractAddress + '\'' +
				", blockNumber='" + blockNumber + '\'' +
				", tokenName='" + tokenName + '\'' +
				", tokenSymbol='" + tokenSymbol + '\'' +
				", tokenDecimal='" + tokenDecimal + '\'' +
				", gasUsed='" + gasUsed + '\'' +
				", gas='" + gas + '\'' +
				", cumulativeGasUsed='" + cumulativeGasUsed + '\'' +
				", confirmations='" + confirmations + '\'' +
				", transFrom='" + transFrom + '\'' +
				", transTo='" + transTo + '\'' +
				", transValue='" + transValue + '\'' +
				", transHash='" + transHash + '\'' +
				", gasPrice='" + gasPrice + '\'' +
				'}';
	}

	public static BlockExploror getBlockFromJson(String json){
		if(json == null || "".equals(json)){
			return null;
		}
		JSONObject jsonObject = new JSONObject(json);
		BlockExploror dto = new BlockExploror();

		dto.blockNumber = jsonObject.getString("blockNumber");
		dto.timeStamp = jsonObject.getString("timeStamp");
		dto.transHash = jsonObject.getString("hash");
		dto.blockHash = jsonObject.getString("blockHash");
		dto.nonce = jsonObject.getString("nonce");
		dto.transFrom = jsonObject.getString("from");
		dto.contractAddress = jsonObject.getString("contractAddress");
		dto.transTo = jsonObject.getString("to");
		dto.transValue = jsonObject.getString("value");
		dto.tokenName = jsonObject.getString("tokenName");
		dto.tokenSymbol = jsonObject.getString("tokenSymbol");
		dto.tokenDecimal = jsonObject.getString("tokenDecimal");
		dto.transactionIndex = jsonObject.getString("transactionIndex");
		dto.gas = jsonObject.getString("gas");
		dto.gasPrice = jsonObject.getString("gasPrice");
		dto.gasUsed = jsonObject.getString("gasUsed");
		dto.cumulativeGasUsed = jsonObject.getString("cumulativeGasUsed");
		dto.input = jsonObject.getString("input");
		dto.confirmations = jsonObject.getString("confirmations");
		return dto;
	}
}

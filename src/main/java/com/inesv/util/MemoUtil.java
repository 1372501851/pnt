package com.inesv.util;

import com.alibaba.fastjson.JSONObject;
import org.bitcoinj.wallet.DeterministicSeed;

import java.security.SecureRandom;
import java.util.*;

public class MemoUtil {

    public static String createMemo(){
        // TODO 给用户添加助记词，返回助记词给前端
        String passphrase = "";
        SecureRandom secureRandom = new SecureRandom();
        long creationTimeSeconds = System.currentTimeMillis() / 1000;
        DeterministicSeed deterministicSeed = new DeterministicSeed(secureRandom, 128, passphrase, creationTimeSeconds);
        List<String> mnemonicCode = deterministicSeed.getMnemonicCode();
        String memo=String.join(" ", mnemonicCode);
        Map map = new HashMap();
        map.put("memo",memo);
        JSONObject json =new JSONObject(map);
        return json.toString();

    }

    public static void main(String[] args) {
        String data=MemoUtil.createMemo();
        System.out.println(data);
    }

}

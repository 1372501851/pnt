package com.inesv.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class PayHttpUtils {
    private static final String CHARSET = "UTF-8";

    public PayHttpUtils() {
    }

    public static String postJson(String url, String json) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            StringEntity stringEntity = new StringEntity(json, "UTF-8");
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);
            CloseableHttpResponse response = httpClient.execute(httpPost);

            String var7;
            try {
                HttpEntity entity = response.getEntity();
                if (entity == null) {
                    return null;
                }

                var7 = EntityUtils.toString(entity, "UTF-8");
            } finally {
                response.close();
                httpClient.close();
            }

            return var7;
        } catch (Exception var12) {
            var12.printStackTrace();
            return null;
        }
    }
}

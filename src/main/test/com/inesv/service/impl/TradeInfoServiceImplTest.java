package com.inesv.service.impl;

import com.inesv.run.PNTApplication;
import com.inesv.service.TradeInfoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={PNTApplication.class})
public class TradeInfoServiceImplTest {

    @Autowired
    private TradeInfoService tradeInfoService;

    @Test
    public void tradeMocToSystem() {
//        tradeInfoService.tradeMocToSystem()
        tradeInfoService.tradeMocToSystem(1L,new BigDecimal("0.001"), new BigDecimal("0.001"),"MOxb48b72c9cdb44200f856b91e97b5441e48d8a086","测试");
    }
}
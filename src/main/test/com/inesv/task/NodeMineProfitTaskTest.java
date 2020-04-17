package com.inesv.task;

import com.inesv.mapper.NodeMineProfitMapper;
import com.inesv.run.PNTApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={PNTApplication.class})
public class NodeMineProfitTaskTest {

    @Autowired
    private NodeMineProfitTask nodeMineProfitTask;

    @Test
    public void getMineProfit() {
        nodeMineProfitTask.getMineProfit();
    }

    @Test
    public void saveAddressBalance() {
        nodeMineProfitTask.saveAddressBalance();
    }
}
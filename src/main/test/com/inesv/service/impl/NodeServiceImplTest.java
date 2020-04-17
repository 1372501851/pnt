package com.inesv.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.inesv.mapper.NodeMapper;
import com.inesv.mapper.base.NodeBaseMapper;
import com.inesv.model.Node;
import com.inesv.model.NodeUserDraw;
import com.inesv.run.PNTApplication;
import com.inesv.service.NodeService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes={PNTApplication.class})
public class NodeServiceImplTest {

    @Autowired
    private NodeMapper nodeMapper;

    @Autowired
    private NodeBaseMapper nodeBaseMapper;

    @Autowired
    private NodeService nodeService;

    @Test
    public void testAll () {
        String parListByPid = nodeMapper.getParListByPid(8L, 5l);
        System.out.println(parListByPid);
        Node node = new Node();
        node.setName("社区1");
        List<Node> nodes = nodeBaseMapper.queryNode(null);
        System.out.println(JSONObject.toJSONString(nodes));
    }

    @Test
    public void getUserNode() {

        nodeService.userBuyNode(31L, new BigDecimal("500"));

    }

    @Test
    public void getParListByPid() {
    }

    @Test
    public void isExistNode() {
    }

    @Test
    public void userBuyNode() {
    }

    @Test
    public void nodeUserDrawRecord() {
        List<NodeUserDraw> nodeUserDraws = nodeService.nodeUserDrawRecord(31L);
        log.info("提取数据：{}", nodeUserDraws);
    }
}
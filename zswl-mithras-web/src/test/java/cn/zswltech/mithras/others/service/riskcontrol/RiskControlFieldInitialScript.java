package cn.zswltech.mithras.others.service.riskcontrol;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.foundation.enums.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.mapper.corp.CorpCommerceInfoMapper;
import cn.zswltech.mithras.customer.mapper.lib.client.CorpCommerceInfoLibMapper;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.customer.versioning.dto.CorpCommerceInfoLibDto;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.Data;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/3/17 09:39
 */
public class RiskControlFieldInitialScript extends ApplicationTest {
    @Resource
    private CorpCommerceInfoLibMapper corpCommerceInfoLibMapper;
    @Resource
    private CorpCommerceInfoMapper corpCommerceInfoMapper;
    @Resource
    private ClientMapper clientMapper;

    @Test
    public void initClassify() throws IOException {
        File file = new File("/Users/zhaozhengkang/classify.json");
        FileReader fileReader = new FileReader(file);
        Reader reader = new InputStreamReader(new FileInputStream(file), "Utf-8");
        int ch = 0;
        StringBuffer sb = new StringBuffer();
        while ((ch = reader.read()) != -1) {
            sb.append((char) ch);
        }
        fileReader.close();
        reader.close();
        String jsonStr = sb.toString();

        List<ClassifyCheck> classifyChecks = JSON.parseObject(jsonStr, new TypeReference<List<ClassifyCheck>>() {
        });
        Set<String> clientNames = classifyChecks.stream().map(ClassifyCheck::getClientName).collect(Collectors.toSet());

        List<Client> clients = clientMapper.selectList(Wrappers.<Client>lambdaQuery().in(Client::getClientName, clientNames));
        if (clients.size() != clientNames.size()) {
            throw new RuntimeException("客户名录对不上");
        }
        System.out.println();

        CorpCommerceInfoLibDto dto = new CorpCommerceInfoLibDto();
        dto.setInClientIds(clients.stream().map(Client::getId).collect(Collectors.toSet()));
        List<CorpCommerceInfoLib> corpCommerceInfoLibs = corpCommerceInfoLibMapper.listNewestCommerceInfo(dto);

        Map<String, Long> name2Id = clients.stream().collect(Collectors.toMap(Client::getClientName, Client::getId));

        Map<Long, String> diff = new HashMap<>();
        Map<Long, String> checkerMap = classifyChecks.stream().collect(Collectors.toMap(v -> name2Id.get(v.getClientName()), ClassifyCheck::getRiskConttolIndustryClassify, (k1, k2) -> k1));
        ;

        for (CorpCommerceInfoLib lib : corpCommerceInfoLibs) {
            String right = checkerMap.get(lib.getClientId());
            if (ObjectUtil.isEmpty(lib.getRiskControlIndustryClassify())) {
                diff.put(lib.getClientId(), "null->" + right);
            } else {
                RiskControlIndustryClassify riskControlIndustryClassify = RiskControlIndustryClassify.valueOf(lib.getRiskControlIndustryClassify());
                if (!riskControlIndustryClassify.display().equals(right)) {
                    diff.put(lib.getClientId(), riskControlIndustryClassify.display() + "->" + right);
                }
            }
        }
        diff.forEach((aLong, s) -> {
            String[] split = s.split("->");
            LambdaUpdateWrapper<CorpCommerceInfo> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(CorpCommerceInfo::getClientId, aLong);
            updateWrapper.set(CorpCommerceInfo::getRiskControlIndustryClassify, RiskControlIndustryClassify.display2Name(split[1]));
            corpCommerceInfoMapper.update(null, updateWrapper);

            LambdaUpdateWrapper<CorpCommerceInfoLib> libUpdateWrapper = new LambdaUpdateWrapper<>();
            libUpdateWrapper.eq(CorpCommerceInfo::getClientId, aLong);
            libUpdateWrapper.set(CorpCommerceInfo::getRiskControlIndustryClassify, RiskControlIndustryClassify.display2Name(split[1]));
            corpCommerceInfoLibMapper.update(null, libUpdateWrapper);
        });
    }

    @Test
    public void initGroup() throws IOException {
        File file = new File("/Users/zhaozhengkang/classify.json");
        FileReader fileReader = new FileReader(file);
        Reader reader = new InputStreamReader(new FileInputStream(file), "Utf-8");
        int ch = 0;
        StringBuffer sb = new StringBuffer();
        while ((ch = reader.read()) != -1) {
            sb.append((char) ch);
        }
        fileReader.close();
        reader.close();
        String jsonStr = sb.toString();

        List<ClassifyCheck> classifyChecks = JSON.parseObject(jsonStr, new TypeReference<List<ClassifyCheck>>() {
        });
        Set<String> clientNames = classifyChecks.stream().map(ClassifyCheck::getClientName).collect(Collectors.toSet());

        // 处理集团
        Set<String> groupNames = classifyChecks.stream().map(ClassifyCheck::getBelongGroupName).collect(Collectors.toSet());
        List<Client> groupClients = clientMapper.selectList(Wrappers.<Client>lambdaQuery().in(Client::getClientName, groupNames));
        Map<Long, String> groupNameMap = groupClients.stream().collect(Collectors.toMap(Client::getId, Client::getClientName));
        Map<String, Long> groupName2Id = groupClients.stream().collect(Collectors.toMap(Client::getClientName, Client::getId));
        List<Client> clients = clientMapper.selectList(Wrappers.<Client>lambdaQuery().in(Client::getClientName, clientNames));
        Map<String, Long> name2Id = clients.stream().collect(Collectors.toMap(Client::getClientName, Client::getId));

        CorpCommerceInfoLibDto dto = new CorpCommerceInfoLibDto();
        dto.setInClientIds(clients.stream().map(Client::getId).collect(Collectors.toSet()));
        List<CorpCommerceInfoLib> corpCommerceInfoLibs = corpCommerceInfoLibMapper.listNewestCommerceInfo(dto);

        if (groupClients.size() != groupNames.size()) {
            Set<String> dbGroups = groupClients.stream().map(Client::getClientName).collect(Collectors.toSet());
            groupNames.removeAll(dbGroups);
            System.out.println("集团名录对不上：" + groupNames);
        }
        Map<Long, String> groupDiff = new HashMap<>();
        Map<Long, String> groupCheckerMap = classifyChecks.stream().collect(Collectors.toMap(v -> name2Id.get(v.getClientName()), v -> String.join(":", v.getIsGroup(), v.belongGroupName), (k1, k2) -> k1));
        for (CorpCommerceInfoLib lib : corpCommerceInfoLibs) {
            String right = groupCheckerMap.get(lib.getClientId());
            String dbValue;
            if (lib.getGroupFlag() == null || lib.getGroupFlag() == 0) {
                dbValue = String.join(":", "否", "null");
            } else {
                dbValue = String.join(":", "是", groupNameMap.getOrDefault(lib.getBelongGroupClientId(), "null"));
            }
            if (!right.equals(dbValue)) {
                groupDiff.put(lib.getClientId(), dbValue + "->" + right);
            }
        }

        groupDiff.forEach(new BiConsumer<Long, String>() {
            @Override
            public void accept(Long aLong, String s) {
                String[] split = s.split("->");
                String[] right = split[0].split(":");
                LambdaUpdateWrapper<CorpCommerceInfo> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(CorpCommerceInfo::getClientId, aLong);
                updateWrapper.set(CorpCommerceInfo::getGroupFlag, right[0].equals("是") ? 1 : 0);
                updateWrapper.set(CorpCommerceInfo::getBelongGroupClientId, groupName2Id.getOrDefault(right[1], null));
                corpCommerceInfoMapper.update(null, updateWrapper);

                LambdaUpdateWrapper<CorpCommerceInfoLib> libUpdateWrapper = new LambdaUpdateWrapper<>();
                libUpdateWrapper.eq(CorpCommerceInfo::getClientId, aLong);
                libUpdateWrapper.set(CorpCommerceInfo::getGroupFlag, right[0].equals("是") ? 1 : 0);
                libUpdateWrapper.set(CorpCommerceInfo::getBelongGroupClientId, groupName2Id.getOrDefault(right[1], null));
                corpCommerceInfoLibMapper.update(null, libUpdateWrapper);
            }
        });
        System.out.println();
    }

    @Data
    public static class ClassifyCheck {
        private Long clientId;
        private String clientName;
        private String riskConttolIndustryClassify;
        private String belongGroupName;
        private String isGroup;
    }
}

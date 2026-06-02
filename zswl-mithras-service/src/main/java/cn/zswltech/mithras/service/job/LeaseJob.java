package cn.zswltech.mithras.service.job;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.contract.mapper.contract.ContractLeaseItemMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.*;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.contract.service.contract.ContractLeaseItemNameService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author luyujie
 * @date 2025/12/25
 * @description 合同租赁物名称拼接调度
 */
@Slf4j
@Component
public class LeaseJob {

    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractLeaseItemMapper contractLeaseItemMapper;
    @Resource
    private ContractLeaseItemNameService contractLeaseItemNameService;

    @XxlJob("setContractLeaseItemNameJob")
    public void setContractLeaseItemNameJob() {
        try {
            log.info("setContractLeaseItemNameJob start");
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            // 获取合同
            LambdaQueryWrapper<ContractBaseInfo> contractBaseInfoQuery = Wrappers.lambdaQuery();
            List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.list(contractBaseInfoQuery);
            if (CollectionUtil.isEmpty(contractBaseInfoList)) {
                log.info("未获取到合同数据！");
                return;
            }
            //获取拼接租赁物名称
            List<ContractLeaseItemName> contractLeaseItemNameList = contractLeaseItemNameService.list(Wrappers.<ContractLeaseItemName>lambdaQuery());
            Map<Long, ContractLeaseItemName> contractLeaseItemNameMap = CollectionUtil.isEmpty(contractLeaseItemNameList) ?
                    new HashMap<>() : contractLeaseItemNameList.stream().collect(Collectors.toMap(ContractLeaseItemName::getId, contractLeaseItemName -> contractLeaseItemName));
            List<ContractLeaseItemName> insertList = new ArrayList<>();
            List<ContractLeaseItemName> updateList = new ArrayList<>();
            for (ContractBaseInfo contractBaseInfo : contractBaseInfoList) {
                // 获取合同对应租赁物
                LambdaQueryWrapper<ContractLeaseItem> query = new LambdaQueryWrapper<>();
                query.eq(ContractLeaseItem::getContractId, contractBaseInfo.getId());
                List<ContractLeaseItem> contractLeaseItemList = contractLeaseItemMapper.selectList(query);
                if (CollectionUtil.isEmpty(contractLeaseItemList)) {
                    log.info("未获取到合同租赁物数据！");
                    continue;
                }
                //创建一个set数组容纳租赁物名称并去重
                Set<String> leaseNameSet = new HashSet<>();
                for (ContractLeaseItem contractLeaseItem : contractLeaseItemList) {
                    //根据不同种类租赁物获取租赁物名称
                    log.info("获取到合同租赁物数据id:" + contractLeaseItem.getId().toString());
                    String leaseName = "";
                    Map<String, Object> map = JSON.parseObject(contractLeaseItem.getRowData().replaceAll("(?<!\\\\)\\\\(?!([\"\\\\/bfnrtu]|u[0-9a-fA-F]{4}))", "\\\\\\\\"), HashMap.class);
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        //log.info("key is {}, value is {}", entry.getKey(), entry.getValue());
                        if (Objects.nonNull(entry.getKey()) &&
                                ("名称".equals(entry.getKey()) || "名称*".equals(entry.getKey())
                                        || "租赁船舶名称".equals(entry.getKey()) || "设备名称".equals(entry.getKey()) || "设备名称*".equals(entry.getKey()))) {
                            if (entry.getValue() != null) {
                                leaseName = entry.getValue().toString();
                            }
                        }
                    }
                    if (StrUtil.isNotBlank(leaseName)) {
                        leaseNameSet.add("\"" + leaseName + "\"");
                    }
                }
                //创建拼接租赁物名称
                String leaseNameConact = "";
                //将数据租赁物名称拼接
                for (String item : leaseNameSet) {
                    leaseNameConact = leaseNameConact + item + ",";
                }
                //拼接租赁物名称不为空加入数据库
                if (StrUtil.isNotBlank(leaseNameConact)) {
                    //去除末尾','
                    leaseNameConact = leaseNameConact.substring(0, leaseNameConact.length() - 1);
                    //将合同编号和对应租赁物名称加入租赁物名称表
                    ContractLeaseItemName contractLeaseItemName = contractLeaseItemNameMap.get(contractBaseInfo.getId());
                    if (contractLeaseItemName == null) {
                        contractLeaseItemName = new ContractLeaseItemName();
                        contractLeaseItemName.setId(contractBaseInfo.getId());
                        contractLeaseItemName.setName(leaseNameConact);
                        insertList.add(contractLeaseItemName);
                        //  contractLeaseItemNameService.save(contractLeaseItemName);
                    } else {
                        contractLeaseItemName.setName(leaseNameConact);
                        updateList.add(contractLeaseItemName);
                        //contractLeaseItemNameService.updateById(contractLeaseItemName);
                    }
                }
            }
            if (CollectionUtil.isNotEmpty(insertList)) {
                contractLeaseItemNameService.saveBatch(insertList);
            }
            if (CollectionUtil.isNotEmpty(updateList)) {
                contractLeaseItemNameService.updateBatchById(updateList);
            }
            stopWatch.stop();
            log.info("setContractLeaseItemNameJob end!!! 耗时={}s", stopWatch.prettyPrint(TimeUnit.SECONDS));
        } catch (Exception e) {
            log.error("setContractLeaseItemNameJob 执行异常，e={}", e);
        }
    }
}

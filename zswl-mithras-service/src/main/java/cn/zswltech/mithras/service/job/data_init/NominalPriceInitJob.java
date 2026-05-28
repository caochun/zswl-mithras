package cn.zswltech.mithras.service.job.data_init;

import cn.hutool.core.date.StopWatch;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.contract.ContractStatus;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractService;
import cn.zswltech.mithras.service.util.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static cn.zswltech.mithras.service.others.SpringContextHolder.getBean;

/**
 * @description: 因吧初始化名义价款 生成时间节点放到 起租后，
 * 存在 合同起租后-结清前数据没有名义价款  所以这里初始化 上线跑一次就行了
 * @author: huangping
 * @date: 2025/12/16  14:30
 * @version: 1.0
 */
@Slf4j
@Component
public class NominalPriceInitJob {

    @Resource
    protected ContractService contractService;

    @Resource
    protected ContractBaseInfoService contractBaseInfoService;

    @XxlJob("NominalPriceInitJob")
    public void NominalPriceInitJob() {
        try {
            log.info("NominalPriceInitJob start");
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            String jobParam = XxlJobHelper.getJobParam();
           // jobParam = "1951";
            initNominalPriceList(jobParam);
            stopWatch.stop();
            log.info("NominalPriceInitJob end!!! 耗时={}s", stopWatch.prettyPrint(TimeUnit.SECONDS));
        } catch (Exception e) {
            log.error("NominalPriceInitJob 执行异常，e={}", e);
        }
    }


    private void initNominalPriceList(String jobParam) {
        if (StringUtils.isNotEmpty(jobParam)) {
            ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(Long.valueOf(jobParam));
            signleDeal(contractBaseInfo);
            return;
        }
        List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                .eq(ContractBaseInfo::getContractStatus, ContractStatus.START_RENT.name()));
        if (CollectionUtils.isEmpty(contractBaseInfos)){
            log.info("initNominalPriceList  无起租合同！！！");
            return;
        }
        for (ContractBaseInfo contractBaseInfo : contractBaseInfos) {
            signleDeal(contractBaseInfo);
        }
    }


    private void signleDeal(ContractBaseInfo contractBaseInfo) {
        if (Objects.isNull(contractBaseInfo)) {
            return;
        }
        Long contractId = contractBaseInfo.getId();
        //仅起租
        log.info("signleDeal -----start!!! contractId:{}", contractId);
        if (!ObjectUtils.equals(ContractStatus.START_RENT.name(), contractBaseInfo.getContractStatus())) {
            return;
        }
        //取名义价款
        List<CollectionBaseInfo> collectionList = getBean(CollectionBaseInfoService.class).list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getContractId, contractId)
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.NOMINAL_PRICE.name())
                .orderByAsc(CollectionBaseInfo::getPlanCollectionDate));
        if (!CollectionUtils.isEmpty(collectionList)) {
            log.info("signleDeal 已存在名义价款 contratId:{}  合同已存在名义价款", contractId);
            return;
        }
        log.info("signleDeal -----push!!! contractId:{}", contractId);
        contractService.notifyOnStartRentPass(contractBaseInfo, ProcessModelTypeEnum.ContractStartRentFlow);
    }

}

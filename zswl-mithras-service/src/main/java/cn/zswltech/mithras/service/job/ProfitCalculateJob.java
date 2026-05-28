package cn.zswltech.mithras.service.job;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.contract.ContractStatus;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.service.ProfitCalculateResultService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author dingqi
 * @date 2023/6/25
 * @description
 */
@Slf4j
@Component
public class ProfitCalculateJob {
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ProfitCalculateResultService profitCalculateResultService;

    @XxlJob("profitCalculate")
    public void profitCalculate() {
        log.info("会计利润测算测算任务 <<<<<<<<<<<<<<<< 开始");
        List<ContractBaseInfo> todoList;
        String params = XxlJobHelper.getJobParam();
        if (StrUtil.isNotBlank(params)) {
            ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(Long.parseLong(params));
            todoList = Collections.singletonList(contractBaseInfo);
        } else {
            LambdaQueryWrapper<ContractBaseInfo> query = Wrappers.lambdaQuery();
            query.in(ContractBaseInfo::getContractStatus, Arrays.asList(ContractStatus.TAKE_EFFECT.name(), ContractStatus.START_RENT.name(), ContractStatus.SETTLE.name()));
            todoList = contractBaseInfoService.list(query);
        }
        if (CollectionUtil.isEmpty(todoList)) {
            log.info("没有需要进行利润测算的合同");
            return;
        }
        for (ContractBaseInfo contractBaseInfo : todoList) {
            String uuid = UUID.randomUUID().toString();
            MDC.put(GlobalConstants.LOG_TRACE_ID, uuid);
            log.info("{}的利润测算 <<<<<<<<<<<<< 开始", contractBaseInfo.getContractCode());
            try {
                profitCalculateResultService.calculate(contractBaseInfo.getId());
            } catch (Exception e) {
                log.error("{}的利润测算发生异常", contractBaseInfo.getContractCode(), e);
            }
            log.info("{}的利润测算 <<<<<<<<<<<<< 结束", contractBaseInfo.getContractCode());
        }
        log.info("会计利润测算测算任务 <<<<<<<<<<<<<<<< 结束");
    }
}

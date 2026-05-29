package cn.zswltech.mithras.service.job;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.common.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.VersionTypeEnum;
import cn.zswltech.mithras.service.gendoc.BusinessDataRepository;
import cn.zswltech.mithras.service.mapper.model.budget.BudgetPlanPay;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.BizDataFixService;
import cn.zswltech.mithras.service.service.Listener.SystemSwitchRefreshEvent;
import cn.zswltech.mithras.service.service.budget.BudgetPlanPayFlowService;
import cn.zswltech.mithras.service.service.budget.BudgetPlanPayService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.lib.contract.ContractVersionService;
import cn.zswltech.mithras.service.service.lib.fund.financing.FundFinancingLibVersionService;
import cn.zswltech.mithras.service.service.third.DmImportService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2022/9/27
 * @description 系统任务，可用于非前台业务的后门操作
 */
@Slf4j
@Component
public class SystemJob {
    @Resource
    private BusinessDataRepository businessDataRepository;
    @Resource
    private FundFinancingLibVersionService fundFinancingLibVersionService;
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;
    @Resource
    private DmImportService dmImportService;
    @Resource
    private BizDataFixService bizDataFixService;

//    @XxlJob("initHistoryProjectClientMaterial")
//    public void initHistoryProjectClientMaterial() {
//        bizDataFixService.initHistoryProjectClientMaterial();
//    }

    @XxlJob("createBudgetFinancialFlow")
    public void createBudgetFinancialFlow() {
        String param = XxlJobHelper.getJobParam();
//        String param = "{\"budgetPlanPayId\":9,\"isMonthFlag\":true,\"startUserId\":107}";
        if (StrUtil.isBlank(param)) {
            throw new MithrasException("控制台参数缺失");
        }
        JSONObject jsonObject = JSONUtil.parseObj(param);
        Long budgetPlanPayId = jsonObject.getLong("budgetPlanPayId");
        Boolean isMonthFlag = jsonObject.getBool("isMonthFlag");
        Long startUserId = jsonObject.getLong("startUserId");
        if (Objects.isNull(budgetPlanPayId) || Objects.isNull(isMonthFlag) || Objects.isNull(startUserId)) {
            throw new MithrasException("控制台创建流程缺少必要的业务参数");
        }
        BudgetPlanPay budgetPlanPay = SpringUtil.getBean(BudgetPlanPayService.class).getById(budgetPlanPayId);
        SpringUtil.getBean(BudgetPlanPayFlowService.class).createBudgetFinancialFlow(budgetPlanPay, isMonthFlag, startUserId);
    }

    @XxlJob("importFtpInterestData")
    public void initFtpInterestData() {
        bizDataFixService.importFtpInterestData();
    }

    @XxlJob("createSettleContractLib")
    public void createSettleContractLib() {
        // 按照合同最新数据生成一个版本，主要用于补全结清合同缺失版本的问题，也可以方便数据订正时候的生成版本
//        String contractCodes = "浙商租【2021】租字第(A-0025)号,浙商租【2023】租字第(A-0113)号,浙商租【2021】租字第(A-0067)号,浙商租【2022】租字第(A-0037)号,浙商租【2022】租字第(A-0009)号,浙商租【2021】租字第(A-0056)号,浙商租【2021】租字第(XB-0003)号,浙商租【2021】租字第(A-0035)号,浙商租【2023】租字第(A-0014)号,浙商租【2022】保理字第(B-0014)号,浙商租【2022】保理字第(B-0018)号,浙商租【2023】保理字第(B-0001)号,浙商租【2022】租字第(A-0044)号,浙商租【2022】保理字第(B-0009)号,浙商租【2023】保理字第(B-0004)号,浙商租【2023】保理字第(B-0005)号,浙商租【2023】保理字第(B-0009)号,浙商租【2023】保理字第(B-0026)号,浙商租【2023】保理字第(B-0012)号,浙商租【2023】租字第(A-0053)号,浙商租【2022】保理字第(B-0010)号,浙商租【2022】保理字第(B-0011)号,浙商租【2022】保理字第(B-0012)号,浙商租【2022】保理字第(B-0013)号,浙商租【2021】租字第(A-0050)号,浙商租【2022】租字第(A-0118)号,浙商租【2023】租字第(A-0168)号,浙商租【2023】租字第(A-0181)号,浙商租【2022】租字第(A-0036)号,浙商租【2021】租字第(A-0074)号,浙商租【2021】租字第(A-0051)号,浙商租【2021】租字第(A-0034)号,浙商租【2021】租字第(A-0048)号,浙商租【2023】租字第(A-0083)号,中拓租【2020】租字第(A-0004)号,浙商租【2022】租字第(A-0007)号,中拓租【2020】租字第(A-0010)号,浙商租【2022】租字第(C-0008)号,浙商租【2022】租字第(C-0004)号,浙商租【2024】租字第(A-0002)号,浙商租【2022】租字第(A-0061)号,浙商租【2022】租字第(A-0120)号,浙商租【2022】租字第(A-0119)号,中拓租【2020】租字第(A-0034)号,中拓租【2020】租字第(A-0020)号,中拓租【2020】租字第(A-0021)号,浙商租【2021】租字第(A-0052)号,浙商租【2021】租字第(A-0075)号,浙商租【2023】租字第(A-0068)号,浙商租【2022】租字第(A-0057)号";
        String contractCodes = XxlJobHelper.getJobParam();
        if (StrUtil.isBlank(contractCodes)) {
            return;
        }
        List<String> contractCodeList = ListUtil.of(contractCodes.split(","));
        List<ContractBaseInfo> contractBaseInfoList = SpringUtil.getBean(ContractBaseInfoService.class).list(
                Wrappers.<ContractBaseInfo>lambdaQuery().in(ContractBaseInfo::getContractCode, contractCodeList)
        );
        List<Long> targetContractIds = contractBaseInfoList.stream().map(ContractBaseInfo::getId).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(targetContractIds)) {
            for (Long contractId : targetContractIds) {
                SpringUtil.getBean(ContractVersionService.class).recordVersion(contractId, VersionTypeEnum.EFFECT, ObjectUtil.isEmpty(AccountUtil.getLoginInfo()) ? 3L : AccountUtil.getLoginInfo().getId(), null, VersionTypeConstants.NORMAL);
            }
        }
    }

    @XxlJob("initRatingClientIndicator")
    public void initRatingClientIndicator() {
        bizDataFixService.fixRatingClientAreaIndicator();
    }

    @XxlJob("importDMData")
    public void importDMData() {
        dmImportService.importData();
    }

    @XxlJob("financingRecordVersion")
    public void fixFundFinanceSettle() {
        String param = XxlJobHelper.getJobParam();
        log.info("准备生成融资数据版本[控制台参数:{}]", param);
        if (StrUtil.isBlank(param)) {
            return;
        }
        String[] array = param.split(",");
        for (String s : array) {
            try {
                Long financingId = Long.parseLong(s);
                fundFinancingLibVersionService.recordVersion(financingId, VersionTypeEnum.EFFECT, null, null, VersionTypeConstants.NORMAL);
            } catch (Exception e) {
                log.error("生成融资数据版本异常[financingId:{}]", s, e);
            }
        }
    }

    @XxlJob("refreshDataDictLocalCache")
    public ReturnT<String> refreshDataDictLocalCache() {
        String param = XxlJobHelper.getJobParam();
        log.info("开始执行【刷新数据字典本地缓存】任务[控制台参数: {}]", param);
        try {
            if ("general".equals(param)) {
                businessDataRepository.refreshGeneralDictionaryLocalCache();
            }
            if ("address".equals(param)) {
                businessDataRepository.refreshAddressLocalCache();
            }
            if ("industry".equals(param)) {
                businessDataRepository.refreshIndustryLocalCache();
            }
        } catch (Exception e) {
            log.error("刷新数据字典本地缓存发生异常", e);
            return ReturnT.FAIL;
        }
        log.info("结束执行【刷新数据字典本地缓存】任务");
        return ReturnT.SUCCESS;
    }

    @XxlJob("refreshSystemSwitchLocalCache")
    public void refreshSystemSwitchLocalCache() {
        SystemSwitchRefreshEvent systemSwitchRefreshEvent = new SystemSwitchRefreshEvent("xxl job");
        applicationEventPublisher.publishEvent(systemSwitchRefreshEvent);
    }
}

package cn.zswltech.mithras.metric.financialcloudmetric.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.financialcloudmetric.FinancialCloudMetricValueApi;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.financialcloudmetric.*;
import cn.zswltech.mithras.metric.financialcloudmetric.model.FinancialCloudMetric;
import cn.zswltech.mithras.metric.financialcloudmetric.model.FinancialCloudMetricValue;
import cn.zswltech.mithras.metric.financialcloudmetric.service.FinancialCloudMetricService;
import cn.zswltech.mithras.metric.financialcloudmetric.service.FinancialCloudMetricValueService;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.Id2NameService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description 金融云指标
 * @date 2023-04-12
 */
@RestController
public class FinancialCloudMetricValueController implements FinancialCloudMetricValueApi {

    @Resource
    private FinancialCloudMetricValueService baseService;
    @Resource
    private FinancialCloudMetricService metricService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;

    @Override
    public R<Void> cacl(FinancialCloudMetricValueCalcREQ req) {
        baseService.calc(req.getDataTime());
        return R.ok();
    }

    @Override
    public R<Void> modify(List<FinancialCloudMetricValueModifyREQ> req) {
        baseService.modify(req);
        return R.ok();
    }

    @Override
    public R<FinancialCloudMetricValueRSP> list(FinancialCloudMetricValueListREQ req) {
        FinancialCloudMetricValueRSP rsp = new FinancialCloudMetricValueRSP();
        rsp.setLastReportTime(baseService.getLastReportTime());
        rsp.setDataList(baseService.list(req));
        return R.ok(rsp);
    }

    @Override
    public R<Void> report(FinancialCloudMetricValueCalcREQ req) {
        baseService.report(req.getDataTime());
        return R.ok();
    }

    @Override
    public R<Void> create(FinancialCloudMetricValueCalcREQ req) {
        metricService.financialCloudMetricJobHandler(req.getDataTime());
        return R.ok();
    }

    @Override
    public R<Map<String, Set<String>>> pulldownList() {
        return R.ok(metricService.pulldownList());
    }

    @Override
    public R<FinancialCloudMetricDetailRsp> detail(SinglePkREQ req) {
        FinancialCloudMetricValue metricValue = baseService.getById(req.getId());
        if (ObjectUtil.isEmpty(metricValue)) {
            throw new MithrasException("记录不存在!");
        }
        FinancialCloudMetricDetailRsp rsp =
                BeanUtil.copyProperties(metricValue, FinancialCloudMetricDetailRsp.class);
        if (ObjectUtil.isNotEmpty(metricValue.getContractDetail())) {
            List<ContractDetail> contractDetails =
                    JSON.parseArray(metricValue.getContractDetail(), ContractDetail.class);
            Set<Long> contractIds = contractDetails.parallelStream().map(ContractDetail::getContractId).collect(Collectors.toSet());
            Map<Long, String> contractId2Name = id2NameService.contractId2Name(contractIds);

            Map<Long, ContractBaseInfo> contractBaseInfos = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                            .select(ContractBaseInfo::getId, ContractBaseInfo::getClientId, ContractBaseInfo::getContractCode)
                            .in(ContractBaseInfo::getId, contractIds))
                    .stream().collect(Collectors.toMap(ContractBaseInfo::getId, v -> v));
            for (ContractDetail contractDetail : contractDetails) {
                if (ObjectUtil.isEmpty(contractDetail.getClientId())) {
                    contractDetail.setClientId(
                            contractBaseInfos.getOrDefault(contractDetail.getContractId(),
                                    new ContractBaseInfo()).getClientId());
                }
            }

            Set<Long> clientIds = contractDetails.parallelStream().map(ContractDetail::getClientId).collect(Collectors.toSet());
            Map<Long, String> clientId2Name = id2NameService.clientId2Name(clientIds);

            for (ContractDetail contractDetail : contractDetails) {
                contractDetail.setContractCode(contractId2Name.get(contractDetail.getContractId()));
                contractDetail.setClientName(clientId2Name.get(contractDetail.getClientId()));
            }
            rsp.setContractDetailList(contractDetails);
        }
        FinancialCloudMetric metric = metricService.getById(metricValue.getMetricId());
        rsp.setUnit(metric.getUnit());

        return R.ok(rsp);
    }

    @GetMapping("/financial/cloud/metric/test")
    public R<Void> test(@RequestParam("metricCode") String metricCode) {
        baseService.testSpecificMetric(metricCode);
        return R.ok();
    }

}

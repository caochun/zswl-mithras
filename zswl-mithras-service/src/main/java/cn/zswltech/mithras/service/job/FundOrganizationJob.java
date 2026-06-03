package cn.zswltech.mithras.service.job;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.mithras.service.mapper.model.datashare.DataShareMerchants;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.FundOrganization;
import cn.zswltech.mithras.service.service.fund.FundOrganizationService;
import cn.zswltech.mithras.service.service.share.DataShareMerchantsService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FundOrganizationJob {

    @Resource
    private DataShareMerchantsService dataShareMerchantsService;
    @Resource
    private FundOrganizationService fundOrganizationService;

    @XxlJob("FundOrganizationCodeJob")
    public void FundOrganizationCode() {
        log.info("FundOrganizationCodeJob, start");
        LambdaQueryWrapper<FundOrganization> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.isNotNull(FundOrganization::getUscCode);
        queryWrapper.isNull(FundOrganization::getInstitutionCode);
        List<FundOrganization> fundOrganizationList = fundOrganizationService.list(queryWrapper);
        log.info("获取客商数据信息{}", fundOrganizationList);
        List<String> uscCodes = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(fundOrganizationList)) {
            uscCodes = fundOrganizationList.stream().map(FundOrganization::getUscCode).collect(Collectors.toList());
        }
        //获取客商信息数据，补全资金管理-机构表的机构编码
        List<DataShareMerchants> dataShareMerchantsList = null;
        Map<String, Long> clientIdMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(uscCodes)) {
            dataShareMerchantsList = dataShareMerchantsService.list(Wrappers.<DataShareMerchants>lambdaQuery()
                    .in(DataShareMerchants::getCreditCode, uscCodes));
        }
        if (CollectionUtil.isNotEmpty(dataShareMerchantsList)){
            // 过滤掉creditCode为空的元素
            clientIdMap = dataShareMerchantsList.stream().filter(dataShareMerchants -> StringUtils.isNotBlank(dataShareMerchants.getCreditCode()))
                    .collect(Collectors.toMap(DataShareMerchants::getCreditCode, DataShareMerchants::getClientId, (k1, k2) -> k1));
        }
        if (CollectionUtil.isNotEmpty(fundOrganizationList) && CollectionUtil.isNotEmpty(clientIdMap)) {
            for (FundOrganization fundOrg : fundOrganizationList) {
                Long aLong = clientIdMap.get(fundOrg.getUscCode());
                if (aLong != null){
                    fundOrg.setInstitutionCode(String.valueOf(aLong));
                }
            }
            fundOrganizationService.saveOrUpdateBatch(fundOrganizationList);
        }
        log.info("FundOrganizationCodeJob, end");
    }
}


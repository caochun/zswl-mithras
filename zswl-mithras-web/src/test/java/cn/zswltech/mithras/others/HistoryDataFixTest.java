package cn.zswltech.mithras.others;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.projestablish.baseinfo.jsonbean.ProjEstablishPersonInfo;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.customer.mapper.model.client.Client;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishBaseInfoLib;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.contract.core.application.ContractBaseInfoService;
import cn.zswltech.mithras.projectprocess.application.lib.projestablish.ProjEstablishBaseInfoLibService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projestablish.ProjEstablishBaseInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2022/10/28
 * @description
 */
public class HistoryDataFixTest extends ApplicationTest {
    @Resource
    private ClientService clientService;
    @Resource
    private ProjEstablishBaseInfoService projEstablishBaseInfoService;
    @Resource
    private ProjEstablishBaseInfoLibService projEstablishBaseInfoLibService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;

    @Test
    public void projEstablishDataFix() {
        LambdaQueryWrapper<ProjEstablishBaseInfo> query = Wrappers.lambdaQuery();
        query.in(ProjEstablishBaseInfo::getBizType, Arrays.asList(ProjectBizType.BL.name(), ProjectBizType.ZR.name()));
        List<ProjEstablishBaseInfo> projEstablishBaseInfoList = projEstablishBaseInfoService.list(query);
        if (CollectionUtil.isEmpty(projEstablishBaseInfoList)) {
            return;
        }
        for (ProjEstablishBaseInfo projEstablishBaseInfo : projEstablishBaseInfoList) {
            if (Objects.isNull(projEstablishBaseInfo.getCreditorClientId())) {
                continue;
            }
            Client client = clientService.getById(projEstablishBaseInfo.getCreditorClientId());
            if (Objects.isNull(client)) {
                log.warn("没有找到客户信息[clientId: {}]", projEstablishBaseInfo.getCreditorClientId());
                continue;
            }
            ProjEstablishPersonInfo info = new ProjEstablishPersonInfo();
            info.setClientId(client.getId());
            info.setClientName(client.getClientName());
            info.setClientType(client.getClientType());
            info.setStockRiskExposure(contractBaseInfoService.getStockRiskExposure(client.getId(), projEstablishBaseInfo.getId(), BusinessModuleEnum.PROJ_ESTABLISH.name()));
            projEstablishBaseInfo.setCreditorInfo(JSONUtil.toJsonStr(Collections.singletonList(info)));
            projEstablishBaseInfoService.updateById(projEstablishBaseInfo);
        }

        LambdaQueryWrapper<ProjEstablishBaseInfoLib> queryLib = Wrappers.lambdaQuery();
        queryLib.in(ProjEstablishBaseInfoLib::getBizType, Arrays.asList(ProjectBizType.BL.name(), ProjectBizType.ZR.name()));
        List<ProjEstablishBaseInfoLib> projEstablishBaseInfoLibList = projEstablishBaseInfoLibService.list(queryLib);
        if (CollectionUtil.isEmpty(projEstablishBaseInfoLibList)) {
            return;
        }
        for (ProjEstablishBaseInfoLib projEstablishBaseInfoLib : projEstablishBaseInfoLibList) {
            if (Objects.isNull(projEstablishBaseInfoLib.getCreditorClientId())) {
                continue;
            }
            Client client = clientService.getById(projEstablishBaseInfoLib.getCreditorClientId());
            if (Objects.isNull(client)) {
                log.warn("没有找到客户信息[clientId: {}]", projEstablishBaseInfoLib.getCreditorClientId());
                continue;
            }
            ProjEstablishPersonInfo info = new ProjEstablishPersonInfo();
            info.setClientId(client.getId());
            info.setClientName(client.getClientName());
            info.setClientType(client.getClientType());
            info.setStockRiskExposure(contractBaseInfoService.getStockRiskExposure(client.getId(), projEstablishBaseInfoLib.getOriginId(), BusinessModuleEnum.PROJ_ESTABLISH.name()));
            projEstablishBaseInfoLib.setCreditorInfo(JSONUtil.toJsonStr(Collections.singletonList(info)));
            projEstablishBaseInfoLibService.updateById(projEstablishBaseInfoLib);
        }
    }
}

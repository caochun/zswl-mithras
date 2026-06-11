package cn.zswltech.mithras.customer.application.lib.client.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.client.clientversion.ClientVersionListRSP;
import cn.zswltech.mithras.dto.version.CommonVersionDiffBO;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.customer.enums.CorpAddressType;
import cn.zswltech.mithras.customer.enums.InfoModule;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.mapper.corp.CorpAddressInfoMapper;
import cn.zswltech.mithras.foundation.persistence.dto.ChangeDTO;
import cn.zswltech.mithras.foundation.persistence.model.CommonVersion;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.customer.model.client.ClientBaseModel;
import cn.zswltech.mithras.customer.model.client.CorpAddressInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.version.CommonVersionService;
import cn.zswltech.mithras.customer.application.lib.client.handler.ClientLibAbstractHandler;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Service
public class ClientVersionServiceImpl extends CommonVersionService<Client> {
    private static final String DEFAULT_USER_NAME = "未知用户";

    @Resource
    private List<ClientLibAbstractHandler> libHandlerList;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private CorpAddressInfoMapper addressInfoMapper;

    @Override
    public void customFlushData(Client client, String version, boolean needClearLastFlag, Integer versionType) {
        throw new MithrasException("暂未支持的功能");
    }

    @Override
    public void customFlushData(Client client, String version, boolean needClearLastFlag, Integer versionType, Map<String, Object> extraMap) {
        // 处理抄表逻辑
        for (ClientLibAbstractHandler libHandler : libHandlerList) {
            libHandler.flushData(version, client, needClearLastFlag, versionType, extraMap);
        }
        // 设置最新的版本
        if (Objects.equals(VersionTypeConstants.NORMAL, versionType)) {
            client.setNewestVersion(version);
            if (!client.isArtificialProvince()) {
                client.setProvinceOfAffiliation(getNewstProvince(client.getId()));
            }
            clientMapper.updateById(client);
        }
    }

    private String getNewstProvince(Long clientId) {
        List<CorpAddressInfo> addressInfos = addressInfoMapper.selectList(Wrappers.<CorpAddressInfo>lambdaQuery()
                .eq(ClientBaseModel::getClientId, clientId));
        Map<String, List<CorpAddressInfo>> addressMap = addressInfos.stream().collect(Collectors.groupingBy(CorpAddressInfo::getAddressType));
        String province = null;
        if (addressMap.containsKey(CorpAddressType.WORK_ADDRESS.name())) {
            for (CorpAddressInfo workAddress : addressMap.get(CorpAddressType.WORK_ADDRESS.name())) {
                province = workAddress.getProvince();
                if (province != null) {
                    break;
                }
            }
        }
        if (province == null) {
            if (addressMap.containsKey(CorpAddressType.REGISTRY_ADDRESS.name())) {
                CorpAddressInfo registryAddress = addressMap.get(CorpAddressType.REGISTRY_ADDRESS.name()).get(0);
                province = registryAddress.getProvince();
            }
        }
        return province;
    }

    @Override
    public ChangeDTO checkActualChange(Long mainId) {
        ChangeDTO changeDTO = new ChangeDTO();
        Client client = clientMapper.selectById(mainId);
        if (Objects.isNull(client)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        CommonVersion newestVersion = findNewestVersion(mainId);
        if (Objects.isNull(newestVersion)) {
            // 不存在版本 则判为可以新增版本
            changeDTO.setChangeFlag(true);
            changeDTO.setNeedApprovalChangeFlag(true);
            return changeDTO;
        }
        for (ClientLibAbstractHandler libHandler : libHandlerList) {
            if (libHandler.needHandle(mainId, ClientType.of(client.getClientType()))) {
                ChangeDTO moduleChangeDTO = libHandler.checkActualChange(newestVersion);
                if (Boolean.TRUE.equals(moduleChangeDTO.getNeedApprovalChangeFlag())) {
                    // 快速返回
                    changeDTO.setChangeFlag(true);
                    changeDTO.setNeedApprovalChangeFlag(true);
                    return changeDTO;
                } else if (Boolean.TRUE.equals(moduleChangeDTO.getChangeFlag())) {
                    // 还需要找到最坏情况
                    changeDTO.setChangeFlag(true);
                }
            }
        }
        return changeDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void customReset(Client client, CommonVersion commonVersion) {
        // 处理抄表逻辑
        for (ClientLibAbstractHandler libHandler : libHandlerList) {
            // 文件记录表不回退，现有多用户文件在同一个表中，回退会有问题
            if (libHandler.getSubModule() == InfoModule.CORP_MATERIALS_LIST) {
                continue;
            }
            if (libHandler.getSubModule() == InfoModule.NORMAL_MATERIALS_LIST) {
                continue;
            }
            libHandler.reset(client, commonVersion.getVersion());
        }
    }

    @Override
    public void validateData(Long mainId) {
        Client client = clientMapper.selectById(mainId);
        if (Objects.isNull(client)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        for (ClientLibAbstractHandler libHandler : libHandlerList) {
            libHandler.validateData(client);
        }
    }

    @Override
    public CommonVersionDiffRSP doCompare(CommonVersion newVersion, CommonVersion oldVersion) {
        Client client = clientMapper.selectById(newVersion.getMainId());
        if (Objects.isNull(client)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        CommonVersionDiffRSP versionDiffRSP = new CommonVersionDiffRSP();
        Map<String, List> oldData = new HashMap<>();
        Map<String, List<Map<String, DiffValue>>> newData = new HashMap<>();
        Map<String, Boolean> moduleChanged = new HashMap<>();
        for (ClientLibAbstractHandler libHandler : libHandlerList) {
            if (libHandler.needHandle(client.getId(), ClientType.of(client.getClientType()))) {
                CommonVersionDiffBO commonVersionDiffBO = libHandler.libCompareLib(newVersion, oldVersion);
                oldData.put(libHandler.getSubModule().name(), commonVersionDiffBO.getBeforeData());
                newData.put(libHandler.getSubModule().name(), commonVersionDiffBO.getAfterData());
                moduleChanged.put(libHandler.getSubModule().name(), commonVersionDiffBO.getModuleChanged());
            }
        }
        versionDiffRSP.setOldData(oldData);
        versionDiffRSP.setNewData(newData);
        versionDiffRSP.setModuleChanged(moduleChanged);
        return versionDiffRSP;
    }

    @Override
    protected CommonVersionListRSP convertPageRsp(CommonVersion cv, Client client, Map<Long, String> userNameMap) {
        ClientVersionListRSP rsp = BeanUtil.copyProperties(cv, ClientVersionListRSP.class);
        rsp.setClientName(client.getClientName());
        rsp.setGmtModify(cv.getUpdateTime());
        rsp.setOperatorId(cv.getUpdateBy());
        rsp.setOperatorName(Optional.ofNullable(userNameMap.get(cv.getUpdateBy()))
                .orElse(DEFAULT_USER_NAME));
        return rsp;
    }

    @Override
    protected String getBusinessModuleName() {
        return "CLIENT";
    }
}

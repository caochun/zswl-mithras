package cn.zswltech.mithras.service.service.lib.contract;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.projreview.ProjReviewVersionListRSP;
import cn.zswltech.mithras.dto.version.*;
import cn.zswltech.mithras.service.constant.MithrasConstants;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.service.enums.contract.ContractLibModelEnum;
import cn.zswltech.mithras.service.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.dto.ChangeDTO;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.lib.CommonVersionService;
import cn.zswltech.mithras.service.service.lib.contract.handler.AbstractContractAccountLibHandler;
import cn.zswltech.mithras.service.service.lib.contract.handler.ContractLibAbstractHandler;
import cn.zswltech.mithras.service.service.lib.contract.handler.impl.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * @description
 * @since
 */
@Service
public class ContractVersionService extends CommonVersionService<ContractBaseInfo> {
    @Autowired
    private List<ContractLibAbstractHandler> libHandlerList;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;

    private static final String RECEIPT_ID = "receiptId";

    private static final String ESTIMATED_LEASE_DATE = "estimatedLeaseDate";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void customFlushData(ContractBaseInfo baseInfo, String version, boolean needClearLastFlag, Integer versionType) {
        // 处理抄表逻辑
        for (ContractLibAbstractHandler libHandler : libHandlerList) {
            if (libHandler instanceof AbstractContractAccountLibHandler) {
                boolean hit = this.isTargetLibHandler(libHandler, baseInfo.getBizType());
                if (!hit) {
                    continue;
                }
            }
            libHandler.flushData(version, baseInfo.getId(), needClearLastFlag, versionType);
        }
    }

    @Override
    public ChangeDTO checkActualChange(Long mainId) {
        ChangeDTO changeDTO = new ChangeDTO();
        ContractBaseInfo baseInfo = contractBaseInfoMapper.selectById(mainId);
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        CommonVersion newestVersion = findNewestVersion(mainId);
        if (Objects.isNull(newestVersion)) {
            // 不存在版本 则判为可以新增版本
            changeDTO.setChangeFlag(true);
            changeDTO.setNeedApprovalChangeFlag(true);
            return changeDTO;
        }
        for (ContractLibAbstractHandler libHandler : libHandlerList) {
            if (libHandler.needHandle(mainId)) {
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
    public void customReset(ContractBaseInfo baseInfo, CommonVersion commonVersion) {
        // 处理抄表逻辑
        for (ContractLibAbstractHandler libHandler : libHandlerList) {
            if (libHandler instanceof AbstractContractAccountLibHandler) {
                boolean hit = this.isTargetLibHandler(libHandler, baseInfo.getBizType());
                if (!hit) {
                    continue;
                }
            }
            libHandler.reset(baseInfo.getId(), commonVersion.getVersion());
        }
    }


    @Override
    public CommonVersionDiffRSP comparePreVersion(Long id) {
        CommonVersionDiffRSP commonVersionDiffRSP = super.comparePreVersion(id);
        commonVersionHandle(commonVersionDiffRSP);
        return commonVersionDiffRSP;
    }

    private void commonVersionHandle(CommonVersionDiffRSP commonVersionDiffRSP){
        Map<String, List<Map<String, DiffValue>>> newData = commonVersionDiffRSP.getNewData();
        // TODO 处理租赁物清单

        //处理实际租金表
        List<Map<String, DiffValue>> newActual = newData.get(ContractLibModelEnum.ACTUAL_ESTIMATE.name());
        List<Map<String, DiffValue>> newItem = newData.get(ContractLibModelEnum.ACTUAL_ESTIMATE_ITEM.name());
        Map<String, List<Map<String, DiffValue>>> itemMap = new HashMap<>();
        newItem.forEach(map -> {
            DiffValue receiptId = map.get(RECEIPT_ID);
            if(itemMap.containsKey(String.valueOf(receiptId.getValue()))){
                List<Map<String, DiffValue>> maps = itemMap.get(String.valueOf(receiptId.getValue()));
                maps.add(map);
            }else {
                List<Map<String, DiffValue>> diffMaps = new ArrayList<>();
                diffMaps.add(map);
                itemMap.put(String.valueOf(receiptId.getValue()), diffMaps);
            }

        });
        newActual.forEach(res -> {
            DiffValue id = res.get("id");
            List<Map<String, DiffValue>> maps = itemMap.get(String.valueOf(id.getValue()));
            if(ObjectUtil.isNotEmpty(maps)){
                DiffValueList diffValueList = new DiffValueList();
                diffValueList.setLsitMap(maps);
                res.put("rentActualList", diffValueList);
            }
        });
        commonVersionDiffRSP.getOldData().remove(ContractLibModelEnum.ACTUAL_ESTIMATE_ITEM.name());
        commonVersionDiffRSP.getNewData().remove(ContractLibModelEnum.ACTUAL_ESTIMATE_ITEM.name());
        Map<String, Boolean> moduleChanged = commonVersionDiffRSP.getModuleChanged();
        if(!moduleChanged.get(ContractLibModelEnum.ACTUAL_ESTIMATE.name())){
            moduleChanged.put(ContractLibModelEnum.ACTUAL_ESTIMATE.name(), moduleChanged.get(ContractLibModelEnum.ACTUAL_ESTIMATE_ITEM.name()));
        }
        //处理概算租金表
        List oldEstimate = commonVersionDiffRSP.getOldData().get(ContractLibModelEnum.RENT_ESTIMATE.name());
        List<Map<String, DiffValue>> newEstimate = newData.get(ContractLibModelEnum.RENT_ESTIMATE.name());
        List<Map<String, DiffValue>> newBaseInfo = newData.get(ContractLibModelEnum.BASE_INFO.name());
        newBaseInfo.forEach(map -> {
            if(ObjectUtil.isNull(oldEstimate)){
                return;
            }
            Map<String, DiffValue> baseMap = new HashMap<>();
            Map<String, Object> oldMap = new HashMap<>();
            DiffValue estimatedLeaseDate = map.get(ESTIMATED_LEASE_DATE);
            DiffValueList diffValueList = new DiffValueList();
            if(ObjectUtil.isNotEmpty(estimatedLeaseDate)){
                diffValueList.setBeforeValue(estimatedLeaseDate.getBeforeValue());
                diffValueList.setValue(estimatedLeaseDate.getValue());
                diffValueList.setIsChange(estimatedLeaseDate.getIsChange());
            }
            oldMap.put(ESTIMATED_LEASE_DATE, diffValueList.getBeforeValue());
            oldEstimate.add(0, oldMap);
            diffValueList.setLsitMap(new ArrayList<>(newEstimate));
            baseMap.put(ESTIMATED_LEASE_DATE, estimatedLeaseDate);
            baseMap.put("rentEstimate", diffValueList);
            newEstimate.clear();
            newEstimate.add(baseMap);
        });
    }

    @Override
    public CommonVersionDiffRSP doCompare(CommonVersion newVersion, CommonVersion oldVersion) {
        ContractBaseInfo baseInfo = contractBaseInfoMapper.selectById(newVersion.getMainId());
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        CommonVersionDiffRSP versionDiffRSP = new CommonVersionDiffRSP();
        Map<String, List> oldData = new HashMap<>();
        Map<String, List<Map<String, DiffValue>>> newData = new HashMap<>();
        Map<String, Boolean> moduleChanged = new HashMap<>();
        for (ContractLibAbstractHandler libHandler : libHandlerList) {
            // 根据业务类型剔除一些不需要的handler
            if (libHandler instanceof AbstractContractAccountLibHandler) {
                boolean hit = this.isTargetLibHandler(libHandler, baseInfo.getBizType());
                if (!hit) {
                    continue;
                }
            }
            if (libHandler.needHandle(baseInfo.getId())) {
                CommonVersionDiffBO commonVersionDiffBO = libHandler.libCompareLib(newVersion, oldVersion);
                if(ObjectUtil.isEmpty(commonVersionDiffBO) || ObjectUtil.isEmpty(libHandler.getSubModule())){
                    //为空不比对
                    continue;
                }
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
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.CONTRACT;
    }

    @Override
    protected CommonVersionListRSP convertPageRsp(CommonVersion cv, ContractBaseInfo contractBaseInfo, Map<Long, String> userNameMap) {
        ProjReviewVersionListRSP rsp = BeanUtil.copyProperties(cv, ProjReviewVersionListRSP.class);
        rsp.setGmtModify(cv.getUpdateTime());
        rsp.setOperatorId(cv.getUpdateBy());
        rsp.setOperatorName(Optional.ofNullable(userNameMap.get(cv.getUpdateBy())).orElse(MithrasConstants.DEFAULT_USER_NAME));
        rsp.setProjName(contractBaseInfo.getContractCode());
        return rsp;
    }

    private boolean isTargetLibHandler(ContractLibAbstractHandler<?, ?, ?> libHandler, String bizType) {
        ProjectBizType projectBizType = ProjectBizType.of(bizType);
        switch (projectBizType) {
            case ZL: {
                return libHandler instanceof ContractAccountZLSKLibHandler;
            }
            case ZZ: {
                return libHandler instanceof ContractAccountZZSKLibHandler;
            }
            case BL: {
                return libHandler instanceof ContractAccountBLHKLibHandler || libHandler instanceof ContractAccountBLSKLibHandler;
            }
            case ZR: {
                return libHandler instanceof ContractAccountZRHKLibHandler || libHandler instanceof ContractAccountZRSKLibHandler;
            }
            default: {
                throw new MithrasException("未知的合同业务类型");
            }
        }
    }
}

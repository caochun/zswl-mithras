package cn.zswltech.mithras.service.service.lib.payment.libservice.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.api.payment.version.PaymentVersionListRSP;
import cn.zswltech.mithras.dto.version.*;
import cn.zswltech.mithras.service.constant.MithrasConstants;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.dto.ChangeDTO;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfoLib;
import cn.zswltech.mithras.service.mapper.payment.PaymentBaseInfoMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.lib.CommonVersionService;
import cn.zswltech.mithras.service.service.lib.payment.handler.PaymentAbstractHandler;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/19 18:01
 */
@Service
public class PaymentVersionServiceImpl extends CommonVersionService<PaymentBaseInfo> {
    @Resource
    private List<PaymentAbstractHandler> libHandlerList;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private PaymentBaseInfoLibServiceImpl baseInfoLibService;

    @Override
    public void customFlushData(PaymentBaseInfo paymentBaseInfo, String version, boolean needClearLastFlag, Integer versionType) {
        // 处理抄表逻辑
        for (PaymentAbstractHandler libHandler : libHandlerList) {
            libHandler.flushData(version, paymentBaseInfo.getId(), needClearLastFlag, versionType);
        }
    }

    @Override
    public ChangeDTO checkActualChange(Long mainId) {
        ChangeDTO changeDTO = new ChangeDTO();
        PaymentBaseInfo baseInfo = paymentBaseInfoMapper.selectById(mainId);
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
        for (PaymentAbstractHandler libHandler : libHandlerList) {
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
    public void customReset(PaymentBaseInfo paymentBaseInfo, CommonVersion commonVersion) {
        // 处理抄表逻辑
        for (PaymentAbstractHandler libHandler : libHandlerList) {
            libHandler.reset(paymentBaseInfo.getId(), commonVersion.getVersion());
        }
    }

    @Override
    public CommonVersionDiffRSP doCompare(CommonVersion newVersion, CommonVersion oldVersion) {
        PaymentBaseInfo baseInfo = paymentBaseInfoMapper.selectById(newVersion.getMainId());
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        CommonVersionDiffRSP versionDiffRSP = new CommonVersionDiffRSP();
        Map<String, List> oldData = new HashMap<>();
        Map<String, List<Map<String, DiffValue>>> newData = new HashMap<>();
        Map<String, Boolean> moduleChanged = new HashMap<>();
        for (PaymentAbstractHandler libHandler : libHandlerList) {
            if (libHandler.needHandle(baseInfo.getId())) {
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
    protected CommonVersionListRSP convertPageRsp(CommonVersion cv, PaymentBaseInfo baseModel, Map<Long, String> userNameMap) {
        PaymentVersionListRSP rsp = BeanUtil.copyProperties(cv, PaymentVersionListRSP.class);
        PaymentBaseInfoLib one = baseInfoLibService.getOne(Wrappers.<PaymentBaseInfoLib>lambdaQuery()
                .eq(PaymentBaseInfoLib::getVersion, rsp.getVersion())
                .eq(PaymentBaseInfoLib::getOriginId, rsp.getMainId()));
        rsp.setApplyPaymentAmount(Optional.ofNullable(one).map(PaymentBaseInfoLib::getApplyPaymentAmount).orElse(null));
        rsp.setGmtModify(cv.getUpdateTime());
        rsp.setOperatorId(cv.getUpdateBy());
        rsp.setOperatorName(Optional.ofNullable(userNameMap.get(cv.getUpdateBy())).orElse(MithrasConstants.DEFAULT_USER_NAME));
        return rsp;
    }

    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.PAYMENT;
    }
}

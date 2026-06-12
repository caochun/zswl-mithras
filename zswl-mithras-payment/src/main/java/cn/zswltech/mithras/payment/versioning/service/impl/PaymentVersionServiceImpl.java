package cn.zswltech.mithras.payment.versioning.service.impl;

import cn.zswltech.mithras.api.payment.version.PaymentVersionListRSP;
import cn.zswltech.mithras.dto.version.CommonVersionDiffBO;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.payment.mapper.lib.PaymentBaseInfoLibMapper;
import cn.zswltech.mithras.foundation.persistence.dto.ChangeDTO;
import cn.zswltech.mithras.foundation.persistence.model.CommonVersion;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import cn.zswltech.mithras.payment.model.PaymentBaseInfoLib;
import cn.zswltech.mithras.payment.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.version.CommonVersionService;
import cn.zswltech.mithras.payment.versioning.handler.PaymentAbstractHandler;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/19 18:01
 */
@Service
public class PaymentVersionServiceImpl extends CommonVersionService<PaymentBaseInfo> {
    private static final String DEFAULT_USER_NAME = "未知用户";

    @Resource
    private List<PaymentAbstractHandler> libHandlerList;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private PaymentBaseInfoLibMapper paymentBaseInfoLibMapper;

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
        PaymentVersionListRSP rsp = new PaymentVersionListRSP();
        rsp.setId(cv.getId());
        rsp.setMainId(cv.getMainId());
        rsp.setVersion(cv.getVersion());
        rsp.setType(cv.getType());
        rsp.setModule(cv.getModule());
        rsp.setCreateTime(cv.getCreateTime());
        rsp.setCreateBy(cv.getCreateBy());
        rsp.setUpdateTime(cv.getUpdateTime());
        rsp.setUpdateBy(cv.getUpdateBy());
        PaymentBaseInfoLib one = paymentBaseInfoLibMapper.selectOne(Wrappers.<PaymentBaseInfoLib>lambdaQuery()
                .eq(PaymentBaseInfoLib::getVersion, cv.getVersion())
                .eq(PaymentBaseInfoLib::getOriginId, cv.getMainId())
                .last("LIMIT 1"));
        rsp.setApplyPaymentAmount(Optional.ofNullable(one).map(PaymentBaseInfoLib::getApplyPaymentAmount).orElse(null));
        rsp.setGmtModify(cv.getUpdateTime());
        rsp.setOperatorId(cv.getUpdateBy());
        rsp.setOperatorName(Optional.ofNullable(userNameMap.get(cv.getUpdateBy())).orElse(DEFAULT_USER_NAME));
        return rsp;
    }

    @Override
    protected String getBusinessModuleName() {
        return "PAYMENT";
    }
}

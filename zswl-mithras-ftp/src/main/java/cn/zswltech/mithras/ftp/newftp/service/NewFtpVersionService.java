package cn.zswltech.mithras.ftp.newftp.service;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.dto.newftp.NewFtpDetailReq;
import cn.zswltech.mithras.dto.newftp.NewFtpVersionListRsp;
import cn.zswltech.mithras.dto.version.CommonVersionDiffBO;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.ftp.newftp.enums.NewFtpBusinessModule;
import cn.zswltech.mithras.foundation.enums.VersionTypeEnum;
import cn.zswltech.mithras.foundation.persistence.dto.ChangeDTO;
import cn.zswltech.mithras.foundation.persistence.model.CommonVersion;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.version.CommonVersionService;
import cn.zswltech.mithras.ftp.newftp.controller.NewFtpBaseInfoController;
import cn.zswltech.mithras.ftp.newftp.fms.DefaultNewFtpStateMachine;
import cn.zswltech.mithras.ftp.newftp.fms.NewFtpContext;
import cn.zswltech.mithras.ftp.newftp.fms.NewFtpEvent;
import cn.zswltech.mithras.ftp.newftp.lib.NewFtpLibAbstractHandler;
import cn.zswltech.mithras.ftp.newftp.lib.impl.NewFtpMonthlyGuidanceLibHandler;
import cn.zswltech.mithras.ftp.newftp.lib.impl.NewFtpQuarterlyBasePricingLibHandler;
import cn.zswltech.mithras.ftp.newftp.model.NewFtpBaseInfo;
import cn.zswltech.mithras.basedata.util.DateUtil;
import cn.zswltech.mithras.ftp.newftp.service.port.NewFtpWorkflowPort;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

import static cn.zswltech.mithras.ftp.newftp.enums.NewFtpBusinessModule.NEW_FTP_GUIDANCE;
import static cn.zswltech.mithras.foundation.enums.common.RecordStatus.NEW;
import static cn.zswltech.mithras.foundation.enums.common.RecordStatus.TAKE_EFFECT;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/5/24 10:37
 */
@Service
public class NewFtpVersionService extends CommonVersionService<NewFtpBaseInfo> {
    @Resource
    private List<NewFtpLibAbstractHandler> libHandlerList;
    @Resource
    private NewFtpBaseInfoService baseInfoService;
    @Resource
    private DefaultNewFtpStateMachine defaultNewFtpStateMachine;
    @Resource
    private NewFtpWorkflowPort newFtpWorkflowPort;


    @Override
    public void customFlushData(NewFtpBaseInfo baseInfo, String version, boolean needClearLastFlag, Integer versionType) {
        // 处理抄表逻辑
        for (NewFtpLibAbstractHandler libHandler : libHandlerList) {
            if (libHandler.needHandle(baseInfo.getMainId())) {
                libHandler.flushData(version, baseInfo, needClearLastFlag, versionType);
            }
        }
        // 设置最新的版本
        if (Objects.equals(VersionTypeConstants.NORMAL, versionType)) {
            baseInfo.setNewestVersion(version);
            baseInfoService.updateById(baseInfo);
        }
    }

    @Override
    public void customReset(NewFtpBaseInfo newFtp, CommonVersion commonVersion) {
        // 处理抄表逻辑
        for (NewFtpLibAbstractHandler libHandler : libHandlerList) {
            if (libHandler.needHandle(newFtp.getMainId())) {
                libHandler.reset(newFtp, commonVersion.getVersion());
            }
        }
    }

    @Resource
    private NewFtpBaseInfoController baseInfoController;

    @Override
    public ChangeDTO checkActualChange(Long mainId) {
        ChangeDTO changeDTO = new ChangeDTO();
        NewFtpBaseInfo baseInfo = baseInfoService.getById(mainId);


        String versionCode = baseInfo.getNewestVersion();
        if (Objects.isNull(versionCode)) {
            // 不存在版本 则判为可以新增版本
            changeDTO.setChangeFlag(true);
            changeDTO.setNeedApprovalChangeFlag(true);
            return changeDTO;
        }
        CommonVersion newestVersion = commonVersionMapper.selectOne(Wrappers.<CommonVersion>lambdaQuery()
                .eq(CommonVersion::getVersion, versionCode)
                .eq(CommonVersion::getModule, NEW_FTP_GUIDANCE.name())
                .eq(CommonVersion::getMainId, mainId).last("limit 1"));

        // 时间紧任务重，先干上去，后面有机会再优化
        NewFtpDetailReq newFtpDetailReq = new NewFtpDetailReq();
        newFtpDetailReq.setMainId(mainId);
        Map<String, DiffValue> monthlyMap = baseInfoController.compareMonthly(newFtpDetailReq).getData();
        monthlyMap.forEach((k, v) -> {
            v.getDiffValueList().forEach(diffValue -> {
                if (diffValue.getIsChange()) {
                    changeDTO.setChangeFlag(true);
                }
            });
        });
        if (changeDTO.getChangeFlag()) {
            return changeDTO;
        }
        if (DateUtil.isQuarterStart(baseInfo.getMonth())) {
            Map<String, DiffValue> quarterlyMap = baseInfoController.compareQuarterly(newFtpDetailReq).getData();

            quarterlyMap.forEach((k, v) -> {
                v.getDiffValueList().forEach(diffValue -> {
                    if (diffValue.getIsChange()) {
                        changeDTO.setChangeFlag(true);
                    }
                });
            });
            if (changeDTO.getChangeFlag()) {
                return changeDTO;
            }
        }

        for (NewFtpLibAbstractHandler libHandler : libHandlerList) {
            if (!(libHandler instanceof NewFtpMonthlyGuidanceLibHandler
                    || libHandler instanceof NewFtpQuarterlyBasePricingLibHandler)) {
                ChangeDTO moduleChangeDTO = libHandler.checkActualChange(newestVersion);
                if (Boolean.TRUE.equals(moduleChangeDTO.getChangeFlag())) {
                    // 还需要找到最坏情况
                    changeDTO.setChangeFlag(true);
                    break;
                }
            }
        }
        return changeDTO;
    }

    @Override
    public CommonVersionDiffRSP doCompare(CommonVersion newVersion, CommonVersion oldVersion) {
        NewFtpBaseInfo baseInfo = baseInfoService.getById(newVersion.getMainId());
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        CommonVersionDiffRSP versionDiffRSP = new CommonVersionDiffRSP();
        Map<String, List> oldData = new HashMap<>();
        Map<String, List<Map<String, DiffValue>>> newData = new HashMap<>();
        Map<String, Boolean> moduleChanged = new HashMap<>();
        for (NewFtpLibAbstractHandler libHandler : libHandlerList) {
            if (libHandler.needHandle(baseInfo.getMainId())) {
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
    protected CommonVersionListRSP convertPageRsp(CommonVersion cv, NewFtpBaseInfo baseModel, Map<Long, String> userNameMap) {
        NewFtpVersionListRsp rsp = BeanUtil.copyProperties(cv, NewFtpVersionListRsp.class);
        rsp.setMonth(baseModel.getMonth());
        return rsp;
    }

    @Override
    public NewFtpBusinessModule getBusinessModule() {
        return NEW_FTP_GUIDANCE;
    }

    public void submit(Long mainId) {
        NewFtpBaseInfo baseInfo = baseInfoService.getById(mainId);
        if (Objects.nonNull(baseInfoService.findRelatedProcess(mainId))) {
            throw new AuthCheckException("该数据处于流程中，不允许提交");
        }
        // 判断是否有变动
        ChangeDTO changeDTO = checkActualChange(mainId);
        if (!changeDTO.getChangeFlag()) {
            throw new MithrasException("编辑区未发生变动，无需提交审批");
        }
        // 判断使用创建流程还是修改流程
        if (NEW.name().equals(baseInfo.getFtpRecordStatus())) {
            newFtpWorkflowPort.startGuidanceCreateFlow(mainId, baseInfo.getMonth());
        } else {
            newFtpWorkflowPort.startGuidanceModifyFlow(mainId, baseInfo.getMonth());
        }
        defaultNewFtpStateMachine.execute(NewFtpContext.of(baseInfo, NewFtpEvent.SUBMIT_APPROVAL, baseInfo.getProcessStatus()));
    }

    @Transactional(rollbackFor = Throwable.class)
    public void processEnd(Long id, boolean processPass, boolean processCancel, Long startUserId, String processInstanceId) {
        NewFtpBaseInfo baseInfo = baseInfoService.getById(id);
        // 修改状态
        if (processPass) {
            // 审批通过 新增版本
            defaultNewFtpStateMachine.execute(NewFtpContext.of(baseInfo, NewFtpEvent.APPROVAL_PASS, baseInfo.getProcessStatus()));
        } else {
            if (processCancel) {
                if (TAKE_EFFECT.name().equals(baseInfo.getFtpRecordStatus())) {
                    defaultNewFtpStateMachine.execute(NewFtpContext.of(baseInfo, NewFtpEvent.MODIFY_WITHDRAW, baseInfo.getProcessStatus()));
                } else {
                    defaultNewFtpStateMachine.execute(NewFtpContext.of(baseInfo, NewFtpEvent.NEW_WITHDRAW, baseInfo.getProcessStatus()));
                }
            }
        }
        // 记录版本
        int versionType = processPass ? VersionTypeConstants.NORMAL : VersionTypeConstants.INVALID;
        recordVersion(id, VersionTypeEnum.APPROVAL, startUserId, processInstanceId, versionType);
        if(processPass){
            //将生效的配置抄送到配置区
            baseInfoService.copyConfig();
        }

        if (!processPass && processCancel) {
            reset(id);
        }
    }

}

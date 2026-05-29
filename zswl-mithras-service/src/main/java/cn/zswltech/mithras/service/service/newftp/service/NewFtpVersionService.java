package cn.zswltech.mithras.service.service.newftp.service;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.dao.OrgDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.dto.newftp.NewFtpDetailReq;
import cn.zswltech.mithras.dto.newftp.NewFtpVersionListRsp;
import cn.zswltech.mithras.dto.version.CommonVersionDiffBO;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.common.constant.ResultMsg;
import cn.zswltech.mithras.common.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.VersionTypeEnum;
import cn.zswltech.mithras.service.mapper.dto.ChangeDTO;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.lib.CommonVersionService;
import cn.zswltech.mithras.service.service.newftp.controller.NewFtpBaseInfoController;
import cn.zswltech.mithras.service.service.newftp.fms.DefaultNewFtpStateMachine;
import cn.zswltech.mithras.service.service.newftp.fms.NewFtpContext;
import cn.zswltech.mithras.service.service.newftp.fms.NewFtpEvent;
import cn.zswltech.mithras.service.service.newftp.lib.NewFtpLibAbstractHandler;
import cn.zswltech.mithras.service.service.newftp.lib.impl.NewFtpMonthlyGuidanceLibHandler;
import cn.zswltech.mithras.service.service.newftp.lib.impl.NewFtpQuarterlyBasePricingLibHandler;
import cn.zswltech.mithras.service.service.newftp.model.NewFtpBaseInfo;
import cn.zswltech.mithras.service.util.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;

import static cn.zswltech.mithras.service.enums.BusinessModuleEnum.NEW_FTP_GUIDANCE;
import static cn.zswltech.mithras.common.enums.RecordStatus.NEW;
import static cn.zswltech.mithras.common.enums.RecordStatus.TAKE_EFFECT;

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
    private OrgDOMapper orgDOMapper;
    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private DefaultNewFtpStateMachine defaultNewFtpStateMachine;


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
    public BusinessModuleEnum getBusinessModule() {
        return NEW_FTP_GUIDANCE;
    }

    public void submit(Long mainId) {
        NewFtpBaseInfo baseInfo = baseInfoService.getById(mainId);
        ProcessResp processResp = baseInfoService.findRelatedProcess(mainId);
        if (Objects.nonNull(processResp)) {
            throw new AuthCheckException("该数据处于流程中，不允许提交");
        }
        StartProcessReq startProcessReq = new StartProcessReq();
        // 判断是否有变动
        ChangeDTO changeDTO = checkActualChange(mainId);
        if (!changeDTO.getChangeFlag()) {
            throw new MithrasException("编辑区未发生变动，无需提交审批");
        }
        // 判断使用创建流程还是修改流程
        if (NEW.name().equals(baseInfo.getFtpRecordStatus())) {
            startProcessReq.setModelKey(ProcessModelTypeEnum.FtpMonthlyGuidanceCreateFlow.name());
        } else {
            startProcessReq.setModelKey(ProcessModelTypeEnum.FtpMonthlyGuidanceModifyFlow.name());
        }
        startProcessReq.setStartUserId(Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .map(String::valueOf)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN)));
        startProcessReq.setBusinessKey(String.valueOf(mainId));
        LocalDate month = baseInfo.getMonth();
        startProcessReq.setProcessInstanceName(month.getYear() + "年" + month.getMonthValue() + "月FTP定价指导审批流程");
        OrgDO jhcwb = orgDOMapper.queryByCode("JHCWB");
        startProcessReq.setStartUserDeptId(Optional.ofNullable(jhcwb).map(OrgDO::getId)
                .map(String::valueOf).orElse(null));
        processApiService.start(startProcessReq);
        defaultNewFtpStateMachine.execute(NewFtpContext.of(baseInfo, NewFtpEvent.SUBMIT_APPROVAL, baseInfo.getProcessStatus()));
    }

    @Transactional(rollbackFor = Throwable.class)
    public void processEnd(Long id, Integer endType, Long startUserId, String processInstanceId) {
        boolean processPass = ProcessBusinessStatusEnum.success(endType);
        NewFtpBaseInfo baseInfo = baseInfoService.getById(id);
        // 修改状态
        if (processPass) {
            // 审批通过 新增版本
            defaultNewFtpStateMachine.execute(NewFtpContext.of(baseInfo, NewFtpEvent.APPROVAL_PASS, baseInfo.getProcessStatus()));
        } else {
            if (ProcessBusinessStatusEnum.CANCEL.getType().equals(endType)) {
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

        if (!processPass && ProcessBusinessStatusEnum.CANCEL.getType().equals(endType)) {
            reset(id);
        }
    }

}

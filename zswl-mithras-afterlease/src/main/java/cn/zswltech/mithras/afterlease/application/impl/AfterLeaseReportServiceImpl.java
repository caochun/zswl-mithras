package cn.zswltech.mithras.afterlease.application.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.afterlease.application.AfterLeaseMaterialSnapshot;
import cn.zswltech.mithras.afterlease.application.AfterLeaseMaterialsPort;
import cn.zswltech.mithras.afterlease.application.AfterLeaseModifyAuthPort;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseReportListREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseReportUploadREQ;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.afterlease.enums.AfterLeaseAdjustMaterialsEnum;
import cn.zswltech.mithras.foundation.enums.common.RecordStatus;
import cn.zswltech.mithras.afterlease.mapper.AfterLeaseAdjustInfoMapper;
import cn.zswltech.mithras.afterlease.model.AfterLeaseAdjustInfo;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.afterlease.application.AfterLeaseAdjustInfoService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseReportService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.OutputStream;
import java.util.*;

/**
 * @ClassName AfterLeaseReportServiceImpl
 * @Author jackerhe
 * @Date 2022/11/11 11:45 上午
 * @Version 1.0
 **/
@Service
public class AfterLeaseReportServiceImpl implements AfterLeaseReportService {

    private static final String BUSINESS_MODULE_ADJUST = "ADJUST";

    @Resource
    private AfterLeaseAdjustInfoService adjustInfoService;
    @Resource
    private AfterLeaseMaterialsPort afterLeaseMaterialsPort;
    @Resource
    private AfterLeaseModifyAuthPort afterLeaseModifyAuthPort;

    @Override
    @Deprecated
    public void upload(MultipartFile multipartFile, AfterLeaseReportUploadREQ req) {
        Long adjustId = req.getAdjustId();
        AfterLeaseAdjustInfo baseInfo = adjustInfoService.getById(adjustId);
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        // 确定是否为发起人
        if (Objects.equals(AccountUtil.getLoginInfo().getId(), baseInfo.getProjSponsorUserId())) {
            // 只上传尽调报告
            if(!AfterLeaseAdjustMaterialsEnum.DUE_DILIGENCE_REPORT.name().equals(req.getMaterialsType())){
                throw new MithrasException("发起人只允许上传业务申请资料");
            }
            afterLeaseMaterialsPort.add(multipartFile, adjustId, req.getMaterialsType(), BUSINESS_MODULE_ADJUST);
        } else {
            throw new AuthCheckException("详情页只允许发起人上传，其他岗位请在审批流中上传相应文件报告");
        }
    }

    @Override
    public void download(OutputStream outputStream, Long materialsId) {
        afterLeaseMaterialsPort.download(outputStream, Collections.singletonList(materialsId));
    }

    @Override
    public List<AfterLeaseMaterialSnapshot> list(AfterLeaseReportListREQ req) {
        List<String> materialsTypes;
        if (ObjectUtil.isEmpty(req.getProcessInstanceId())) {
            // 项目评审详情页可展示所有文件
            materialsTypes = AfterLeaseAdjustMaterialsEnum.listAll();
        }else {
            // 评审流程详情页展示其他文件
            materialsTypes = AfterLeaseAdjustMaterialsEnum.listAdjust();
        }
        return afterLeaseMaterialsPort.list(BUSINESS_MODULE_ADJUST,materialsTypes ,
                Collections.singletonList(req.getAdjustId()));
    }

    @Override
    public void remove(Long materialsId) {
        // 先查询文档信息
        AfterLeaseMaterialSnapshot materialsList = afterLeaseMaterialsPort.getById(materialsId);
        if (!Objects.isNull(materialsList)) {// 查询项目评审主数据
            AfterLeaseAdjustInfo baseInfo = adjustInfoService.getById(materialsList.getBelongId());
            afterLeaseModifyAuthPort.check(BUSINESS_MODULE_ADJUST, AfterLeaseAdjustInfoMapper.class, baseInfo.getId(), null);
            if (!Objects.equals(RecordStatus.CLOSED.name(), baseInfo.getProcessStatus())) {
                afterLeaseMaterialsPort.remove(Collections.singletonList(materialsId));
                return;
            }
            throw new MithrasException("关闭状态不支持删除报告文件");
        } else {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
    }

}

package cn.zswltech.mithras.service.service.afterlese.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseReportListREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseReportUploadREQ;
import cn.zswltech.mithras.service.auth.checker.implnew.CommonModifyMainAuthCheckerNew;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.afterlease.domain.enums.AfterLeaseAdjustMaterialsEnum;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.AfterLeaseAdjustInfoMapper;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.AfterLeaseAdjustInfo;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseAdjustInfoService;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseReportService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
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

    @Resource
    private AfterLeaseAdjustInfoService adjustInfoService;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private CommonModifyMainAuthCheckerNew commonModifyMainAuthChecker;
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
            materialsListService.add(multipartFile, adjustId, req.getMaterialsType(), BusinessModuleEnum.ADJUST.name());
        } else {
            throw new AuthCheckException("详情页只允许发起人上传，其他岗位请在审批流中上传相应文件报告");
        }
    }

    @Override
    public void download(OutputStream outputStream, Long materialsId) {
        materialsListService.download(outputStream, Collections.singletonList(materialsId));
    }

    @Override
    public List<MaterialsList> list(AfterLeaseReportListREQ req) {
        List<String> materialsTypes;
        if (ObjectUtil.isEmpty(req.getProcessInstanceId())) {
            // 项目评审详情页可展示所有文件
            materialsTypes = AfterLeaseAdjustMaterialsEnum.listAll();
        }else {
            // 评审流程详情页展示其他文件
            materialsTypes = AfterLeaseAdjustMaterialsEnum.listAdjust();
        }
        return materialsListService.list(BusinessModuleEnum.ADJUST.name(),materialsTypes ,
                Collections.singletonList(req.getAdjustId()));
    }

    @Override
    public void remove(Long materialsId) {
        // 先查询文档信息
        MaterialsList materialsList = materialsListService.getById(materialsId);
        if (!Objects.isNull(materialsList)) {// 查询项目评审主数据
            AfterLeaseAdjustInfo baseInfo = adjustInfoService.getById(materialsList.getBelongId());
            commonModifyMainAuthChecker.check(BusinessModuleEnum.ADJUST, AfterLeaseAdjustInfoMapper.class, baseInfo.getId(), null);
            if (!Objects.equals(RecordStatus.CLOSED.name(), baseInfo.getProcessStatus())) {
                materialsListService.remove(Collections.singletonList(materialsId));
                return;
            }
            throw new MithrasException("关闭状态不支持删除报告文件");
        } else {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
    }

}

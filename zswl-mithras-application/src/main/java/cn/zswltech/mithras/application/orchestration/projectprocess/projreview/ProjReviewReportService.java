package cn.zswltech.mithras.application.orchestration.projectprocess.projreview;

import cn.hutool.core.io.IoUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.util.Page;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewPriceDetailRSP;
import cn.zswltech.mithras.dto.projreview.report.ProjReviewReportGenerateREQ;
import cn.zswltech.mithras.dto.projreview.report.ProjReviewReportListREQ;
import cn.zswltech.mithras.dto.projreview.report.ProjReviewReportUploadREQ;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjReviewMaterialsEnum;
import cn.zswltech.mithras.application.orchestration.document.gendoc.render.ProjReviewEarningsRateRender;
import cn.zswltech.mithras.application.orchestration.document.gendoc.render.ProjReviewJDReportZLRender;
import cn.zswltech.mithras.document.model.MaterialsList;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.projectprocess.application.bo.ProjReviewRenderBO;
import cn.zswltech.mithras.projectprocess.application.projreview.ProjReviewUpdateAdvice;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.workflow.flow.util.FlowUtil;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.*;

import static cn.zswltech.mithras.foundation.enums.common.RecordStatus.CLOSED;
import static cn.zswltech.mithras.foundation.enums.common.RecordStatus.EXPIRE;

/**
 * @author dingqi
 * @date 2022/8/5
 * @description 项目评审-报告清单相关
 */
@Service
public class ProjReviewReportService implements ProjReviewUpdateAdvice {
    //已经统一维护至ProjEstablishCheckHandler
    private static final Set<String> SPONSOR_COULD_REMOVE_TYPE_SET = new HashSet<>();

    static {
        SPONSOR_COULD_REMOVE_TYPE_SET.add(ProjReviewMaterialsEnum.DUE_DILIGENCE_REPORT.name());
        SPONSOR_COULD_REMOVE_TYPE_SET.add(ProjReviewMaterialsEnum.OTHER.name());
    }

    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;
    @Resource
    private ProjReviewPriceService projReviewPriceService;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private ProjReviewJDReportZLRender projReviewJDReportZLRender;
    @Resource
    private FlowTaskApiService flowTaskApiService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private ProjReviewEarningsRateRender projReviewEarningsRateRender;
    @Resource
    private ProjReviewService projReviewService;

    @Transactional(rollbackFor = Exception.class)
    public Long generate(ProjReviewReportGenerateREQ req) throws Exception {
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoMapper.selectById(req.getProjReviewId());
        if (Objects.isNull(projReviewBaseInfo)) {
            throw new MithrasException("基本信息未保存，请先保存");
        }
        // 确认报价方案和现金流数据都已维护
        ProjReviewPriceDetailRSP priceDetailRSP = projReviewPriceService.detail(projReviewBaseInfo.getId());
        if (Objects.isNull(priceDetailRSP.getAocPriceDetailRSP()) && Objects.isNull(priceDetailRSP.getLeasePriceDetailRSP()) && Objects.isNull(priceDetailRSP.getFactoringPriceDetailRSP())) {
            throw new MithrasException("报价信息未保存，请先保存");
        }
        // 获取类型
        ProjectBizType projectBizType = ProjectBizType.of(projReviewBaseInfo.getBizType());
        if (Objects.isNull(projectBizType)) {
            throw new MithrasException("未定义的业务类型");
        }
        // 获取当前登陆用户
        AccountVO accountVO = AccountUtil.getLoginInfo();
        List<String> jobs = sysUserService.queryUserJobList(accountVO.getId());
        if (CollectionUtils.isEmpty(jobs)) {
            throw new AuthCheckException("登陆用户角色为空");
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        String fileName;
        ProjReviewMaterialsEnum projReviewMaterialsEnum;
        if (jobs.contains(JobEnum.projmanager.name())) {
            if (projectBizType != ProjectBizType.ZL) {
                throw new MithrasException("暂不支持【租赁】类型之外的类型生成报告，请直接手动上传");
            }
            projReviewMaterialsEnum = ProjReviewMaterialsEnum.DUE_DILIGENCE_REPORT;
            fileName = projReviewJDReportZLRender.render(os, new ProjReviewRenderBO(true, projReviewBaseInfo, projReviewMaterialsEnum));
        } else if (jobs.contains(JobEnum.legalmanager.name())) {
            if (projectBizType != ProjectBizType.ZL) {
                throw new MithrasException("暂不支持【租赁】类型之外的类型生成报告，请直接手动上传");
            }
            projReviewMaterialsEnum = ProjReviewMaterialsEnum.LEGAL_COMPLIANCE_REPORT;
            fileName = projReviewJDReportZLRender.render(os, new ProjReviewRenderBO(false, projReviewBaseInfo, projReviewMaterialsEnum));
        } else if (jobs.contains(JobEnum.riskmanager.name())) {
            if (projectBizType != ProjectBizType.ZL) {
                throw new MithrasException("暂不支持【租赁】类型之外的类型生成报告，请直接手动上传");
            }
            projReviewMaterialsEnum = ProjReviewMaterialsEnum.RISK_REVIEW_REPORT;
            fileName = projReviewJDReportZLRender.render(os, new ProjReviewRenderBO(false, projReviewBaseInfo, projReviewMaterialsEnum));
        } else {
            throw new AuthCheckException("只允许项目经理、风控经理、法务经理操作");
        }
        recordStatus(req.getProjReviewId());
        return materialsListService.add(IoUtil.toStream(os.toByteArray()), fileName,
                req.getProjReviewId(), projReviewMaterialsEnum.name(), "TMP");
    }

    @SneakyThrows
    public Long generateEarningsRate(@Valid ProjReviewReportGenerateREQ req){
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoMapper.selectById(req.getProjReviewId());
        ProjReviewMaterialsEnum projReviewMaterialsEnum = ProjReviewMaterialsEnum.YIELD_REVIEW_REPORT;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // 财务主管生成的是项目收益率审查意见书
        String fileName = projReviewEarningsRateRender.render(os, new ProjReviewRenderBO(false, projReviewBaseInfo, projReviewMaterialsEnum));
        return materialsListService.add(IoUtil.toStream(os.toByteArray()), fileName,
                req.getProjReviewId(), projReviewMaterialsEnum.name(), "TMP");
    }

    public void download(OutputStream outputStream, Long materialsId) {
        materialsListService.download(outputStream, Collections.singletonList(materialsId));
    }

    public List<MaterialsList> list(ProjReviewReportListREQ req) {
        List<String> materialsTypes;
        // 项目评审详情页可展示所有文件
        materialsTypes = ProjReviewMaterialsEnum.listAll();

        return materialsListService.list(BusinessModuleEnum.PROJ_REVIEW.name(), materialsTypes,
                Collections.singletonList(req.getProjReviewId()));
    }

    @Transactional(rollbackFor = Exception.class)
    public void remove(Long materialsId) {
        // 先查询文档信息
        MaterialsList materialsList = materialsListService.getById(materialsId);
        if (Objects.isNull(materialsList)) {
            throw new MithrasException("没有找到对应报告文件");
        }
        // 查询项目评审主数据
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoMapper.selectById(materialsList.getBelongId());
        authCheck(projReviewBaseInfo);
        if (!SPONSOR_COULD_REMOVE_TYPE_SET.contains(materialsList.getMaterialsType())) {
            throw new MithrasException("只能删除尽调报告/业务定价审批表/其他");
        }
        materialsListService.remove(Collections.singletonList(materialsId));
        recordStatus(projReviewBaseInfo.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    public void upload(MultipartFile multipartFile, ProjReviewReportUploadREQ req) {
        if(Objects.isNull(ProjReviewMaterialsEnum.getByName(req.getMaterialsType()))){
            throw new MithrasException("不支持上传该文件类型");
        }
        Long projReviewId = req.getProjReviewId();
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoMapper.selectById(projReviewId);
        authCheck(projReviewBaseInfo);
        // 确定是否为发起人
        if (Objects.equals(AccountUtil.getLoginInfo().getId(), projReviewBaseInfo.getProjSponsorUserId())) {
            // 上传尽调报告 或 其他文件
            materialsListService.add(multipartFile, projReviewId, req.getMaterialsType(), BusinessModuleEnum.PROJ_REVIEW.name());
        } else {
            throw new AuthCheckException("详情页只允许发起人上传，其他岗位请在审批流中上传相应文件报告");
        }
        recordStatus(req.getProjReviewId());
    }

    private ProcessResp getRunningProcessNode(Long projReviewId) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setModelKeyList(Arrays.asList(ProcessModelTypeEnum.ProjReviewCreateFlow.name(), ProcessModelTypeEnum.ProjReviewModifyFlow.name()));
        processPageReq.setBusinessKey(projReviewId.toString());
        processPageReq.setProcessStatusList(Collections.singletonList(ProcessBusinessStatusEnum.RUNNING.getType()));
        Page<ProcessResp> processRespPage = flowTaskApiService.queryProcess(processPageReq);
        if (CollectionUtils.isEmpty(processRespPage.getContents())) {
            return null;
        } else {
            return processRespPage.getContents().get(0);
        }
    }

    private void authCheck(ProjReviewBaseInfo projReviewBaseInfo) {
        if (Objects.isNull(projReviewBaseInfo)) {
            throw new MithrasException("项目评审信息不存在");
        }
        if (CLOSED.name().equals(projReviewBaseInfo.getProjReviewStatus()) || EXPIRE.name().equals(projReviewBaseInfo.getProjReviewStatus())) {
            throw new MithrasException("该评审已关闭，不允许再修改有关信息");
        }
        // 判断是否在流程中 且 是否在发起人节点
        ProcessResp processResp = projReviewService.findRelatedProcess(projReviewBaseInfo.getId());
        if (Objects.nonNull(processResp)) {
            boolean isStartUserNode = FlowUtil.isStartUserNode(processResp);
            if (!isStartUserNode) {
                throw new AuthCheckException("该数据处于流程中，且流程不在发起人节点，不允许修改数据");
            }
        }
    }
}

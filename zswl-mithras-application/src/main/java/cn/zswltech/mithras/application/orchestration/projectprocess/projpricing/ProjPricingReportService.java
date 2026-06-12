package cn.zswltech.mithras.application.orchestration.projectprocess.projpricing;

import cn.hutool.core.io.IoUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.util.Page;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingPriceDetailRSP;
import cn.zswltech.mithras.dto.projpricing.report.ProjPricingReportGenerateREQ;
import cn.zswltech.mithras.dto.projpricing.report.ProjPricingReportListREQ;
import cn.zswltech.mithras.dto.projpricing.report.ProjPricingReportUploadREQ;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.projectprocess.enums.projpricing.ProjPricingMaterialsEnum;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjReviewMaterialsEnum;
import cn.zswltech.mithras.application.orchestration.document.gendoc.render.ProjPricingEarningsRateRender;
import cn.zswltech.mithras.application.orchestration.document.gendoc.render.ProjPricingJDReportZLRender;
import cn.zswltech.mithras.document.persistence.model.MaterialsList;
import cn.zswltech.mithras.projectprocess.model.projpricing.ProjPricingBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.projpricing.ProjPricingBaseInfoMapper;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.projectprocess.application.model.ProjPricingRenderBO;
import cn.zswltech.mithras.projectprocess.application.projpricing.ProjPricingUpdateAdvice;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.workflow.flow.util.FlowUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.*;

import static cn.zswltech.mithras.foundation.enums.common.RecordStatus.CLOSED;

/**
 * @author dingqi
 * @date 2022/8/5
 * @description 项目评审-报告清单相关
 */
@Service
public class ProjPricingReportService implements ProjPricingUpdateAdvice {
    //已经统一维护至ProjEstablishCheckHandler
    private static final Set<String> SPONSOR_COULD_REMOVE_TYPE_SET = new HashSet<>();

    static {
        SPONSOR_COULD_REMOVE_TYPE_SET.add(ProjPricingMaterialsEnum.BUSINESS_PRICING_APPROVAL_FORM.name());
        SPONSOR_COULD_REMOVE_TYPE_SET.add(ProjPricingMaterialsEnum.PRICING_OTHER.name());
    }

    @Resource
    private ProjPricingBaseInfoMapper projPricingBaseInfoMapper;
    @Resource
    private ProjPricingBaseInfoService projPricingBaseInfoService;
    @Resource
    private ProjPricingService projPricingService;
    @Resource
    private ProjPricingPriceService projPricingPriceService;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private ProjPricingJDReportZLRender projPricingJDReportZLRender;
    @Resource
    private ProjPricingEarningsRateRender projPricingEarningsRateRender;
    @Resource
    private FlowTaskApiService flowTaskApiService;
    @Resource
    private SysUserService sysUserService;

    @Transactional(rollbackFor = Exception.class)
    public Long generate(ProjPricingReportGenerateREQ req) throws Exception {
        ProjPricingBaseInfo projPricingBaseInfo = projPricingBaseInfoMapper.selectById(req.getProjPricingId());
        if (Objects.isNull(projPricingBaseInfo)) {
            throw new MithrasException("基本信息未保存，请先保存");
        }
        // 确认报价方案和现金流数据都已维护
        ProjPricingPriceDetailRSP priceDetailRSP = projPricingPriceService.detail(projPricingBaseInfo.getId());
        if (Objects.isNull(priceDetailRSP.getAocPriceDetailRSP()) && Objects.isNull(priceDetailRSP.getLeasePriceDetailRSP()) && Objects.isNull(priceDetailRSP.getFactoringPriceDetailRSP())) {
            throw new MithrasException("报价信息未保存，请先保存");
        }
        // 获取类型
        ProjectBizType projectBizType = ProjectBizType.of(projPricingBaseInfo.getBizType());
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
        ProjPricingMaterialsEnum projPricingMaterialsEnum;
        if (jobs.contains(JobEnum.projmanager.name())) {
            if (projectBizType != ProjectBizType.ZL) {
                throw new MithrasException("暂不支持【租赁】类型之外的类型生成报告，请直接手动上传");
            }
            projPricingMaterialsEnum = ProjPricingMaterialsEnum.BUSINESS_PRICING_APPROVAL_FORM;
            fileName = projPricingJDReportZLRender.render(os, new ProjPricingRenderBO(true,projPricingBaseInfo,projPricingMaterialsEnum));
        } else {
            throw new AuthCheckException("只允许项目经理操作");
        }
        recordStatus(req.getProjPricingId());
        return materialsListService.add(IoUtil.toStream(os.toByteArray()), fileName,
                req.getProjPricingId(), projPricingMaterialsEnum.name(), "TMP");
    }

    public void download(OutputStream outputStream, Long materialsId) {
        materialsListService.download(outputStream, Collections.singletonList(materialsId));
    }

    public List<MaterialsList> list(ProjPricingReportListREQ req) {
        List<String> materialsTypes;
//        if (ObjectUtil.isEmpty(req.getProcessInstanceId())) {
            // 项目评审详情页可展示所有文件
        materialsTypes = ProjPricingMaterialsEnum.listAll();
//        } else {
//            ProcessResp processResp = flowTaskApiService.queryProcessById(req.getProcessInstanceId());
//            ProcessModelTypeEnum byName = ProcessModelTypeEnum.getByName(processResp.getModelKey());
//            if (ProcessModelTypeEnum.ProjReviewPricingApprovalFlow.equals(byName)) {
//                // 定价流程详情页展示三份类文件
//                materialsTypes = ProjReviewMaterialsEnum.listPricing();
//            } else {
//                // 评审流程详情页展示其他文件
//                materialsTypes = ProjReviewMaterialsEnum.listReview();
//            }
//        }
        return materialsListService.list(BusinessModuleEnum.PROJ_PRICING.name(), materialsTypes,
                Collections.singletonList(req.getProjPricingId()));
    }

    @Transactional(rollbackFor = Exception.class)
    public void remove(Long materialsId) {
        // 先查询文档信息
        MaterialsList materialsList = materialsListService.getById(materialsId);
        if (Objects.isNull(materialsList)) {
            throw new MithrasException("没有找到对应报告文件");
        }
        // 查询项目评审主数据
        ProjPricingBaseInfo projPricingBaseInfo = projPricingBaseInfoMapper.selectById(materialsList.getBelongId());
        authCheck(projPricingBaseInfo);
        if (!SPONSOR_COULD_REMOVE_TYPE_SET.contains(materialsList.getMaterialsType())) {
            throw new MithrasException("只能删除尽调报告/业务定价审批表/其他");
        }
        materialsListService.remove(Collections.singletonList(materialsId));
        recordStatus(projPricingBaseInfo.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    public void upload(MultipartFile multipartFile, ProjPricingReportUploadREQ req) {
        if(Objects.isNull(ProjPricingMaterialsEnum.getByName(req.getMaterialsType()))){
            throw new MithrasException("不支持上传该文件类型");
        }
        Long projPricingId = req.getProjPricingId();
        ProjPricingBaseInfo projPricingBaseInfo = projPricingBaseInfoMapper.selectById(projPricingId);
        authCheck(projPricingBaseInfo);
        // 确定是否为发起人
        if (Objects.equals(AccountUtil.getLoginInfo().getId(), projPricingBaseInfo.getProjSponsorUserId())) {
            // 上传业务定价报告 或 其他文件
            materialsListService.add(multipartFile, projPricingId, req.getMaterialsType(), BusinessModuleEnum.PROJ_PRICING.name());
        } else {
            throw new AuthCheckException("详情页只允许发起人上传，其他岗位请在审批流中上传相应文件报告");
        }
        recordStatus(req.getProjPricingId());
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

    private void authCheck(ProjPricingBaseInfo projPricingBaseInfo) {
        if (Objects.isNull(projPricingBaseInfo)) {
            throw new MithrasException("项目定价不存在");
        }
        if (CLOSED.name().equals(projPricingBaseInfo.getProjPricingStatus())) {
            throw new MithrasException("该项目定价已关闭，不允许再修改有关信息");
        }
        // 判断是否在流程中 且 是否在发起人节点
        ProcessResp processResp = projPricingService.findRelatedProcess(projPricingBaseInfo.getId());
        if (Objects.nonNull(processResp)) {
            boolean isStartUserNode = FlowUtil.isStartUserNode(processResp);
            if (!isStartUserNode) {
                throw new AuthCheckException("该数据处于流程中，且流程不在发起人节点，不允许修改数据");
            }
        }
    }
}

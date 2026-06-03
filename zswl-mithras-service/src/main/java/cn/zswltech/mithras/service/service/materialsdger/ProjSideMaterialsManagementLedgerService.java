package cn.zswltech.mithras.service.service.materialsdger;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.StopWatch;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.fileledger.ProjSideArchivedMaterialsQueryREQ;
import cn.zswltech.mithras.dto.fileledger.ProjSideArchivedMaterialsQueryRSP;
import cn.zswltech.mithras.dto.utils.BeanCopyUtils;
import cn.zswltech.mithras.filingmaterials.infrastructure.persistence.mapper.model.ProjFilingMaterialsQuery;
import cn.zswltech.mithras.filingmaterials.infrastructure.persistence.mapper.model.ProjFilingMaterialsResult;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.filingmaterials.FilingMaterialsService;
import cn.zswltech.mithras.service.util.DateUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProjSideMaterialsManagementLedgerService extends AbstractMaterialsManagementLedger<ProjSideArchivedMaterialsQueryREQ, ProjSideArchivedMaterialsQueryRSP> {

    @Resource
    private FilingMaterialsService filingMaterialsService;

    @Resource
    private Id2NameService id2NameService;

    @Override
    public PageR<ProjSideArchivedMaterialsQueryRSP> queryMaterialsLedger(ProjSideArchivedMaterialsQueryREQ req) {
        ProjFilingMaterialsQuery projFilingMaterialsQuery = BeanCopyUtils.generatorObject(req, ProjFilingMaterialsQuery.class);
        projFilingMaterialsQuery.setCreateTimeFrom(DateUtil.startOfDay(req.getCreateTimeFrom()));
        projFilingMaterialsQuery.setCreateTimeTo(DateUtil.endOfDay(req.getCreateTimeTo()));
        projFilingMaterialsQuery.setEndTimeFrom(DateUtil.startOfDay(req.getEndTimeFrom()));
        projFilingMaterialsQuery.setEndTimeTo(DateUtil.endOfDay(req.getEndTimeTo()));
        StopWatch sw = new StopWatch();
        sw.start("获取项目归档资料信息");
        Page<ProjFilingMaterialsResult> projFilingMaterialsResults = filingMaterialsService.getBaseMapper().queryProjFilingMaterials(new Page<>(req.getPage(), req.getPageSize()), projFilingMaterialsQuery);
        if (CollUtil.isEmpty(projFilingMaterialsResults.getRecords())) {
            log.info("根据所输入条件查询数据为空！");
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        sw.stop();
        log.info("db耗时：{}", sw.prettyPrint(TimeUnit.SECONDS));
        sw.start("项目归档资料信息补充");
        List<ProjSideArchivedMaterialsQueryRSP> supplementRsp = projFilingMaterialsResults.getRecords().stream().map(this::genProjSideArchivedDocumentQueryRSP).collect(Collectors.toList());
        sw.stop();
        log.info("耗时：{}", sw.prettyPrint(TimeUnit.SECONDS));
        return PageR.of(projFilingMaterialsResults, supplementRsp);
    }

    /**
     * 获取项目端归档资料返回
     *
     * @param result
     * @return
     */
    private ProjSideArchivedMaterialsQueryRSP genProjSideArchivedDocumentQueryRSP(ProjFilingMaterialsResult result) {
        ProjSideArchivedMaterialsQueryRSP rsp = new ProjSideArchivedMaterialsQueryRSP();
        String bizDeptName = id2Name(bizDeptId2NameMap, result.getBizDeptId(), id2NameService::deptId2NameSingle);
        rsp.setBizDeptName(bizDeptName);
        String clientName = id2Name(clientId2NameMap, result.clientId, id2NameService::clientId2NameSingle);
        rsp.setClientName(clientName);
        rsp.setProjName(result.getProjName());
        rsp.setProjCode(result.getProjCode());
        rsp.setContractCode(result.getContractCode());
        rsp.setRiskControlIndustryClassify(result.getRiskControlIndustryClassify());
        String projSponsorUserName = id2Name(userId2NameMap, result.getSponsorUserId(), id2NameService::sysUserId2NameSingle);
        rsp.setProjSponsorUserName(projSponsorUserName);
        String materialsReReviewUserName = id2Name(userId2NameMap, result.getMaterialsReReviewUserId(), id2NameService::sysUserId2NameSingle);
        rsp.setMaterialsReReviewUserName(materialsReReviewUserName);
        rsp.setProcessInstanceId(result.getProcessInstanceId());
        rsp.setArchiveOverDueFlag(result.getArchiveOverDueFlag());
        rsp.setSupplementDocOverdueFlag(result.getSupplementDocOverdueFlag());
        rsp.setDuration(result.getDuration());
        // 档案管理初审耗时（工作日） = SUM【“档案管理岗（初审）”提交日期－流程到达“档案管理岗（初审）”日期】（仅计算工作日）+1
        rsp.setMaterialsManagerReviewDuration(result.getMaterialsManagerReviewDuration());
        // 档案管理复核耗时（工作日） = SUM【“档案管理岗（复核）”提交日期－流程到达“档案管理岗（复核）”日期】（仅计算工作日）+1
        rsp.setMaterialsManagerReReviewDuration(result.getMaterialsManagerReReviewDuration());
        rsp.setCreateTime(result.getCreateTime());
        rsp.setEndTime(result.getEndTime());
        // 应归档日 = “项目资料归档”流程推送至项目主办日期+45个工作日（推送当天纳入计算）,45 -（剔除发起当天+结尾多算一天）
        rsp.setDueArchiveDate(DateUtil.safeGetNextWorkdayAfterDays(result.getCreateTime(), 43));
        rsp.setProjectHostSubmitTime(result.getProjectHostSubmitTime());
        // 获取“档案管理岗（初审）”最后一次提交审批时间格式：yyyy-mm-dd hh:mm:ss
        rsp.setArchiveReviewSubmitTime(result.getArchiveReviewSubmitTime());
        // 获取“档案管理岗（复核）”最后一次提交审批时间格式：yyyy-mm-dd hh:mm:ss
        rsp.setArchiveReReviewSubmitTime(result.getArchiveReReviewSubmitTime());
        // “档案管理岗（初审）”退回发起人次数加总
        rsp.setArchiveReviewRejectCount(result.getArchiveReviewRejectCount());
        // “档案管理岗（复核）”退回发起人次数加总
        rsp.setArchiveReReviewRejectCount(result.getArchiveReReviewRejectCount());
        // “档案管理岗（初审）”退回发起人时的审批意见汇总。格式：根据退回次数拼接展示，“第【】次退回：【审批意见】；”
        rsp.setArchiveReviewRejectReason(result.getArchiveReviewRejectReason());
        // “档案管理岗（复核）”退回发起人时的审批意见汇总。格式：根据退回次数拼接展示，“第【】次退回：【审批意见】；”
        rsp.setArchiveReReviewRejectReason(result.getArchiveReReviewRejectReason());
        return rsp;
    }

    @Override
    protected void init() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("项目端归档资料查询初始化缓存数据");
        Page<ProjFilingMaterialsResult> projFilingMaterialsResults = filingMaterialsService.getBaseMapper().queryProjFilingMaterials(new Page<>(1, 200), new ProjFilingMaterialsQuery());
        projFilingMaterialsResults.getRecords().forEach(pfm -> {
            if (Objects.nonNull(pfm.getBizDeptId())) {
                bizDeptId2NameMap.put(pfm.getBizDeptId(), id2NameService.deptId2NameSingle(pfm.getBizDeptId()));
            }
            if (Objects.nonNull(pfm.getClientId())) {
                clientId2NameMap.put(pfm.getClientId(), id2NameService.clientId2NameSingle(pfm.getClientId()));
            }
            if (Objects.nonNull(pfm.getSponsorUserId())) {
                userId2NameMap.put(pfm.getSponsorUserId(), id2NameService.sysUserId2NameSingle(pfm.getSponsorUserId()));
            }
            if (Objects.nonNull(pfm.getMaterialsReReviewUserId())) {
                userId2NameMap.put(pfm.getMaterialsReReviewUserId(), id2NameService.sysUserId2NameSingle(pfm.getMaterialsReReviewUserId()));
            }
        });
        stopWatch.stop();
        log.info("耗时：{}s", stopWatch.prettyPrint(TimeUnit.SECONDS));
    }
}

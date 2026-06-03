package cn.zswltech.mithras.service.controller.afterlease;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.api.afterlease.AfterLeaseCheckExternalQueryApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.afterlease.*;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.implnew.CommonModifyMainAuthCheckerNew;
import cn.zswltech.mithras.service.auth.checker.implnew.CommonModifySubAuthCheckerNew;
import cn.zswltech.mithras.service.auth.checker.implnew.CommonViewMainAuthCheckerNew;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.convert.afterlease.ExternalQueryConverter;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.afterlease.ExternalQueryStatus;
import cn.zswltech.mithras.service.gendoc.render.AfterLeaseCheckExternalQueryRender;
import cn.zswltech.mithras.service.mapper.afterlease.NewAfterLeaseCheckExternalQueryClientInfoMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.corp.IndustryTypeMapper;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.mapper.model.afterlease.*;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckExternalQueryClientInfoLib;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckExternalQueryClientInfo;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.IndustryType;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseCheckExternalQueryClientInfoService;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseCheckExternalQueryService;
import cn.zswltech.mithras.service.service.lib.afterlease.AfterLeaseCheckExternalQueryClientInfoLibService;
import cn.zswltech.mithras.service.service.lib.afterlease.AfterLeaseCheckExternalQueryLibService;
import cn.zswltech.mithras.service.service.lib.afterlease.handler.impl.AfterLeaseCheckExternalQueryClientInfoLibHandler;
import cn.zswltech.mithras.service.service.lib.afterlease.handler.impl.AfterLeaseCheckExternalQueryLibHandler;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description 租后检查外部查询任务
 * @date 2022-11-17
 */
@RestController
public class AfterLeaseCheckExternalQueryController implements AfterLeaseCheckExternalQueryApi {
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private IndustryTypeMapper industryTypeMapper;
    @Resource
    private ExternalQueryConverter externalQueryConverter;
    @Resource
    private AfterLeaseCheckExternalQueryService externalQueryService;
    @Resource
    private AfterLeaseCheckExternalQueryLibService externalQueryLibService;
    @Resource
    private AfterLeaseCheckExternalQueryLibHandler externalQueryLibHandler;
    @Resource
    private AfterLeaseCheckExternalQueryClientInfoService clientInfoService;
    @Resource
    private AfterLeaseCheckExternalQueryClientInfoLibService clientInfoLibService;
    @Resource
    private AfterLeaseCheckExternalQueryClientInfoLibHandler clientInfoLibHandler;
    @Resource
    private AfterLeaseCheckExternalQueryRender externalQueryRender;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private HttpServletResponse httpServletResponse;

    @Override
    public R<Void> submit(AfterLeaseCheckExternalQueryIdReq req) {
        NewAfterLeaseCheckExternalQuery query = externalQueryService.getById(req.getId());
        Assert.notNull(query, () -> MithrasException.newException(ResultMsg.RECORD_NOT_EXIST));
        submitValidate(query);
        externalQueryService.submitCheck(query);
        externalQueryService.submit(req.getId());
        return R.ok();
    }

    @Override
    @DataAuthCheck(
            keyFieldName = "id",
            checkerClass = CommonModifyMainAuthCheckerNew.class,
            businessModule = BusinessModuleEnum.NEW_AFTER_LEASE_CHECK_EXTERNAL_QUERY
    )
    public R<Void> modify(AfterLeaseCheckExternalQueryModifyReq req) {
        NewAfterLeaseCheckExternalQuery query = externalQueryService.getById(req.getId());
        Assert.notNull(query, () -> MithrasException.newException(ResultMsg.RECORD_NOT_EXIST));
        modifyValidate(query);
        externalQueryService.updateById(externalQueryConverter.modifyReq2Entity(req));
        return R.ok();
    }

    @Override
    @DataAuthCheck(
            keyFieldName = "id",
            checkerClass = CommonModifyMainAuthCheckerNew.class,
            businessModule = BusinessModuleEnum.NEW_AFTER_LEASE_CHECK_EXTERNAL_QUERY
    )
    public R<Void> modifyConclusion(AfterLeaseCheckExternalQueryConclusionModifyReq req) {
        NewAfterLeaseCheckExternalQuery query = externalQueryService.getById(req.getId());
        Assert.notNull(query, () -> MithrasException.newException(ResultMsg.RECORD_NOT_EXIST));
        modifyValidate(query);
        externalQueryService.updateById(externalQueryConverter.conclusionModifyReq2Entity(req));
        return R.ok();
    }

    @Override
    public R<AfterLeaseCheckExternalQueryListStatisticsRsp> listStatistics(AfterLeaseCheckExternalQueryListReq req) {
        return R.ok(externalQueryService.listStatistics(req));
    }

    @Override
    public R<PageR<AfterLeaseCheckExternalQueryListRsp>> list(AfterLeaseCheckExternalQueryListReq req) {
        Page<NewAfterLeaseCheckExternalQuery> page = externalQueryService.list(req);
        List<Long> clientIds = new ArrayList<>();
        List<Long> userIds = new ArrayList<>();
        page.getRecords().forEach(query -> {
            clientIds.add(query.getClientId());
            userIds.add(query.getSponsorUserId());
        });
        Map<Long, String> clientId2Name = id2NameService.clientId2Name(clientIds);
        Map<Long, String> userId2Name = id2NameService.sysUserId2Name(userIds);
        List<AfterLeaseCheckExternalQueryListRsp> rspList = new ArrayList<>();
        page.getRecords().forEach(query -> {
            AfterLeaseCheckExternalQueryListRsp rsp = externalQueryConverter.entity2ListRsp(query);
            rsp.setClientName(clientId2Name.get(query.getClientId()));
            rsp.setProjSponsorUserName(userId2Name.get(query.getSponsorUserId()));
            rspList.add(rsp);
        });
        return R.ok(PageR.of(page, rspList));
    }

    @Override
    @DataAuthCheck(
            keyFieldName = "id",
            checkerClass = CommonViewMainAuthCheckerNew.class,
            businessModule = BusinessModuleEnum.NEW_AFTER_LEASE_CHECK_EXTERNAL_QUERY
    )
    public R<AfterLeaseCheckExternalQueryDetailRsp> detail(AfterLeaseCheckExternalQueryIdReq req) {
        NewAfterLeaseCheckExternalQuery query;
        if (StrUtil.isBlank(req.getVersion())) {
            query = externalQueryService.getById(req.getId());
            Assert.notNull(query, () -> MithrasException.newException(ResultMsg.RECORD_NOT_EXIST));
        } else {
            NewAfterLeaseCheckExternalQueryLib queryLib = externalQueryLibService.getByOriginIdAndVersion(req.getId(), req.getVersion());
            Assert.notNull(queryLib, () -> MithrasException.newException("指定版本的数据不存在"));
            query = externalQueryLibHandler.actualLib2Entity(queryLib);
        }
        AfterLeaseCheckExternalQueryDetailRsp detailRsp = doGetDetail(query, req.getVersion());
        detailRsp.setCanModify(canModify(query));
        detailRsp.setCanSubmit(canSubmit(query));
        return R.ok(detailRsp);
    }

    @Override
    @DataAuthCheck(
            keyFieldName = "id",
            checkerClass = CommonModifySubAuthCheckerNew.class,
            businessModule = BusinessModuleEnum.NEW_AFTER_LEASE_CHECK_EXTERNAL_QUERY,
            mapperClass = NewAfterLeaseCheckExternalQueryClientInfoMapper.class
    )
    public R<Void> clientInfoModify(AfterLeaseCheckExternalQueryClientInfoModifyReq req) {
        NewAfterLeaseCheckExternalQueryClientInfo clientInfo = clientInfoService.getById(req.getId());
        Assert.notNull(clientInfo, () -> MithrasException.newException(ResultMsg.RECORD_NOT_EXIST));
        NewAfterLeaseCheckExternalQuery query = externalQueryService.getById(clientInfo.getQueryId());
        modifyValidate(query);
        NewAfterLeaseCheckExternalQueryClientInfo updateEntity = externalQueryConverter.clientInfoModifyReq2Entity(req);
        clientInfoService.updateById(updateEntity);
        return R.ok();
    }

    @Override
    public R<Void> downLoadReport(AfterLeaseCheckExternalQueryIdReq req) throws Exception {
        NewAfterLeaseCheckExternalQuery query = externalQueryService.getById(req.getId());
        Assert.notNull(query, () -> MithrasException.newException(ResultMsg.RECORD_NOT_EXIST));
        if (!ExternalQueryStatus.APPROVAL_PASS.name().equals(query.getApprovalStatus())) {
            throw new MithrasException("审批通过后才可以下载报告");
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        String fileName = externalQueryRender.render(os, doGetDetail(query, null));
        Long materialsId = materialsListService.add(IoUtil.toStream(os.toByteArray()), fileName,
                req.getId(), "EXTERNAL_QUERY_REPORT", "TMP");

        MaterialsList materialsList = materialsListService.getById(materialsId);
        httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        httpServletResponse.setHeader("Content-Disposition",
                "attachment;filename="
                        + URLEncoder.encode(materialsList.getFilename(), StandardCharsets.UTF_8.name()));
        materialsListService.download(httpServletResponse.getOutputStream(), Collections.singletonList(materialsId));
        return R.ok();
    }

    private AfterLeaseCheckExternalQueryDetailRsp doGetDetail(NewAfterLeaseCheckExternalQuery query, String version) {
        AfterLeaseCheckExternalQueryDetailRsp detailRsp = externalQueryConverter.entity2DetailRsp(query);
        List<NewAfterLeaseCheckExternalQueryClientInfo> clientInfo;
        if (StrUtil.isBlank(version)) {
            clientInfo = clientInfoService.list(query.getId());
        } else {
            List<NewAfterLeaseCheckExternalQueryClientInfoLib> clientInfoLibList = clientInfoLibService.listByQueryIdAndVersion(query.getId(), version);
            clientInfo = clientInfoLibList.stream().map(clientInfoLibHandler::actualLib2Entity).collect(Collectors.toList());
        }
        List<Long> clientIds = clientInfo.stream().map(NewAfterLeaseCheckExternalQueryClientInfo::getClientId)
                .collect(Collectors.toList());
        clientIds.add(query.getClientId());
        // ids 2 names
        Map<Long, String> clientId2Name = id2NameService.clientId2Name(clientIds);
        Map<Long, String> deptId2Name = id2NameService.deptId2Name(
                Collections.singletonList(query.getDeptId()));
        String industryType = industryTypeMapper
                .selectOne(Wrappers.<IndustryType>lambdaQuery().eq(IndustryType::getCode, query.getIndustryType()))
                .getDisplay();
        detailRsp.setIndustryType(industryType);
        String sponsorUserName = id2NameService.sysUserId2NameSingle(query.getSponsorUserId());
        detailRsp.setClientName(clientId2Name.get(query.getClientId()));
        detailRsp.setDeptName(deptId2Name.get(query.getDeptId()));
        detailRsp.setSponsorUserName(sponsorUserName);
        // 查询客户信息
        List<AfterLeaseCheckExternalQueryClientInfoListRsp> clientInfoDetails = new ArrayList<>();

        for (NewAfterLeaseCheckExternalQueryClientInfo c : clientInfo) {
            AfterLeaseCheckExternalQueryClientInfoListRsp clientInfoDetailRsp =
                    externalQueryConverter.clientInfoEntity2DetailRsp(c);
            clientInfoDetailRsp.setClientName(clientId2Name.get(c.getClientId()));
            clientInfoDetails.add(clientInfoDetailRsp);
        }
        detailRsp.setClientInfos(clientInfoDetails);
        return detailRsp;
    }

    /**
     * 判断当前时间是否与查询任务的月份匹配
     */
    private boolean inSameMonth(LocalDateTime inspectionMonth) {
        LocalDateTime today = LocalDateTime.now();
        return inspectionMonth.getMonth().equals(today.getMonth());
    }

    /**
     * 处理日在处理月内，并且状态不为审批通过, 可以编辑
     */
    private boolean canModify(NewAfterLeaseCheckExternalQuery query) {
        return inSameMonth(query.getInspectionMonth())
                && !ExternalQueryStatus.APPROVAL_PASS.name().equals(query.getApprovalStatus());
    }
    private boolean canSubmit(NewAfterLeaseCheckExternalQuery query) {
        return inSameMonth(query.getInspectionMonth())
                && !ExternalQueryStatus.APPROVAL_PASS.name().equals(query.getApprovalStatus())
                && ObjectUtil.isEmpty(query.getProcessInstanceId());
    }

    /**
     * 提交和编辑之前的校验
     */
    private void modifyValidate(NewAfterLeaseCheckExternalQuery query) {
        if (!inSameMonth(query.getInspectionMonth())) {
            throw new MithrasException("任务已跨月，不可编辑");
        }
        if (ExternalQueryStatus.APPROVAL_PASS.name().equals(query.getApprovalStatus())) {
            throw new MithrasException("已审批通过，不可编辑");
        }
    }

    private void submitValidate(NewAfterLeaseCheckExternalQuery query) {
        if (!inSameMonth(query.getInspectionMonth())) {
            throw new MithrasException("任务已跨月，不可提交");
        }
        if (ExternalQueryStatus.APPROVAL_PASS.name().equals(query.getApprovalStatus())) {
            throw new MithrasException("已审批通过，不可提交");
        }
        if (ObjectUtil.isNotEmpty(query.getProcessInstanceId())) {
            throw new MithrasException("已存在审批中的流程实例，不可提交");
        }
    }

}
package cn.zswltech.mithras.ftp.oldftp.application.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.zswl.oss.core.OssClient;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.ftp.FtpGuidanceIdReq;
import cn.zswltech.mithras.dto.ftp.FtpQuarterlyBasePricingRsp;
import cn.zswltech.mithras.dto.ftp.FtpQuarterlyCustomerPrincipalPricingRsp;
import cn.zswltech.mithras.dto.ftp.FtpQuarterlyEnterprisePricingRsp;
import cn.zswltech.mithras.dto.ftp.FtpQuarterlyGuidanceAddReq;
import cn.zswltech.mithras.dto.ftp.FtpQuarterlyGuidanceDetailRsp;
import cn.zswltech.mithras.dto.ftp.FtpQuarterlyGuidanceListReq;
import cn.zswltech.mithras.dto.ftp.FtpQuarterlyGuidanceListRsp;
import cn.zswltech.mithras.dto.ftp.FtpQuarterlyMonthPricingRsp;
import cn.zswltech.mithras.dto.ftp.FtpQuarterlyNewestRsp;
import cn.zswltech.mithras.dto.ftp.FtpVersionDiffREQ;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.ftp.oldftp.convert.FtpQuarterlyGuidanceConverter;
import cn.zswltech.mithras.ftp.oldftp.enums.FtpProcessStatus;
import cn.zswltech.mithras.ftp.oldftp.model.FtpQuarterlyGuidance;
import cn.zswltech.mithras.ftp.oldftp.application.FtpQuarterlyGuidanceApplicationService;
import cn.zswltech.mithras.ftp.oldftp.application.model.FtpQuarterlyGuidanceExportInfo;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.foundation.enums.common.RecordStatus;
import cn.zswltech.mithras.foundation.persistence.dto.ChangeDTO;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.ftp.oldftp.service.FtpQuarterlyGuidanceVersionService;
import cn.zswltech.mithras.ftp.oldftp.service.FtpQuarterlyGuidanceService;
import cn.zswltech.mithras.ftp.oldftp.application.port.model.FtpGuidanceProcessInfo;
import cn.zswltech.mithras.foundation.port.AdminAuthResolver;
import cn.zswltech.mithras.foundation.port.CurrentUserDeptResolver;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.foundation.constant.ResultMsg.RECORD_NOT_EXIST;

@Service
public class FtpQuarterlyGuidanceApplicationServiceImpl implements FtpQuarterlyGuidanceApplicationService {

    @Resource
    private FtpQuarterlyGuidanceService quarterlyGuidanceService;
    @Resource
    private FtpQuarterlyGuidanceConverter quarterlyGuidanceConverter;
    @Resource
    private OssClient ossClient;
    @Resource
    private FtpQuarterlyGuidanceVersionService quarterlyGuidanceVersionService;
    @Resource
    private CurrentUserDeptResolver currentUserDeptResolver;
    
    private AdminAuthResolver adminAuthResolver;

    @Override
    public Long add(FtpQuarterlyGuidanceAddReq req) {
        if (req.getQuarter() > 4 || req.getQuarter() < 1) {
            throw new MithrasException("请选择正确的季度");
        }
        assertFtpOperator();
        FtpQuarterlyGuidance toBeInsert = quarterlyGuidanceConverter.addReq2Entity(req);
        toBeInsert.setGuidanceProcessStatus(FtpProcessStatus.NEW_UN_SUBMIT.name());
        toBeInsert.setGuidanceRecordStatus(RecordStatus.NEW.name());
        try {
            quarterlyGuidanceService.save(toBeInsert);
        } catch (DuplicateKeyException e) {
            throw new MithrasException("已存在该季度定价指导");
        }
        return toBeInsert.getId();
    }

    @Override
    public PageR<FtpQuarterlyGuidanceListRsp> list(FtpQuarterlyGuidanceListReq req) {
        return quarterlyGuidanceService.list(req);
    }

    @Override
    public FtpQuarterlyGuidanceDetailRsp detail(FtpGuidanceIdReq req) {
        FtpQuarterlyGuidance guidance = quarterlyGuidanceService.getById(req.getId());
        if (ObjectUtil.isEmpty(guidance)) {
            throw new MithrasException("记录不存在");
        }
        return quarterlyGuidanceConverter.entity2DetailRsp(guidance);
    }

    @Override
    public List<FtpQuarterlyBasePricingRsp> detailPricingBase(FtpGuidanceIdReq req) {
        return quarterlyGuidanceService.detailPricingBase(req);
    }

    @Override
    public List<FtpQuarterlyMonthPricingRsp> detailPricingMonth(FtpGuidanceIdReq req) {
        return quarterlyGuidanceService.detailPricingMonth(req);
    }

    @Override
    public List<FtpQuarterlyCustomerPrincipalPricingRsp> detailPricingCustomer(FtpGuidanceIdReq req) {
        return quarterlyGuidanceService.detailPricingCustomer(req);
    }

    @Override
    public List<FtpQuarterlyEnterprisePricingRsp> detailPricingEnterprise(FtpGuidanceIdReq req) {
        return quarterlyGuidanceService.detailPricingEnterprise(req);
    }

    @Override
    public String templateDownload() {
        assertFtpOperator();
        try {
            return ossClient.getPreviewUrl(GlobalConstants.FTP_QUARTERLY_GUIDANCE, GlobalConstants.FILE_TEMPLATE_EXPIRY);
        } catch (Exception e) {
            throw new MithrasException("获取季度最低收益率指导模板出错!");
        }
    }

    @Override
    public void importExcel(InputStream inputStream, Long guidanceId) throws Exception {
        assertFtpOperator();
        quarterlyGuidanceService.importExcel(inputStream, guidanceId);
    }

    @Override
    public FtpQuarterlyGuidanceExportInfo getExportInfo(Long id) {
        FtpQuarterlyGuidance guidance = quarterlyGuidanceService.getById(id);
        if (isNull(guidance)) {
            throw new MithrasException(RECORD_NOT_EXIST);
        }
        return new FtpQuarterlyGuidanceExportInfo(guidance.getYear(), guidance.getQuarter());
    }

    @Override
    public void exportExcel(Long guidanceId, OutputStream outputStream) {
        quarterlyGuidanceService.exportExcel(guidanceId, outputStream);
    }

    @Override
    public void submit(Long id) {
        assertFtpOperator();
        FtpQuarterlyGuidance guidance = quarterlyGuidanceService.getById(id);
        if (isNull(guidance)) {
            throw new MithrasException(RECORD_NOT_EXIST);
        }
        FtpGuidanceProcessInfo relatedProcess = quarterlyGuidanceService.findRelatedProcess(id);
        if (ObjectUtil.isNotEmpty(relatedProcess)) {
            throw new MithrasException(String.format("已处于'%s'中，提交审批失败", relatedProcess.getModelDisplayName()));
        }
        ChangeDTO changeDTO = quarterlyGuidanceVersionService.checkActualChange(id);
        if (!Boolean.TRUE.equals(changeDTO.getChangeFlag())) {
            throw new MithrasException("数据未变动，无需提交数据");
        }
        quarterlyGuidanceService.submit(id);
    }

    @Override
    public PageR<CommonVersionListRSP> versionList(CommonVersionListREQ req) {
        if (StringUtils.isEmpty(req.getModule())) {
            req.setModule("FTP_QUARTERLY_GUIDANCE");
        }
        return quarterlyGuidanceVersionService.selectPage(req);
    }

    @Override
    public CommonVersionDiffRSP comparePreVersion(FtpVersionDiffREQ req) {
        return quarterlyGuidanceVersionService.comparePreVersion(req.getId());
    }

    @Override
    public FtpQuarterlyNewestRsp newestVersionData(Long id) {
        return quarterlyGuidanceService.newestVersionData(id);
    }

    private void assertFtpOperator() {
        if (!currentUserDeptResolver.currentUserIsSpecificDept("JHCWB", "ZJGLB") && !adminAuthResolver.adminAuth()) {
            throw new MithrasException("只有计划财务部及资金管理部员工可以操作");
        }
    }
}

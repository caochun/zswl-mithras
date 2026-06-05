package cn.zswltech.mithras.service.adapter.ftp;

import cn.hutool.core.util.ObjectUtil;
import cn.zswl.oss.core.OssClient;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.ftp.FtpGuidanceIdReq;
import cn.zswltech.mithras.dto.ftp.FtpMonthlyGuidanceAddReq;
import cn.zswltech.mithras.dto.ftp.FtpMonthlyGuidanceDetailRsp;
import cn.zswltech.mithras.dto.ftp.FtpMonthlyGuidanceListReq;
import cn.zswltech.mithras.dto.ftp.FtpMonthlyGuidanceListRsp;
import cn.zswltech.mithras.dto.ftp.FtpMonthlyPricingRsp;
import cn.zswltech.mithras.dto.ftp.FtpMonthlyValuationRsp;
import cn.zswltech.mithras.dto.ftp.FtpVersionDiffREQ;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.ftp.oldftp.convert.FtpMonthlyGuidanceConverter;
import cn.zswltech.mithras.ftp.oldftp.enums.FtpProcessStatus;
import cn.zswltech.mithras.ftp.oldftp.model.FtpMonthlyGuidance;
import cn.zswltech.mithras.ftp.oldftp.service.FtpMonthlyPricingService;
import cn.zswltech.mithras.ftp.oldftp.service.FtpMonthlyValuationService;
import cn.zswltech.mithras.ftp.oldftp.service.application.FtpMonthlyGuidanceApplicationService;
import cn.zswltech.mithras.ftp.oldftp.service.application.FtpMonthlyGuidanceExportInfo;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.service.mapper.dto.ChangeDTO;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.ftp.FtpMonthlyGuidanceService;
import cn.zswltech.mithras.service.service.ftp.FtpMonthlyGuidanceVersionService;
import cn.zswltech.mithras.system.service.SysUserService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.service.constant.ResultMsg.RECORD_NOT_EXIST;

@Service
public class FtpMonthlyGuidanceApplicationAdapter implements FtpMonthlyGuidanceApplicationService {

    @Resource
    private FtpMonthlyGuidanceService monthlyGuidanceService;
    @Resource
    private FtpMonthlyGuidanceConverter monthlyGuidanceConverter;
    @Resource
    private FtpMonthlyValuationService monthlyValuationService;
    @Resource
    private FtpMonthlyPricingService monthlyPricingService;
    @Resource
    private OssClient ossClient;
    @Resource
    private FtpMonthlyGuidanceVersionService monthlyGuidanceVersionService;
    @Resource
    private SysUserService sysUserService;

    @Override
    public Long add(FtpMonthlyGuidanceAddReq req) {
        if (req.getMonth() > 12 || req.getMonth() < 1) {
            throw new MithrasException("参数month错误，请选择正确的月份");
        }
        assertFtpOperator();
        FtpMonthlyGuidance toBeInsert = monthlyGuidanceConverter.addReq2Entity(req);
        toBeInsert.setGuidanceProcessStatus(FtpProcessStatus.NEW_UN_SUBMIT.name());
        toBeInsert.setGuidanceRecordStatus(RecordStatus.NEW.name());
        try {
            monthlyGuidanceService.save(toBeInsert);
        } catch (DuplicateKeyException e) {
            throw new MithrasException("已存在该月度定价指导");
        }
        return toBeInsert.getId();
    }

    @Override
    public PageR<FtpMonthlyGuidanceListRsp> list(FtpMonthlyGuidanceListReq req) {
        return monthlyGuidanceService.list(req);
    }

    @Override
    public FtpMonthlyGuidanceDetailRsp detail(FtpGuidanceIdReq req) {
        return monthlyGuidanceService.detail(req);
    }

    @Override
    public List<FtpMonthlyValuationRsp> detailValuation(FtpGuidanceIdReq req) {
        return monthlyValuationService.detailValuation(req);
    }

    @Override
    public List<FtpMonthlyPricingRsp> detailPricing(FtpGuidanceIdReq req) {
        return monthlyPricingService.detailPricing(req);
    }

    @Override
    public String templateDownload() {
        assertFtpOperator();
        try {
            return ossClient.getPreviewUrl(GlobalConstants.FTP_MONTHLY_GUIDANCE, GlobalConstants.FILE_TEMPLATE_EXPIRY);
        } catch (Exception e) {
            throw new MithrasException("获取月度ftp定价指导模板出错!");
        }
    }

    @Override
    public void importExcel(InputStream inputStream, Long guidanceId) throws Exception {
        assertFtpOperator();
        monthlyGuidanceService.importExcel(inputStream, guidanceId);
    }

    @Override
    public FtpMonthlyGuidanceExportInfo getExportInfo(Long id) {
        FtpMonthlyGuidance guidance = monthlyGuidanceService.getById(id);
        if (isNull(guidance)) {
            throw new MithrasException(RECORD_NOT_EXIST);
        }
        return new FtpMonthlyGuidanceExportInfo(guidance.getYear(), guidance.getMonth());
    }

    @Override
    public void exportExcel(Long guidanceId, OutputStream outputStream) {
        monthlyGuidanceService.exportExcel(guidanceId, outputStream);
    }

    @Override
    public void submit(Long id) {
        assertFtpOperator();
        FtpMonthlyGuidance guidance = monthlyGuidanceService.getById(id);
        if (isNull(guidance)) {
            throw new MithrasException(RECORD_NOT_EXIST);
        }
        ProcessResp relatedProcess = monthlyGuidanceService.findRelatedProcess(id);
        if (ObjectUtil.isNotEmpty(relatedProcess)) {
            ProcessModelTypeEnum modelTypeEnum = ProcessModelTypeEnum.valueOf(relatedProcess.getModelKey());
            throw new MithrasException(String.format("已处于'%s'中，提交审批失败", modelTypeEnum.getDisplay()));
        }
        ChangeDTO changeDTO = monthlyGuidanceVersionService.checkActualChange(id);
        if (!Boolean.TRUE.equals(changeDTO.getChangeFlag())) {
            throw new MithrasException("数据未变动，无需提交数据");
        }
        monthlyGuidanceService.submit(id);
    }

    @Override
    public PageR<CommonVersionListRSP> versionList(CommonVersionListREQ req) {
        if (StringUtils.isEmpty(req.getModule())) {
            req.setModule(BusinessModuleEnum.FTP_MONTHLY_GUIDANCE.name());
        }
        return monthlyGuidanceVersionService.selectPage(req);
    }

    @Override
    public CommonVersionDiffRSP comparePreVersion(FtpVersionDiffREQ req) {
        return monthlyGuidanceVersionService.comparePreVersion(req.getId());
    }

    private void assertFtpOperator() {
        if (!sysUserService.currentUserIsSpecificDept("JHCWB", "ZJGLB") && !sysUserService.adminAuth()) {
            throw new MithrasException("只有计划财务部及资金管理部员工可以操作");
        }
    }
}

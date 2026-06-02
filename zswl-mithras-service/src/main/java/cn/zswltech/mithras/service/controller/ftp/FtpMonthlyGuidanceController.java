package cn.zswltech.mithras.service.controller.ftp;

import cn.hutool.core.util.ObjectUtil;
import cn.zswl.oss.core.OssClient;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.ftp.FtpMonthlyGuidanceApi;
import cn.zswltech.mithras.dto.ftp.*;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.convert.ftp.FtpMonthlyGuidanceConverter;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.ftp.enums.FtpProcessStatus;
import cn.zswltech.mithras.service.mapper.dto.ChangeDTO;
import cn.zswltech.mithras.ftp.model.FtpMonthlyGuidance;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.ftp.FtpMonthlyGuidanceService;
import cn.zswltech.mithras.service.service.ftp.FtpMonthlyGuidanceVersionService;
import cn.zswltech.mithras.service.service.ftp.FtpMonthlyPricingService;
import cn.zswltech.mithras.service.service.ftp.FtpMonthlyValuationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.service.constant.ResultMsg.RECORD_NOT_EXIST;

/**
 * @author zhaozhengkang
 * @description 月度指导
 * @date 2023-01-10
 */
@RestController
@Slf4j
public class FtpMonthlyGuidanceController implements FtpMonthlyGuidanceApi {

    @Resource
    private FtpMonthlyGuidanceService mainService;
    @Resource
    private FtpMonthlyGuidanceConverter mainConverter;
    @Resource
    private FtpMonthlyValuationService valuationService;
    @Resource
    private FtpMonthlyPricingService pricingService;
    @Resource
    private HttpServletResponse response;
    @Resource
    private OssClient ossClient;
    @Resource
    private FtpMonthlyGuidanceVersionService guidanceVersionService;
    @Resource
    private SysUserService sysUserService;

    @Override
    public R<Long> add(FtpMonthlyGuidanceAddReq req) {
        if (req.getMonth() > 12 || req.getMonth() < 1) {
            throw new MithrasException("参数month错误，请选择正确的月份");
        }
        if (!sysUserService.currentUserIsSpecificDept("JHCWB","ZJGLB") && !sysUserService.adminAuth()) {
            throw new MithrasException("只有计划财务部及资金管理部员工可以操作");
        }
        FtpMonthlyGuidance toBeInsert = mainConverter.addReq2Entity(req);
        toBeInsert.setGuidanceProcessStatus(FtpProcessStatus.NEW_UN_SUBMIT.name());
        toBeInsert.setGuidanceRecordStatus(RecordStatus.NEW.name());
        try {
            mainService.save(toBeInsert);
        } catch (DuplicateKeyException e) {
            throw new MithrasException("已存在该月度定价指导");
        }
        return R.ok(toBeInsert.getId());
    }

    @Override
    public R<PageR<FtpMonthlyGuidanceListRsp>> list(FtpMonthlyGuidanceListReq req) {
        PageR<FtpMonthlyGuidanceListRsp> rsps = mainService.list(req);
        return R.ok(rsps);
    }

    @Override
    public R<FtpMonthlyGuidanceDetailRsp> detail(FtpGuidanceIdReq req) {
        FtpMonthlyGuidanceDetailRsp rsp = mainService.detail(req);
        return R.ok(rsp);
    }

    @Override
    public R<List<FtpMonthlyValuationRsp>> detailValuation(FtpGuidanceIdReq req) {
        return R.ok(valuationService.detailValuation(req));
    }

    @Override
    public R<List<FtpMonthlyPricingRsp>> detailPricing(FtpGuidanceIdReq req) {
        return R.ok(pricingService.detailPricing(req));
    }

    @Override
    public R<String> templateDownload() {
        if (!sysUserService.currentUserIsSpecificDept("JHCWB","ZJGLB") && !sysUserService.adminAuth()) {
            throw new MithrasException("只有计划财务部及资金管理部员工可以操作");
        }
        try {
            /*response.setHeader("Content-Disposition", "attachment;filename="
                    + URLEncoder.encode(GlobalConstants.FTP_MONTHLY_GUIDANCE, StandardCharsets.UTF_8.name()));
            ossClient.downLoad(response.getOutputStream(), GlobalConstants.FTP_MONTHLY_GUIDANCE);*/
            return R.ok(ossClient.getPreviewUrl(GlobalConstants.FTP_MONTHLY_GUIDANCE, GlobalConstants.FILE_TEMPLATE_EXPIRY));
        } catch (Exception e) {
            log.error("下载月度ftp定价表单模板发生未知异常", e);
            throw new MithrasException("下载月度ftp定价表单模板发生未知异常");
        }
    }

    @Override
    public R<Void> importExcel(MultipartFile file, Long guidanceId) {
        if (!sysUserService.currentUserIsSpecificDept("JHCWB","ZJGLB") && !sysUserService.adminAuth()) {
            throw new MithrasException("只有计划财务部及资金管理部员工可以操作");
        }
        try {
            mainService.importExcel(file.getInputStream(), guidanceId);
        } catch (IOException e) {
            throw new MithrasException("导入excel文件出现错误!");
        }
        return R.ok();
    }

    @Override
    public void exportExcel(FtpGuidanceIdReq req) {
        try {
            FtpMonthlyGuidance guidance = mainService.getById(req.getId());
            String fileName = "FTP定价测算表" + guidance.getYear() + String.format("%02d", guidance.getMonth()) + ".xlsx";
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()));
            mainService.exportExcel(req.getId(), response.getOutputStream());
        } catch (IOException e) {
            throw new MithrasException("导出月度ftp定价指导出错!");
        }
    }

    @Override
    public R<Void> submit(FtpGuidanceIdReq req) {
        if (!sysUserService.currentUserIsSpecificDept("JHCWB","ZJGLB") && !sysUserService.adminAuth()) {
            throw new MithrasException("只有计划财务部及资金管理部员工可以操作");
        }
        FtpMonthlyGuidance guidance = mainService.getById(req.getId());
        if (isNull(guidance)) {
            throw new MithrasException(RECORD_NOT_EXIST);
        }
        ProcessResp relatedProcess = mainService.findRelatedProcess(req.getId());
        if (ObjectUtil.isNotEmpty(relatedProcess)) {
            ProcessModelTypeEnum modelTypeEnum = ProcessModelTypeEnum.valueOf(relatedProcess.getModelKey());
            throw new MithrasException(String.format("已处于'%s'中，提交审批失败", modelTypeEnum.getDisplay()));
        }
        // 数据变动 全量数据校验 判断数据是否变动 和 最新版本数据对比 如果不存在版本则放行
        ChangeDTO changeDTO = guidanceVersionService.checkActualChange(req.getId());
        if (!Boolean.TRUE.equals(changeDTO.getChangeFlag())) {
            throw new MithrasException("数据未变动，无需提交数据");
        }
        mainService.submit(req.getId());
        return R.ok();
    }

    @Override
    public R<PageR<CommonVersionListRSP>> list(CommonVersionListREQ req) {
        if (StringUtils.isEmpty(req.getModule())) {
            req.setModule(BusinessModuleEnum.FTP_MONTHLY_GUIDANCE.name());
        }
        PageR<CommonVersionListRSP> data = guidanceVersionService.selectPage(req);
        return R.ok(data);
    }

    @Override
    public R<CommonVersionDiffRSP> comparePreVersion(FtpVersionDiffREQ req) {
        return R.ok(guidanceVersionService.comparePreVersion(req.getId()));
    }
}
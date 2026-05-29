package cn.zswltech.mithras.service.controller.ftp;

import cn.hutool.core.util.ObjectUtil;
import cn.zswl.oss.core.OssClient;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.ftp.FtpQuarterlyGuidanceApi;
import cn.zswltech.mithras.dto.ftp.*;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.convert.ftp.FtpQuarterlyGuidanceConverter;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.common.enums.RecordStatus;
import cn.zswltech.mithras.service.enums.ftp.FtpProcessStatus;
import cn.zswltech.mithras.service.mapper.dto.ChangeDTO;
import cn.zswltech.mithras.service.mapper.model.ftp.FtpQuarterlyGuidance;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.ftp.FtpQuarterlyGuidanceService;
import cn.zswltech.mithras.service.service.ftp.FtpQuarterlyGuidanceVersionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.service.constant.ResultMsg.RECORD_NOT_EXIST;

/**
 * @author zhaozhengkang
 * @description 季度最低收益率指导
 * @date 2023-01-09
 */
@RestController
@Slf4j
public class FtpQuarterlyGuidanceController implements FtpQuarterlyGuidanceApi {

    @Resource
    private FtpQuarterlyGuidanceService mainService;
    @Resource
    private FtpQuarterlyGuidanceConverter mainConverter;
    @Resource
    private HttpServletResponse response;
    @Resource
    private OssClient ossClient;
    @Resource
    private FtpQuarterlyGuidanceVersionService guidanceVersionService;
    @Resource
    private SysUserService sysUserService;

    @Override
    public R<Long> add(FtpQuarterlyGuidanceAddReq req) {
        if (req.getQuarter() > 4 || req.getQuarter() < 1) {
            throw new MithrasException("请选择正确的季度");
        }
        if(!sysUserService.currentUserIsSpecificDept("JHCWB","ZJGLB") && !sysUserService.adminAuth()){
            throw new MithrasException("只有计划财务部及资金管理部员工可以操作");
        }
        FtpQuarterlyGuidance toBeInsert = mainConverter.addReq2Entity(req);
        toBeInsert.setGuidanceProcessStatus(FtpProcessStatus.NEW_UN_SUBMIT.name());
        toBeInsert.setGuidanceRecordStatus(RecordStatus.NEW.name());
        try {
            mainService.save(toBeInsert);
        } catch (DuplicateKeyException e) {
            throw new MithrasException("已存在该季度定价指导");
        }
        return R.ok(toBeInsert.getId());
    }

    @Override
    public R<PageR<FtpQuarterlyGuidanceListRsp>> list(FtpQuarterlyGuidanceListReq req) {
        PageR<FtpQuarterlyGuidanceListRsp> rsps = mainService.list(req);
        return R.ok(rsps);
    }

    @Override
    public R<FtpQuarterlyGuidanceDetailRsp> detail(FtpGuidanceIdReq req) {
        FtpQuarterlyGuidance guidance = mainService.getById(req.getId());
        if (ObjectUtil.isEmpty(guidance)) {
            throw new MithrasException("记录不存在");
        }
        return R.ok(mainConverter.entity2DetailRsp(guidance));
    }

    @Override
    public R<List<FtpQuarterlyBasePricingRsp>> detailPricingBase(FtpGuidanceIdReq req) {
        return R.ok(mainService.detailPricingBase(req));
    }

    @Override
    public R<List<FtpQuarterlyMonthPricingRsp>> detailPricingMonth(FtpGuidanceIdReq req) {
        return R.ok(mainService.detailPricingMonth(req));
    }

    @Override
    public R<List<FtpQuarterlyCustomerPrincipalPricingRsp>> detailPricingCustomer(FtpGuidanceIdReq req) {
        return R.ok(mainService.detailPricingCustomer(req));
    }

    @Override
    public R<List<FtpQuarterlyEnterprisePricingRsp>> detailPricingEnterprise(FtpGuidanceIdReq req) {
        return R.ok(mainService.detailPricingEnterprise(req));
    }

    @Override
    public R<String> templateDownload() {
        if(!sysUserService.currentUserIsSpecificDept("JHCWB","ZJGLB") && !sysUserService.adminAuth()){
            throw new MithrasException("只有计划财务部及资金管理部员工可以操作");
        }
        try {
            /*response.setHeader("Content-Disposition", "attachment;filename=" +
                    URLEncoder.encode(GlobalConstants.FTP_QUARTERLY_GUIDANCE, StandardCharsets.UTF_8.name()));
            ossClient.downLoad(response.getOutputStream(), GlobalConstants.FTP_QUARTERLY_GUIDANCE);*/
            return R.ok(ossClient.getPreviewUrl(GlobalConstants.FTP_QUARTERLY_GUIDANCE, GlobalConstants.FILE_TEMPLATE_EXPIRY));
        } catch (Exception e) {
            log.error("下载季度最低收益率指导模板发生未知异常", e);
            throw new MithrasException("下载月度ftp定价表单模板发生未知异常");
        }
    }

    @Override
    public R<Void> importExcel(MultipartFile file, Long guidanceId) {
        if(!sysUserService.currentUserIsSpecificDept("JHCWB","ZJGLB") && !sysUserService.adminAuth()){
            throw new MithrasException("只有计划财务部及资金管理部员工可以操作");
        }
        try {
            mainService.importExcel(file.getInputStream(), guidanceId);
        } catch (IOException e) {
            throw new MithrasException("导入文件发生错误!");
        }
        return R.ok();
    }

    @Override
    public void exportExcel(FtpGuidanceIdReq req) {
        try {
            FtpQuarterlyGuidance guidance = mainService.getById(req.getId());
            String fileName = "最低收益率指导" + guidance.getYear() + "第" + guidance.getQuarter() + "季度.xlsx";
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(URLEncoder.encode(fileName, "utf-8").getBytes("UTF-8"), "ISO-8859-1"));
            mainService.exportExcel(req.getId(), response.getOutputStream());
        } catch (IOException e) {
            throw new MithrasException("导出季度最低收益率指导出错!");
        }
    }

    @Override
    public R<Void> submit(FtpGuidanceIdReq req) {
        if(!sysUserService.currentUserIsSpecificDept("JHCWB","ZJGLB") && !sysUserService.adminAuth()){
            throw new MithrasException("只有计划财务部及资金管理部员工可以操作");
        }
        FtpQuarterlyGuidance guidance = mainService.getById(req.getId());
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
            req.setModule(BusinessModuleEnum.FTP_QUARTERLY_GUIDANCE.name());
        }
        PageR<CommonVersionListRSP> data = guidanceVersionService.selectPage(req);
        return R.ok(data);
    }

    @Override
    public R<CommonVersionDiffRSP> comparePreVersion(FtpVersionDiffREQ req) {
        return R.ok(guidanceVersionService.comparePreVersion(req.getId()));
    }

    @Override
    public R<FtpQuarterlyNewestRsp> newestVersionData(FtpGuidanceIdReq req) {
        return R.ok(mainService.newestVersionData(req.getId()));
    }

}
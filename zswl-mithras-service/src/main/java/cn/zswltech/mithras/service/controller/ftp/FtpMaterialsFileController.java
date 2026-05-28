package cn.zswltech.mithras.service.controller.ftp;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.ftp.FtpMaterialsFileApi;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.ftp.FtpBatchIdsReq;
import cn.zswltech.mithras.dto.ftp.FtpMaterialListRSP;
import cn.zswltech.mithras.dto.ftp.FtpMaterialListReq;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.ftp.FtpMonthlyMaterialsEnum;
import cn.zswltech.mithras.service.enums.ftp.FtpQuarterlyMaterialsEnum;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.ftp.FtpMonthlyGuidanceService;
import cn.zswltech.mithras.service.service.ftp.FtpQuarterlyGuidanceService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/10 10:00
 */
@RestController
public class FtpMaterialsFileController implements FtpMaterialsFileApi {
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private HttpServletResponse response;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private FtpQuarterlyGuidanceService quarterlyGuidanceService;
    @Resource
    private FtpMonthlyGuidanceService monthlyGuidanceService;

    @Override
    public R<List<FtpMaterialListRSP>> fileList(FtpMaterialListReq req) {
        List<MaterialsList> materialsLists = materialsListService
                .listBy(req.getBizType(), req.getId());
        List<FtpMaterialListRSP> rsps = new ArrayList<>();
        materialsLists.forEach(materialsList -> {
            if("MEETING_FILE".equals(materialsList.getMaterialsType())){
                return;
            }
            FtpMaterialListRSP rsp = new FtpMaterialListRSP();
            rsp.setId(materialsList.getId());
            rsp.setFileName(materialsList.getFilename());
            rsps.add(rsp);
        });
        return R.ok(rsps);
    }

    @Override
    public R<Void> fileUpload(MultipartFile file, String bizType, Long belongId) {
        ProcessResp relatedProcesses = null;
        String materialsType = null;
        if("FTP_QUARTERLY_GUIDANCE".equals(bizType)){
            relatedProcesses = quarterlyGuidanceService.findRelatedProcess(belongId);
            materialsType = FtpQuarterlyMaterialsEnum.DEFAULT.name();
        }else if("FTP_MONTHLY_GUIDANCE".equals(bizType)){
            relatedProcesses = monthlyGuidanceService.findRelatedProcess(belongId);
            materialsType = FtpMonthlyMaterialsEnum.DEFAULT.name();
        }
        if(ObjectUtil.isNotEmpty(relatedProcesses)){
            throw new MithrasException("数据处于审批流程中，不能上传!");
        }
        if(!sysUserService.currentUserIsSpecificDept("JHCWB","ZJGLB") && !sysUserService.adminAuth()){
            throw new MithrasException("只有计划财务部及资金管理部员工可以操作");
        }
        materialsListService.add(file, belongId, materialsType, bizType);
        return R.ok();
    }

    @Override
    public R<Void> fileRemove(FtpBatchIdsReq req) {
        if(!sysUserService.currentUserIsSpecificDept("JHCWB","ZJGLB") && !sysUserService.adminAuth()){
            throw new MithrasException("只有计划财务部及资金管理部员工可以操作");
        }
        materialsListService.remove(req.getIds());
        return R.ok();
    }

    @Override
    @Deprecated
    public R<FileListRSP> fileDownload(FtpBatchIdsReq req) {
        if (CollectionUtils.isEmpty(req.getIds())) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        List<MaterialsList> materials = materialsListService.getByIds(req.getIds());
        try {
            if (materials.size() == 1) {
                return R.ok(materialsListService.download(req.getIds().get(0)));
            }
            ServletOutputStream outputStream = response.getOutputStream();
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("资料清单.zip", StandardCharsets.UTF_8.name()));
            materialsListService.download(outputStream, req.getIds());
        }catch (IOException e) {
            throw new MithrasException("下载失败!");
        }
        return R.ok();
    }
}

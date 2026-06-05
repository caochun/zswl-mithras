package cn.zswltech.mithras.service.adapter.ftp;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.ftp.FtpBatchIdsReq;
import cn.zswltech.mithras.dto.ftp.FtpMaterialListRSP;
import cn.zswltech.mithras.dto.ftp.FtpMaterialListReq;
import cn.zswltech.mithras.ftp.oldftp.enums.FtpMonthlyMaterialsEnum;
import cn.zswltech.mithras.ftp.oldftp.enums.FtpQuarterlyMaterialsEnum;
import cn.zswltech.mithras.ftp.oldftp.service.application.FtpMaterialsFileApplicationService;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.ftp.FtpMonthlyGuidanceService;
import cn.zswltech.mithras.service.service.ftp.FtpQuarterlyGuidanceService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.system.service.SysUserService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class FtpMaterialsFileApplicationAdapter implements FtpMaterialsFileApplicationService {

    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private FtpQuarterlyGuidanceService quarterlyGuidanceService;
    @Resource
    private FtpMonthlyGuidanceService monthlyGuidanceService;

    @Override
    public List<FtpMaterialListRSP> fileList(FtpMaterialListReq req) {
        List<MaterialsList> materialsLists = materialsListService.listBy(req.getBizType(), req.getId());
        List<FtpMaterialListRSP> rsps = new ArrayList<>();
        materialsLists.forEach(materialsList -> {
            if ("MEETING_FILE".equals(materialsList.getMaterialsType())) {
                return;
            }
            FtpMaterialListRSP rsp = new FtpMaterialListRSP();
            rsp.setId(materialsList.getId());
            rsp.setFileName(materialsList.getFilename());
            rsps.add(rsp);
        });
        return rsps;
    }

    @Override
    public void fileUpload(MultipartFile file, String bizType, Long belongId) {
        ProcessResp relatedProcesses = null;
        String materialsType = null;
        if ("FTP_QUARTERLY_GUIDANCE".equals(bizType)) {
            relatedProcesses = quarterlyGuidanceService.findRelatedProcess(belongId);
            materialsType = FtpQuarterlyMaterialsEnum.DEFAULT.name();
        } else if ("FTP_MONTHLY_GUIDANCE".equals(bizType)) {
            relatedProcesses = monthlyGuidanceService.findRelatedProcess(belongId);
            materialsType = FtpMonthlyMaterialsEnum.DEFAULT.name();
        }
        if (ObjectUtil.isNotEmpty(relatedProcesses)) {
            throw new MithrasException("数据处于审批流程中，不能上传!");
        }
        assertFtpOperator();
        materialsListService.add(file, belongId, materialsType, bizType);
    }

    @Override
    public void fileRemove(FtpBatchIdsReq req) {
        assertFtpOperator();
        materialsListService.remove(req.getIds());
    }

    @Override
    public FileListRSP fileDownload(FtpBatchIdsReq req, ServletOutputStream outputStream) {
        if (CollectionUtils.isEmpty(req.getIds())) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        List<MaterialsList> materials = materialsListService.getByIds(req.getIds());
        if (materials.size() == 1) {
            return materialsListService.download(req.getIds().get(0));
        }
        materialsListService.download(outputStream, req.getIds());
        return null;
    }

    private void assertFtpOperator() {
        if (!sysUserService.currentUserIsSpecificDept("JHCWB", "ZJGLB") && !sysUserService.adminAuth()) {
            throw new MithrasException("只有计划财务部及资金管理部员工可以操作");
        }
    }
}

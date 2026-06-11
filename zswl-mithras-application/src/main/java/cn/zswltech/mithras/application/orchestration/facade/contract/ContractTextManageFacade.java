package cn.zswltech.mithras.application.orchestration.facade.contract;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.contract.application.ContractTextManageApplicationService;
import cn.zswltech.mithras.dto.contract.text.*;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.application.orchestration.contract.text.ContractTextManageService;
import cn.zswltech.mithras.application.orchestration.contract.text.ContractTextSignInfoService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * @author bigbear
 * @date 2024/11/18 17:06
 * @description
 */
@Slf4j
@Service
public class ContractTextManageFacade implements ContractTextManageApplicationService {

    @Resource
    private ContractTextManageService contractTextManageService;
    @Resource
    private ContractTextSignInfoService contractTextSignInfoService;
    @Resource
    private HttpServletResponse httpServletResponse;


    @Override
    public R<PageR<ContractTextManageListRSP>> list(ContractTextManageListREQ req) {
        return R.ok(contractTextManageService.list(req));
    }

    @Override
    public R<List<ContractTextManageUnSignDetailRSP>> unSignedDetail(ContractTextManageUnSignDetailREQ req) {
        return R.ok(contractTextManageService.unSignedDetail(req));
    }

    @Override
    public R<List<FileListRSP>> signedDetail(ContractTextManageSignedDetailREQ req) {
        return R.ok(contractTextManageService.signedDetail(req));
    }

    @Override
    public R<Void> downloadAll(ContractTextManageDownloadAllREQ req) {
        try {
            contractTextManageService.downloadAll(httpServletResponse, req);
        } catch (IOException e) {
            log.error("导出失败", e);
            return R.fail("导出失败, 请稍后再试或者联系管理员");
        }
        return R.ok();
    }

    @Override
    public R<Void> updateDefaultSigningWay(ContractTextManageUpdateDefaultSigningWayREQ req) {
        contractTextManageService.updateDefaultSigningWay(req);
        return R.ok();
    }

    @Override
    public R<Void> updateSingleSigningWay(ContractTextManageUpdateSingleSigningWayREQ req) {
        contractTextManageService.updateSingleSigningWay(req);
        return R.ok();
    }

    @Override
    public R<String> batchSign(ContractTextManageBatchSignREQ req) {
        return R.ok(contractTextSignInfoService.batchSign(req));
    }

    @Override
    public R<Void> singleSign(ContractTextManageSingleSignREQ req) {
        contractTextSignInfoService.singleSign(req);
        return R.ok();
    }

    @Override
    public R<Void> downloadWaitSign(ContractTextManageDownloadWaitSignREQ req) {
        try {
            contractTextManageService.downloadWaitSign(httpServletResponse, req);
        } catch (IOException e) {
            log.error("批量导出失败", e);
            return R.fail("批量导出失败, 请稍后再试或者联系管理员");
        }
        return R.ok();
    }

    @Override
    public R<List<ContractTextSignInfoSignPhotosAndVideosRSP>> signPhotosAndVideos(ContractTextSignInfoSignPhotosAndVideosREQ req) {
        return R.ok(contractTextManageService.getSignPhotosAndVideos(req));
    }
}

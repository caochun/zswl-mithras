package cn.zswltech.mithras.service.controller.riskcontrol;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.riskcontrol.RiskControlScoreCardApi;
import cn.zswltech.mithras.dto.file.FileDownLoadREQ;
import cn.zswltech.mithras.dto.file.FileDownLoadRSP;
import cn.zswltech.mithras.dto.file.FileUploadREQ;
import cn.zswltech.mithras.dto.riskcontrol.scorecard.*;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.controller.FileController;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.riskcontrol.common.RiskControlScoreCardFileTypeEnum;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.service.materialsfile.filecheck.handler.RiskControlScoreCardCheckHandler;
import cn.zswltech.mithras.riskcontrol.scorecard.application.RiskControlScoreCardAreaAndTargetService;
import cn.zswltech.mithras.riskcontrol.scorecard.application.RiskControlScoreCardService;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

/**
 * @ClassName 评分卡管理
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/2/27 3:34 下午
 * @Version 1.0
 **/
@RestController
public class RiskControlScoreCardController implements RiskControlScoreCardApi {

    @Resource
    private RiskControlScoreCardService riskControlScoreCardService;
    @Resource
    private FileController fileController;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private RiskControlScoreCardAreaAndTargetService riskControlScoreCardAreaAndTargetService;
    @Resource
    private RiskControlScoreCardCheckHandler riskControlScoreCardCheckHandler;

    @Override
    public R<Void> importFile(RiskControlScoreCordImportREQ req) {
        FileUploadREQ fileUploadREQ = new FileUploadREQ();
        fileUploadREQ.setFile(req.getFile());
        fileUploadREQ.setModuleType(BusinessModuleEnum.RISK_CONTROL_SCORE_CARD.name());
        fileUploadREQ.setMaterialsType(RiskControlScoreCardFileTypeEnum.ECONOMIC_DATA.name());
        fileUploadREQ.setMainId((long) LocalDate.now().getYear());
        //权限检查
        riskControlScoreCardCheckHandler.checkUpload(fileUploadREQ.getModuleType(), fileUploadREQ.getMainId(), fileUploadREQ.getMaterialsType());
        riskControlScoreCardService.importFile(req);
        materialsListService.remove(Wrappers.<MaterialsList>lambdaQuery()
        .eq(MaterialsList::getBusinessType, BusinessModuleEnum.RISK_CONTROL_SCORE_CARD.name())
        .eq(MaterialsList::getBelongId, LocalDate.now().getYear()));
        //保存文件
        fileController.upload(fileUploadREQ);
        return R.ok();
    }

    @Override
    public R<FileDownLoadRSP> downLoad(RiskControlScoreCordDownloadREQ req) {
        if(ObjectUtil.isEmpty(req.getYear())){
            req.setYear(LocalDate.now().getYear());
        }
        MaterialsList one = materialsListService.getOne(Wrappers.<MaterialsList>lambdaQuery()
                .eq(MaterialsList::getBusinessType, BusinessModuleEnum.RISK_CONTROL_SCORE_CARD.name())
                .eq(MaterialsList::getBelongId, req.getYear())
                .last(StringUtil.mysqlLimitOne()));
        if(ObjectUtil.isEmpty(one)){
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        FileDownLoadREQ fileDownLoadREQ = new FileDownLoadREQ();
        fileDownLoadREQ.setFileId(one.getId());
        fileDownLoadREQ.setMainId(one.getMainId());
        fileDownLoadREQ.setModuleType(one.getBusinessType());
        return fileController.download(fileDownLoadREQ);
    }

   /* @Override
    public R<Void> effect(@Valid RiskControlScoreEffectREQ req) {
        if(!riskControlScoreCardService.effect(req.getId())){
            throw new MithrasException("指标权重相加不等于100");
        }
        return R.ok();
    }*/

    @Override
    public R<List<RiskControlScoreCordAreaSearchRSP>> areaSearch(@Valid RiskControlScoreCordAreaSearchREQ req) {
        if(ObjectUtil.isEmpty(req.getYear())){
            req.setYear(LocalDate.now().getYear());
        }
        return R.ok(riskControlScoreCardService.areaSearch(req));
    }

    @Override
    public R<List<RiskControlScoreCordAreaAllRSP>> areaAll(RiskControlScoreCordAreaSearchREQ req) {
        if(ObjectUtil.isEmpty(req.getYear())){
            req.setYear(LocalDate.now().getYear());
        }
        return R.ok(riskControlScoreCardService.areaAll(req));
    }

    @Override
    public R<RiskControlScoreCordTryCalculateRSP> tryCalculate(RiskControlScoreCordTryCalculateREQ req) {
        return R.ok(riskControlScoreCardService.tryCalculate(req));
    }

    @Override
    public R<RiskControlScoreCordChangeCardRSP> calculate(@Valid RiskControlScoreCordCalculateREQ req) {
        return R.ok(riskControlScoreCardService.calculate(req));
    }

    @Override
    public R<Void> calculateSave(RiskControlScoreCordCalculateSaveREQ req) {
        riskControlScoreCardAreaAndTargetService.save(req);
        return R.ok();
    }

    @Override
    public R<RiskControlScoreCordCalculateDetailRSP> calculateDetail(RiskControlScoreCordCalculateDetailREQ req) {
        if(ObjectUtil.isEmpty(req.getYear())){
            req.setYear(LocalDate.now().getYear());
        }
        return R.ok(riskControlScoreCardAreaAndTargetService.calculateDetail(req));
    }
}

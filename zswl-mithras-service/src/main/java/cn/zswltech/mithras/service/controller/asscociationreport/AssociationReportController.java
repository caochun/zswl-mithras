package cn.zswltech.mithras.service.controller.asscociationreport;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.api.associationreport.AssociationReportApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.associationreport.AssociationReportException;
import cn.zswltech.mithras.associationreport.excel.AssociationReportBaseModel;
import cn.zswltech.mithras.associationreport.service.*;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.associationreport.*;
import cn.zswltech.mithras.common.annotation.Log;
import cn.zswltech.mithras.service.config.redis.RedisDistLock;
import cn.zswltech.mithras.service.constant.AssociationReportConstants;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.DataSource;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.associationreport.AssociationProcessStatusEnum;
import cn.zswltech.mithras.service.enums.associationreport.AssociationReportCategoryEnum;
import cn.zswltech.mithras.service.enums.associationreport.AssociationReportPeriodCategoryEnum;
import cn.zswltech.mithras.common.enums.BusinessType;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationReport;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationReportApply;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationReportDataAccess;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.service.constant.ResultMsg.CONCURRENT_OPERATION;

/**
 * @author dingqi
 * @date 2025/4/18
 * @description
 */
@Slf4j
@RestController
public class AssociationReportController implements AssociationReportApi {

    protected static final String DICT_UNKNOWN_CODE = "DICT_UNKNOWN_CODE";

    @Resource
    private AssociationReportService associationReportService;
    @Resource
    private AssociationDictionaryService associationDictionaryService;
    @Resource
    private AssociationBasicSituationService associationBasicSituationService;
    @Resource
    private AssociationReportApplyService associationReportApplyService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private UserService userService;
    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private RedisDistLock redisDistLock;


    @Override
    public R<Map<String, Map<String, String>> > getAssociationDict(){
        return R.ok(associationDictionaryService.getCode2DisplayMap());
    }

    @Override
    public R<AssociationReportImportRSP> push(@Valid AssociationReportPushREQ req) {
        AssociationReportImportRSP rsp = new AssociationReportImportRSP();
        try {
            associationReportService.pushSubmit(req.getIds());
        } catch (AssociationReportException e) {
            rsp.setImportSuccess(Boolean.FALSE);
            rsp.setErrorMessageList(e.getErrorMessageList());
            return R.ok(rsp);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("金融局报送-推送数据提交流程发生未知异常", e);
            throw new MithrasException("推送数据提交流程发生未知异常");
        }
        rsp.setImportSuccess(Boolean.TRUE);
        return R.ok(rsp);
    }

    @Override
    public R<String> getTemplateFileUrl(@Valid AssociationReportTemplateDownloadREQ req) {
        return R.ok(associationReportService.getTemplateFileUrl(req.getReportCategoryCode()));
    }

    @Override
    public R<String> create(@Valid AssociationReportCreateREQ req) {
        Set<String> addReportCategoryCodes = SpringUtil.getBean(AssociationReportDataAccessService.class).getCurrentUserAccessAddReportCategoryCodes();
        if (CollectionUtil.isEmpty(addReportCategoryCodes) || !addReportCategoryCodes.contains(req.getReportCategoryCode())) {
            throw new MithrasException("无权限新增该报表");
        }
        req.setDataSource(DataSource.MANUAL.getDisplay());
        return R.ok(associationReportService.create(req));
    }

    @Override
    public R<AssociationReportImportRSP> importExcel(@Valid AssociationReportImportREQ req) {
        AssociationReport associationReport = associationReportService.findByReportInstanceId(req.getReportInstanceId());
        Set<String> addReportCategoryCodes = SpringUtil.getBean(AssociationReportDataAccessService.class).getCurrentUserAccessAddReportCategoryCodes();
        if (CollectionUtil.isEmpty(addReportCategoryCodes) || !addReportCategoryCodes.contains(associationReport.getReportCategoryCode())) {
            throw new MithrasException("无权限导入该报表数据");
        }
        return R.ok(associationReportService.storeFromExcel(req));
    }

    @Override
    public R<PageR<AssociationReportListRSP>> pageList(@Valid AssociationReportListREQ req) {
        return R.ok(associationReportService.pageList(req));
    }

    @Override
    public R<Void> delete(@Valid SinglePkREQ req) {
        AssociationReport associationReport = associationReportService.getById(req.getId());
        if (ObjectUtil.isNull(associationReport)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        //系统生成的数据不能删除
        if (DataSource.SYSTEM.getDisplay().equals(associationReport.getDataSource())) {
            throw new MithrasException("系统生成数据不能删除");
        }
        Set<String> addReportCategoryCodes = SpringUtil.getBean(AssociationReportDataAccessService.class).getCurrentUserAccessAddReportCategoryCodes();
        if (CollectionUtil.isEmpty(addReportCategoryCodes) || !addReportCategoryCodes.contains(associationReport.getReportCategoryCode())) {
            throw new MithrasException("无权限删除该报表");
        }
        associationReportService.deleteById(req.getId());
        return R.ok();
    }

    @Override
    public R<AssociationDetailBasicSituationRSP> detailBasicSituation(@Valid AssociationReportDetailREQ req) {
        if (StringUtils.isBlank(req.getVersion())) {
            return R.ok(SpringUtil.getBean(AssociationBasicSituationService.class).getByReportInstanceId(req.getReportInstanceId()));
        } else {
            return R.ok(SpringUtil.getBean(AssociationBasicSituationLibService.class).getByReportInstanceIdAndVersion(req.getReportInstanceId(), req.getVersion()));
        }
    }

    @Override
    @Log(title = "金融局报送", businessType = BusinessType.UPDATE)
    public R<AssociationReportModifyRSP> modifyBasicSituation(AssociationBasicSituationModifyREQ req) {
        Set<String> addReportCategoryCodes = SpringUtil.getBean(AssociationReportDataAccessService.class).getCurrentUserAccessAddReportCategoryCodes();
        if (CollectionUtil.isEmpty(addReportCategoryCodes) || !addReportCategoryCodes.contains(AssociationReportCategoryEnum.J0001.name())) {
            throw new MithrasException("无权限编辑该报表");
        }
        AssociationReportModifyRSP rsp = new AssociationReportModifyRSP();
        try {
            // 数据校验
            this.checkBasicSituation(req);
            associationBasicSituationService.modify(req);
        } catch (AssociationReportException e) {
            rsp.setModifySuccess(Boolean.FALSE);
            rsp.setErrorMessageList(e.getErrorMessageList());
            return R.ok(rsp);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            throw new MithrasException("修改数据发生未知异常");
        }
        rsp.setModifySuccess(Boolean.TRUE);
        return R.ok(rsp);
    }

    private void checkBasicSituation(AssociationBasicSituationModifyREQ e) {
        List<String> errorList = new ArrayList<>();
            if (ObjectUtil.isEmpty(e.getEconClasCode()) || Objects.equals(e.getEconClasCode(), DICT_UNKNOWN_CODE)) {
                errorList.add("序号9：经济成分，必填字段,取值字典范围");
            }

            if (Objects.equals(e.getEconClasCode(), "01") && (ObjectUtil.isEmpty(e.getCtarCorpHoldFlag()) || ObjectUtil.isEmpty(e.getLcalSoeHoldFlag()))) {
                errorList.add("序号10、11：经济成分为国有控股时，请选择控股标志");
            }

            if (Objects.equals(e.getCtarCorpHoldFlag(), DICT_UNKNOWN_CODE)) {
                errorList.add("序号10：是否中央企业控股，取值字典范围");
            }

            if (Objects.equals(e.getLcalSoeHoldFlag(), DICT_UNKNOWN_CODE)) {
                errorList.add("序号11：是否地方国企控股，取值字典范围");
            }

            if (Objects.equals(e.getCorpClasCode(), DICT_UNKNOWN_CODE)) {
                errorList.add("序号15：内资/内资试点/外资，取值字典范围");
            }

            if (Objects.equals(e.getMnfrFlag(), DICT_UNKNOWN_CODE)) {
                errorList.add("序号16：厂商系/非厂商系，取值字典范围");
            }

            if (Objects.equals(e.getListFlag(), DICT_UNKNOWN_CODE)) {
                errorList.add("序号17：上市/非上市，取值字典范围");
            }

        if (CollectionUtil.isNotEmpty(errorList)) {
            throw new AssociationReportException(errorList);
        }
    }

    @Override
    public R<List<AssociationDetailShahStorInfoRSP>> detailShahStorInfo(@Valid AssociationReportDetailREQ req) {
        if (StringUtils.isBlank(req.getVersion())) {
            return R.ok(SpringUtil.getBean(AssociationShahStorInfoService.class).listByReportInstanceId(req.getReportInstanceId()));
        } else {
            return R.ok(SpringUtil.getBean(AssociationShahStorInfoLibService.class).listByReportInstanceIdAndVersion(req.getReportInstanceId(), req.getVersion()));
        }
    }


    @Override
    @Log(title = "金融局报送", businessType = BusinessType.UPDATE)
    public R<AssociationReportModifyRSP> modifyShahStorInfo(@RequestBody @Valid AssociationShahStorInfoModifyListREQ req){
        Set<String> addReportCategoryCodes = SpringUtil.getBean(AssociationReportDataAccessService.class).getCurrentUserAccessAddReportCategoryCodes();
        if (CollectionUtil.isEmpty(addReportCategoryCodes) || !addReportCategoryCodes.contains(AssociationReportCategoryEnum.J0002.name())) {
            throw new MithrasException("无权限编辑该报表");
        }
        AssociationReportModifyRSP rsp = new AssociationReportModifyRSP();
        try {
            if(req.getDataList().isEmpty()){//列表为空，删除所有
                SpringUtil.getBean(AssociationShahStorInfoService.class).deleteByReportInstanceId(req.getReportInstanceId());
            }else {
                // 数据校验
                this.checkShahStorInfo(req.getDataList());
                req.getDataList().forEach(e -> e.setReportInstanceId(req.getReportInstanceId()));
                SpringUtil.getBean(AssociationShahStorInfoService.class).modifyBatch(req.getDataList());
            }
        } catch (AssociationReportException e) {
            rsp.setModifySuccess(Boolean.FALSE);
            rsp.setErrorMessageList(e.getErrorMessageList());
            return R.ok(rsp);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            throw new MithrasException("修改数据发生未知异常");
        }
        rsp.setModifySuccess(Boolean.TRUE);
        return R.ok(rsp);
    }

    private void checkShahStorInfo(List<AssociationShahStorInfoModifyREQ> dataList) {
        List<String> errorList = new ArrayList<>();
        dataList.forEach(e -> {
            if (Objects.equals(e.getShahCharCode(), DICT_UNKNOWN_CODE)) {
                errorList.add("股东性质，取值字典范围");
            }

            if (Objects.equals(e.getShahGtoMode(), DICT_UNKNOWN_CODE)) {
                errorList.add("进入方式，取值字典范围");
            }

            if (Objects.equals(e.getStorTranFlag(), DICT_UNKNOWN_CODE)) {
                errorList.add("股权转让，取值字典范围");
            }

        });
        if (CollectionUtil.isNotEmpty(errorList)) {
            throw new AssociationReportException(errorList);
        }
    }


    @Override
    public R<List<AssociationDetailShahChangeInfoRSP>> detailShahChangeInfo(@Valid AssociationReportDetailREQ req) {
        if (StringUtils.isBlank(req.getVersion())) {
            return R.ok(SpringUtil.getBean(AssociationShahChangeInfoService.class).listByReportInstanceId(req.getReportInstanceId()));
        } else {
            return R.ok(SpringUtil.getBean(AssociationShahChangeInfoLibService.class).listByReportInstanceIdAndVersion(req.getReportInstanceId(), req.getVersion()));
        }
    }

    @Override
    @Log(title = "金融局报送", businessType = BusinessType.UPDATE)
    public R<AssociationReportModifyRSP> modifyShahChangeInfo(@RequestBody @Valid AssociationShahChangeInfoModifyListREQ req){
        Set<String> addReportCategoryCodes = SpringUtil.getBean(AssociationReportDataAccessService.class).getCurrentUserAccessAddReportCategoryCodes();
        if (CollectionUtil.isEmpty(addReportCategoryCodes) || !addReportCategoryCodes.contains(AssociationReportCategoryEnum.J0003.name())) {
            throw new MithrasException("无权限编辑该报表");
        }
        AssociationReportModifyRSP rsp = new AssociationReportModifyRSP();
        try {
            if(req.getDataList().isEmpty()){//列表为空，删除所有
                SpringUtil.getBean(AssociationShahChangeInfoService.class).deleteByReportInstanceId(req.getReportInstanceId());
            }else {
                // 数据校验
                this.checkShahChangeInfo(req.getDataList());
                req.getDataList().forEach(e -> e.setReportInstanceId(req.getReportInstanceId()));
                SpringUtil.getBean(AssociationShahChangeInfoService.class).modifyBatch(req.getDataList());
            }
        } catch (AssociationReportException e) {
            rsp.setModifySuccess(Boolean.FALSE);
            rsp.setErrorMessageList(e.getErrorMessageList());
            return R.ok(rsp);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            throw new MithrasException("修改数据发生未知异常");
        }
        rsp.setModifySuccess(Boolean.TRUE);
        return R.ok(rsp);
    }

    private void checkShahChangeInfo(List<AssociationShahChangeInfoModifyREQ> dataList) {
        List<String> errorList = new ArrayList<>();
        dataList.forEach(e -> {
            if (Objects.equals(e.getShahCharCode(), DICT_UNKNOWN_CODE)) {
                errorList.add("股东性质，取值字典范围");
            }

            if (Objects.equals(e.getShahGtoMode(), DICT_UNKNOWN_CODE)) {
                errorList.add("进入方式，取值字典范围");
            }

            if (Objects.equals(e.getStorTranFlag(), DICT_UNKNOWN_CODE)) {
                errorList.add("股权转让，取值字典范围");
            }

        });
        if (CollectionUtil.isNotEmpty(errorList)) {
            throw new AssociationReportException(errorList);
        }
    }

    @Override
    public R<List<AssociationDetailSeniorExecutiveInfoRSP>> detailSeniorExecutiveInfo(@Valid AssociationReportDetailREQ req) {
        if (StringUtils.isBlank(req.getVersion())) {
            return R.ok(SpringUtil.getBean(AssociationSeniorExecutiveInfoService.class).listByReportInstanceId(req.getReportInstanceId()));
        }else{
            return R.ok(SpringUtil.getBean(AssociationSeniorExecutiveInfoLibService.class).listByReportInstanceIdAndVersion(req.getReportInstanceId(), req.getVersion()));
        }

    }

    @Override
    @Log(title = "金融局报送", businessType = BusinessType.UPDATE)
    public R<AssociationReportModifyRSP> modifySeniorExecutiveInfo(@RequestBody @Valid AssociationSeniorExecutiveInfoModifyListREQ req){
        Set<String> addReportCategoryCodes = SpringUtil.getBean(AssociationReportDataAccessService.class).getCurrentUserAccessAddReportCategoryCodes();
        if (CollectionUtil.isEmpty(addReportCategoryCodes) || !addReportCategoryCodes.contains(AssociationReportCategoryEnum.J0004.name())) {
            throw new MithrasException("无权限编辑该报表");
        }
        AssociationReportModifyRSP rsp = new AssociationReportModifyRSP();
        try {
            if(req.getDataList().isEmpty()){//列表为空，删除所有
                SpringUtil.getBean(AssociationSeniorExecutiveInfoService.class).deleteByReportInstanceId(req.getReportInstanceId());
            }else {
                // 数据校验
                this.checkSeniorExecutiveInfo(req.getDataList());
                req.getDataList().forEach(e -> e.setReportInstanceId(req.getReportInstanceId()));
                SpringUtil.getBean(AssociationSeniorExecutiveInfoService.class).modifyBatch(req.getDataList());
            }
        } catch (AssociationReportException e) {
            rsp.setModifySuccess(Boolean.FALSE);
            rsp.setErrorMessageList(e.getErrorMessageList());
            return R.ok(rsp);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            throw new MithrasException("修改数据发生未知异常");
        }
        rsp.setModifySuccess(Boolean.TRUE);
        return R.ok(rsp);
    }

    private void checkSeniorExecutiveInfo(List<AssociationSeniorExecutiveInfoModifyREQ> dataList) {
        List<String> errorList = new ArrayList<>();
        dataList.forEach(e -> {
            if (Objects.equals(e.getCurrDutyCode(), DICT_UNKNOWN_CODE)) {
                errorList.add("现任职务，取值字典范围");
            }

            if (Objects.equals(e.getHighEduCode(), DICT_UNKNOWN_CODE)) {
                errorList.add("最高学历，取值字典范围");
            }

        });
        if (CollectionUtil.isNotEmpty(errorList)) {
            throw new AssociationReportException(errorList);
        }
    }

    @Override
    public R<List<AssociationDetailLawInvolvedVisitRelatedInfoRSP>> detailLawInvolvedVisitRelatedInfo(@Valid AssociationReportDetailREQ req) {
        if (StringUtils.isBlank(req.getVersion())) {
            return R.ok(SpringUtil.getBean(AssociationLawInvolvedVisitRelatedInfoService.class).listByReportInstanceId(req.getReportInstanceId()));
        }else{
            return R.ok(SpringUtil.getBean(AssociationLawInvolvedVisitRelatedInfoLibService.class).listByReportInstanceIdAndVersion(req.getReportInstanceId(), req.getVersion()));
        }
    }

    @Override
    @Log(title = "金融局报送", businessType = BusinessType.UPDATE)
    public R<AssociationReportModifyRSP> modifyLawInvolvedVisitRelatedInfo(@RequestBody @Valid AssociationLawInvolvedVisitRelatedInfoModifyListREQ req){
        Set<String> addReportCategoryCodes = SpringUtil.getBean(AssociationReportDataAccessService.class).getCurrentUserAccessAddReportCategoryCodes();
        if (CollectionUtil.isEmpty(addReportCategoryCodes) || !addReportCategoryCodes.contains(AssociationReportCategoryEnum.J0013.name())) {
            throw new MithrasException("无权限编辑该报表");
        }
        AssociationReportModifyRSP rsp = new AssociationReportModifyRSP();
        try {
            // 数据校验
            if(req.getDataList().isEmpty()){//列表为空，删除所有
                SpringUtil.getBean(AssociationLawInvolvedVisitRelatedInfoService.class).deleteByReportInstanceId(req.getReportInstanceId());
            }else {
                // 数据校验
                this.checkLawInvolvedVisitRelatedInfo(req.getDataList());
                req.getDataList().forEach(e -> e.setReportInstanceId(req.getReportInstanceId()));
                SpringUtil.getBean(AssociationLawInvolvedVisitRelatedInfoService.class).modifyBatch(req.getDataList());
            }
        } catch (AssociationReportException e) {
            rsp.setModifySuccess(Boolean.FALSE);
            rsp.setErrorMessageList(e.getErrorMessageList());
            return R.ok(rsp);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            throw new MithrasException("修改数据发生未知异常");
        }
        rsp.setModifySuccess(Boolean.TRUE);
        return R.ok(rsp);
    }

    private void checkLawInvolvedVisitRelatedInfo(List<AssociationLawInvolvedVisitRelatedInfoModifyREQ> dataList) {
        List<String> errorList = new ArrayList<>();
        dataList.forEach(e -> {
            if (Objects.equals(e.getCaseClasCode(), DICT_UNKNOWN_CODE)) {
                errorList.add("信息类别，取值字典范围");
            }
            if (Objects.equals(e.getCanbFlag(), DICT_UNKNOWN_CODE)) {
                errorList.add("是否销号，取值字典范围");
            }
        });
        if (CollectionUtil.isNotEmpty(errorList)) {
            throw new AssociationReportException(errorList);
        }
    }

    @Override
    public R<AssociationDetailMajorMattersBasicReportRSP> detailMajorMattersBasicReport(@Valid AssociationReportDetailREQ req) {
        if (StringUtils.isBlank(req.getVersion())) {
            return R.ok(SpringUtil.getBean(AssociationMajorMattersBasicReportService.class).getByReportInstanceId(req.getReportInstanceId()));
        }else{
            return R.ok(SpringUtil.getBean(AssociationMajorMattersBasicReportLibService.class).getByReportInstanceIdAndVersion(req.getReportInstanceId(), req.getVersion()));
        }
    }

    @Override
    @Log(title = "金融局报送", businessType = BusinessType.UPDATE)
    public R<AssociationReportModifyRSP> modifyMajorMattersBasicReport(AssociationMajorMattersBasicReportModifyREQ req) {
        Set<String> addReportCategoryCodes = SpringUtil.getBean(AssociationReportDataAccessService.class).getCurrentUserAccessAddReportCategoryCodes();
        if (CollectionUtil.isEmpty(addReportCategoryCodes) || !addReportCategoryCodes.contains(AssociationReportCategoryEnum.J0014.name())) {
            throw new MithrasException("无权限编辑该报表");
        }
        AssociationReportModifyRSP rsp = new AssociationReportModifyRSP();
        try {
            // 数据校验
            checkMajorMattersBasicReport(req);
            SpringUtil.getBean(AssociationMajorMattersBasicReportService.class).modify(req);
        } catch (AssociationReportException e) {
            rsp.setModifySuccess(Boolean.FALSE);
            rsp.setErrorMessageList(e.getErrorMessageList());
            return R.ok(rsp);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            throw new MithrasException("修改数据发生未知异常");
        }
        rsp.setModifySuccess(Boolean.TRUE);
        return R.ok(rsp);
    }

    private void checkMajorMattersBasicReport(AssociationMajorMattersBasicReportModifyREQ e) {

    }

    @Override
    public R<List<AssociationDetailMajorMattersEventReportRSP>> detailMajorMattersEventReport(@Valid AssociationReportDetailREQ req) {
        if (StringUtils.isBlank(req.getVersion())) {
            return R.ok(SpringUtil.getBean(AssociationMajorMattersEventReportService.class).listByReportInstanceId(req.getReportInstanceId()));
        }else{
            return R.ok(SpringUtil.getBean(AssociationMajorMattersEventReportLibService.class).listByReportInstanceIdAndVersion(req.getReportInstanceId(), req.getVersion()));
        }
    }

    @Override
    @Log(title = "金融局报送", businessType = BusinessType.UPDATE)
    public R<AssociationReportModifyRSP> modifyMajorMattersEventReport(@RequestBody @Valid AssociationMajorMattersEventReportModifyListREQ req){
        Set<String> addReportCategoryCodes = SpringUtil.getBean(AssociationReportDataAccessService.class).getCurrentUserAccessAddReportCategoryCodes();
        if (CollectionUtil.isEmpty(addReportCategoryCodes) || !addReportCategoryCodes.contains(AssociationReportCategoryEnum.J0015.name())) {
            throw new MithrasException("无权限编辑该报表");
        }
        AssociationReportModifyRSP rsp = new AssociationReportModifyRSP();
        try {
            // 数据校验
            if(req.getDataList().isEmpty()){//列表为空，删除所有
                SpringUtil.getBean(AssociationMajorMattersEventReportService.class).deleteByReportInstanceId(req.getReportInstanceId());
            }else {
                // 数据校验
                this.checkMajorMattersEventReport(req.getDataList());
                req.getDataList().forEach(e -> e.setReportInstanceId(req.getReportInstanceId()));
                SpringUtil.getBean(AssociationMajorMattersEventReportService.class).modifyBatch(req.getDataList());
            }
        } catch (AssociationReportException e) {
            rsp.setModifySuccess(Boolean.FALSE);
            rsp.setErrorMessageList(e.getErrorMessageList());
            return R.ok(rsp);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            throw new MithrasException("修改数据发生未知异常");
        }
        rsp.setModifySuccess(Boolean.TRUE);
        return R.ok(rsp);
    }

    private void checkMajorMattersEventReport(List<AssociationMajorMattersEventReportModifyREQ> dataList) {

    }


    @Override
    public R<AssociationDetailBusinessSituationRSP> detailBusinessSituation(@Valid AssociationReportDetailREQ req) {
        if (StringUtils.isBlank(req.getVersion())) {
            return R.ok(SpringUtil.getBean(AssociationBusinessSituationService.class).getByReportInstanceId(req.getReportInstanceId()));
        }else{
            return R.ok(SpringUtil.getBean(AssociationBusinessSituationLibService.class).getByReportInstanceIdAndVersion(req.getReportInstanceId(), req.getVersion()));
        }
    }

    @Override
    @Log(title = "金融局报送", businessType = BusinessType.UPDATE)
    public R<AssociationReportModifyRSP> modifyBusinessSituation(AssociationBusinessSituationModifyREQ req) {
        Set<String> addReportCategoryCodes = SpringUtil.getBean(AssociationReportDataAccessService.class).getCurrentUserAccessAddReportCategoryCodes();
        if (CollectionUtil.isEmpty(addReportCategoryCodes) || !addReportCategoryCodes.contains(AssociationReportCategoryEnum.J0005.name())) {
            throw new MithrasException("无权限编辑该报表");
        }
        AssociationReportModifyRSP rsp = new AssociationReportModifyRSP();
        try {
            // 数据校验
            this.checkBusinessSituation(req);
            SpringUtil.getBean(AssociationBusinessSituationService.class).modify(req);
        } catch (AssociationReportException e) {
            rsp.setModifySuccess(Boolean.FALSE);
            rsp.setErrorMessageList(e.getErrorMessageList());
            return R.ok(rsp);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            throw new MithrasException("修改数据发生未知异常");
        }
        rsp.setModifySuccess(Boolean.TRUE);
        return R.ok(rsp);
    }
    private void checkBusinessSituation(AssociationBusinessSituationModifyREQ e) {
        List<String> errorList = new ArrayList<>();
            // 融资租赁投放额期初数必填
            if (isNotEqualBigDecimalSum(e.getFnlRelsAbop(), e.getDirtLeasRelsAbop(), e.getSlbkRelsAbop())) {
                errorList.add("（期初数）序号14：融资租赁投放额=序号15：直接租赁投放额+序号16：售后回租投放额");
            }
            // 总收入_期初数必填
            if (isNotEqualBigDecimalSum(e.getTotIncmAbop(), e.getOperLeasBusiIncmAbop() , e.getFnlBusiIncmAbop(), e.getOthIncmAbop())) {
                errorList.add("（期初数）序号1：总收入=序号2：经营租赁业务收入+序号3：融资租赁业务收入+序号6：其他收入");
            }

            // 融资租赁业务收入_期初数必填
            if (isNotEqualBigDecimalSum(e.getFnlBusiIncmAbop(), e.getIntrIncmAbop(), e.getFeeIncmAbop())) {
                errorList.add("（期初数）序号3：融资租赁业务收入=序号4：利息收入+序号5：费用收入");
            }

            // 租赁资产_期初数必填
            if (isNotEqualBigDecimalSum(e.getLeasAstAbop(), e.getOperLeasAstAbop(), e.getFinLeasAstAbop())) {
                errorList.add("（期初数）序号7：租赁资产=序号8：经营租赁资产+序号9：融资租赁资产");
            }

            // 直接租赁资产_期初数必填
            if (isNotEqualBigDecimalSum(e.getFinLeasAstAbop(), e.getDirtLeasAstAbop(), e.getSlbkAstAbop())) {
                errorList.add("（期初数）序号9：融资租赁资产=序号10：直接租赁资产+序号11：售后回租资产");
            }

            // 融资租赁投放额_期末数必填
            if (isNotEqualBigDecimalSum(e.getFnlRelsAeop(), e.getDirtLeasRelsAeop(), e.getSlbkRelsAeop())) {
                errorList.add("（期末数）序号14：融资租赁投放额=序号15：直接租赁投放额+序号16：售后回租投放额");
            }

            // 总收入_期末数必填
            if (isNotEqualBigDecimalSum(e.getTotIncmAeop(), e.getOperLeasBusiIncmAeop(), e.getFnlBusiIncmAeop(), e.getOthIncmAeop())) {
                errorList.add("（期末数）序号1：总收入=序号2：经营租赁业务收入+序号3：融资租赁业务收入+序号6：其他收入");
            }

            // 利息收入_期末数必填
            if (isNotEqualBigDecimalSum(e.getFnlBusiIncmAeop(), e.getIntrIncmAeop(), e.getFeeIncmAeop())) {
                errorList.add("（期末数）序号3：融资租赁业务收入=序号4：利息收入+序号5：费用收入");
            }

            // 租赁资产_期末数必填
            if (isNotEqualBigDecimalSum(e.getLeasAstAeop(), e.getOperLeasAstAeop(), e.getFinLeasAstAeop())) {
                errorList.add("（期末数）序号7：租赁资产=序号8：经营租赁资产+序号9：融资租赁资产");
            }

            //直接租赁资产_期末数必填
            if (isNotEqualBigDecimalSum(e.getFinLeasAstAeop(), e.getDirtLeasAstAeop(), e.getSlbkAstAeop())) {
                errorList.add("（期末数）序号9：融资租赁资产=序号10：直接租赁资产+序号11：售后回租资产");
            }
        if (CollectionUtil.isNotEmpty(errorList)) {
            throw new AssociationReportException(errorList);
        }
    }

    private boolean isNotEqualBigDecimalSum(BigDecimal target, BigDecimal... addends) {
        // 处理目标值为 null 的情况（视为 0）
        BigDecimal targetValue = target != null ? target : BigDecimal.ZERO;

        // 计算加数的和（每个加数为 null 时视为 0）
        BigDecimal sum = BigDecimal.ZERO;
        if (addends != null) {
            for (BigDecimal addend : addends) {
                BigDecimal validAddend = (addend != null) ? addend : BigDecimal.ZERO;
                sum = sum.add(validAddend);
            }
        }

        // 比较数值是否相等（compareTo 返回 0 表示数值相等，不考虑精度）
        return !(targetValue.compareTo(sum) == 0);
    }

    @Override
    public R<AssociationDetailProfitRSP> detailCompanyProfit(@Valid AssociationReportDetailREQ req) {
        if (StringUtils.isBlank(req.getVersion())) {
            return R.ok(SpringUtil.getBean(AssociationCompanyProfitStatementService.class).getByReportInstanceId(req.getReportInstanceId()));
        }else {
            return R.ok(SpringUtil.getBean(AssociationCompanyProfitStatementLibService.class).getByReportInstanceIdAndVersion(req.getReportInstanceId(), req.getBusinessVersion()));
        }
    }

    @Override
    @Log(title = "金融局报送", businessType = BusinessType.UPDATE)
    public R<AssociationReportModifyRSP> modifyCompanyProfit(AssociationCompanyProfitStatementModifyREQ req) {
        Set<String> addReportCategoryCodes = SpringUtil.getBean(AssociationReportDataAccessService.class).getCurrentUserAccessAddReportCategoryCodes();
        if (CollectionUtil.isEmpty(addReportCategoryCodes) || !addReportCategoryCodes.contains(AssociationReportCategoryEnum.J0008.name())) {
            throw new MithrasException("无权限编辑该报表");
        }
        AssociationReportModifyRSP rsp = new AssociationReportModifyRSP();
        try {
            // 数据校验
            this.checkCompanyProfit(req);
            SpringUtil.getBean(AssociationCompanyProfitStatementService.class).modify(req);
        } catch (AssociationReportException e) {
            rsp.setModifySuccess(Boolean.FALSE);
            rsp.setErrorMessageList(e.getErrorMessageList());
            return R.ok(rsp);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            throw new MithrasException("修改数据发生未知异常");
        }
        rsp.setModifySuccess(Boolean.TRUE);
        return R.ok(rsp);
    }

    private void checkCompanyProfit(AssociationCompanyProfitStatementModifyREQ e) {
        List<String> errorList = new ArrayList<>();
            if (isNotEqualBigDecimalSum(e.getBusiProfCply(),
                    bigDecimalAddSum(e.getMainBusiProfCply(), e.getOthBusiProfCply()).subtract(bigDecimalAddSum(e.getBusiFeeCply(), e.getMagFeeCply(), e.getFinFeeCply(), e.getIpoaLossCply(), e.getCredDecrLossCply())))) {
                errorList.add("（上年同期）行号11：营业利润=行号4：主营业务利润+行号5：其他业务利润-行号6：营业费用-行号7：管理费用-行号8：财务费用-行号9：资产减值损失-行号10：信用减值损失");
            }
            if (isNotEqualBigDecimalSum(e.getProfGamtCply(), bigDecimalAddSum(e.getBusiProfCply(), e.getIvsmPayfCply(), e.getNoprIncmCply()).subtract(bigDecimalAddSum(e.getNoprPayCply())))) {
                errorList.add("（上年同期）行号15：利润总额=行号11：营业利润+行号12：投资收益+行号13：营业外收入-行号14：营业外支出");
            }

            if (isNotEqualBigDecimalSum(e.getNetProfCply(), bigDecimalAddSum(e.getProfGamtCply()).subtract(bigDecimalAddSum(e.getInctFeeCply())))) {
                errorList.add("（上年同期）行号17：净利润=行号15：利润总额-行号16：所得税费用");
            }

            if (isNotEqualBigDecimalSum(e.getMainBusiProfCply(), bigDecimalAddSum(e.getMainBusiIncmCply()).subtract(bigDecimalAddSum(e.getMainBusiCostCply(), e.getMainBusiTaxAddCply())))) {
                errorList.add("（上年同期）行号4：主营业务利润=行号1：主营业务收入-行号2：主营业务成本-行号3：主营业务税金及附加");
            }

            if (isNotEqualBigDecimalSum(e.getBusiProfActm(), bigDecimalAddSum(e.getMainBusiProfActm(), e.getOthBusiProfActm()).subtract(bigDecimalAddSum(e.getBusiFeeActm(), e.getMagFeeActm(), e.getFinFeeActm(), e.getIpoaLossActm(), e.getCredDecrLossActm())))) {
                errorList.add("（本季金额）行号11：营业利润=行号4：主营业务利润+行号5：其他业务利润-行号6：营业费用-行号7：管理费用-行号8：财务费用-行号9：资产减值损失-行号10：信用减值损失");
            }

            if (isNotEqualBigDecimalSum(e.getProfGamtActm(), bigDecimalAddSum(e.getBusiProfActm(), e.getIvsmPayfActm(), e.getNoprIncmActm()).subtract(bigDecimalAddSum(e.getNoprPayActm())))) {
                errorList.add("（本季金额）行号15：利润总额=行号11：营业利润+行号12：投资收益+行号13：营业外收入-行号14：营业外支出");
            }

            if (isNotEqualBigDecimalSum(e.getNetProfActm(), bigDecimalAddSum(e.getProfGamtActm()).subtract(bigDecimalAddSum(e.getInctFeeActm())))) {
                errorList.add("（本季金额）行号17：净利润=行号15：利润总额-行号16：所得税费用");
            }

            if (isNotEqualBigDecimalSum(e.getMainBusiProfActm(), bigDecimalAddSum(e.getMainBusiIncmActm()).subtract(bigDecimalAddSum(e.getMainBusiCostActm(), e.getMainBusiTaxAddActm())))) {
                errorList.add("（本季金额）行号4：主营业务利润=行号1：主营业务收入-行号2：主营业务成本-行号3：主营业务税金及附加");
            }

            if (isNotEqualBigDecimalSum(e.getBusiProfTyag(), bigDecimalAddSum(e.getMainBusiProfTyag(), e.getOthBusiProfTyag()).subtract(bigDecimalAddSum(e.getBusiFeeTyag(), e.getMagFeeTyag(), e.getFinFeeTyag(), e.getIpoaLossTyag(), e.getCredDecrLossTyag())))) {
                errorList.add("（本年累计）行号11：营业利润=行号4：主营业务利润+行号5：其他业务利润-行号6：营业费用-行号7：管理费用-行号8：财务费用-行号9：资产减值损失-行号10：信用减值损失");
            }

            if (isNotEqualBigDecimalSum(e.getProfGamtTyag(), bigDecimalAddSum(e.getBusiProfTyag(), e.getIvsmPayfTyag(), e.getNoprIncmTyag()).subtract(bigDecimalAddSum(e.getNoprPayTyag())))) {
                errorList.add("（本年累计）行号15：利润总额=行号11：营业利润+行号12：投资收益+行号13：营业外收入-行号14：营业外支出");
            }
            if (isNotEqualBigDecimalSum(e.getNetProfTyag(), bigDecimalAddSum(e.getProfGamtTyag()).subtract(bigDecimalAddSum(e.getInctFeeTyag())))) {
                errorList.add("（本年累计）行号17：净利润=行号15：利润总额-行号16：所得税费用");
            }
            if (isNotEqualBigDecimalSum(e.getMainBusiProfTyag(), bigDecimalAddSum(e.getMainBusiIncmTyag()).subtract(bigDecimalAddSum(e.getMainBusiCostTyag(), e.getMainBusiTaxAddTyag())))) {
                errorList.add("（本年累计）行号4：主营业务利润=行号1：主营业务收入-行号2：主营业务成本-行号3：主营业务税金及附加");
            }
        if (CollectionUtil.isNotEmpty(errorList)) {
            throw new AssociationReportException(errorList);
        }
    }

    @Override
    public R<AssociationDetailBalanceSheetPartialRSP> detailBalanceSheetPartial(@Valid AssociationReportDetailREQ req) {
        if (StringUtils.isBlank(req.getVersion())) {
            return R.ok(SpringUtil.getBean(AssociationBalanceSheetPartialService.class).getByReportInstanceId(req.getReportInstanceId()));
        }else{
            return R.ok(SpringUtil.getBean(AssociationBalanceSheetPartialLibService.class).getByReportInstanceIdAndVersion(req.getReportInstanceId(), req.getVersion()));
        }
    }

    @Override
    @Log(title = "金融局报送", businessType = BusinessType.UPDATE)
    public R<AssociationReportModifyRSP> modifyBalanceSheetPartial(AssociationBalanceSheetPartialModifyREQ req) {
        Set<String> addReportCategoryCodes = SpringUtil.getBean(AssociationReportDataAccessService.class).getCurrentUserAccessAddReportCategoryCodes();
        if (CollectionUtil.isEmpty(addReportCategoryCodes) || !addReportCategoryCodes.contains(AssociationReportCategoryEnum.J0007.name())) {
            throw new MithrasException("无权限编辑该报表");
        }
        AssociationReportModifyRSP rsp = new AssociationReportModifyRSP();
        try {
            // 数据校验
            this.checkBalanceSheetPartial(req);
            SpringUtil.getBean(AssociationBalanceSheetPartialService.class).modify(req);
        } catch (AssociationReportException e) {
            rsp.setModifySuccess(Boolean.FALSE);
            rsp.setErrorMessageList(e.getErrorMessageList());
            return R.ok(rsp);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            throw new MithrasException("修改数据发生未知异常");
        }
        rsp.setModifySuccess(Boolean.TRUE);
        return R.ok(rsp);
    }

    private void checkBalanceSheetPartial(AssociationBalanceSheetPartialModifyREQ e) {
        List<String> errorList = new ArrayList<>();
            // 资产总计年初值必填 ObjectUtil.isEmpty(e.getAstTotAboy()) || ObjectUtil.isEmpty(e.getLiabToeqAboy()) ||
            if ( isNotEqualBigDecimalSum(e.getAstTotAboy(), e.getLiabToeqAboy())) {
                errorList.add("（年初数）行次37：资产总计=行次77：负债及所有者权益总计");
            }
            // 资产总计_期末数必填ObjectUtil.isEmpty(e.getAstTotAeop()) || ObjectUtil.isEmpty(e.getLiabToeqAeop()) ||
            if (isNotEqualBigDecimalSum(e.getAstTotAeop(), e.getLiabToeqAeop())) {
                errorList.add("（期末数）行次37：资产总计=行次77：负债及所有者权益总计");
            }
        if (CollectionUtil.isNotEmpty(errorList)) {
            throw new AssociationReportException(errorList);
        }
    }

    @Override
    public R<AssociationDetailEntityEconomyServiceRSP> detailEntityEconomyService(@Valid AssociationReportDetailREQ req) {
        if (StringUtils.isBlank(req.getVersion())) {
            return R.ok(SpringUtil.getBean(AssociationEntityEconomyServiceService.class).getByReportInstanceId(req.getReportInstanceId()));
        }else{
            return R.ok(SpringUtil.getBean(AssociationEntityEconomyServiceLibService.class).getByReportInstanceIdAndVersion(req.getReportInstanceId(), req.getVersion()));
        }
    }

    @Override
    @Log(title = "金融局报送", businessType = BusinessType.UPDATE)
    public R<AssociationReportModifyRSP> modifyEntityEconomyService(AssociationEntityEconomyServiceModifyREQ req) {
        Set<String> addReportCategoryCodes = SpringUtil.getBean(AssociationReportDataAccessService.class).getCurrentUserAccessAddReportCategoryCodes();
        if (CollectionUtil.isEmpty(addReportCategoryCodes) || !addReportCategoryCodes.contains(AssociationReportCategoryEnum.J0006.name())) {
            throw new MithrasException("无权限编辑该报表");
        }
        AssociationReportModifyRSP rsp = new AssociationReportModifyRSP();
        try {
            // 数据校验
            this.checkEntityEconomyService(req);
            SpringUtil.getBean(AssociationEntityEconomyServiceService.class).modify(req);
        } catch (AssociationReportException e) {
            rsp.setModifySuccess(Boolean.FALSE);
            rsp.setErrorMessageList(e.getErrorMessageList());
            return R.ok(rsp);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            throw new MithrasException("修改数据发生未知异常");
        }
        rsp.setModifySuccess(Boolean.TRUE);
        return R.ok(rsp);
    }

    private void checkEntityEconomyService(AssociationEntityEconomyServiceModifyREQ e) {

    }

    @Override
    public R<List<AssociationDetailExternalFinancingRSP>> detailExternalFinancing(@Valid AssociationReportDetailREQ req) {
        if (StringUtils.isBlank(req.getVersion())) {
            return R.ok(SpringUtil.getBean(AssociationExternalFinancingService.class).listByReportInstanceId(req.getReportInstanceId()));
        }else{
            return R.ok(SpringUtil.getBean(AssociationExternalFinancingLibService.class).listByReportInstanceIdAndVersion(req.getReportInstanceId(), req.getVersion()));
        }
    }

    @Override
    @Log(title = "金融局报送", businessType = BusinessType.UPDATE)
    public R<AssociationReportModifyRSP> modifyExternalFinancing(@RequestBody @Valid AssociationExternalFinancingModifyListREQ req){
        Set<String> addReportCategoryCodes = SpringUtil.getBean(AssociationReportDataAccessService.class).getCurrentUserAccessAddReportCategoryCodes();
        if (CollectionUtil.isEmpty(addReportCategoryCodes) || !addReportCategoryCodes.contains(AssociationReportCategoryEnum.J0010.name())) {
            throw new MithrasException("无权限编辑该报表");
        }
        AssociationReportModifyRSP rsp = new AssociationReportModifyRSP();
        try {
            // 数据校验
            if(req.getDataList().isEmpty()){//列表为空，删除所有
                SpringUtil.getBean(AssociationExternalFinancingService.class).deleteByReportInstanceId(req.getReportInstanceId());
            }else {
                // 数据校验
                this.checkExternalFinancing(req.getDataList());
                req.getDataList().forEach(e -> e.setReportInstanceId(req.getReportInstanceId()));
                SpringUtil.getBean(AssociationExternalFinancingService.class).modifyBatch(req.getDataList());
            }
        } catch (AssociationReportException e) {
            rsp.setModifySuccess(Boolean.FALSE);
            rsp.setErrorMessageList(e.getErrorMessageList());
            return R.ok(rsp);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            throw new MithrasException("修改数据发生未知异常");
        }
        rsp.setModifySuccess(Boolean.TRUE);
        return R.ok(rsp);
    }

    private void checkExternalFinancing(List<AssociationExternalFinancingModifyREQ> dataList) {
        List<String> errorList = new ArrayList<>();
        dataList.forEach(e -> {
            if (ObjectUtil.isEmpty(e.getLoanBal())) {
                errorList.add("借款余额，必填字段");
            }
            if (ObjectUtil.isEmpty(e.getFinBusiTypeCode()) || Objects.equals(e.getFinBusiTypeCode(), DICT_UNKNOWN_CODE)) {
                errorList.add("融资业务类型，必填字段,取值字典范围");
            }
            if (ObjectUtil.isEmpty(e.getCptlProv())) {
                errorList.add("资金提供方，必填字段");
            }
            if (ObjectUtil.isEmpty(e.getFinIntr())) {
                errorList.add("融资利率，必填字段");
            }
            if (ObjectUtil.isEmpty(e.getFinLoanDate())) {
                errorList.add("借款日期，必填字段");
            }
            if (ObjectUtil.isEmpty(e.getFinMatuDate())) {
                errorList.add("到期日，必填字段");
            }
        });
        if (CollectionUtil.isNotEmpty(errorList)) {
            throw new AssociationReportException(errorList);
        }
    }

    @Override
    public R<List<AssociationDetailMainBusinessRSP>> detailMainBusiness(@Valid AssociationReportDetailREQ req) {
        if (StringUtils.isBlank(req.getVersion())) {
            return R.ok(SpringUtil.getBean(AssociationMainBusinessService.class).listByReportInstanceId(req.getReportInstanceId()));
        }else{
            return R.ok(SpringUtil.getBean(AssociationMainBusinessLibService.class).getByReportInstanceIdAndVersion(req.getReportInstanceId(), req.getVersion()));
        }
    }

    @Override
    @Log(title = "金融局报送", businessType = BusinessType.UPDATE)
    public R<AssociationReportModifyRSP> modifyMainBusiness(@RequestBody @Valid AssociationMainBusinessModifyListREQ req){
        Set<String> addReportCategoryCodes = SpringUtil.getBean(AssociationReportDataAccessService.class).getCurrentUserAccessAddReportCategoryCodes();
        if (CollectionUtil.isEmpty(addReportCategoryCodes) || !addReportCategoryCodes.contains(AssociationReportCategoryEnum.J0009.name())) {
            throw new MithrasException("无权限编辑该报表");
        }
        AssociationReportModifyRSP rsp = new AssociationReportModifyRSP();
        try {
            // 数据校验
            if(req.getDataList().isEmpty()){//列表为空，删除所有
                SpringUtil.getBean(AssociationMainBusinessService.class).deleteByReportInstanceId(req.getReportInstanceId());
            }else {
                // 数据校验
                this.checkMainBusiness(req.getDataList());
                req.getDataList().forEach(e -> e.setReportInstanceId(req.getReportInstanceId()));
                SpringUtil.getBean(AssociationMainBusinessService.class).modifyBatch(req.getDataList());
            }
        } catch (AssociationReportException e) {
            rsp.setModifySuccess(Boolean.FALSE);
            rsp.setErrorMessageList(e.getErrorMessageList());
            return R.ok(rsp);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            throw new MithrasException("修改数据发生未知异常");
        }
        rsp.setModifySuccess(Boolean.TRUE);
        return R.ok(rsp);
    }

    private void checkMainBusiness(List<AssociationMainBusinessModifyREQ> dataList) {
        List<String> errorMessageList = new LinkedList<>();
        for (AssociationMainBusinessModifyREQ associationMainBusiness : dataList) {
            // 合同类型取值字典范围
            if (Objects.equals(associationMainBusiness.getAgmtTypeCode(), DICT_UNKNOWN_CODE)) {
//                errorMessageList.add(String.format("第%s行数据（不含表头）的<合同类型>不符合模板中的字典取值", associationMainBusiness.getRowNum()));
                errorMessageList.add("合同类型，取值字典范围");
            }
            // 项目行业分类取值字典范围
            if (Objects.equals(associationMainBusiness.getProjIndtClasCode(), DICT_UNKNOWN_CODE)) {
//                errorMessageList.add(String.format("第%s行数据（不含表头）的<项目行业分类>不符合模板中的字典取值", associationMainBusiness.getRowNum()));
                errorMessageList.add("项目涉及行业，取值字典范围");
            }
            // 客户规模取值字典范围
            if (Objects.equals(associationMainBusiness.getCustScalCode(), DICT_UNKNOWN_CODE)) {
//                errorMessageList.add(String.format("第%s行数据（不含表头）的<客户规模>不符合模板中的字典取值", associationMainBusiness.getRowNum()));
                errorMessageList.add("客户规模，取值字典范围");
            }
            // 综合融资成本不可超过24%
            if (Objects.nonNull(associationMainBusiness.getCmphFinCost())) {
                if (associationMainBusiness.getCmphFinCost().compareTo(BigDecimal.valueOf(0.24)) > 0) {
//                    errorMessageList.add(String.format("第%s行数据（不含表头）的<综合融资成本>超过24%%", associationMainBusiness.getRowNum()));
                    errorMessageList.add("综合融资成本，不可超过24%");
                }
            }
            // 增信情况取值字典范围
            if (Objects.equals(associationMainBusiness.getUdpnSituCode(), DICT_UNKNOWN_CODE)) {
//                errorMessageList.add(String.format("第%s行数据（不含表头）的<增信情况>不符合模板中的字典取值", associationMainBusiness.getRowNum()));
                errorMessageList.add("增信情况，取值字典范围");
            }
            // 逾期天数取值字典范围
            if (Objects.equals(associationMainBusiness.getOvduDaysCode(), DICT_UNKNOWN_CODE)) {
//                errorMessageList.add(String.format("第%s行数据（不含表头）的<逾期天数>不符合模板中的字典取值", associationMainBusiness.getRowNum()));
                errorMessageList.add("逾期天数，取值字典范围");
            }
            // 是否纳入不良取值字典范围
            if (Objects.equals(associationMainBusiness.getNpFlag(), DICT_UNKNOWN_CODE)) {
//                errorMessageList.add(String.format("第%s行数据（不含表头）的<是否纳入不良>不符合模板中的字典取值", associationMainBusiness.getRowNum()));
                errorMessageList.add("是否纳入不良，取值字典范围");
            }
            // 客户数量必填
            if (Objects.isNull(associationMainBusiness.getCustVol())) {
//                errorMessageList.add(String.format("第%s行数据（不含表头）的<客户数量>不能为空", associationMainBusiness.getRowNum()));
                errorMessageList.add("客户数量，必填字段");
            }
        }
        if (CollectionUtil.isNotEmpty(errorMessageList)) {
            throw new AssociationReportException(errorMessageList);
        }
    }

    @Override
    public R<List<AssociationDetailRelationRSP>> detailRelation(@Valid AssociationReportDetailREQ req) {
        if (StringUtils.isBlank(req.getVersion())) {
            return R.ok(SpringUtil.getBean(AssociationRelationService.class).listByReportInstanceId(req.getReportInstanceId()));
        }else{
            return R.ok(SpringUtil.getBean(AssociationRelationLibService.class).listByReportInstanceIdAndVersion(req.getReportInstanceId(), req.getVersion()));
        }

    }

    @Override
    @Log(title = "金融局报送", businessType = BusinessType.UPDATE)
    public R<AssociationReportModifyRSP> modifyRelation(@RequestBody @Valid AssociationRelationModifyListREQ req){
        Set<String> addReportCategoryCodes = SpringUtil.getBean(AssociationReportDataAccessService.class).getCurrentUserAccessAddReportCategoryCodes();
        if (CollectionUtil.isEmpty(addReportCategoryCodes) || !addReportCategoryCodes.contains(AssociationReportCategoryEnum.J0012.name())) {
            throw new MithrasException("无权限编辑该报表");
        }
        AssociationReportModifyRSP rsp = new AssociationReportModifyRSP();
        try {
            // 数据校验
            if(req.getDataList().isEmpty()){//列表为空，删除所有
                SpringUtil.getBean(AssociationRelationService.class).deleteByReportInstanceId(req.getReportInstanceId());
            }else {
                //行号自动生成
                for(int rowNum = 1; rowNum < req.getDataList().size()+1; rowNum++){
                    req.getDataList().get(rowNum-1).setRowNum(rowNum);
                    req.getDataList().get(rowNum-1).setOnum(String.valueOf(rowNum));
                }
                // 数据校验
                this.checkRelation(req.getDataList());
                req.getDataList().forEach(e -> e.setReportInstanceId(req.getReportInstanceId()));
                SpringUtil.getBean(AssociationRelationService.class).modifyBatch(req.getDataList());
            }
        } catch (AssociationReportException e) {
            rsp.setModifySuccess(Boolean.FALSE);
            rsp.setErrorMessageList(e.getErrorMessageList());
            return R.ok(rsp);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            throw new MithrasException("修改数据发生未知异常");
        }
        rsp.setModifySuccess(Boolean.TRUE);
        return R.ok(rsp);
    }

    private void checkRelation(List<AssociationRelationModifyREQ> dataList) {
        for (AssociationRelationModifyREQ associationRelation : dataList) {
            // 是否为本公司关联股东方取值字典范围
            if (Objects.equals(associationRelation.getCorpShahRelpFlag(), DICT_UNKNOWN_CODE)) {
                throw new MithrasException(String.format("第%s行数据（不含表头）的<是否为本公司关联股东方>非法", associationRelation.getOnum()));
            }
            // 是本公司关联股东方时，本公司股东名称必填
            if (Objects.nonNull(associationRelation.getCorpShahRelpFlag()) && Objects.equals(associationRelation.getCorpShahRelpFlag(), String.valueOf(YesOrNoNumberEnum.YES.getCode()))) {
                if (StrUtil.isBlank(associationRelation.getCorpShahName())) {
                    throw new MithrasException(String.format("第%s行数据（不含表头）的<本公司关联股东名称>不能为空", associationRelation.getOnum()));
                }
            }
        }
    }

    @Override
    public R<List<AssociationDetailTop10ClientConcentrationRSP>> detailTop10ClientConcentration(@Valid AssociationReportDetailREQ req) {
        if (StringUtils.isBlank(req.getVersion())) {
            return R.ok(SpringUtil.getBean(AssociationTop10ClientConcentrationService.class).listByReportInstanceId(req.getReportInstanceId()));
        }else{
            return R.ok(SpringUtil.getBean(AssociationTop10ClientConcentrationLibService.class).listByReportInstanceIdAndVersion(req.getReportInstanceId(), req.getVersion()));
        }
    }

    @Override
    @Log(title = "金融局报送", businessType = BusinessType.UPDATE)
    public R<AssociationReportModifyRSP> modifyTop10ClientConcentration(@RequestBody @Valid AssociationTop10ClientConcentrationModifyListREQ req){
        Set<String> addReportCategoryCodes = SpringUtil.getBean(AssociationReportDataAccessService.class).getCurrentUserAccessAddReportCategoryCodes();
        if (CollectionUtil.isEmpty(addReportCategoryCodes) || !addReportCategoryCodes.contains(AssociationReportCategoryEnum.J0011.name())) {
            throw new MithrasException("无权限编辑该报表");
        }
        AssociationReportModifyRSP rsp = new AssociationReportModifyRSP();
        try {
            // 数据校验
            if(req.getDataList().isEmpty()){//列表为空，删除所有
                SpringUtil.getBean(AssociationTop10ClientConcentrationService.class).deleteByReportInstanceId(req.getReportInstanceId());
            }else {
                // 数据校验
                this.checkTop10ClientConcentration(req.getDataList());
                req.getDataList().forEach(e -> e.setReportInstanceId(req.getReportInstanceId()));
                SpringUtil.getBean(AssociationTop10ClientConcentrationService.class).modifyBatch(req.getDataList());
            }
        } catch (AssociationReportException e) {
            rsp.setModifySuccess(Boolean.FALSE);
            rsp.setErrorMessageList(e.getErrorMessageList());
            return R.ok(rsp);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            throw new MithrasException("修改数据发生未知异常");
        }
        rsp.setModifySuccess(Boolean.TRUE);
        return R.ok(rsp);

    }
    private void checkTop10ClientConcentration(List<AssociationTop10ClientConcentrationModifyREQ> dataList) {

    }
    private BigDecimal bigDecimalAddSum(BigDecimal... addends) {
        // 计算加数的和（每个加数为 null 时视为 0）
        BigDecimal sum = BigDecimal.ZERO;
        if (addends != null) {
            for (BigDecimal addend : addends) {
                BigDecimal validAddend = (addend != null) ? addend : BigDecimal.ZERO;
                sum = sum.add(validAddend);
            }
        }

        // 比较数值是否相等（compareTo 返回 0 表示数值相等，不考虑精度）
        return sum;
    }

    @Override
    public R<AssociationReportImportRSP> reportApplySubmit(@RequestBody @Valid ReportApplySubmitREQ req){
        AssociationReportImportRSP rsp = new AssociationReportImportRSP();
        try {
            List<String> reportInstanceIdList = req.getReportInstanceIdList();
            if (CollUtil.isEmpty(reportInstanceIdList)) {
                throw new MithrasException("您还未选择要提交的报表");
            }
            //验证报表是否可以提交
            List<AssociationReport> associationReportList = associationReportService.getReportList(reportInstanceIdList);
            associationReportList.forEach(e -> {
                if (Objects.equals(e.getReportStatus(), AssociationProcessStatusEnum.UNDER_APPROVAL.name())) {
                    throw new MithrasException(String.format("报表【%s】审批中,不允许提交", e.getReportCategoryName()));
                }
                if (Objects.equals(e.getReportStatus(), AssociationProcessStatusEnum.APPROVAL_PASS.name())) {
                    throw new MithrasException(String.format("报表【%s】审批通过,不允许提交", e.getReportCategoryName()));
                }
            });
            //验证报表提交的数据不能为空
            List<String> errorList = new com.aspose.slides.Collections.ArrayList();
            for (AssociationReport report : associationReportList) {
                List<AssociationReportBaseModel> details = associationReportService.getReportList(report.getReportCategoryCode(), report.getReportInstanceId());
                if (ObjectUtil.isEmpty(details)) {
                    String msgTips = String.format("%s-%s-%s-%s", report.getReportCategoryName(), report.getReportYear(), AssociationReportPeriodCategoryEnum.getReportCategoryName(AssociationReportPeriodCategoryEnum.findByName(report.getReportPeriodCategory()), report.getReportPeriod()), report.getBatchNo());
                            msgTips+= ",提交审核的数据为空，不允许提交";
                    errorList.add(msgTips);
                }
            }
            //检查
            if(errorList.size() > 0) {
                throw new AssociationReportException(errorList);
            }
            //获取有权限操作的报表
            Set<String> addReportCategoryCodes = SpringUtil.getBean(AssociationReportDataAccessService.class).getCurrentUserAccessAddReportCategoryCodes();
            List<String> reportCategoryCodeList = associationReportList.stream().map(AssociationReport::getReportCategoryCode).collect(Collectors.toList());
            reportCategoryCodeList.forEach(item->{
                if(!addReportCategoryCodes.contains(item)){
                    throw new MithrasException("您没有权限提交该报表");
                }
            });
            //获取金融局报送提交的工作流
            List<AssociationReportDataAccess> dataAccessList = SpringUtil.getBean(AssociationReportDataAccessService.class).getFlowByCategoryCodes(reportCategoryCodeList);
            if (CollUtil.isEmpty(dataAccessList)) {
                throw new MithrasException("您没有权限提交该报表");
            }
            Set<String> flowNameList = dataAccessList.stream().filter(e -> StringUtils.isNotBlank(e.getFlowName())).map(AssociationReportDataAccess::getFlowName).collect(Collectors.toSet());
            if (CollUtil.isEmpty(flowNameList)) {
                throw new MithrasException("您没有权限提交该报表");
            }
            if (flowNameList.size() > 1) {
                throw new MithrasException("一次不能提交多个流程");
            }
            flowSubmit(reportInstanceIdList,associationReportList,new ArrayList<>(flowNameList).get(0));
        } catch (AssociationReportException e) {
            rsp.setImportSuccess(Boolean.FALSE);
            rsp.setErrorMessageList(e.getErrorMessageList());
            return R.ok(rsp);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("金融局报送-提交审批发生未知异常", e);
            throw new MithrasException("金融局报送-提交审批发生未知异常");
        }
        rsp.setImportSuccess(Boolean.TRUE);
        return R.ok(rsp);
    }

    @Override
    public R<List<AssociationReportListRSP>> applyList(AssociationReportApplyREQ req) {
         AssociationReportApply associationReportApply = associationReportApplyService.getById(req.getId());
        if (ObjectUtil.isNull(associationReportApply)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        String reportInstanceIds  = associationReportApply.getReportInstanceIds();
        List<String> reportInstanceIdList = Arrays.asList(reportInstanceIds.split(","));
        req.setReportInstanceIdList(reportInstanceIdList);
        List<AssociationReport> associationReportList = associationReportService.getReportList(req);
        List<AssociationReportListRSP> associationReportListRSPList = BeanUtil.copyToList(associationReportList, AssociationReportListRSP.class);
        return R.ok(associationReportListRSPList);
    }

    @Override
    public R<Void> recalculate(SinglePkREQ req) {
        associationReportService.recalculate(req.getId());
        return R.ok();
    }

    @Transactional(rollbackFor = Exception.class)
    public void flowSubmit(List<String> reportInstanceIdList,List<AssociationReport> associationReportList,String modelKey) {
        String lockKey = AssociationReportConstants.REPORT_APPLY_LOCK_KEY;
        boolean getLockFlag = redisDistLock.tryLockWithoutReleaseTime(lockKey, 1000L);
        if (!getLockFlag) {
            throw new MithrasException(CONCURRENT_OPERATION);
        }
        try {
            //插入主表
            AssociationReportApply associationReportApply = new AssociationReportApply();
            associationReportApply.setReportInstanceIds(reportInstanceIdList.stream().collect(Collectors.joining(",")));
            associationReportApply.setApprovalStatus(AssociationProcessStatusEnum.UNDER_APPROVAL.name());
            associationReportApplyService.save(associationReportApply);
            // 生成流程实例
            StartProcessReq startProcessReq = buildReportApplyStartProcessReq(associationReportApply,modelKey);
            // 填充变量用于判断流程分支
//            boolean allRealtimeReport = true;
//            for (AssociationReport associationReport : associationReportList) {
//                if (!StrUtil.equals(associationReport.getReportPeriodCategory(), AssociationReportPeriodCategoryEnum.REALTIME.name())) {
//                    // 存在非实时报表则修改变量
//                    allRealtimeReport = false;
//                    break;
//                }
//            }
//            startProcessReq.setVariables(MapUtil.of("allRealtimeReport", allRealtimeReport));
            processApiService.start(startProcessReq);
            // 更新上报主表association_report的流程状态
            associationReportList.forEach(e -> e.setProcessStatus(AssociationProcessStatusEnum.UNDER_APPROVAL.name()));
            associationReportService.updateBatchById(associationReportList);
        }finally {
            redisDistLock.unlock(lockKey);
        }
    }


    private StartProcessReq buildReportApplyStartProcessReq(AssociationReportApply associationReportApply,String modelKey) {
        Long currentUserId = Optional.ofNullable(AccountUtil.getLoginInfo()).map(AccountVO::getId).orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN));
        String currentUserName = sysUserService.getUserName(currentUserId);
        List<OrgDO> deptList = sysUserService.getSpecificUserDeptList(currentUserId);
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setBusinessKey(String.valueOf(associationReportApply.getId()));
        startProcessReq.setModelKey(modelKey);
        // 审批流列表表单名称展示
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(currentUserName).append("发起报表上报申请流程");
        startProcessReq.setProcessInstanceName(stringBuilder.toString());
        startProcessReq.setStartUserId(currentUserId.toString());
        Long deptId = null;
        Optional<OrgDO> first = deptList.stream().filter(e -> e.getType()!= 1 ).findFirst();
        if (first.isPresent()) {//首选非业务部门
            deptId = first.get().getId();
        } else if (!deptList.isEmpty()) {
            deptId = deptList.get(0).getId();
        }
        startProcessReq.setStartUserDeptId(deptId.toString());
        //设值流程变量参数值
//        startProcessReq.setVariables(MapUtil.of(
//
//        ));
        return startProcessReq;
    }


}

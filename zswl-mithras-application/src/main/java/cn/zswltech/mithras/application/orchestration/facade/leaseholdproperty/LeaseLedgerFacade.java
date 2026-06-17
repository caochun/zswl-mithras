package cn.zswltech.mithras.application.orchestration.facade.leaseholdproperty;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.leaseholdproperty.application.LeaseLedgerApplicationService;
import cn.zswltech.mithras.dto.MultiplePkREQ;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.file.FileUploadRSP;
import cn.zswltech.mithras.dto.file.template.FileTemplateListREQ;
import cn.zswltech.mithras.dto.file.template.FileTemplateListRSP;
import cn.zswltech.mithras.dto.leaseholdproperty.*;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.document.persistence.model.FileTemplate;
import cn.zswltech.mithras.document.persistence.model.MaterialsList;
import cn.zswltech.mithras.leaseholdproperty.model.LeaseItemInfo;
import cn.zswltech.mithras.leaseholdproperty.model.LeaseItemListRowData;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.document.file.template.FileTemplateService;
import cn.zswltech.mithras.leaseholdproperty.application.LeaseItemInfoService;
import cn.zswltech.mithras.leaseholdproperty.application.LeaseItemListRowDataService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;
import static cn.zswltech.mithras.application.orchestration.auth.BusinessModuleEnum.FILE_TEMPLATE;

/**
 * @author yangxiong
 * @description 租赁物台账接口
 * @since 2023-09-19
 */
@Slf4j
@Service
public class LeaseLedgerFacade implements LeaseLedgerApplicationService {

    @Resource
    private LeaseItemInfoService leaseItemInfoService;
    @Resource
    private LeaseItemListRowDataService leaseItemListRowDataService;
    @Resource
    private HttpServletResponse httpServletResponse;
    @Resource
    private FileTemplateService fileTemplateService;

    @Override
    public R<PageR<LeaseLedgerMainRSP>> getPage(LeaseLedgerMainREQ param) {
        return leaseItemInfoService.getPage(param);
    }

    @Override
    public void download(LeaseLedgerMainREQ param) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("台账管理" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            leaseItemInfoService.download(httpServletResponse.getOutputStream(), param.getIds());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出台账管理表发生未知异常", e);
            throw new MithrasException("导出台账管理表发生未知异常");
        }
    }

    @Override
    public R<LedgerContractDetailRSP> getContractInfoById(LeaseLedgerDetailREQ param) {
        return leaseItemInfoService.getContractInfoById(param.getId());
    }

    @Override
    public R<LeaseCheckRepeatRSP> getLeaseCheckRepeatById(LeaseLedgerDetailREQ param) {
        return leaseItemInfoService.getLeaseCheckRepeatById(param.getId());
    }

    @Override
    public R<Boolean> checkRepeatSave(LeaseCheckRepeatREQ param) {
        return leaseItemInfoService.checkRepeatSave(param);
    }

    @Override
    public R<Void> saveLeaseItemTotalAmount(@Valid LeaseItemAmountREQ req) {
        LeaseItemInfo leaseItemInfo = leaseItemInfoService.getById(req.getId());
        if (Objects.isNull(leaseItemInfo)) {
            throw new MithrasException("租赁物审核管理数据不存在");
        }
//        leaseItemInfo.setTotalAmountOfLeaseItem(req.getTotalAmount());
        leaseItemInfoService.updateById(leaseItemInfo);
        return R.ok();
    }

    @Override
    public R<Void> saveLeaseItemMetadata(@Valid LeaseItemMetadataREQ req) {
        leaseItemInfoService.saveLeaseItemMetadata(req);
        return R.ok();
    }

    @Override
    public R<Void> init() {
        leaseItemInfoService.init();
        return R.ok();
    }

    @Override
    public R<LeaseItemMetadataRSP> getLeaseItemMetadata(@Valid SinglePkREQ req) {
        return R.ok(leaseItemInfoService.getLeaseItemMetadata(req.getId()));
    }

    @Override
    public void downloadLeaseItemTemplate(@Valid SinglePkREQ req) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("租赁物清单模板.zip", StandardCharsets.UTF_8.name()));
            leaseItemInfoService.downloadLeaseItemTemplate(req.getId(), httpServletResponse.getOutputStream());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("下载模板发生未知异常", e);
            throw new MithrasException("下载模板发生未知异常");
        }
    }

    @Override
    public R<Void> importLeaseItemList(@Valid LeaseItemImportREQ req) {
        try {
            leaseItemInfoService.importItemList(req.getId(), req.getFile().getInputStream());
            return R.ok();
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("租赁物管理-导入租赁物清单发生未知异常", e);
            throw new MithrasException("导入租赁物清单发生未知异常");
        }
    }

    @Override
    public R<LeaseItemListRSP> listItemWithPage(@Valid LeaseItemListREQ req) {
        return R.ok(leaseItemInfoService.listItemWithPage(req));
    }

    @Override
    public void exportLeaseItemList(@Valid LeaseItemListExportREQ req) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode("租赁物清单" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            leaseItemInfoService.exportItemList(req, httpServletResponse.getOutputStream());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出数据发生未知异常", e);
            throw new MithrasException("导出数据发生未知异常");
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public R<Void> removeLeaseItemList(@Valid MultiplePkREQ req) {
        List<LeaseItemListRowData> list = leaseItemListRowDataService.listByIds(req.getIds());
        if (CollectionUtil.isEmpty(list)) {
            return R.fail("没有可删除的数据");
        }
        Long leaseItemInfoId = list.get(0).getLeaseItemInfoId();
        log.warn("租赁物审核管理-租赁物清单-批量删除[userId:{}, data:{}]", AccountUtil.getLoginInfo().getId(), JSONUtil.toJsonStr(list));
        leaseItemListRowDataService.removeByIds(req.getIds());
        LeaseItemInfo leaseItemInfo = leaseItemInfoService.getById(leaseItemInfoId);
        leaseItemInfoService.generateLeaseItemFile(leaseItemInfo);
        return R.ok();
    }

    @Override
    public R<List<FileUploadRSP>> flowUpdate(@Valid LeaseFlowUploadREQ param) {
        return R.ok(leaseItemInfoService.flowUpdate(param));
    }

    @Override
    public R<PageR<FileTemplateListRSP>> downloadCheckRepeatTemplate(FileTemplateListREQ req) {
        Page<FileTemplate> data = fileTemplateService.listTemplate(req);
        List<FileTemplate> records = data.getRecords();
        List<FileTemplateListRSP> list = BeanUtil.copyToList(records, FileTemplateListRSP.class);
        //填充fileId
        List<Long> idList = list.stream().map(FileTemplateListRSP::getId).collect(Collectors.toList());
        Map<Long, Long> map = getBean(MaterialsListService.class).list(FILE_TEMPLATE.name(), null, idList).stream()
                .collect(Collectors.toMap(MaterialsList::getBelongId, MaterialsList::getId));
        list.forEach(e -> e.setFileId(map.get(e.getId())));
        //
        List<Long> createByList = list.stream().map(FileTemplateListRSP::getCreateBy).collect(Collectors.toList());
        Map<Long, String> nameMap = getBean(Id2NameService.class).sysUserId2Name(createByList);
        list.forEach(e -> e.setCreateByName(nameMap.get(e.getCreateBy())));
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<LeaseItemRedupRSP> dedup(@Valid LeaseItemListREQ req) {
        return R.ok(leaseItemInfoService.dedup(req));
    }
}

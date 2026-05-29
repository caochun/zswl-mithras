package cn.zswltech.mithras.service.controller.leaseholdproperty;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.leaseholdproperty.LeaseLedgerApi;
import cn.zswltech.mithras.basic.Constant;
import cn.zswltech.mithras.dto.MultiplePkREQ;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.file.FileUploadRSP;
import cn.zswltech.mithras.dto.file.template.FileTemplateListREQ;
import cn.zswltech.mithras.dto.file.template.FileTemplateListRSP;
import cn.zswltech.mithras.dto.leaseholdproperty.*;
import cn.zswltech.mithras.common.constant.GlobalConstants;
import cn.zswltech.mithras.service.mapper.file.template.model.FileTemplate;
import cn.zswltech.mithras.service.mapper.leaseholdproperty.LeaseItemInfoMapper;
import cn.zswltech.mithras.service.mapper.leaseholdproperty.LeaseItemListRowDataMapper;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.mapper.model.leaseholdproperty.LeaseItemInfo;
import cn.zswltech.mithras.service.mapper.model.leaseholdproperty.LeaseItemListRowData;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.file.template.FileTemplateService;
import cn.zswltech.mithras.service.service.leaseholdproperty.LeaseItemInfoService;
import cn.zswltech.mithras.service.service.leaseholdproperty.LeaseItemListRowDataService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;
import static cn.zswltech.mithras.basic.Constant.approveStatusMap;
import static cn.zswltech.mithras.basic.Constant.leaseItemDedupTypeParam;
import static cn.zswltech.mithras.service.enums.BusinessModuleEnum.FILE_TEMPLATE;

/**
 * @author yangxiong
 * @description 租赁物台账接口
 * @since 2023-09-19
 */
@Slf4j
@RestController
public class LeaseLedgerController implements LeaseLedgerApi {

    @Resource
    private LeaseItemInfoService leaseItemInfoService;
    @Resource
    private LeaseItemListRowDataService leaseItemListRowDataService;
    @Resource
    private HttpServletResponse httpServletResponse;
    @Resource
    private FileTemplateService fileTemplateService;
    @Autowired
    private LeaseItemInfoMapper leaseItemInfoMapper;
    @Autowired
    private LeaseItemListRowDataMapper leaseItemListRowDataMapper;

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
    public R<LeaseItemRedupRSP> dedup(@RequestBody @Valid LeaseItemListREQ req) {
        LeaseItemRedupRSP rsp = new LeaseItemRedupRSP();
        Map<Long,Set<String>> matchDetailSet = new HashMap<>(); // 记录匹配的新的租赁物清单id 和被击中的字段
        Set<Long> matchProjSet = new HashSet<>(); // 记录匹配的存量抵押物管理id
        rsp.setLeaseItemListRowData(matchDetailSet);

        /*通过租赁物id获取对应的信息和租赁物清单数据*/
        LeaseItemInfo leaseItemInfo = leaseItemInfoService.getById(req.getId());
        JSONArray arr = new JSONArray(leaseItemInfo.getLeaseItemTypes());
        List<String> leaseItemTypes = arr.toList(String.class);

        /*针对重复查重的情况  需要将匹配结果置空*/
        LambdaUpdateWrapper<LeaseItemListRowData> updateRowDataWrapper = new LambdaUpdateWrapper<>();
        updateRowDataWrapper.in(LeaseItemListRowData::getLeaseItemInfoId, leaseItemInfo.getId())
                .set(LeaseItemListRowData::getMatchColumns, null);
        leaseItemListRowDataMapper.update(null, updateRowDataWrapper);


        /*遍历  并根据每一种类型 关联其他的租赁物进行查重*/
        for (String type : leaseItemTypes) {
            if(!leaseItemDedupTypeParam.containsKey(type)){
                log.info("租赁物内部查重不包含该类型:{}",type);
                continue;
            }
            /*需要进行查重的租赁物所有对比字段要素和id    */
            Map<String, Set<String>> newDetailMap = getMapByIds(Arrays.asList(leaseItemInfo.getId()), type);

            LambdaQueryWrapper<LeaseItemInfo> wrapper = new LambdaQueryWrapper();
            wrapper.like(LeaseItemInfo::getLeaseItemTypes, type);
            wrapper.notIn(LeaseItemInfo::getId, leaseItemInfo.getId());
            List<LeaseItemInfo> rowDataList = leaseItemInfoMapper.selectList(wrapper);
            List<Long> ids = new ArrayList<>();
            for (int i = 0; i < rowDataList.size(); i++) {
                LeaseItemInfo info = rowDataList.get(i);
                ids.add(info.getId());
                if ((i + 1) % 100 == 0) { // 每满一百个执行一次 防止一次性太多内容需要对比导致内存溢出
                    /*存量租赁物所有对比字段要素和id    */
                    Map<String, Set<String>> detailMap = getMapByIds(ids, type);
                    match(newDetailMap, detailMap, matchDetailSet, matchProjSet);
                    ids = new ArrayList<>();
                }
            }
            if (ids.size() > 0) {// 不足100个  最后单独处理
                Map<String, Set<String>> detailMap = getMapByIds(ids, type);
                match(newDetailMap, detailMap, matchDetailSet, matchProjSet);
            }
        }
        if(matchProjSet.size() > 0) {
            rsp.setMatchFlag(true);
            /*leaseItemListRowData查重有相同的租赁物清单，如果需要标记到数据库，在表中加入字段matchFlag*/
            for(Map.Entry<Long,Set<String>> entry : matchDetailSet.entrySet()){
                LambdaUpdateWrapper<LeaseItemListRowData> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.in(LeaseItemListRowData::getId, entry.getKey())
                        .set(LeaseItemListRowData::getMatchColumns, String.join(",", entry.getValue()));
                leaseItemListRowDataMapper.update(null, updateWrapper);
            }

            /*找到所有的被匹配到的存量 租赁物管理信息  将相关联的数据存入返回实体*/
            LambdaQueryWrapper<LeaseItemInfo> wrapper = new LambdaQueryWrapper();
            wrapper.in(LeaseItemInfo::getId, matchProjSet);
            List<LeaseItemInfo> rowDataList = leaseItemInfoMapper.selectList(wrapper);
            for (LeaseItemInfo info : rowDataList) {
                LeaseItemRedupRSP.ProjModel projModel = new LeaseItemRedupRSP.ProjModel();
                projModel.setId(info.getId());
                projModel.setProjName(info.getProjName());
                projModel.setFlowId(info.getFlowId());
                projModel.setApprovalStatus(approveStatusMap.get(info.getApprovalStatus()));
                rsp.addProjList(projModel);
            }
        }
        return R.ok(rsp);
    }

    private static void match(Map<String, Set<String>> newDetailMap, Map<String, Set<String>> detailMap, Map<Long,Set<String>> matchDetailMap, Set<Long> matchProjSet) {
        for (Map.Entry<String, Set<String>> entry : newDetailMap.entrySet()) {
            if (detailMap.containsKey(entry.getKey())) { // 相同规则下 key相同则表明数据匹配，需要记录下租赁物的清单id 和存量的管理id
                Set<String> newDetailSet = entry.getValue();
                Set<String> detailSet = detailMap.get(entry.getKey());
                for(String newDetailLine : newDetailSet){
                    String detailId = newDetailLine.split(Constant.splitLine)[0];
                    for(String detailLine : detailSet){
                        String leaseItemInfoId =detailLine.split(Constant.splitLine)[1];
                        Set<String> matchCloumnSet = matchDetailMap.get(Long.parseLong(detailId));
                        if (matchCloumnSet == null) {
                            matchCloumnSet = new HashSet<>();
                            matchDetailMap.put(Long.valueOf(detailId), matchCloumnSet);
                        }
                        matchCloumnSet.add(entry.getKey().split(Constant.splitLine)[0]);
                        matchProjSet.add(Long.valueOf(leaseItemInfoId));
                        log.info("match keys {} in lease_item_list_row_data newId {} -> oldId {}  leaseItemInfoId is {}" , entry.getKey(), detailId, detailLine.split(Constant.splitLine)[0], leaseItemInfoId);
                    }
                }
            }
        }
    }

    /**
     * 根据不同的数据类型，将数据对应的字段独立取出  type_value   Set<id_leaseItemInfoId>
     * @return  eg: 名称_XXX  : {id1,id2}
     * */
    private Map<String, Set<String>> getMapByIds(List<Long> ids, String type) {
        Map<String, Set<String>> result = new HashMap<>();
        LambdaQueryWrapper<LeaseItemListRowData> wrapper = new LambdaQueryWrapper();
        wrapper.in(LeaseItemListRowData::getLeaseItemInfoId, ids);
        List<LeaseItemListRowData> lines = leaseItemListRowDataService.list(wrapper);
        for (LeaseItemListRowData line : lines) {
            JSONObject row = JSONUtil.parseObj(line.getRowData());
            Map<String, Object> map = row.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey,
                            e -> e.getValue().toString()));

            List<String> paramList = leaseItemDedupTypeParam.get(type); // 不同租赁物种类需要对比的字段
            String idMsg = line.getId() + Constant.splitLine + line.getLeaseItemInfoId();
            for (String param : paramList) {
                Object value = map.get(param);
                if(value == null){
                    value = map.get(param+"*");  // 如果必填会对存储造成影响，如果取不到就按照必填再取一次
                }
                if(value == null || value.toString().trim().length() == 0 || "/".equals(value.toString())){
                    continue;
                }else{
                    String key = param + Constant.splitLine + value;
                    if(result.containsKey(key)){
                        result.get(key).add(idMsg);
                    }else{
                        Set<String> set = new HashSet<>();
                        set.add(idMsg);
                        result.put(key, set);
                    }
                }
            }
        }
        return result;
    }
}

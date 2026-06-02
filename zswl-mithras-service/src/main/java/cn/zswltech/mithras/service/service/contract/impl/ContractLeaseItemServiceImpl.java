package cn.zswltech.mithras.service.service.contract.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.zswl.oss.core.OssClient;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.contract.ContractSingleIdREQ;
import cn.zswltech.mithras.dto.contract.leaseitem.*;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractTypeEnum;
import cn.zswltech.mithras.service.enums.lease.LeaseOperationTypeEnum;
import cn.zswltech.mithras.service.enums.lease.LeaseTextFileEnum;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.contract.mapper.contract.ContractLeaseItemMapper;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractLeaseItem;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractLeaseItemLib;
import cn.zswltech.mithras.service.mapper.model.leaseholdproperty.LeaseItemInfo;
import cn.zswltech.mithras.service.mapper.model.leaseholdproperty.LeaseItemListRowData;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.LeaseItemCommonService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractLeaseItemService;
import cn.zswltech.mithras.service.service.leaseholdproperty.LeaseItemInfoService;
import cn.zswltech.mithras.service.service.leaseholdproperty.LeaseItemListRowDataService;
import cn.zswltech.mithras.service.service.lib.contract.ContractBaseInfoLibService;
import cn.zswltech.mithras.contract.archive.application.ContractLeaseItemLibService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2022/8/15
 * @description
 */
@Slf4j
@Service
public class ContractLeaseItemServiceImpl extends ServiceImpl<ContractLeaseItemMapper, ContractLeaseItem> implements ContractLeaseItemService {
    @Resource
    private ContractLeaseItemLibService contractLeaseItemLibService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractBaseInfoLibService contractBaseInfoLibService;
    @Resource
    private LeaseItemInfoService leaseItemInfoService;
    @Resource
    private LeaseItemListRowDataService leaseItemListRowDataService;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private OssClient ossClient;
    @Resource
    private LeaseItemCommonService leaseItemCommonService;

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void importExcel(InputStream inputStream, Long contractId) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
        if (Objects.isNull(contractBaseInfo)) {
            throw new MithrasException("合同数据不存在");
        }
        // 清除历史数据
        this.removeByContractId(contractId);
        // 读取文件处理
        ExcelReader excelReader = ExcelUtil.getReader(inputStream);
        // 总行数
        int total = excelReader.getRowCount();
        if (total > 15000) {
            throw new MithrasException("最多支持导入15000条数据");
        }
        // 读取表头
        List<Object> firstRow = excelReader.readRow(0);
        if (CollectionUtil.isEmpty(firstRow)) {
            throw new MithrasException("没有从文件中获取到表头，请检查导入文件");
        }
        List<String> headerList = firstRow.stream().map(e -> Optional.ofNullable(e).map(Object::toString).orElse("")).filter(e -> !Objects.equals(e, "序号")).collect(Collectors.toList());
        contractBaseInfo.setItemListHeader(JSONUtil.toJsonStr(headerList));
        contractBaseInfoService.updateById(contractBaseInfo);
        // 读取数据
        List<Map<String, Object>> dataList = excelReader.readAll();
        if (CollectionUtil.isNotEmpty(dataList)) {
            List<ContractLeaseItem> itemListRowDataList = new LinkedList<>();
            for (Map<String, Object> dataMap : dataList) {
                // 预处理
                leaseItemCommonService.preHandle(dataMap);
                ContractLeaseItem contractLeaseItem = new ContractLeaseItem();
                contractLeaseItem.setContractId(contractId);
                contractLeaseItem.setRowData(JSONUtil.toJsonStr(dataMap, JSONConfig.create().setIgnoreNullValue(false)));
                itemListRowDataList.add(contractLeaseItem);
            }
            this.saveBatch(itemListRowDataList);
        }
    }

    @Override
    public void exportExcel(OutputStream outputStream, ContractLeaseItemExportREQ req) {
        ContractBaseInfo contractBaseInfo;
        List<ContractLeaseItem> contractLeaseItemList;
        if (StrUtil.isBlank(req.getVersion())) {
            contractBaseInfo = contractBaseInfoService.getById(req.getContractId());
            if (Objects.isNull(contractBaseInfo)) {
                throw new MithrasException("合同信息不存在");
            }
            if (StrUtil.isBlank(contractBaseInfo.getItemListHeader())) {
                throw new MithrasException("租赁物清单表头数据不存在");
            }
            LambdaQueryWrapper<ContractLeaseItem> query = Wrappers.lambdaQuery();
            query.eq(ContractLeaseItem::getContractId, req.getContractId());
            if (CollectionUtil.isNotEmpty(req.getItemIds())) {
                query.in(ContractLeaseItem::getId, req.getItemIds());
            }
            contractLeaseItemList = this.list(query);
        } else {
            ContractBaseInfoLib contractBaseInfoLib = contractBaseInfoLibService.getByOriginIdVersion(req.getContractId(), req.getVersion());
            if (Objects.isNull(contractBaseInfoLib)) {
                throw new MithrasException("合同信息不存在");
            }
            if (StrUtil.isBlank(contractBaseInfoLib.getItemListHeader())) {
                throw new MithrasException("租赁物清单表头数据不存在");
            }
            contractBaseInfo = BeanUtil.copyProperties(contractBaseInfoLib, ContractBaseInfo.class);
            contractBaseInfo.setId(contractBaseInfoLib.getOriginId());
            LambdaQueryWrapper<ContractLeaseItemLib> query = Wrappers.lambdaQuery();
            query.eq(ContractLeaseItem::getContractId, req.getContractId());
            query.eq(ContractLeaseItemLib::getVersion, req.getVersion());
            if (CollectionUtil.isNotEmpty(req.getItemIds())) {
                query.in(ContractLeaseItemLib::getOriginId, req.getItemIds());
            }
            List<ContractLeaseItemLib> contractLeaseItemLibList = contractLeaseItemLibService.list(query);
            contractLeaseItemList = contractLeaseItemLibList.stream().map(e -> {
                ContractLeaseItem contractLeaseItem = BeanUtil.copyProperties(e, ContractLeaseItem.class);
                contractLeaseItem.setId(e.getOriginId());
                return contractLeaseItem;
            }).collect(Collectors.toList());
        }
        // 写文件
        ExcelWriter excelWriter = ExcelUtil.getWriter(true);
        // 写表头
        excelWriter.writeHeadRow(leaseItemCommonService.preHandleHeader(JSONUtil.toList(contractBaseInfo.getItemListHeader(), String.class)));
        // 写数据
        if (CollectionUtil.isNotEmpty(contractLeaseItemList)) {
            for (int i = 0; i < contractLeaseItemList.size(); i++) {
                ContractLeaseItem contractLeaseItem = contractLeaseItemList.get(i);
                Map<String, Object> map = JSONUtil.toBean(contractLeaseItem.getRowData().replace("\n", "\\n"), Map.class);
                map.put("序号", i + 1);
                excelWriter.writeRow(map, false);
            }
        }
        excelWriter.autoSizeColumnAll();
        // 写出到输出流
        excelWriter.flush(outputStream, true);
    }

    @Override
    public List<ContractLeaseItem> listByContractId(Long contractId) {
        LambdaQueryWrapper<ContractLeaseItem> query = Wrappers.lambdaQuery();
        query.eq(ContractLeaseItem::getContractId, contractId);
        return this.list(query);
    }

    @Override
    public void removeByContractId(Long contractId) {
        LambdaQueryWrapper<ContractLeaseItem> query = new LambdaQueryWrapper<>();
        query.eq(ContractLeaseItem::getContractId, contractId);
        this.remove(query);
    }

    @Override
    public ContractLeaseItemListRSP pageList(ContractLeaseItemListREQ req) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(req.getContractId());
        if (Objects.isNull(contractBaseInfo)) {
            throw new MithrasException("合同数据不存在");
        }
        Page<ContractLeaseItem> pageResult;
        if (StrUtil.isBlank(req.getVersion())) {
            Page<ContractLeaseItem> pageQuery = new Page<>(req.getPage(), req.getPageSize());
            LambdaQueryWrapper<ContractLeaseItem> conditionQuery = Wrappers.lambdaQuery();
            conditionQuery.eq(ContractLeaseItem::getContractId, req.getContractId());
            pageResult = this.page(pageQuery, conditionQuery);
        } else {
            Page<ContractLeaseItemLib> pageQuery = new Page<>(req.getPage(), req.getPageSize());
            LambdaQueryWrapper<ContractLeaseItemLib> conditionQuery = Wrappers.lambdaQuery();
            conditionQuery.eq(ContractLeaseItem::getContractId, req.getContractId());
            conditionQuery.eq(ContractLeaseItemLib::getVersion, req.getVersion());
            Page<ContractLeaseItemLib> libPageResult = contractLeaseItemLibService.page(pageQuery, conditionQuery);
            pageResult = new Page<>();
            BeanUtil.copyProperties(libPageResult, pageResult);
        }
        ContractLeaseItemListRSP rsp = new ContractLeaseItemListRSP();
        //rsp.setLeaseItemTotalAmount(contractBaseInfo.getItemTotalAmount());
        if (StrUtil.isNotBlank(contractBaseInfo.getItemListHeader())) {
            rsp.setHeaderList(leaseItemCommonService.preHandleHeader(JSONUtil.toList(contractBaseInfo.getItemListHeader(), String.class)));
        }
        List<ContractLeaseItem> contractLeaseItemList = pageResult.getRecords();
        if (CollectionUtil.isEmpty(contractLeaseItemList)) {
            rsp.setPageList(PageR.empty(req.getPage(), req.getPageSize()));
        } else {
            List<ContractLeaseItemListRSP.RowDataModel> rowDataModelList = new LinkedList<>();
            for (int i = 0; i < contractLeaseItemList.size(); i++) {
                ContractLeaseItem contractLeaseItem = contractLeaseItemList.get(i);
                ContractLeaseItemListRSP.RowDataModel rowDataModel = new ContractLeaseItemListRSP.RowDataModel();
                rowDataModel.setItemId(contractLeaseItem.getId());
                rowDataModel.setDataList(new LinkedList<>());
                Map<String, Object> cellMap = JSONUtil.toBean(contractLeaseItem.getRowData().replace("\n", "\\n"), Map.class);
                cellMap.put("序号", i + 1);
                for (Map.Entry<String, Object> entry : cellMap.entrySet()) {
                    rowDataModel.getDataList().add(new ContractLeaseItemListRSP.CellDataModel(entry.getKey(), entry.getValue()));
                }
                rowDataModelList.add(rowDataModel);
            }
            rsp.setPageList(PageR.of(rowDataModelList, pageResult.getTotal(), req.getPage(), req.getPageSize()));
        }
        return rsp;
    }

    @Override
    public LeaseItemInfo getNewestOne(ContractSingleIdREQ req) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(req.getContractId());
        if (Objects.isNull(contractBaseInfo)) {
            throw new MithrasException("合同数据不存在");
        }
        return leaseItemInfoService.getNewestOne(contractBaseInfo.getProjReviewId());
    }

    @Override
    public ContractPreChooseLeaseItemRSP getPreChooseLeaseItem(ContractPreChooseLeaseItemREQ req) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(req.getContractId());
        if (Objects.isNull(contractBaseInfo)) {
            throw new MithrasException("合同数据不存在");
        }
        if (!Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.hui_zu.name())) {
            throw new MithrasException("仅回租合同支持引入租赁物");
        }
        LeaseItemInfo leaseItemInfo = leaseItemInfoService.getNewestOne(contractBaseInfo.getProjReviewId());
        if (Objects.isNull(leaseItemInfo)) {
            throw new MithrasException("没有找到最新生效的租赁物信息");
        }
        if (StrUtil.isBlank(leaseItemInfo.getItemListHeader())) {
            throw new MithrasException("暂无可选择的租赁物");
        }
        ContractPreChooseLeaseItemRSP rsp = new ContractPreChooseLeaseItemRSP();
        rsp.setLeaseItemInfoId(leaseItemInfo.getId());
        rsp.setHeaderList(leaseItemCommonService.preHandleHeader(JSONUtil.toList(leaseItemInfo.getItemListHeader(), String.class)));
        Page<LeaseItemListRowData> pageQuery = new Page<>(req.getPage(), req.getPageSize());
        LambdaQueryWrapper<LeaseItemListRowData> conditionQuery = new LambdaQueryWrapper<>();
        conditionQuery.eq(LeaseItemListRowData::getLeaseItemInfoId, leaseItemInfo.getId());
        if (StrUtil.isNotBlank(req.getName())) {
            List<String> applySqlList = new LinkedList<>();
            applySqlList.add(String.format("json_extract(row_data, '$.\"名称\"') like '%s'", "%" + req.getName() + "%"));
            applySqlList.add(String.format("json_extract(row_data, '$.\"设备名称\"') like '%s'", "%" + req.getName() + "%"));
            applySqlList.add(String.format("json_extract(row_data, '$.\"租赁船舶名称\"') like '%s'", "%" + req.getName() + "%"));
            applySqlList.add(String.format("json_extract(row_data, '$.\"名称*\"') like '%s'", "%" + req.getName() + "%"));
            applySqlList.add(String.format("json_extract(row_data, '$.\"设备名称*\"') like '%s'", "%" + req.getName() + "%"));
            applySqlList.add(String.format("json_extract(row_data, '$.\"租赁船舶名称*\"') like '%s'", "%" + req.getName() + "%"));
            conditionQuery.apply("(" + CharSequenceUtil.join(" or ", applySqlList) + ")");
        }
        Page<LeaseItemListRowData> pageResult = leaseItemListRowDataService.page(pageQuery, conditionQuery);
        if (CollectionUtil.isEmpty(pageResult.getRecords())) {
            rsp.setPageList(PageR.empty(req.getPage(), req.getPageSize()));
            return rsp;
        }
        List<LeaseItemListRowData> originList = pageResult.getRecords();
        // 查询对应项目的租赁物审核管理数据
        LambdaQueryWrapper<ContractLeaseItem> query = Wrappers.lambdaQuery();
        query.in(ContractLeaseItem::getLeaseItemRowDataId, originList.stream().map(LeaseItemListRowData::getId).collect(Collectors.toList()));
        List<ContractLeaseItem> contractLeaseItemList = this.list(query);
        Map<Long, ContractLeaseItem> existMap = contractLeaseItemList.stream().filter(e -> !Objects.equals(e.getLeaseItemRowDataId(), -1L)).collect(Collectors.toMap(ContractLeaseItem::getLeaseItemRowDataId, e -> e));
        List<ContractPreChooseLeaseItemRSP.RowDataModel> rowDataModelList = new LinkedList<>();
        for (int i = 0; i < originList.size(); i++) {
            LeaseItemListRowData leaseItemListRowData = originList.get(i);
            ContractPreChooseLeaseItemRSP.RowDataModel rowDataModel = new ContractPreChooseLeaseItemRSP.RowDataModel();
            rowDataModel.setItemId(leaseItemListRowData.getId());
            Map<String, Object> cellMap = JSONUtil.toBean(leaseItemListRowData.getRowData().replace("\n", "\\n"), Map.class);
            cellMap.put("序号", i + 1);
            List<ContractPreChooseLeaseItemRSP.CellDataModel> cellDataModelList = new LinkedList<>();
            for (Map.Entry<String, Object> entry : cellMap.entrySet()) {
                ContractPreChooseLeaseItemRSP.CellDataModel cellDataModel = new ContractPreChooseLeaseItemRSP.CellDataModel();
                cellDataModel.setKey(entry.getKey());
                cellDataModel.setValue(entry.getValue());
                cellDataModelList.add(cellDataModel);
            }
            rowDataModel.setDataList(cellDataModelList);
            ContractLeaseItem hasChoose = existMap.get(leaseItemListRowData.getId());
            // 判断是否可选择
            if (Objects.isNull(hasChoose)) {
                rowDataModel.setCanChoose(YesOrNoNumberEnum.YES.getCode());
                rowDataModel.setHasChoose(YesOrNoNumberEnum.NO.getCode());
            } else {
                // 已被选择的话进一步判断是否当前合同
                if (Objects.equals(hasChoose.getContractId(), contractBaseInfo.getId())) {
                    rowDataModel.setCanChoose(YesOrNoNumberEnum.YES.getCode());
                    rowDataModel.setHasChoose(YesOrNoNumberEnum.YES.getCode());
                } else {
                    rowDataModel.setCanChoose(YesOrNoNumberEnum.NO.getCode());
                    rowDataModel.setHasChoose(YesOrNoNumberEnum.NO.getCode());
                }
            }
            rowDataModelList.add(rowDataModel);
        }
        rsp.setPageList(PageR.of(rowDataModelList, pageResult.getTotal(), req.getPage(), req.getPageSize()));
        return rsp;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void chooseLeaseItem(ContractChooseLeaseItemREQ req) {
        Long contractId = req.getContractId();
        // 查询合同数据
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
        if (Objects.isNull(contractBaseInfo)) {
            throw new MithrasException("合同数据不存在");
        }
        if (!Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.hui_zu.name())) {
            throw new MithrasException("仅回租合同支持引入租赁物");
        }
        // 查询租赁物审核管理数据
        LeaseItemInfo leaseItemInfo = leaseItemInfoService.getById(req.getLeaseItemInfoId());
        if (Objects.isNull(leaseItemInfo)) {
            throw new MithrasException("租赁物审核管理数据不存在");
        }
        // 清除老数据
        log.warn("用户重新选择租赁物，删除已有的数据后重新生成[userId:{}, contractId:{}]", AccountUtil.getLoginInfo().getId(), req.getContractId());
        this.removeByContractId(req.getContractId());
        if (CollectionUtil.isEmpty(req.getItemIds())) {
            contractBaseInfo.setLeaseItemInfoId(-1L);
            contractBaseInfoService.updateById(contractBaseInfo);
            return;
        }
        // 查询租赁物数据
        LambdaQueryWrapper<LeaseItemListRowData> rowDataQuery = Wrappers.lambdaQuery();
        rowDataQuery.eq(LeaseItemListRowData::getLeaseItemInfoId, leaseItemInfo.getId());
        rowDataQuery.in(LeaseItemListRowData::getId, req.getItemIds());
        List<LeaseItemListRowData> rowDataList = leaseItemListRowDataService.list(rowDataQuery);
        if (CollectionUtil.isEmpty(rowDataList)) {
            return;
        }
        List<ContractLeaseItem> contractLeaseItemList = rowDataList.stream().map(e -> {
            ContractLeaseItem contractLeaseItem = new ContractLeaseItem();
            contractLeaseItem.setContractId(contractId);
            contractLeaseItem.setRowData(e.getRowData());
            contractLeaseItem.setLeaseItemRowDataId(e.getId());
            return contractLeaseItem;
        }).collect(Collectors.toList());
        // 更新数据
        contractBaseInfo.setItemListHeader(leaseItemInfo.getItemListHeader());
        contractBaseInfo.setLeaseItemInfoId(leaseItemInfo.getId());
        contractBaseInfo.setLeaseItemTypes(leaseItemInfo.getLeaseItemTypes());
        contractBaseInfoService.updateById(contractBaseInfo);
        this.saveBatch(contractLeaseItemList);
        leaseItemInfoService.updateContractIds(leaseItemInfo.getId(), contractId, LeaseOperationTypeEnum.INSERT);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void unbindLeaseItem(Long contractId) {
        List<ContractLeaseItem> contractLeaseItemList = this.listByContractId(contractId);
        if (CollectionUtil.isEmpty(contractLeaseItemList)) {
            return;
        }
        contractLeaseItemList.forEach(e -> e.setLeaseItemRowDataId(-1L));
        this.updateBatchById(contractLeaseItemList);
    }

    @Override
    public void initLeaseItem(Long contractId, Long projReviewId, LeaseItemInfo leaseItemInfo) {
        // 找到项目下所有的合同
        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.listByProjReviewIds(Collections.singletonList(projReviewId));
        // 去掉自己
        contractBaseInfoList.removeIf(e -> Objects.equals(e.getId(), contractId));
        List<LeaseItemListRowData> leaseItemListRowData;
        if (CollectionUtil.isEmpty(contractBaseInfoList)) {
            // 说明没有其他合同，直接使用全部租赁物
            leaseItemListRowData = leaseItemListRowDataService.listByLeaseItemInfoId(leaseItemInfo.getId());
        } else {
            // 说明已经存在其他合同，需要去掉被占用的
            List<Long> contractIds = contractBaseInfoList.stream().map(ContractBaseInfo::getId).collect(Collectors.toList());
            leaseItemListRowData = leaseItemListRowDataService.listFreeItem(leaseItemInfo.getId(), contractIds);
        }
        // 去掉已经被占用的
        if (CollectionUtil.isNotEmpty(leaseItemListRowData)) {
            List<ContractLeaseItem> contractLeaseItemList = leaseItemListRowData.stream().map(e -> {
                ContractLeaseItem contractLeaseItem = new ContractLeaseItem();
                contractLeaseItem.setContractId(contractId);
                contractLeaseItem.setRowData(e.getRowData());
                contractLeaseItem.setLeaseItemRowDataId(e.getId());
                return contractLeaseItem;
            }).collect(Collectors.toList());
            this.saveBatch(contractLeaseItemList);
            // 更新合同主表，重新查一下保证数据是最新的
            ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
            contractBaseInfo.setLeaseItemInfoId(leaseItemInfo.getId());
            contractBaseInfo.setItemListHeader(leaseItemInfo.getItemListHeader());
            //contractBaseInfo.setItemTotalAmount(leaseItemInfo.getTotalAmountOfLeaseItem());
            contractBaseInfo.setLeaseItemTypes(leaseItemInfo.getLeaseItemTypes());
            contractBaseInfoService.updateById(contractBaseInfo);
        }
    }

    @Override
    public void copyLeaseItemFile(Long contractId, LeaseItemInfo leaseItemInfo) {
        List<MaterialsList> materialsListList = materialsListService.list(BusinessModuleEnum.LEASE_TEXT.name(), Arrays.asList(LeaseTextFileEnum.LEASE_ITEM.name(), LeaseTextFileEnum.LEASE_ENTER_LETTER.name()), Collections.singletonList(leaseItemInfo.getId()));
        if (CollectionUtil.isEmpty(materialsListList)) {
            return;
        }
        List<MaterialsList> todoList = new LinkedList<>();
        materialsListList.forEach(item -> {
            try {
                String bizPath = BusinessModuleEnum.CONTRACT.name() + "/" + contractId + "/" + ContractTypeEnum.MAIN_CONTRACT.name();
                String newFilePath = ossClient.getBasePath() + bizPath;
                String newOssFilename = bizPath + "/" + item.getFilename();
                ossClient.copy(ossClient.getBasePath() + item.getOssFilename(), ossClient.getBasePath() + newOssFilename);
                MaterialsList newMaterial = new MaterialsList();
                newMaterial.setBelongId(contractId);
                newMaterial.setMaterialsType(ContractTypeEnum.MAIN_CONTRACT.name());
                newMaterial.setBusinessType(BusinessModuleEnum.CONTRACT.name());
                newMaterial.setOssFilename(newOssFilename);
                newMaterial.setSuffix(item.getSuffix());
                newMaterial.setFilename(item.getFilename());
                newMaterial.setFilePath(newFilePath);
                newMaterial.setSystemGenerate(YesOrNoNumberEnum.NO.getCode());
                todoList.add(newMaterial);
            } catch (Exception e) {
                log.error("创建合同时拷贝租赁物文本文件发生异常[{}]", JSONUtil.toJsonStr(item), e);
            }
        });
        materialsListService.saveBatch(todoList);
    }
}

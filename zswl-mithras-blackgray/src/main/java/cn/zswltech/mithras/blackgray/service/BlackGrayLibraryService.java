package cn.zswltech.mithras.blackgray.service;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.blackgray.dto.req.*;
import cn.zswltech.mithras.blackgray.dto.rsp.*;
import cn.zswltech.mithras.blackgray.mapper.model.BlackGrayLibrary;

import java.util.List;
import java.util.Set;

/**
* @description 黑灰名单库
* @author
* @date 2023-11-28
*/
public interface BlackGrayLibraryService {

    void add(BlackGrayLibraryAddREQ req);

    void modify(BlackGrayLibraryModifyREQ req);

    PageR<BlackGrayLibraryListRSP> list(BlackGrayLibraryListREQ req);

    PageR<BlackGrayLibraryDistinctListRSP> distinctList(BlackGrayLibraryDistinctListREQ req, boolean needPage);

    BlackGrayLibraryRSP libraryRecord(BlackGrayLibraryREQ req);

    PageR<BlackGrayLibraryOrgListRSP> orgList(BlackGrayLibraryListREQ req);

    BlackGrayLibraryDetailRSP detail(Long id);

    PageR<BlackGrayWarehouseRecordListRSP> recordStock(BlackGrayWarehouseRecordListREQ req);

    List<BlackGrayCanBreakBusinessRSP> getCanBreakBusiness(BlackGrayCanBreakREQ req);

    BlackGrayEnterpriseRiskScaleRSP getEnterpriseRiskScale(BlackGrayEnterpriseRiskScaleREQ req);

    List<BlackGrayLibraryListRSP> batchQuery(BlackGrayBatchQueryFileREQ req);

    List<BlackGrayLibraryListRSP> batchQuery(List<BlackGrayBatchQueryREQ> req, List<String> businessTypeList);

    void remove(BlackGrayLibraryRemoveREQ req);

    //尝试入库。获取库中黑灰名单，判断是否已经有对应数据做更新，无则插入。级别小于库中级别丢弃
    //void attemptWarehouse(BlackGrayLibrary blackGrayLibrary);

    //尝试入库。获取库中黑灰名单，判断是否已经有对应数据做更新，无则插入。
    void attemptBatchWarehouse(List<BlackGrayLibrary> blackGrayLibrary);

    //全量入库，库中已经存在改类型全量出库，新数据入库 applyReasonType 申请原因类型编号
    void completeWarehouse(List<CompleteWarehouseREQ> completeWarehouseREQS, String applyReasonType);

    //出库，金控出自己的，企业会出自己+金控的
    void outbound(BlackGrayLibrary blackGrayLibrary);

    //辐射子企业
    void radiationSubsidiary(BlackGrayLibrary blackGrayLibrary);

    List<BlackGrayLibrary> listByNames(Set<String> enterpriseNames);

    // 撞库查询一条数据
    BlackGrayLibrary getOne(BlackGrayCollisionLibraryREQ req);

    // 根据机构code，查询字典，获取有权限的业务类型
    List<String> businessType(List<String> orgCodeList);

   /* List<BlackGrayLibCountDTO> countBlackGrayByApplyOrgAndSetCache(String businessType);

    List<BlackGrayLibAllCountVo> countBlackGrayByAllAndSetCache(String businessType);

    // 统计黑灰名单企业数量。 orgCodeList决定查询范围，金控查所有，公司查本公司
    List<BlackGrayLibCountDTO> blackGrayCount(List<String> orgCodeList, String businessType);

    // 单一集团维度列表
    PageR<BlackGrayGroupListRSP> blackGrayGroupList(BlackGrayGroupListREQ req, boolean needPage);

    *//**
     * 单一集团维度详情
     *//*
    BlackGrayGroupDetailRSP blackGrayGroupDetail(String groupName);

    *//**
     * 集团主企业的在库详情
     *//*
    PageR<GroupInStockListRSP> groupStockList(GroupStockListREQ req, boolean needPage);

    *//**
     * 集团下属企业在库列表
     *//*
    PageR<BlackGrayLibraryDistinctListRSP> groupCompanyStockList(GroupStockListREQ req, boolean needPage);

    void blackGrayAutoWarehouseJob();*/
}
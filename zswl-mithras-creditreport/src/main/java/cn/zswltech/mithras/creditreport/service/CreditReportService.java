package cn.zswltech.mithras.creditreport.service;

import cn.zswltech.mithras.dto.creditreport.CreditSearchCompareBusinessDTO;
import cn.zswltech.mithras.workflow.application.flow.port.FlowEndEventProcessor;

import java.util.List;

public interface CreditReportService extends FlowEndEventProcessor {

   /* *//**
     * 新增征信报告查询
     * @param cmd 征信报告请求参数
     * @return 新增成功或新增失败
     *//*
    void add(CreditReportAddCmd cmd);

    *//**
     * 查询有征信报告查询权限的客户列表信息
     * @param clientName 客户名称
     * @param creditReportId 征信查询主id
     * @return 有征信报告查询权限的客户列表信息
     *//*
    List<ClientInfo> getClientInfo(String clientName, Long creditReportId);

    *//**
     * 通过客户id反显客户信息和项目信息
     * @param clientId 客户id
     * @return 反显客户信息和项目信息
     *//*
    CreditReportClientAddDTO showCreditReportByClientId(Long clientId);

    *//**
     * 保存征信报告查询详情的客户信息
     * @param cmd 征信报告请求参数
     *//*
    void saveCreditReport(CreditReportSaveCmd cmd);

    *//**
     * 征信报告查询提交审批
     * @param cmd
     *//*
    List<CreditReportSubmitDTO> submit(CreditReportSubmitCmd cmd);

    *//**
     * 征信报告查询列表
     * @param req 征信报告查询请求参数
     * @return 征信报告查询列表
     *//*
    PageR<CreditReportListDTO> list(CreditReportListREQ req);

    *//**
     * 征信查询详情
     * @param id
     * @return
     *//*
    CreditReportDetailDTO detail(Long id);*/

   /* *//**
     * 征信查询删除
     * @param id
     *//*
    void delete(Long id);
*/
    /**
     * 多个clientId的工商信息校验
     * @param
     * @param isHistory
     * @return
     */
    List<CreditSearchCompareBusinessDTO> compareBusiness(Long creditSearchId,Boolean isHistory);



  /*  *//**
     * 导出征信查询
     * @param outputStream
     * @param req
     *//*
    void export(ServletOutputStream outputStream, CreditReportListREQ req);*/
}

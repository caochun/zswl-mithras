package cn.zswltech.mithras.service.service.creditreport;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.creditreport.CreditReportListDTO;
import cn.zswltech.mithras.dto.creditreport.CreditReportProjectReviewAddDTO;
import cn.zswltech.mithras.dto.creditreport.CreditSearchProjectCmd;
import cn.zswltech.mithras.dto.creditreport.CreditSearchProjectQuery;

public interface CreditSearchProjectService {


    /**
     * 征信报告查询列表
     * @param req 征信报告查询请求参数
     * @return 征信报告查询列表
     */
    PageR<CreditReportListDTO> list(CreditSearchProjectQuery req);

    /**
     * 通过项目id反显客户信息和项目信息
     * @param cmd 项目id及项目业务类型
     * @return 反显客户信息和项目信息
     */
    CreditReportProjectReviewAddDTO showCreditReportByProjId(CreditSearchProjectCmd cmd);

/*    *//**
     * 删除征信报告
     * @param id
     *//*
    void delete(Long id);*/
}

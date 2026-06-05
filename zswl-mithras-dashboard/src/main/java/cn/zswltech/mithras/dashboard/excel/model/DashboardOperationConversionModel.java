package cn.zswltech.mithras.dashboard.excel.model;

import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.service.excel.model.ExcelModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardOperationConversionModel extends ExcelModel {

    @SimpleExcelHeader(headerName = "部门名称", headerOrder = 10)
    private String bizDeptName;

    @SimpleExcelHeader(headerName = "调整人数", headerOrder = 20)
    private String adjustCount;

    @SimpleExcelHeader(headerName = "拜访个数", headerOrder = 30)
    private String visitCount;

    @SimpleExcelHeader(headerName = "立项个数", headerOrder = 40)
    private String projEstaCount;

    @SimpleExcelHeader(headerName = "尽调个数", headerOrder = 50)
    private String dueDiligenceCount;

    @SimpleExcelHeader(headerName = "评审个数", headerOrder = 60)
    private String reviewCount;

    @SimpleExcelHeader(headerName = "投放个数", headerOrder = 70)
    private String deliveryCount;

    @SimpleExcelHeader(headerName = "「访客-立项」转化率-立项数/访客数：当期", headerOrder = 80)
    private String projEstaVisitCurrentTerm;

    @SimpleExcelHeader(headerName = "「访客-立项」转化率-立项数/访客数：去年同期", headerOrder = 90)
    private String projEstaVisitLastTerm;

    @SimpleExcelHeader(headerName = "「访客-立项」转化率-立项数/访客数：当年平均", headerOrder = 100)
    private String projEstaVisitCurrentAverage;

    @SimpleExcelHeader(headerName = "「访客-立项」转化率-立项数/访客数：去年平均", headerOrder = 110)
    private String projEstaVisitLastAverage;

    @SimpleExcelHeader(headerName = "「访客-投放」转化率-投放个数/访客个数：当期", headerOrder = 120)
    private String deliveryVisitCurrentTerm;

    @SimpleExcelHeader(headerName = "「访客-投放」转化率-投放个数/访客个数：去年同期", headerOrder = 130)
    private String deliveryVisitLastTerm;

    @SimpleExcelHeader(headerName = "「访客-投放」转化率-投放个数/访客个数：去年平均", headerOrder = 140)
    private String deliveryVisitLastAverage;

    @SimpleExcelHeader(headerName = "「访客-投放」转化率-投放个数/访客个数：当年平均", headerOrder = 150)
    private String deliveryVisitCurrentAverage;

    @SimpleExcelHeader(headerName = "「立项-尽调」转化率-尽调数/立项数：当期", headerOrder = 160)
    private String dueDiliProjEstaCurrentTerm;

    @SimpleExcelHeader(headerName = "「立项-尽调」转化率-尽调数/立项数：去年同期", headerOrder = 170)
    private String dueDiliProjEstaLastTerm;

    @SimpleExcelHeader(headerName = "「立项-尽调」转化率-尽调数/立项数：当年平均", headerOrder = 180)
    private String dueDiliProjEstaCurrentAverage;

    @SimpleExcelHeader(headerName = "「立项-尽调」转化率-尽调数/立项数：去年平均", headerOrder = 190)
    private String dueDiliProjEstaLastAverage;

    @SimpleExcelHeader(headerName = "「尽调-评审」转化率-评审数/尽调数：当期", headerOrder = 200)
    private String reviewDueDiliCurrentTerm;

    @SimpleExcelHeader(headerName = "「尽调-评审」转化率-评审数/尽调数：去年同期", headerOrder = 210)
    private String reviewDueDiliLastTerm;

    @SimpleExcelHeader(headerName = "「尽调-评审」转化率-评审数/尽调数：去年平均", headerOrder = 220)
    private String reviewDueDiliLastAverage;

    @SimpleExcelHeader(headerName = "「尽调-评审」转化率-评审数/尽调数：当年平均", headerOrder = 230)
    private String reviewDueDiliCurrentAverage;

    @SimpleExcelHeader(headerName = "「尽调-投放」转化率-投放数/尽调数：当期", headerOrder = 240)
    private String deliveryDueDiliCurrentTerm;

    @SimpleExcelHeader(headerName = "「尽调-投放」转化率-投放数/尽调数：去年同期", headerOrder = 250)
    private String deliveryDueDiliLastTerm;

    @SimpleExcelHeader(headerName = "「尽调-投放」转化率-投放数/尽调数：去年平均", headerOrder = 260)
    private String deliveryDueDiliLastAverage;

    @SimpleExcelHeader(headerName = "「尽调-投放」转化率-投放数/尽调数：当年平均", headerOrder = 270)
    private String deliveryDueDiliCurrentAverage;

    @SimpleExcelHeader(headerName = "「评审-投放」转化率-投放数/评审数：当期", headerOrder = 280)
    private String deliveryReviewCurrentTerm;

    @SimpleExcelHeader(headerName = "「评审-投放」转化率-投放数/评审数：去年同期", headerOrder = 290)
    private String deliveryReviewLastTerm;

    @SimpleExcelHeader(headerName = "「评审-投放」转化率-投放数/评审数：去年平均", headerOrder = 300)
    private String deliveryReviewLastAverage;

    @SimpleExcelHeader(headerName = "「评审-投放」转化率-投放数/评审数：当年平均", headerOrder = 310)
    private String deliveryReviewCurrentAverage;
}
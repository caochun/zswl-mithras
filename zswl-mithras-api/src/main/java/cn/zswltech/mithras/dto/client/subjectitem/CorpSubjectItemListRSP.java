package cn.zswltech.mithras.dto.client.subjectitem;

import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author junke
 */
@Data
@NoArgsConstructor
public class CorpSubjectItemListRSP {

    @ApiModelProperty("sheet名称")
    private String subjectType;
    @ApiModelProperty("报告类型")
    private String reportType;
    @ApiModelProperty("年份")
    private Integer year;
    @ApiModelProperty("报告期")
    private Integer quarter;
    @ApiModelProperty("科目列表")
    private List<SubjectItem> itemList = new ArrayList<>();

    public CorpSubjectItemListRSP(Integer year, Integer quarter, String reportType, String subjectType) {
        this.year = year;
        this.quarter = quarter;
        this.reportType = reportType;
        this.subjectType = subjectType;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubjectItem {

        @Deprecated
        //改字段用于排序，前端用不到
        // 该字段排序会有问题，不用了，用order
        private Long id;

        private Integer order;

        @ApiModelProperty("科目代码")
        private String subjectCode;
        @ApiModelProperty("科目名称")
        private String subjectName;
        @ApiModelProperty("科目值，已经扩大10000倍")
        private Long subjectValue;
        @ApiModelProperty("百分比，同金额，已经扩大10000倍")
        private Long subjectPercent;
        @ApiModelProperty("同比，已经扩大10000倍")
        private Long subjectOverYear;
        @ApiModelProperty("科目值，已转字符串")
        private String subjectValueStr;
        @ApiModelProperty("百分比str")
        private String subjectPercentStr;
        @ApiModelProperty("同比百分比str")
        private String subjectOverYearStr;

        public String getSubjectPercentStr() {
//            return null == subjectPercentStr ? null : subjectPercentStr + "%";
            if (StrUtil.isBlank(subjectPercentStr)) {
                return null;
            }
            if (subjectPercentStr.endsWith("%")) {
                return subjectPercentStr;
            }
            return subjectPercentStr + "%";
        }

        public String getSubjectOverYearStr() {
//            return null == subjectOverYearStr ? null : subjectOverYearStr + "%";
            if (StrUtil.isBlank(subjectOverYearStr)) {
                return null;
            }
            if (subjectOverYearStr.endsWith("%")) {
                return subjectOverYearStr;
            }
            return subjectOverYearStr + "%";
        }


        public SubjectItem(Long id, String subjectCode, String subjectName, Long subjectValue) {
            this.id = id;
            this.subjectCode = subjectCode;
            this.subjectName = subjectName;
            this.subjectValue = subjectValue;
        }
    }
}

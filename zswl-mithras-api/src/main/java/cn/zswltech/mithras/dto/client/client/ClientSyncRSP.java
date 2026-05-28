package cn.zswltech.mithras.dto.client.client;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.client.commerceinfo.CorpCommerceInfoAddREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;

/**
 * @author junke
 */
@Data
@ApiModel("客户基本信息天眼查同步-返回体")
public class ClientSyncRSP {

    /**
     * 工商信息变化项。如果值不为null，就代表有变化，里面的是新值。
     */
    private CorpCommerceInfoAddREQ changedCommerceInfo;

    @ApiModelProperty("法人公司注册地址变更；没有注册地址时是新增，存在时是修改")
    private PageR<RegisterAddressChangedItem> registerAddressChangedItemList;


    /**
     * 股东信息变化
     */
    @ApiModelProperty("股东信息变化列表；新增ADD时里面是天眼查的新值，修改MODIFY时只有有变化的字段才有新值，DELETE删除时里面的值都是null")
    private PageR<ShareholderInfoChangedItem> shareholderInfoChangedItemList;

    /**
     * 关联企业变化
     */
    @ApiModelProperty("关联企业变化列表；新增ADD时里面是天眼查的新值，修改MODIFY时只有有变化的字段才有新值，DELETE删除时里面的值都是null")
    private PageR<RelatedEnterpriseChangedItem> relatedEnterpriseChangedItemList;

    @Data
    public static class RegisterAddressChangedItem {
        @ApiModelProperty("差异类型  ；ADD/MODIFY")
        private String changedType;
        @ApiModelProperty("currentId；当差异类型是MODIFY的时候，此只为当前系统内的注册地址记录id")
        private Long id;
        @ApiModelProperty(value = "地址类型", required = true)
        private String addressType;
        @ApiModelProperty(value = "国别", required = true)
        private String country;
        @ApiModelProperty(value = "国别名称", required = true)
        private String countryName;
        @ApiModelProperty(value = "省份，国内时必填")
        private String province;
        @ApiModelProperty(value = "省份名称，国内时必填")
        private String provinceName;
        @ApiModelProperty("城市，国内时必填")
        private String city;
        @ApiModelProperty("城市名称，国内时必填")
        private String cityName;
        @ApiModelProperty("区/县，国内时必填")
        private String district;
        @ApiModelProperty("区/县名称，国内时必填")
        private String districtName;
        @ApiModelProperty("详细地址，国内时必填")
        private String detail;
        @ApiModelProperty("行政区域代码，国内时必填")
        private String regionCode;
    }

    @Data
    public static class RelatedEnterpriseChangedItem {
        @ApiModelProperty("差异类型；ADD/MODIFY/DELETE")
        private String changedType;

        @ApiModelProperty("id；当差异类型是MODIFY/DELETE的时候，此只为当前系统内的注册地址记录id")
        private Long id;

        /**
         * 关联企业名称
         */
        @ApiModelProperty(value = "关联企业名称", required = true)
        private String enterpriseName;

        /**
         * 关联关系
         */
        @ApiModelProperty("关联关系")
        private String relationship;

        /**
         * 注册资本
         */

        @ApiModelProperty("注册资本")
        private Long registerCapital;

        /**
         * 持股比例
         */
        @ApiModelProperty("持股比例")
        private Long shareholdingRatio;

        /**
         * 投资金额（万元）
         */

        @ApiModelProperty("投资金额（万元）")
        private Long investAmount;


        @ApiModelProperty("存续状态")
        private String continuousStatus;

        @ApiModelProperty("成立日期")
        private LocalDate establishDate;

        @ApiModelProperty("行业")
        private String industryType;

        @ApiModelProperty("行业名称")
        private String industryTypeName;

        @ApiModelProperty("改变的属性")
        private List<String> changeFields;
    }

    @Data
    public static class ShareholderInfoChangedItem {
        @ApiModelProperty("差异类型；ADD/MODIFY/DELETE")
        private String changedType;

        @ApiModelProperty("id；当差异类型是MODIFY/DELETE的时候，此只为当前系统内的注册地址记录id")
        private Long id;

        /**
         * 股东类型
         */
        @NotBlank
        @ApiModelProperty("股东类型")
        private String shareholderType;

        /**
         * 股东姓名
         */
        @ApiModelProperty("股东姓名")
        private String shareholderName;


        @ApiModelProperty("认缴金额")
        private Long paidTotal;


        @ApiModelProperty("实缴金额")
        private Long actualPaidTotal;

        /**
         * 出资方式
         */
        @ApiModelProperty("出资方式")
        private String capitalWay;

        /**
         * 出资占比
         */
        @ApiModelProperty("出资占比")
        private Long capitalPercent;

        /**
         * 是否实际控制人
         */
        @ApiModelProperty("是否实际控制人")
        private Boolean realController;

        @ApiModelProperty("改变的属性")
        private List<String> changeFields;


    }

}

package cn.zswltech.mithras.service.mapper.model.afterlease;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description 租后检查外部查询承租人/担保人信息
 * @date 2022-11-17
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("new_after_lease_check_external_query_client_info")
public class NewAfterLeaseCheckExternalQueryClientInfo extends BaseModel implements Serializable, IEntity {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属外部信息查询任务id
     */
    @TableField("query_id")
    private Long queryId;

    /**
     * 客户角色-主承租人（MAIN_LESSEE）承租人（LESSEE）担保人（GUARANTEE）
     */
    @TableField("client_role")
    private String clientRole;

    /**
     * 承租人/担保人id
     */
    @TableField("client_id")
    private Long clientId;

    @TableField("client_type")
    private String clientType;

    /**
     * 查询时间
     */
    @TableField("query_time_from")
    private LocalDate queryTimeFrom;

    /**
     * 查询时间
     */
    @TableField("query_time_to")
    private LocalDate queryTimeTo;

    /**
     * 全国企业信用信息公示系统查询
     */
    @TableField("credit_info")
    private String creditInfo;

    /**
     * 全国法院被执行人或被纳入失信人查询
     */
    @TableField("court_info")
    private String courtInfo;

    /**
     * 裁判文书网
     */
    @TableField("referee_network_info")
    private String refereeNetworkInfo;

    /**
     * 中登网登记及抵押登记
     */
    @TableField("zhongdeng_info")
    private String zhongdengInfo;

    /**
     * 信用报告
     */
    @TableField("credit_report")
    private String creditReport;

    /**
     * 其他
     */
    @TableField("other")
    private String other;

    @Override
    public void setMainId(Long id) {
        this.queryId = id;
    }

    @Override
    public Long getMainId() {
        return this.queryId;
    }
}

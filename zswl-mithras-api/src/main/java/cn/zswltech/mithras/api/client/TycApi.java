package cn.zswltech.mithras.api.client;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.external.ExternalPageREQ;
import cn.zswltech.mithras.dto.client.external.ExternalSyncREQ;
import cn.zswltech.mithras.dto.client.external.tyc.TycAbnormalRSP;
import cn.zswltech.mithras.dto.client.external.tyc.TycConsumptionRestrictionRSP;
import cn.zswltech.mithras.dto.client.external.tyc.TycDishonestRSP;
import cn.zswltech.mithras.dto.client.external.tyc.TycEquityInfoRSP;
import cn.zswltech.mithras.dto.client.external.tyc.TycJudicialRSP;
import cn.zswltech.mithras.dto.client.external.tyc.TycLawSuitRSP;
import cn.zswltech.mithras.dto.client.external.tyc.TycMortgageInfoRSP;
import cn.zswltech.mithras.dto.client.external.tyc.TycPunishmentInfoRSP;
import cn.zswltech.mithras.dto.client.external.tyc.TycZhixingInfoRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * 外部信息 天眼查
 *
 * @author wangchuanhao
 * @date 2022/6/20 11:14 PM
 */
@Api(tags = "外部信息-天眼查-接口")
@RequestMapping("/tyc")
public interface TycApi {

    /**
     * 动产抵押
     * @param req
     * @return
     */
    @ApiOperation("动产抵押")
    @PostMapping("/mortgageInfo/list")
    R<PageR<TycMortgageInfoRSP>> mortgageInfoList(@RequestBody @Valid ExternalPageREQ req);

    /**
     * 股权出质
     * @param req
     * @return
     */
    @ApiOperation("股权出质")
    @PostMapping("/equityInfo/list")
    R<PageR<TycEquityInfoRSP>> equityInfo(@RequestBody @Valid ExternalPageREQ req);

    /**
     * 行政处罚
     * @param req
     * @return
     */
    @ApiOperation("行政处罚")
    @PostMapping("/punishmentInfo/list")
    R<PageR<TycPunishmentInfoRSP>> punishmentInfoList(@RequestBody @Valid ExternalPageREQ req);

    /**
     * 经营异常
     * @param req
     * @return
     */
    @ApiOperation("经营异常")
    @PostMapping("/abnormal/list")
    R<PageR<TycAbnormalRSP>> abnormalList(@RequestBody @Valid ExternalPageREQ req);

    /**
     * 司法协助
     * @param req
     * @return
     */
    @ApiOperation("司法协助")
    @PostMapping("/judicial/list")
    R<PageR<TycJudicialRSP>> judicialList(@RequestBody @Valid ExternalPageREQ req);

    /**
     * 法律诉讼
     * @param req
     * @return
     */
    @ApiOperation("法律诉讼")
    @PostMapping("/lawSuit/list")
    R<PageR<TycLawSuitRSP>> lawSuitList(@RequestBody @Valid ExternalPageREQ req);

    /**
     * 限制消费令
     * @param req
     * @return
     */
    @ApiOperation("限制消费令")
    @PostMapping("/consumptionRestriction/list")
    R<PageR<TycConsumptionRestrictionRSP>> consumptionRestrictionList(@RequestBody @Valid ExternalPageREQ req);

    /**
     * 被执行人
     * @param req
     * @return
     */
    @ApiOperation("被执行人")
    @PostMapping("/zhixingInfo/list")
    R<PageR<TycZhixingInfoRSP>> zhixingInfoList(@RequestBody @Valid ExternalPageREQ req);

    /**
     * 失信人
     * @param req
     * @return
     */
    @ApiOperation("失信人")
    @PostMapping("/dishonest/list")
    R<PageR<TycDishonestRSP>> dishonestList(@RequestBody @Valid ExternalPageREQ req);

    /**
     * 同步天眼查外部数据
     * @param req
     * @return
     */
    @ApiOperation("同步天眼查外部数据")
    @PostMapping("/external/sync")
    R<Void> externalSync(@RequestBody @Valid ExternalSyncREQ req);
}

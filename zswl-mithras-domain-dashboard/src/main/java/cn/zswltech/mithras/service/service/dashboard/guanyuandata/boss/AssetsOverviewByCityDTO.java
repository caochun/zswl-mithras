package cn.zswltech.mithras.service.service.dashboard.guanyuandata.boss;

import cn.zswltech.mithras.service.service.dashboard.guanyuandata.GuanYuanColumnPopulate;
import lombok.Data;

import java.util.Map;
import java.util.Optional;

/**
 * @author dingqi
 * @date 2024/5/20
 * @description
 */
@Data
public class AssetsOverviewByCityDTO implements GuanYuanColumnPopulate {
    private String provinceCode;
    private String provinceDisplay;
    private String cityCode;
    private String cityDisplay;
    private Long cityPayAmount;
    private Long cityCollectionAmount;
    private Long cityRemainingAmount;
    private Integer cityContinueProjectQuantity;
    private Integer cityContinueClientQuantity;
    private Long cityPayAmountThisYear;

    @Override
    public void populate(Map<String, String> map) {
        this.setProvinceCode(map.get("province_code"));
        this.setProvinceDisplay(map.get("province_display"));
        this.setCityCode(map.get("city_code"));
        this.setCityDisplay(map.get("city_display"));
        this.setCityPayAmount(Optional.ofNullable(map.get("city_pay_amount")).map(Long::valueOf).orElse(0L));
        this.setCityCollectionAmount(Optional.ofNullable(map.get("city_collection_amount")).map(Long::valueOf).orElse(0L));
        this.setCityRemainingAmount(Optional.ofNullable(map.get("city_remaining_amount")).map(Long::valueOf).orElse(0L));
        this.setCityContinueProjectQuantity(Optional.ofNullable(map.get("city_continue_project_quantity")).map(Integer::valueOf).orElse(0));
        this.setCityContinueClientQuantity(Optional.ofNullable(map.get("city_continue_client_quantity")).map(Integer::valueOf).orElse(0));
        this.setCityPayAmountThisYear(Optional.ofNullable(map.get("city_pay_amount_this_year")).map(Long::valueOf).orElse(0L));
    }
}

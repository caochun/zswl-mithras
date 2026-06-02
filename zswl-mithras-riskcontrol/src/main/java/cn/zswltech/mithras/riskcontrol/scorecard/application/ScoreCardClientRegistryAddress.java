package cn.zswltech.mithras.riskcontrol.scorecard.application;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoreCardClientRegistryAddress {

    private String provinceName;
    private String cityName;
    private String districtName;
}

package cn.zswltech.mithras.payment.application.pubinfo;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Value
@Builder
public class PublicInfoOuterQuerySnapshot {

    LocalDateTime queryTime;

    @Builder.Default
    Map<String, List<String>> queryResultByConfigKey = Collections.emptyMap();
}

package cn.zswltech.mithras.riskcontrol.scorecard.application;

import java.util.Optional;

public interface ScoreCardClientAddressResolver {

    Optional<ScoreCardClientRegistryAddress> registryAddress(Long clientId);
}

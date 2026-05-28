package cn.zswltech.mithras.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author luyi
 */
@Data
public class IdREQ {
    @NotNull
    private Long id;
}

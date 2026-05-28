package cn.zswltech.mithras.service.service.bo;

import cn.zswltech.mithras.service.enums.ClientAuthEnum;
import lombok.Data;

/**
 * @ClassName ClientAuthBO
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/65:38 下午
 * @Version 1.0
 **/
@Data
public class ClientAuthBO {

    private ClientAuthEnum clientAuthEnum;

    private Long ProjEstablishId;
   /**
     * 项目主办用户id
     */
    private Long projSponsorUserId;

    /**
     * 业务部门id
     */
    private Long bizDeptId;
}

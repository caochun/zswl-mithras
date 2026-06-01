package cn.zswltech.mithras.service.service.third.model.qiyuesuo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author bigbear
 * @date 2024/11/26 11:32
 * @description
 */
@Data
@NoArgsConstructor
public class UploadLocalFileResponse {

    /**
     * result : {"documentId":"3251380860066882273"}
     * code : 0
     * message : SUCCESS
     */

    private ResultBean result;
    private int code;
    private String message;

    @NoArgsConstructor
    @Data
    public static class ResultBean {
        /**
         * documentId : 3251380860066882273
         */

        private String documentId;
    }
}

package cn.zswltech.mithras.service.service.message.dto;

import lombok.Data;

import java.util.List;

/**
 * @ClassName CicoOaListReq
 * @Description TODO
 * @Author jackerhe
 * @Date 2025/1/17 11:19
 * @Version 1.0
 **/
@Data
public class CicoOaListRSP {


    private int pagesize;
    private boolean hasRight;
    private int sum;
    private boolean api_status;
    private boolean hasnext;
    private int pagenum;
    private int allpage;
    private List<CicoOaListBody> data;


    @Data
    public static class CicoOaListBody {
        private String flowid;
        private String receiver;
        private String syscode;
        private String isremark;
        private String appurl;
        private String pcurl;
        private String createdate;
        private String createtime;
        private String requestlevel;
    }


}

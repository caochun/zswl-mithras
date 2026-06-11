package cn.zswltech.mithras.others.service.util;

import cn.zswltech.gruul.common.util.ShaUtil;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.third.util.PwdUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class PwdUtilsTest {

    @Test
    public  void encrypt() throws Exception {
        String pwd = "Wn31wb04";
        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDiOnj+yzhdNiH9B+RLWGY/1HhIP9xSv7XIJhgRnsbqa6oHEhUAAMX7/hyzQ3S9emIsFg5BsNMBBZeBrNzQ5LH8R8enB6K3oQ/u3F8gHlvDE+qZtAc9c2Ma60lUbJLKjcmLkD8V7pbxNIyiHLop3JoPXp3X76hf4R4dWwGZdIfzQIDAQAB";
        String pwdEncode = PwdUtils.encrypt(pwd, publicKey);
        Assertions.assertNotNull(pwdEncode);
        System.out.println(pwdEncode);
    }


    @Test
    public  void shaEncrypt() throws Exception {

        String secret = "9cfa722382a0492931bf0747159df68a6fc92677";
        String random = "300ebc2c4fb04b8daf899b9a6c94f1d5";
        String timestamp = "1669966187053";
        if(!secret.equals(ShaUtil.shaEncode("6460520c-7520-4be4-a913-8ed596706ac6" + timestamp + random))){
            System.out.println(ShaUtil.shaEncode("6460520c-7520-4be4-a913-8ed596706ac6" + timestamp + random));
        }
        System.out.println(ShaUtil.shaEncode("6460520c-7520-4be4-a913-8ed596706ac6" + timestamp + random));
    }

}
package cn.zswltech.mithras.blackgray.port;

public interface BlackGrayCustomerPort {

    CustomerInfo getById(Long clientId);

    class CustomerInfo {
        private Long id;

        private String name;

        private String unifiedSocialCreditCode;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUnifiedSocialCreditCode() {
            return unifiedSocialCreditCode;
        }

        public void setUnifiedSocialCreditCode(String unifiedSocialCreditCode) {
            this.unifiedSocialCreditCode = unifiedSocialCreditCode;
        }
    }
}

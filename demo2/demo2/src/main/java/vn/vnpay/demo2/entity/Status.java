package vn.vnpay.demo2.entity;

public enum Status {
    INITIALIZED("Khởi tạo", "01"),
    FEE_COLLECTION("Thu phí", "02"),
    STOPPED("Dừng thu", "03");

    private final String label;
    private final String code;

    Status(String label, String code) {
        this.label = label;
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public String getCode() {
        return code;
    }

    public static Status fromCode(String code) {
        for (Status status : Status.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}

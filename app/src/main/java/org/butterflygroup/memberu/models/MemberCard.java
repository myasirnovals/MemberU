package org.butterflygroup.memberu.models;

public class MemberCard {
    private int id;
    private int userId;
    private int categoryId;
    private String categoryName;
    private String merchantName;
    private String memberNumber;
    private String tier;
    private String qrPayload;

    public MemberCard(int id, int userId, int categoryId, String categoryName,
                      String merchantName, String memberNumber, String tier, String qrPayload) {
        this.id = id;
        this.userId = userId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.merchantName = merchantName;
        this.memberNumber = memberNumber;
        this.tier = tier;
        this.qrPayload = qrPayload;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public String getMemberNumber() {
        return memberNumber;
    }

    public String getTier() {
        return tier;
    }

    public String getQrPayload() {
        return qrPayload;
    }
}
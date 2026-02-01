package dangod.springboot.dto;

import dangod.springboot.enums.MemberCardType;
import dangod.springboot.enums.MemberStatus;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDate;

public class MemberRegistrationDto {
    
    @NotBlank(message = "会员姓名不能为空")
    private String name;
    
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @NotNull(message = "会员卡类型不能为空")
    private MemberCardType cardType;
    
    @NotNull(message = "到期日期不能为空")
    private LocalDate expiryDate;
    
    @NotNull(message = "门店ID不能为空")
    private String storeId;
    
    private BigDecimal initialBalance;
    
    private Integer totalSessions;
    
    private Boolean chainStoreAccess = true;
    
    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public MemberCardType getCardType() {
        return cardType;
    }

    public void setCardType(MemberCardType cardType) {
        this.cardType = cardType;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }

    public Integer getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(Integer totalSessions) {
        this.totalSessions = totalSessions;
    }

    public Boolean getChainStoreAccess() {
        return chainStoreAccess;
    }

    public void setChainStoreAccess(Boolean chainStoreAccess) {
        this.chainStoreAccess = chainStoreAccess;
    }
}
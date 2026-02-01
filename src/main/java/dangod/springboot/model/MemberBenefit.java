package dangod.springboot.model;

import dangod.springboot.enums.MemberCardType;
import javax.persistence.*;

@Entity
@Table(name = "member_benefits")
public class MemberBenefit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private MemberCardType cardType;

    @Column(nullable = false)
    private String benefitName;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String benefitType;

    @Column(nullable = false)
    private String value;

    @Column(nullable = false)
    private Boolean isActive = true;

    public MemberBenefit() {
    }

    public MemberBenefit(MemberCardType cardType, String benefitName, String description, 
                         String benefitType, String value) {
        this.cardType = cardType;
        this.benefitName = benefitName;
        this.description = description;
        this.benefitType = benefitType;
        this.value = value;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MemberCardType getCardType() {
        return cardType;
    }

    public void setCardType(MemberCardType cardType) {
        this.cardType = cardType;
    }

    public String getBenefitName() {
        return benefitName;
    }

    public void setBenefitName(String benefitName) {
        this.benefitName = benefitName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBenefitType() {
        return benefitType;
    }

    public void setBenefitType(String benefitType) {
        this.benefitType = benefitType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
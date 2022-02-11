package mobvey.condition;

import mobvey.form.enums.ComparisonType;

/**
 *
 * @author ShamoHumbatli
 */
public class CompareInputValueWithDirectValueCondition extends AbstractCondition {

    private Double compareWith;    
    private String comparingInputId;

    private ComparisonType comparisonType;

    public Double getCompareWith() {
        return compareWith;
    }

    public void setCompareWith(Double compareWith) {
        this.compareWith = compareWith;
    }

    public String getComparingInputId() {
        return comparingInputId;
    }

    public void setComparingInputId(String comparingInputId) {
        this.comparingInputId = comparingInputId;
    }

    public ComparisonType getComparisonType() {
        return comparisonType;
    }

    public void setComparisonType(ComparisonType comparisonType) {
        this.comparisonType = comparisonType;
    }
    
    public CompareInputValueWithDirectValueCondition() {
        super(ConditionType.COMPARE_IV_DV);
    }
}

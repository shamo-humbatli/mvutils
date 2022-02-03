package mobvey.condition;

import mobvey.form.enums.ComparisonType;

/**
 *
 * @author ShamoHumbatli
 */
public class CompareWithInputValueCondition extends AbstractCondition {

    private String compareWith;
    private ComparisonType comparisonType;

    public String getCompareWith() {
        return compareWith;
    }

    public void setCompareWith(String compareWith) {
        this.compareWith = compareWith;
    }

    public ComparisonType getComparisonType() {
        return comparisonType;
    }

    public void setComparisonType(ComparisonType comparisonType) {
        this.comparisonType = comparisonType;
    }

    public CompareWithInputValueCondition() {
        super(ConditionType.COMPARE_WITH_IV);
    }
}

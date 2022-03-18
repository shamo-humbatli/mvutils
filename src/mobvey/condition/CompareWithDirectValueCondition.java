package mobvey.condition;

import mobvey.form.enums.ConditionType;
import mobvey.form.enums.ComparisonType;

/**
 *
 * @author ShamoHumbatli
 */
public class CompareWithDirectValueCondition extends AbstractCondition {

    private Double compareWith;
    private ComparisonType comparisonType;

    public Double getCompareWith() {
        return compareWith;
    }

    public void setCompareWith(Double compareWith) {
        this.compareWith = compareWith;
    }

    public ComparisonType getComparisonType() {
        return comparisonType;
    }

    public void setComparisonType(ComparisonType comparisonType) {
        this.comparisonType = comparisonType;
    }

    public CompareWithDirectValueCondition() {
        super(ConditionType.COMPARE_WITH_DV);
    }

    @Override
    public String[] getConditionParams() {
        return new String[]{
            String.valueOf(compareWith),
            String.valueOf(comparisonType)
        };
    }
}

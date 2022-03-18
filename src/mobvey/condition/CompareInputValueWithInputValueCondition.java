package mobvey.condition;

import mobvey.form.enums.ConditionType;
import mobvey.form.enums.ComparisonType;

/**
 *
 * @author Shamo Humbatli
 */
public class CompareInputValueWithInputValueCondition extends AbstractCondition {

    private String compareWithInputId;
    private String comparingInputId;
    private ComparisonType comparisonType;

    public String getCompareWithInputId() {
        return compareWithInputId;
    }

    public void setCompareWithInputId(String compareWithInputId) {
        this.compareWithInputId = compareWithInputId;
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

    public CompareInputValueWithInputValueCondition() {
        super(ConditionType.COMPARE_IV_IV);
    }

    @Override
    public String[] getConditionParams() {
        return new String[]{
            comparingInputId,
            compareWithInputId,
            String.valueOf(comparisonType)
        };
    }
}

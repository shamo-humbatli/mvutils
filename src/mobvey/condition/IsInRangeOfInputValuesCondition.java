package mobvey.condition;

/**
 *
 * @author ShamoHumbatli
 */
public class IsInRangeOfInputValuesCondition extends AbstractCondition {

    public IsInRangeOfInputValuesCondition() {
        super(ConditionType.IS_IN_RANGE_IVS);
    }
    
    private String _minValueInputId;
    private String _maxValueInputId;

    public String getMinValueInputId() {
        return _minValueInputId;
    }

    public void setMinValueInputId(String minValueInputId) {
        this._minValueInputId = minValueInputId;
    }

    public String getMaxValueInputId() {
        return _maxValueInputId;
    }

    public void setMaxValueInputId(String maxValueInputId) {
        this._maxValueInputId = maxValueInputId;
    }
}

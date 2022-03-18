package mobvey.condition;

import mobvey.form.enums.ConditionType;

/**
 *
 * @author Shamo Humbatli
 */
public class IsInRangeOfDirectValuesCondition extends AbstractCondition {

    public IsInRangeOfDirectValuesCondition() {
        super(ConditionType.IS_IN_RANGE_DVS);
    }

    private double _minValue;
    private double _maxValue;

    public double getMinValue() {
        return _minValue;
    }

    public void setMinValue(double _minValue) {
        this._minValue = _minValue;
    }

    public double getMaxValue() {
        return _maxValue;
    }

    public void setMaxValue(double _maxValue) {
        this._maxValue = _maxValue;
    }

    @Override
    public String[] getConditionParams() {
        return new String[]{
            String.valueOf(_minValue),
            String.valueOf(_maxValue)
        };
    }
}

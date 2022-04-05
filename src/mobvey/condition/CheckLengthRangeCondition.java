package mobvey.condition;

import mobvey.form.enums.ConditionType;

/**
 *
 * @author Shamo Humbatli
 */
public class CheckLengthRangeCondition extends AbstractCondition {

    public CheckLengthRangeCondition() {
        super(ConditionType.CHECK_LENGTH_RANGE);
    }

    protected int _minLength = 0;
    protected int _maxLength = 0;

    public int getMinLength() {
        return _minLength;
    }

    public void setMinLength(int minLength) {
        this._minLength = minLength;
    }

    public int getMaxLength() {
        return _maxLength;
    }

    public void setMaxLength(int maxLength) {
        this._maxLength = maxLength;
    }

    @Override
    public String[] getConditionParams() {
        return new String[]{
            String.valueOf(_minLength),
            String.valueOf(_maxLength)
        };
    }
}

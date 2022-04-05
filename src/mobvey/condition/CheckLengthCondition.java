package mobvey.condition;

import mobvey.form.enums.ConditionType;

/**
 *
 * @author Shamo Humbatli
 */
public class CheckLengthCondition extends AbstractCondition {

    public CheckLengthCondition() {
        super(ConditionType.CHECK_LENGTH);
    }

    protected int _length = 0;

    public int getLength() {
        return _length;
    }

    public void setLength(int length) {
        this._length = length;
    }
    
    @Override
    public String[] getConditionParams() {
        return new String[]{
            String.valueOf(_length)
        };
    }
}

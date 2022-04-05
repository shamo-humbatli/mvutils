package mobvey.condition;

import mobvey.form.enums.ConditionType;

/**
 *
 * @author Shamo Humbatli
 */
public class CheckEnabledCondition extends AbstractCondition {

    public CheckEnabledCondition() {
        super(ConditionType.CHECK_ENABLED);
    }

    protected boolean _enabled = true;

    public boolean isEnabled() {
        return _enabled;
    }

    public void setEnabled(boolean enabled) {
        this._enabled = enabled;
    }

    @Override
    public String[] getConditionParams() {
        return new String[]
        {
            String.valueOf(_enabled)
        };
    }
}

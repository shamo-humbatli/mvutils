package mobvey.condition;

import mobvey.form.enums.ConditionType;

/**
 *
 * @author Shamo Humbatli
 */
public class CheckCheckedCondition extends AbstractCondition {

    public CheckCheckedCondition() {
        super(ConditionType.CHECK_CHECKED);
    }

    protected boolean _checked = false;

    public boolean isChecked() {
        return _checked;
    }

    public void setChecked(boolean _checked) {
        this._checked = _checked;
    }

    @Override
    public String[] getConditionParams() {
        return new String[]{
            String.valueOf(_checked)
        };
    }
}

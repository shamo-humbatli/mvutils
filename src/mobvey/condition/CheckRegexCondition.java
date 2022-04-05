package mobvey.condition;

import mobvey.common.ParamUtil;
import mobvey.form.enums.ConditionType;

/**
 *
 * @author Shamo Humbatli
 */
public class CheckRegexCondition extends AbstractCondition {

    public CheckRegexCondition() {
        super(ConditionType.CHECK_REGEX);
    }

    protected boolean _validity = true;
    protected String _pattern;

    public boolean getValidity() {
        return _validity;
    }

    public void setValidity(boolean validity) {
        this._validity = validity;
    }

    public String getPattern() {
        return _pattern;
    }

    public void setPattern(String pattern) {
        this._pattern = pattern;
    }

    @Override
    public String[] getConditionParams() {
        return new String[]
        {
            String.valueOf(_validity),
            ParamUtil.getAsStringLine(_pattern)
        };
    }
}

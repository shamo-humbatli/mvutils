package mobvey.condition;

import java.util.ArrayList;
import java.util.Collection;
import mobvey.common.ParamUtil;
import mobvey.form.enums.ConditionType;

/**
 *
 * @author Shamo Humbatli
 */
public class CheckRegexOnElementsCondition extends AbstractCondition {

    public CheckRegexOnElementsCondition() {
        super(ConditionType.CHECK_REGEX_ELMS);
    }

    protected boolean _validity = false;
    protected String _pattern;
    protected Collection<String> _elementsIds;

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

    public Collection<String> getElementsIds() {
        if (_elementsIds == null) {
            _elementsIds = new ArrayList<>();
        }

        return _elementsIds;
    }

    public void setElementsIds(Collection<String> _elementsIds) {
        this._elementsIds = _elementsIds;
    }

    @Override
    public String[] getConditionParams() {
        return new String[]{
            String.valueOf(_validity),
            ParamUtil.getAsStringLine(_pattern),
            ParamUtil.getAsArray(_elementsIds)
        };
    }
}

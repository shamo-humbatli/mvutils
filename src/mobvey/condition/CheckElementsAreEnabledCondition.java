package mobvey.condition;

import java.util.ArrayList;
import mobvey.form.enums.ConditionType;
import java.util.Collection;
import mobvey.common.ParamUtil;

/**
 *
 * @author Shamo Humbatli
 */
public class CheckElementsAreEnabledCondition extends AbstractCondition {

    public CheckElementsAreEnabledCondition() {
        super(ConditionType.CHECK_ENABLED_ELMS);
    }

    protected boolean _enabled = false;
    protected Collection<String> _elementsIds;

    public boolean isEnabled() {
        return _enabled;
    }

    public void setEnabled(boolean enabled) {
        this._enabled = enabled;
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
            String.valueOf(_enabled),
            ParamUtil.getAsArray(_elementsIds)
        };
    }
}

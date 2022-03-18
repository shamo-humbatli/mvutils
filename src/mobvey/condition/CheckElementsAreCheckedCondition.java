package mobvey.condition;

import java.util.ArrayList;
import mobvey.form.enums.ConditionType;
import java.util.Collection;
import mobvey.common.ParamUtil;

/**
 *
 * @author Shamo Humbatli
 */
public class CheckElementsAreCheckedCondition extends AbstractCondition {

    public CheckElementsAreCheckedCondition() {
        super(ConditionType.CHECK_CHECKED_ELMS);
    }

    protected boolean _checked = false;
    protected Collection<String> _elementsIds;

    public boolean isChecked() {
        return _checked;
    }

    public void setChecked(boolean checked) {
        this._checked = checked;
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
            String.valueOf(_checked),
            ParamUtil.getAsArray(_elementsIds)
        };
    }
}

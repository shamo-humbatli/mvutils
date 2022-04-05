package mobvey.condition;

import java.util.ArrayList;
import mobvey.form.enums.ConditionType;
import java.util.Collection;
import mobvey.common.ParamUtil;

/**
 *
 * @author Shamo Humbatli
 */
public class CheckLengthRangeOfElementsCondition extends AbstractCondition {

    public CheckLengthRangeOfElementsCondition() {
        super(ConditionType.CHECK_LENGTH_ELMS);
    }

    protected int _minLength = 0;
    protected int _maxLength = 0;
    protected Collection<String> _elementsIds;

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
            String.valueOf(_minLength),
            String.valueOf(_maxLength),
            ParamUtil.getAsArray(_elementsIds)
        };
    }
}

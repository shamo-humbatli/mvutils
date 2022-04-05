package mobvey.condition;

import java.util.ArrayList;
import mobvey.form.enums.ConditionType;
import java.util.Collection;
import mobvey.common.ParamUtil;

/**
 *
 * @author Shamo Humbatli
 */
public class CheckLengthOfElementsCondition extends AbstractCondition {

    public CheckLengthOfElementsCondition() {
        super(ConditionType.CHECK_LENGTH_ELMS);
    }

    protected int _length = 0;
    protected Collection<String> _elementsIds;

    public int getLength() {
        return _length;
    }

    public void setLength(int length) {
        this._length = length;
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
            String.valueOf(_length),
            ParamUtil.getAsArray(_elementsIds)
        };
    }
}

package mobvey.condition;

import mobvey.form.enums.ConditionType;
import java.util.Collection;
import mobvey.common.ParamUtil;

/**
 *
 * @author Shamo Humbatli
 */
public class HasNoContentOfInputsCondition extends AbstractCondition {

    public HasNoContentOfInputsCondition() {
        super(ConditionType.HAS_NO_CONTENT_IVS);
    }

    protected Collection<String> _inputIds;

    public Collection<String> getInputIds() {
        return _inputIds;
    }

    public void setInputIds(Collection<String> inputIds) {
        this._inputIds = inputIds;
    }

    @Override
    public String[] getConditionParams() {
        return new String[]{
            ParamUtil.getAsArray(_inputIds)
        };
    }
}

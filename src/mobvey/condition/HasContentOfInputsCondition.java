package mobvey.condition;

import mobvey.form.enums.ConditionType;
import java.util.Collection;
import mobvey.common.ParamUtil;

/**
 *
 * @author ShamoHumbatli
 */
public class HasContentOfInputsCondition extends AbstractCondition {

    protected Collection<String> _inputIds;

    public Collection<String> getInputIds() {
        return _inputIds;
    }

    public void setInputIds(Collection<String> inputIds) {
        this._inputIds = inputIds;
    }

    public HasContentOfInputsCondition() {
        super(ConditionType.HAS_CONTENT_IVS);
    }

    @Override
    public String[] getConditionParams() {
        return new String[]{
            ParamUtil.getAsArray(_inputIds)
        };
    }
}

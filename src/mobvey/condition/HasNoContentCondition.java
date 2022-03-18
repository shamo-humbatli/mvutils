package mobvey.condition;

import mobvey.form.enums.ConditionType;

/**
 *
 * @author ShamoHumbatli
 */
public class HasNoContentCondition extends AbstractCondition {

    public HasNoContentCondition() {
        super(ConditionType.HAS_NO_CONTENT);
    }

    @Override
    public String[] getConditionParams() {
        return null;
    }
}

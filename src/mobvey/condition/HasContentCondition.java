package mobvey.condition;

import mobvey.form.enums.ConditionType;

/**
 *
 * @author Shamo Humbatli
 */
public class HasContentCondition extends AbstractCondition {

    public HasContentCondition() {
        super(ConditionType.HAS_CONTENT);
    }

    @Override
    public String[] getConditionParams() {
        return null;
    }
}

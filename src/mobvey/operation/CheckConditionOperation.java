package mobvey.operation;
import mobvey.condition.ConditionGroup;

/**
 *
 * @author Shamo Humbatli
 */
public class CheckConditionOperation extends AbstractOperation {

    public CheckConditionOperation() {
        super(OperationType.CHECK_CONDITION);
    }

    protected ConditionGroup _conditionGroup;

    public ConditionGroup getConditionGroup() {
        return _conditionGroup;
    }

    public void setConditionGroup(ConditionGroup conditionGroup) {
        this._conditionGroup = conditionGroup;
    } 
}

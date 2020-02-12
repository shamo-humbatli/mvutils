package mobvey.form.answer.content.operation;

/**
 *
 * @author Shamo Humbatli
 */
public class AbstractOperation {

    private final OperationTypes operationType;

    protected AbstractOperation(OperationTypes operationType) {
        this.operationType = operationType;
    }

    public OperationTypes getOperationType() {
        return operationType;
    }
}

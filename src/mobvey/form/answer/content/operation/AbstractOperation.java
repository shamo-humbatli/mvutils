package mobvey.form.answer.content.operation;

import java.io.Serializable;

/**
 *
 * @author Shamo Humbatli
 */
public class AbstractOperation implements Serializable{

    private final OperationType operationType;

    protected AbstractOperation(OperationType operationType) {
        this.operationType = operationType;
    }

    public OperationType getOperationType() {
        return operationType;
    }
}

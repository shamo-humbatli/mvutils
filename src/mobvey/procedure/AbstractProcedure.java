package mobvey.procedure;

import java.io.Serializable;
import mobvey.common.Strings;
import mobvey.form.enums.EventProcedureType;

/**
 *
 * @author ShamoHumbatli
 */
public abstract class AbstractProcedure implements Serializable {

    private final EventProcedureType _procedureType;

    public AbstractProcedure(EventProcedureType _procedureType) {
        this._procedureType = _procedureType;
    }

    public EventProcedureType getProcedureType() {
        return _procedureType;
    }

    public String getProcedureExpression() {
        return String.format("%s(%s)", _procedureType.toString(), Strings.join(", ", getProcedureParams()));
    }

    @Override
    public String toString() {
        return getProcedureExpression();
    }

    public abstract String[] getProcedureParams();
}

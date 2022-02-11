package mobvey.procedure;

import java.io.Serializable;
import mobvey.form.enums.EventProcedureType;

/**
 *
 * @author ShamoHumbatli
 */
public class AbstractProcedure implements Serializable {
   private final EventProcedureType _procedureType;

    public AbstractProcedure(EventProcedureType _procedureType) {
        this._procedureType = _procedureType;
    }

    public EventProcedureType getProcedureType() {
        return _procedureType;
    }
}

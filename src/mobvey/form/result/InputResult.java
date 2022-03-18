package mobvey.form.result;

import java.io.Serializable;
import mobvey.common.Strings;
import mobvey.form.enums.ColumnDefinitionType;

/**
 *
 * @author Shamo Humbatli
 */
public class InputResult implements Serializable {

    private String id;
    private String columnDefinition;
    private ColumnDefinitionType columnDefinitionType = ColumnDefinitionType.CI;

    private String returnValue;

    public String getColumnDescription() {
        return columnDefinition + ":" + columnDefinition;
    }

    public boolean hasColumnDefinition() {
        return Strings.hasContent(columnDefinition);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(String returnValue) {
        this.returnValue = returnValue;
    }

    public String getColumnDefinition() {
        return columnDefinition;
    }

    public void setColumnDefinition(String columnDefinition) {

        if (Strings.isNullOrEmpty(columnDefinition)) {
            columnDefinition = "0";
        }

        this.columnDefinition = columnDefinition;
    }

    public ColumnDefinitionType getColumnDefinitionType() {
        return columnDefinitionType;
    }

    public void setColumnDefinitionType(ColumnDefinitionType columnDefinitionType) {

        if (columnDefinitionType == ColumnDefinitionType.NS) {
            return;
        }

        this.columnDefinitionType = columnDefinitionType;
    }

    public InputResult CloneExact() {
        InputResult ir = new InputResult();

        ir.setReturnValue(returnValue);
        ir.setColumnDefinition(columnDefinition);
        ir.setColumnDefinitionType(columnDefinitionType);
        ir.setId(id);

        return ir;
    }

    @Override
    public String toString() {
        return "InputResult{" + "id=" + id + ", columnDefinition=" + columnDefinition + ", columnDefinitionType=" + columnDefinitionType + ", returnValue=" + returnValue + '}';
    }
}

package mobvey.form.result;

import java.io.Serializable;
import mobvey.form.enums.ColumnDefinitionType;

/**
 *
 * @author Shamo Humbatli
 */
public class InputResult implements Serializable {

    private String id;
    private String columnDefinition;
    private ColumnDefinitionType columnDefinitionType = ColumnDefinitionType.CI;

    private String columnName;
    private String returnValue;

    public String getColumnDescription()
    {
        return columnDefinitionType + ":" + columnDefinition;
    }
    
    public boolean hasColumnDefinition()
    {
        return columnDefinition != null && !columnDefinition.isBlank();
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

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    /**
     * @return the columnDefinition
     */
    public String getColumnDefinition() {
        return columnDefinition;
    }

    /**
     * @param columnDefinition the columnDefinition to set
     */
    public void setColumnDefinition(String columnDefinition) {
        this.columnDefinition = columnDefinition;
    }

    /**
     * @return the columnDefinitionType
     */
    public ColumnDefinitionType getColumnDefinitionType() {
        return columnDefinitionType;
    }

    /**
     * @param columnDefinitionType the columnDefinitionType to set
     */
    public void setColumnDefinitionType(ColumnDefinitionType columnDefinitionType) {
        this.columnDefinitionType = columnDefinitionType;
    }

    
    public InputResult CloneExact()
    {
        InputResult ir = new InputResult();
        
        ir.setReturnValue(returnValue);
        ir.setColumnDefinition(columnDefinition);
        ir.setColumnDefinitionType(columnDefinitionType);
        ir.setId(id);
        ir.setColumnName(columnName);
        
        return ir;
    }
    
    @Override
    public String toString() {
        return "InputResult{" + "id=" + id + ", columnDefinition=" + columnDefinition + ", columnDefinitionType=" + columnDefinitionType + ", columnName=" + columnName + ", returnValue=" + returnValue + '}';
    }
}

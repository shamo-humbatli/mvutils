package mobvey.form.result;

/**
 *
 * @author Shamo Humbatli
 */
public class InputResult {
    private String id;
    private int columnIndex = 0;
    private String returnValue;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public String getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(String returnValue) {
        this.returnValue = returnValue;
    }

    @Override
    public String toString() {
        return "InputResult{" + "id=" + id + ", columnIndex=" + columnIndex + ", returnValue=" + returnValue + '}';
    }
}

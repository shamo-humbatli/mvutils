package mobvey.form.answer.content;

import mobvey.form.base.AbstractInput;
import mobvey.form.enums.InputTypes;

/**
 *
 * @author Shamo Humbatli
 */
public class InputTextContent extends AbstractInput{
    private String placeHolder;
    private boolean readonly = false;

    public InputTextContent() {
        super(InputTypes.TEXT);
    }

    public String getPlaceHolder() {
        return placeHolder;
    }

    public void setPlaceHolder(String placeHolder) {
        this.placeHolder = placeHolder;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }
}

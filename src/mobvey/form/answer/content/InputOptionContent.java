package mobvey.form.answer.content;

import java.util.ArrayList;
import mobvey.common.Strings;
import mobvey.form.answer.content.container.ContentContainer;
import mobvey.form.base.AbstractInput;
import mobvey.form.enums.FormElementType;
import mobvey.form.enums.InputType;

/**
 *
 * @author Shamo Humbatli
 */
public class InputOptionContent extends AbstractInput {

    private boolean _checked;

    public InputOptionContent() {
        super(InputType.OPTION, FormElementType.INPUT_OPTION);
    }

    public boolean isChecked() {
        return _checked;
    }

    public void setChecked(boolean checked) {
        this._checked = checked;
    }

    @Override
    public boolean isIndividuallyReturnable() {
        return Strings.hasContent(getStringReturnContent()) && isEnabled() && isChecked();
    }

    @Override
    public AbstractInput CloneExact() {
        InputOptionContent ioc = new InputOptionContent();

        ioc.setColumnDefinition(columnDefinition);
        ioc.setColumnDefinitionDeclaredByDefault(columnDefinitionDeclaredByDefault);
        ioc.setColumnDefinitionType(columnDefinitionType);
        ioc.setComplex(complex);
        ioc.setContentItemIndex(contentItemIndex);
        ioc.setId(_id);
        ioc.setEnabled(_enabled);
        ioc.setChecked(_checked);
        ioc.setInputValueType(inputValueType);
        ioc.setParentId(getParentId());
        ioc.setReturnContent(returnContent);
        ioc.setDisplayContent(displayContent);
        ioc.setValueOperation(_valueOperation);

        if (_validations != null) {
            ioc.setValidations(new ArrayList<>(_validations));
        }

        if (_events != null) {
            ioc.setEvents(new ArrayList<>(_events));
        }

        if (containersRequired != null) {
            ioc.setContainersRequired(new ArrayList<>(containersRequired));
        }

        if (contentContainers != null) {
            for (ContentContainer cc : getContentContainers()) {

                if (cc == null) {
                    continue;
                }

                ContentContainer ccCloned = cc.CloneExact();
                ioc.AddContentContainer(ccCloned);
            }
        }

        return ioc;
    }
}

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
public class InputTextContent extends AbstractInput {

    private String placeHolder;
    private boolean readonly = false;

    public InputTextContent() {
        super(InputType.TEXT, FormElementType.INPUT_TEXT);
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

    @Override
    public AbstractInput CloneExact() {
        InputTextContent itc = new InputTextContent();

        itc.setColumnDefinition(columnDefinition);
        itc.setColumnDefinitionDeclaredByDefault(columnDefinitionDeclaredByDefault);
        itc.setColumnDefinitionType(columnDefinitionType);
        itc.setComplex(complex);
        itc.setContentItemIndex(contentItemIndex);
        itc.setId(_id);
        itc.setEnabled(_enabled);
        itc.setInputValueType(inputValueType);
        itc.setParentId(getParentId());
        itc.setReturnContent(returnContent);
        itc.setDisplayContent(displayContent);
        itc.setReadonly(readonly);
        itc.setPlaceHolder(placeHolder);
        itc.setValueOperation(_valueOperation);

        if (_validations != null) {
            itc.setValidations(new ArrayList<>(_validations));
        }

        if (_events != null) {
            itc.setEvents(new ArrayList<>(_events));
        }

        if (containersRequired != null) {
            itc.setContainersRequired(new ArrayList<>(containersRequired));
        }

        if (contentContainers != null) {
            for (ContentContainer cc : getContentContainers()) {

                if (cc == null) {
                    continue;
                }

                ContentContainer ccCloned = cc.CloneExact();
                itc.AddContentContainer(ccCloned);
            }
        }

        return itc;
    }

    @Override
    public boolean isIndividuallyReturnable() {
        return Strings.hasContent(getStringReturnContent()) && isEnabled();
    }
}

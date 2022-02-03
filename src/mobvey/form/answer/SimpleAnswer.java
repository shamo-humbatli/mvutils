package mobvey.form.answer;

import java.util.ArrayList;
import mobvey.form.answer.content.container.ContentContainer;
import mobvey.form.base.AbstractAnswer;
import mobvey.form.enums.FormElementType;

/**
 *
 * @author Shamo Humbatli
 */
public class SimpleAnswer extends AbstractAnswer {

    public SimpleAnswer() {
        super(FormElementType.ANSWER);
    }

    @Override
    public AbstractAnswer CloneExact() {
        SimpleAnswer sa = new SimpleAnswer();

        sa.setId(_id);
        sa.setEnabled(_enabled);
        sa.setParentId(_parentId);

        if (_events != null) {
            sa.setEvents(new ArrayList<>(_events));
        }

        if (answerContentContainers != null) {
            for (ContentContainer cc : getAnswerContentContainers()) {
                ContentContainer ccCloned = cc.CloneExact();
                sa.AddContentContainer(ccCloned);
            }
        }

        return sa;
    }

}

package mobvey.form.answer;

import mobvey.form.answer.content.container.ContentContainer;
import mobvey.form.base.AbstractAnswer;

/**
 *
 * @author Shamo Humbatli
 */
public class SimpleAnswer extends AbstractAnswer {

    @Override
    public AbstractAnswer CloneExact() {
        SimpleAnswer sa = new SimpleAnswer();

        sa.setAnswerId(answerId);
        sa.setEnabled(enabled);

        if (answerContentContainers != null) {
            for (ContentContainer cc : answerContentContainers) {
                ContentContainer ccCloned = cc.CloneExact();
                sa.AddContentContainer(ccCloned);
            }
        }

        return sa;
    }

}

package mobvey.form.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import mobvey.form.answer.content.container.ContentContainer;

/**
 *
 * @author Shamo Humbatli
 */
public class AbstractAnswer implements Serializable{

    private String answerId;
    private boolean enabled = true;
    private List<ContentContainer> answerContentContainers = new ArrayList<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getAnswerId() {
        return answerId;
    }

    public void setAnswerId(String answerId) {
        this.answerId = answerId;
    }

    public List<ContentContainer> getAnswerContentContainers() {
        return answerContentContainers;
    }

    public void setAnswerContentContainers(List<ContentContainer> answerContentContainers) {
        this.answerContentContainers = answerContentContainers;
    }

    public void AddContentContainer(ContentContainer contentContainer) {
        answerContentContainers.add(contentContainer);
    }

}

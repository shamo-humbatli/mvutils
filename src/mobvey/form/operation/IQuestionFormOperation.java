package mobvey.form.operation;

import java.util.List;
import mobvey.common.KeyValuePair;
import mobvey.form.base.AbstractInput;

/**
 *
 * @author Shamo Humbatli
 */
public interface IQuestionFormOperation {

    boolean SetReturnRequired(String contentContainerPath, boolean required);

    void CheckoutForReturnRequired(List<KeyValuePair<String, Boolean>> returns);    
    void CheckoutForReturnRequired(AbstractInput input);

}

package mobvey.form;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import mobvey.common.KeyValuePair;
import mobvey.form.answer.content.container.ContentContainer;
import mobvey.form.base.AbstractAnswer;
import mobvey.form.base.AbstractInput;
import mobvey.form.operation.IQuestionFormOperation;
import mobvey.form.question.Question;

/**
 *
 * @author Shamo Humbatli
 */
public class QuestionForm extends QuestionFormData implements IQuestionFormOperation {
    
    private static final Logger logger = Logger.getLogger(QuestionForm.class.getName());
    private final String treeSplitter = "/";
    
    @Override
    public boolean SetReturnRequired(String contentContainerPath, boolean required) {
        boolean result = false;
        try {
            if (contentContainerPath == null) {
                return false;
            }
            
            contentContainerPath = contentContainerPath.trim();
            
            if ("".equals(contentContainerPath)) {
                return false;
            }
            
            List<Question> questions = getQuestions();
            
            if (questions == null) {
                return false;
            }
            
            String[] eTree = contentContainerPath.split(treeSplitter);

            //get question
            Question question = questions
                    .stream().filter(q -> q.getQuestionId().equals(eTree[0])).findFirst().orElse(null);
            
            if (question == null) {
                return false;
            }
            
            if (question.getAnswers() == null) {
                return false;
            }

            //get answer
            AbstractAnswer answer = question.getAnswers()
                    .stream().filter(aa -> aa.getAnswerId().equals(eTree[1])).findFirst().orElse(null);
            
            if (answer == null) {
                return false;
            }

            //get base content container
            if (answer.getAnswerContentContainers() == null) {
                return false;
            }
            
            ContentContainer baseContainer = answer.getAnswerContentContainers()
                    .stream().filter(cc -> cc.getId().equals(eTree[2])).findFirst().orElse(null);
            
            if (baseContainer == null) {
                return false;
            }
            
            if (eTree.length == 3) {
                baseContainer.setReturnRequeired(required);
                return true;
            }
            
            ContentContainer containerFound = baseContainer;
            for (int subIndex = 3; subIndex < eTree.length; subIndex++) {
                containerFound = GetChilContentContainer(containerFound, eTree[subIndex]);
            }
            
            if (containerFound == null) {
                return false;
            }
            
            if (eTree.length == 3) {
                baseContainer.setReturnRequeired(required);
                return true;
            }
            
        } catch (Exception exp) {
            logger.log(Level.SEVERE, exp.toString());
            result = false;
        }
        
        return result;
    }
     
    @Override
    public void CheckoutForReturnRequired(List<KeyValuePair<String, Boolean>> returns) {
        try {
            if (returns == null || returns.size() == 0) {
                return;
            }
            
            for (KeyValuePair<String, Boolean> pair : returns) {
                
                if (pair.getKey() == null || pair.getValue() == null) {
                    continue;
                }
                
                SetReturnRequired(pair.getKey(), pair.getValue());
            }
        } catch (Exception exp) {
            logger.log(Level.SEVERE, exp.toString());
        }
    }
    
    @Override
    public void CheckoutForReturnRequired(AbstractInput input) {
        if (input == null) {
            return;
        }
        
        if (!input.HasContainersToReviewIfRequired()) {
            return;
        }
        
        CheckoutForReturnRequired(input.getContainersRequired());
    }
    
     private ContentContainer GetChilContentContainer(ContentContainer baseContainer, String childId) {
        if (childId == null) {
            return null;
        }
        
        childId = childId.trim();
        
        if ("".equals(childId)) {
            return null;
        }
        
        if (baseContainer == null) {
            return null;
        }
        
        List<AbstractInput> inputs = baseContainer.getContentInputs();
        
        if (inputs == null) {
            return null;
        }
        
        inputs = inputs.stream().filter(i -> i.getContentContainers() != null).collect(Collectors.toList());
        
        for (AbstractInput ai : inputs) {
            for (ContentContainer subContainer : ai.getContentContainers()) {
                
                if (subContainer.getId().equals(childId)) {
                    return subContainer;
                }
            }
        }
        
        return null;
    }
}

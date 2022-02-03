package mobvey.operation;
import java.util.ArrayList;
import java.util.List;
import mobvey.common.Strings;

/**
 *
 * @author Shamo Humbatli
 */
public class SumInputValuesOperation extends AbstractOperation {

    protected List<String> rangeIds = new ArrayList<>();

    public SumInputValuesOperation() {
        super(OperationType.SUM_IVS);
    }

    public List<String> getRangeIds() {

        if (rangeIds == null) {
            rangeIds = new ArrayList<>();
        }

        return rangeIds;
    }

    public void setRangeIds(List<String> sumIds) {
        this.rangeIds = sumIds;
    }

    public void addRangeId(String id) {
        if (!Strings.hasContent(id)) {
            return;
        }

        getRangeIds().add(id);
    }
}

package  mobvey.operation;

/**
 *
 * @author Shamo Humbatli
 */
public class SumInputValuesByIndexRangeOperation extends AbstractOperation {

    protected int _indexRangeStart;
    protected int _indexRangeEnd;

    public SumInputValuesByIndexRangeOperation() {
        super(OperationType.SUM_IVS_BY_INDEX_RANGE);
    }

    public int getIndexRangeStart() {
        return _indexRangeStart;
    }

    public void setIndexRangeStart(int _indexRangeStart) {
        this._indexRangeStart = _indexRangeStart;
    }

    public int getIndexRangeEnd() {
        return _indexRangeEnd;
    }

    public void setIndexRangeEnd(int _indexRangeEnd) {
        this._indexRangeEnd = _indexRangeEnd;
    }
}

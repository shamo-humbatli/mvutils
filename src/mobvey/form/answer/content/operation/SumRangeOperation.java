/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mobvey.form.answer.content.operation;

import java.util.ArrayList;
import java.util.List;
import mobvey.common.Strings;

/**
 *
 * @author ShamoHumbatli
 */
public class SumRangeOperation extends AbstractOperation {

    protected int rangeStart;
    protected int rangeEnd;

    protected List<String> rangeIds = new ArrayList<>();

    public SumRangeOperation() {
        super(OperationType.SUM);
    }

    public int getRangeStart() {
        return rangeStart;
    }

    public void setRangeStart(int rangeStart) {
        this.rangeStart = rangeStart;
    }

    public int getRangeEnd() {
        return rangeEnd;
    }

    public void setRangeEnd(int rangeEnd) {
        this.rangeEnd = rangeEnd;
    }

    public List<String> getRangeIds() {
        return rangeIds;
    }

    public void setRangeIds(List<String> sumIds) {
        this.rangeIds = sumIds;
    }

    public void addRangeId(String id) {
        if (this.rangeIds == null) {
            this.rangeIds = new ArrayList<>();
        }

        if (!Strings.HasContent(id)) {
            return;
        }

        this.rangeIds.add(id);
    }
}

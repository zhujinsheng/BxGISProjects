package com.bxgis.yczw.event;

/**
 * 更新巡检数量
 * Created by SK on 2017-05-09.
 */

public class UpdateICountEvent {
    int countComplete;
    int countUnfinished;

    public UpdateICountEvent(int countComplete, int countUnfinished) {
        this.countComplete = countComplete;
        this.countUnfinished = countUnfinished;
    }

    public int getCountComplete() {
        return countComplete;
    }

    public void setCountComplete(int countComplete) {
        this.countComplete = countComplete;
    }

    public int getCountUnfinished() {
        return countUnfinished;
    }

    public void setCountUnfinished(int countUnfinished) {
        this.countUnfinished = countUnfinished;
    }
}

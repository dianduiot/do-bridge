package com.github.dianduiot.bridge.model;

public class DoTriggerInfo extends DoObject {
    private Integer index;

    private Boolean enableFlag;
    private Boolean sendFlag;
    private String triggerId;
    private String propertyId;
    private String triggerName;
    private Integer level;
    private Integer conditionType;
    private String xstr;
    private String ystr;
    private Long fpds;
    private Long cpus;
    private Long cpmf;
    private Integer sort;

    public DoTriggerInfo() {
        super("C-T-I");
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Boolean getEnableFlag() {
        return enableFlag;
    }

    public void setEnableFlag(Boolean enableFlag) {
        this.enableFlag = enableFlag;
    }

    public Boolean getSendFlag() {
        return sendFlag;
    }

    public void setSendFlag(Boolean sendFlag) {
        this.sendFlag = sendFlag;
    }

    public String getTriggerId() {
        return triggerId;
    }

    public void setTriggerId(String triggerId) {
        this.triggerId = triggerId;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getTriggerName() {
        return triggerName;
    }

    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getConditionType() {
        return conditionType;
    }

    public void setConditionType(Integer conditionType) {
        this.conditionType = conditionType;
    }

    public String getXstr() {
        return xstr;
    }

    public void setXstr(String xstr) {
        this.xstr = xstr;
    }

    public String getYstr() {
        return ystr;
    }

    public void setYstr(String ystr) {
        this.ystr = ystr;
    }

    public Long getFpds() {
        return fpds;
    }

    public void setFpds(Long fpds) {
        this.fpds = fpds;
    }

    public Long getCpus() {
        return cpus;
    }

    public void setCpus(Long cpus) {
        this.cpus = cpus;
    }

    public Long getCpmf() {
        return cpmf;
    }

    public void setCpmf(Long cpmf) {
        this.cpmf = cpmf;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}

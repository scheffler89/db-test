package de.dbtest.persistence;

/**
 * @author Lennard Scheffler
 */
public class StatusItem {

    private String testCaseIdentifier;
    private String targetIdentifier;
    private int status;

    public StatusItem() {
        this.testCaseIdentifier = null;
        this.targetIdentifier = null;
        this.status = 0;
    }

    public StatusItem(String testCaseIdentifier, String targetIdentifier, int status) {
        this.testCaseIdentifier = testCaseIdentifier;
        this.targetIdentifier = targetIdentifier;
        this.status = status;
    }

    public String getTestCaseIdentifier() {
        return testCaseIdentifier;
    }

    public void setTestCaseIdentifier(String testCaseIdentifier) {
        this.testCaseIdentifier = testCaseIdentifier;
    }

    public String getTargetIdentifier() {
        return targetIdentifier;
    }

    public void setTargetIdentifier(String targetIdentifier) {
        this.targetIdentifier = targetIdentifier;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StatusItem that = (StatusItem) o;

        if (testCaseIdentifier != null ? !testCaseIdentifier.equals(that.testCaseIdentifier) : that.testCaseIdentifier != null)
            return false;
        return targetIdentifier != null ? targetIdentifier.equals(that.targetIdentifier) : that.targetIdentifier == null;
    }

    @Override
    public int hashCode() {
        int result = testCaseIdentifier != null ? testCaseIdentifier.hashCode() : 0;
        result = 31 * result + (targetIdentifier != null ? targetIdentifier.hashCode() : 0);
        return result;
    }
}

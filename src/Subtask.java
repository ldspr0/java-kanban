
public class Subtask extends Task {
    private int epicId;

    public Subtask(int id, String title, String description, Status status, int epicId) {
        super(id, title, description, status);
        this.epicId = epicId;

        Epic parentRecord = (Epic) TaskManager.getRecord(epicId, TaskType.EPIC);
        if (parentRecord != null) {
            parentRecord.getSubtasks().add(id);
        }

    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + super.getId() +
                ", title='" + super.getTitle() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status=" + super.getStatus() +
                ", epicId=" + epicId +
                '}';
    }

    public void setStatus(Status status) {
        super.setStatus(status);
        TaskManager.getRecord(epicId, TaskType.EPIC).setStatus(status);
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}

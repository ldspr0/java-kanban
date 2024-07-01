public enum Status {
    NEW,
    IN_PROGRESS,
    DONE;

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}

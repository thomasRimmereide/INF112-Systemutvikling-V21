package inf112.skeleton.app.game;

public enum GameType {
    NETWORK_HOST("1"),
    NETWORK_JOIN("2"),
    SINGLE_PLAYER("3");

    public final String value;

    private GameType(String value) {
        this.value = value;
    }
}
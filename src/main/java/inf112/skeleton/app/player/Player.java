package inf112.skeleton.app.player;


import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

abstract class Player extends Sprite {

    public TiledMapTileLayer flagLayer;

    public Player(Sprite sprite, TiledMapTileLayer flagLayer) {
        super(sprite);
        this.flagLayer = flagLayer;
    }

    public abstract void updatePlayerLocation(float updateX, float updateY);

    public abstract void setPlayerSize(float width, float height);

    public abstract boolean isPlayerOnFlag(TiledMapTileLayer flagLayer);

    public abstract boolean canPlayerMove(float xDirection, float yDirection);

    public abstract int normalizedCoordinates(float unNormalizedValue);

    public abstract boolean isGameOver(TiledMapTileLayer flagLayer);

}
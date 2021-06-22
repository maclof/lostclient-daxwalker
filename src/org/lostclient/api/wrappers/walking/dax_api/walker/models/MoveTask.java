package org.lostclient.api.wrappers.walking.dax_api.walker.models;

import org.lostclient.api.wrappers.map.Tile;
import org.lostclient.api.wrappers.walking.dax_api.walker.models.enums.Situation;

public class MoveTask {

    private Situation situation;
    private Tile destination, next;

    public MoveTask(Situation situation, Tile destination, Tile next) {
        this.situation = situation;
        this.destination = destination;
        this.next = next;
    }

    public Situation getSituation() {
        return situation;
    }

    /**
     *
     * @return Tile we can walk to.
     */
    public Tile getDestination() {
        return destination;
    }

    /**
     *
     * @return Tile we want to walk to after reaching destination.
     */
    public Tile getNext() {
        return next;
    }

    @Override
    public String toString() {
        return "MoveTask{" +
                "situation=" + situation +
                ", destination=" + destination +
                ", next=" + next +
                '}';
    }
}
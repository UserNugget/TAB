package me.neznamy.tab.shared.features.types;

import lombok.NonNull;
import me.neznamy.tab.shared.platform.TabPlayer;

/**
 * Listener for features listening to objective packet.
 */
public interface ObjectiveListener {

    /**
     * Called when player is about to receive display objective packet
     *
     * @param   receiver
     *          Packet receiver
     * @param   action
     *          Packet action (0 = register, 1 = unregister, 2 = update)
     * @param   objective
     *          Objective name
     */
    void onObjective(@NonNull TabPlayer receiver, int action, @NonNull String objective);
}

package me.neznamy.tab.shared.features.types;

import lombok.NonNull;
import me.neznamy.tab.shared.platform.TabPlayer;

/**
 * Interface for features listening to players switching worlds
 */
public interface WorldSwitchListener {

    /**
     * Called when player switched world
     *
     * @param   changed
     *          Player who changed world
     * @param   from
     *          Name of previous world
     * @param   to
     *          Name of new world
     */
    void onWorldChange(@NonNull TabPlayer changed, @NonNull String from, @NonNull String to);
}

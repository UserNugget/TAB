package me.neznamy.tab.shared.features.types;

import lombok.NonNull;
import me.neznamy.tab.shared.platform.TabPlayer;

/**
 * Interface for features listening to command preprocess event
 * possibly cancelling it.
 */
public interface CommandListener {

    /**
     * Called when player is about to run a command
     *
     * @param   sender
     *          Command sender
     * @param   message
     *          Command line including /
     * @return  Whether the command should be cancelled or not
     */
    boolean onCommand(@NonNull TabPlayer sender, @NonNull String message);
}

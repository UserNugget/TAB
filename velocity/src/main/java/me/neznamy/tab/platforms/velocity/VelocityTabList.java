package me.neznamy.tab.platforms.velocity;

import com.velocitypowered.api.proxy.player.TabListEntry;
import com.velocitypowered.api.util.GameProfile;
import lombok.NonNull;
import lombok.Setter;
import me.neznamy.tab.shared.TAB;
import me.neznamy.tab.shared.platform.TabList;
import me.neznamy.tab.shared.platform.TabPlayer;
import net.elytrium.limboapi.LimboAPI;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * TabList implementation for Velocity using its API.
 */
public class VelocityTabList extends TabList<VelocityTabPlayer, Component> {

    @Setter
    private LimboAPI limbo;

    /**
     * Constructs new instance.
     *
     * @param   player
     *          Player this tablist will belong to
     */
    public VelocityTabList(@NotNull VelocityTabPlayer player) {
        super(player);
    }

    private UUID rewriteUuid(UUID uuid) {
      if (this.limbo != null && this.player.getPlayer().getUniqueId().equals(uuid)) {
        return this.limbo.getInitialID(this.player.getPlayer());
      }

      return uuid;
    }

    @Override
    public void removeEntry(@NonNull UUID entry) {
        player.getPlayer().getTabList().removeEntry(this.rewriteUuid(entry));
    }

    @Override
    public void updateDisplayName0(@NonNull UUID entry, @Nullable Component displayName) {
        player.getPlayer().getTabList().getEntry(this.rewriteUuid(entry)).ifPresent(e -> e.setDisplayName(displayName));
    }

    @Override
    public void updateLatency(@NonNull UUID entry, int latency) {
        player.getPlayer().getTabList().getEntry(this.rewriteUuid(entry)).ifPresent(e -> e.setLatency(latency));
    }

    @Override
    public void updateGameMode(@NonNull UUID entry, int gameMode) {
        player.getPlayer().getTabList().getEntry(this.rewriteUuid(entry)).ifPresent(e -> e.setGameMode(gameMode));
    }

    @Override
    public void updateListed(@NonNull UUID entry, boolean listed) {
        player.getPlayer().getTabList().getEntry(this.rewriteUuid(entry)).ifPresent(e -> e.setListed(listed));
    }

    @Override
    public void addEntry0(@NonNull UUID id, @NonNull String name, @Nullable Skin skin, boolean listed, int latency, int gameMode, @Nullable Component displayName) {
        id = this.rewriteUuid(id);
        TabListEntry e = TabListEntry.builder()
                .tabList(player.getPlayer().getTabList())
                .profile(new GameProfile(
                        id,
                        name,
                        skin == null ? Collections.emptyList() : Collections.singletonList(
                                new GameProfile.Property(TEXTURES_PROPERTY, skin.getValue(), Objects.requireNonNull(skin.getSignature())))
                ))
                .listed(listed)
                .latency(latency)
                .gameMode(gameMode)
                .displayName(displayName)
                .build();

        // Remove entry because:
        // #1 - If player is 1.8 - 1.19.2, KeyedVelocityTabList#addEntry will throw IllegalArgumentException
        //      if the entry is already present (most likely due to an accident trying to add existing player in global playerlist)
        // #2 - If player is 1.20.2+, tablist is cleared by the client itself without requirement to remove
        //      manually by the proxy, however velocity's tablist entry tracker still thinks they are present
        //      and therefore will refuse to add them
        removeEntry(id);

        player.getPlayer().getTabList().addEntry(e);
    }

    @Override
    public void setPlayerListHeaderFooter0(@NonNull Component header, @NonNull Component footer) {
        player.getPlayer().sendPlayerListHeaderAndFooter(header, footer);
    }

    @Override
    public boolean containsEntry(@NonNull UUID entry) {
        return player.getPlayer().getTabList().containsEntry(this.rewriteUuid(entry));
    }

    @Override
    public void checkDisplayNames() {
        for (TabPlayer target : TAB.getInstance().getOnlinePlayers()) {
            player.getPlayer().getTabList().getEntry(this.rewriteUuid(target.getUniqueId())).ifPresent(entry -> {
                Component expectedComponent = getExpectedDisplayName(target);
                if (expectedComponent != null && entry.getDisplayNameComponent().orElse(null) != expectedComponent) {
                    displayNameWrong(entry.getProfile().getName(), player);
                    entry.setDisplayName(expectedComponent);
                }
            });
        }
    }
}

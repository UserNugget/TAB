package me.neznamy.tab.shared.features.layout;

import lombok.Getter;
import me.neznamy.tab.api.ProtocolVersion;
import me.neznamy.tab.shared.TabConstants;
import me.neznamy.tab.shared.chat.IChatBaseComponent;
import me.neznamy.tab.shared.placeholders.conditions.Condition;
import me.neznamy.tab.shared.platform.TabList;
import me.neznamy.tab.shared.platform.TabPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Getter
public class LayoutView {

    private final LayoutManager manager;
    private final LayoutPattern pattern;
    private final TabPlayer viewer;
    private final Condition displayCondition;
    private final List<Integer> emptySlots = IntStream.range(1, 81).boxed().collect(Collectors.toList());
    private final Collection<FixedSlot> fixedSlots;
    private final List<ParentGroup> groups = new ArrayList<>();

    public LayoutView(LayoutManager manager, LayoutPattern pattern, TabPlayer viewer) {
        this.manager = manager;
        this.viewer = viewer;
        this.pattern = pattern;
        this.fixedSlots = pattern.getFixedSlots().values();
        this.displayCondition = pattern.getCondition();
        for (FixedSlot slot : fixedSlots) {
            emptySlots.remove((Integer) slot.getSlot());
        }
        for (GroupPattern group : pattern.getGroups()) {
            emptySlots.removeAll(Arrays.stream(group.getSlots()).boxed().collect(Collectors.toList()));
            groups.add(new ParentGroup(this, group, viewer));
        }
    }

    public void send() {
        if (viewer.getVersion().getMinorVersion() < 8 || viewer.isBedrockPlayer()) return;
        groups.forEach(ParentGroup::sendSlots);
        for (FixedSlot slot : fixedSlots) {
            viewer.getTabList().addEntry(slot.createEntry(viewer));
        }
        for (int slot : emptySlots) {
            viewer.getTabList().addEntry(new TabList.Entry(manager.getUUID(slot), getEntryName(viewer, slot), manager.getSkinManager().getDefaultSkin(),
                    manager.getEmptySlotPing(), 0, new IChatBaseComponent("")));
        }
        tick();
    }

    public String getEntryName(@NotNull TabPlayer viewer, long slot) {
        return viewer.getVersion().getNetworkId() >= ProtocolVersion.V1_19_3.getNetworkId() ? "|slot_" + (10+slot) : "";
    }

    public void destroy() {
        if (viewer.getVersion().getMinorVersion() < 8 || viewer.isBedrockPlayer()) return;
        viewer.getTabList().removeEntries(manager.getUuids().values());
    }

    public void tick() {
        Stream<TabPlayer> str = manager.getSortedPlayers().keySet().stream();
        if (!viewer.hasPermission(TabConstants.Permission.SEE_VANISHED)) {
            str = str.filter(player -> !player.isVanished());
        }
        List<TabPlayer> players = str.collect(Collectors.toList());
        for (ParentGroup group : groups) {
            group.tick(players);
        }
    }

    public PlayerSlot getSlot(@NotNull TabPlayer target) {
        for (ParentGroup group : groups) {
            if (group.getPlayers().containsKey(target)) {
                return group.getPlayers().get(target);
            }
        }
        return null;
    }
}

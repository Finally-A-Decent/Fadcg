package info.asdev.fadcg.gui.lib.pagination;

import com.github.puregero.multilib.MultiLib;
import com.github.puregero.multilib.regionized.RegionizedTask;
import info.asdev.fadcg.Fadcg;
import info.asdev.fadcg.gui.lib.FastInv;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class PaginatedFastInv extends FastInv {
    protected final Player player;

    protected int page = 0;
    protected int index = 0;
    private List<Integer> paginationMappings;
    private final List<PaginatedItem> paginatedItems = new ArrayList<>();
    protected boolean needsClearing = false;
    private final RegionizedTask task;

    protected PaginatedFastInv(int size, @NotNull String title, @NotNull Player player) {
        super(size, title);
        this.player = player;
        this.paginationMappings = List.of(
                11, 12, 13, 14, 15, 16, 20,
                21, 22, 23, 24, 25, 29, 30,
                31, 32, 33, 34, 38, 39, 40,
                41, 42, 43);

        task = MultiLib.getAsyncScheduler().runAtFixedRate(Fadcg.getInstance(), t -> updatePagination(), 20L, 20L);
    }

    protected PaginatedFastInv(int size, @NotNull String title, @NotNull Player player, @NotNull List<Integer> paginationMappings) {
        super(size, title);
        this.player = player;
        this.paginationMappings = paginationMappings;


        task = MultiLib.getAsyncScheduler().runAtFixedRate(Fadcg.getInstance(), t -> updatePagination(), 20L, 20L);
    }

    protected void setPaginationMappings(List<Integer> list) {
        this.paginationMappings = list;
    }

    protected void nextPage() {
        if (paginatedItems == null || paginatedItems.size() < index + 1) {
            return;
        }
        page++;
        populatePage();
        addPaginationControls();
    }

    protected void previousPage() {
        if (page == 0) {
            return;
        }
        page--;
        populatePage();
        addPaginationControls();
    }

    protected void populatePage() {
        int maxItemsPerPage = paginationMappings.size();
        boolean empty = paginatedItems == null || paginatedItems.isEmpty();
        if (empty) {
            if (needsClearing) {
                for (Integer paginationMapping : paginationMappings) removeItem(paginationMapping);
                needsClearing = false;
            }
            paginationEmpty();
            return;
        }

        needsClearing = true;
        
        for (int i = 0; i < maxItemsPerPage; i++) {
            removeItem(paginationMappings.get(i));
            index = maxItemsPerPage * page + i;
            if (index >= paginatedItems.size()) continue;
            PaginatedItem item = paginatedItems.get(index);
            setItem(paginationMappings.get(i), item.itemStack(), item.eventConsumer());
        }
    }

    protected void updatePagination() {
        paginatedItems.clear();
        fillPaginationItems();
        populatePage();
        addPaginationControls();
    }

    protected abstract void paginationEmpty();

    protected abstract void fillPaginationItems();

    protected abstract void addPaginationControls();

    protected void addPaginationItem(PaginatedItem item) {
        paginatedItems.add(item);
    }
}

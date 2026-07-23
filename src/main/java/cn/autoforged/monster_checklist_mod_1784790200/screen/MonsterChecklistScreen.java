package cn.autoforged.monster_checklist_mod_1784790200.screen;

import cn.autoforged.monster_checklist_mod_1784790200.MainMod;
import cn.autoforged.monster_checklist_mod_1784790200.config.ModConfig;
import com.evandev.fieldguide.api.GuideEntry;
import com.evandev.fieldguide.client.ClientFieldGuideManager;
import com.evandev.fieldguide.client.manager.ClientCategoryManager;
import com.evandev.fieldguide.entry.EntryResolver;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class MonsterChecklistScreen extends Screen {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(MainMod.MODID, "textures/gui/monster_book.png");
    private static final int IMG_WIDTH = 186;
    private static final int IMG_HEIGHT = 192;
    private static final int TEXTURE_SIZE = 256;

    private static final int PER_ROW = 1;
    private static final int ROWS_PER_PAGE = 8;
    private static final int PER_PAGE = PER_ROW * ROWS_PER_PAGE;
    private static final int ITEM_SPACING = 18;
    private static final int TEXT_LEFT = 40;
    private static final int GRID_OFFSET_Y = -84;

    private static int[] milestones() { return ModConfig.getMilestoneArray(); }

    private int unlockedCount;
    private int totalCount;
    private List<EntryInfo> allEntries = new ArrayList<>();
    private final List<PageContent> pages = new ArrayList<>();
    private int currentPage;

    private PageTurnButton prevButton;
    private PageTurnButton nextButton;
    private int tickCounter;
    private int lastUnlocked = -1;
    private int lastTotal = -1;

    public MonsterChecklistScreen() {
        super(Component.empty());
    }

    @Override
    protected void init() {
        super.init();
        refreshData();
        buildPages();
        rebuildButtons();
    }

    @Override
    public void tick() {
        super.tick();
        tickCounter++;
        if (tickCounter >= 20) {
            tickCounter = 0;
            refreshIfNeeded();
        }
    }

    private void refreshIfNeeded() {
        var catMgr = ClientCategoryManager.getInstance();
        var resolved = catMgr.getResolvedCategoryEntries();
        if (resolved.isEmpty()) return;

        int newTotal = 0;
        int newUnlocked = 0;
        for (var entry : resolved.entrySet()) {
            if (!entry.getKey().getPath().contains("monster")) continue;
            for (Object obj : entry.getValue()) {
                if (EntryResolver.resolveCoreEntry(obj) instanceof EntityType) {
                    newTotal++;
                    if (ClientFieldGuideManager.isUnlocked(obj)) {
                        newUnlocked++;
                    }
                }
            }
        }

        if (newUnlocked != lastUnlocked || newTotal != lastTotal) {
            refreshData();
            buildPages();
            if (currentPage >= pages.size()) {
                currentPage = Math.max(0, pages.size() - 1);
            }
            rebuildButtons();
        }
    }

    private void refreshData() {
        var catMgr = ClientCategoryManager.getInstance();
        var resolved = catMgr.getResolvedCategoryEntries();
        allEntries = resolved.entrySet().stream()
                .filter(e -> e.getKey().getPath().contains("monster"))
                .flatMap(e -> e.getValue().stream())
                .distinct()
                .filter(entry -> EntryResolver.resolveCoreEntry(entry) instanceof EntityType)
                .map(entry -> {
                    boolean unlocked = ClientFieldGuideManager.isUnlocked(entry);
                    Component name = ClientFieldGuideManager.getEntryName(entry);
                    return new EntryInfo(entry instanceof GuideEntry ge ? ge : null, name, unlocked);
                })
                .sorted(Comparator.<EntryInfo, Boolean>comparing(e -> !e.unlocked)
                        .thenComparing(e -> e.name.getString()))
                .collect(Collectors.toList());

        unlockedCount = (int) allEntries.stream().filter(e -> e.unlocked).count();
        totalCount = allEntries.size();
        lastUnlocked = unlockedCount;
        lastTotal = totalCount;
    }

    private void buildPages() {
        pages.clear();
        pages.add(new ProgressPage());
        for (int i = 0; i < allEntries.size(); i += PER_PAGE) {
            pages.add(new EntityPage(allEntries.subList(i, Math.min(i + PER_PAGE, allEntries.size()))));
        }
    }

    private void rebuildButtons() {
        clearWidgets();
        int cx = getBookLeft() + IMG_WIDTH / 2;
        int by = getBookTop() + 152;

        if (currentPage > 0) {
            prevButton = addRenderableWidget(
                    new PageTurnButton(cx - 48, by, false, b -> {
                        currentPage--;
                        rebuildButtons();
                    }));
        }
        if (currentPage < pages.size() - 1) {
            nextButton = addRenderableWidget(
                    new PageTurnButton(cx + 25, by, true, b -> {
                        currentPage++;
                        rebuildButtons();
                    }));
        }
        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> onClose())
                .bounds(width / 2 - 100, getBookTop() + IMG_HEIGHT + 8, 200, 20).build());
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);
        int bookLeft = getBookLeft();
        int bookTop = getBookTop();

        String pageNum = (currentPage + 1) + "/" + pages.size();
        g.drawString(font, pageNum,
                bookLeft + (IMG_WIDTH - font.width(pageNum)) / 2,
                bookTop + 158, 0x333333, false);

        if (currentPage >= 0 && currentPage < pages.size()) {
            Component tip = pages.get(currentPage).render(g, bookLeft, bookTop, mouseX, mouseY);
            if (tip != null) {
                g.renderTooltip(font, tip, mouseX, mouseY);
            }
        }
    }

    @Override
    public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        renderTransparentBackground(g);
        int bookLeft = getBookLeft();
        int bookTop = getBookTop();
        g.blit(TEXTURE, bookLeft, bookTop, 0, 0, IMG_WIDTH, IMG_HEIGHT, TEXTURE_SIZE, TEXTURE_SIZE);
    }

    private int getBookLeft() {
        return (width - IMG_WIDTH) / 2;
    }

    private int getBookTop() {
        return (height - IMG_HEIGHT) / 2;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @OnlyIn(Dist.CLIENT)
    private static class PageTurnButton extends Button {
        private final boolean forward;

        PageTurnButton(int x, int y, boolean forward, OnPress onPress) {
            super(x, y, 23, 13, CommonComponents.EMPTY, onPress, DEFAULT_NARRATION);
            this.forward = forward;
        }

        @Override
        protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
            if (visible) {
                int texX = isHoveredOrFocused() ? 23 : 0;
                int texY = forward ? 192 : 205;
                g.blit(TEXTURE, getX(), getY(), texX, texY, 23, 13, TEXTURE_SIZE, TEXTURE_SIZE);
            }
        }

        @Override
        public void playDownSound(SoundManager soundManager) {
            soundManager.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
        }
    }

    private record EntryInfo(com.evandev.fieldguide.api.GuideEntry entry, Component name, boolean unlocked) {}

    private interface PageContent {
        Component render(GuiGraphics g, int bookLeft, int bookTop, int mouseX, int mouseY);
    }

    private class ProgressPage implements PageContent {
        @Override
        public Component render(GuiGraphics g, int bookLeft, int bookTop, int mouseX, int mouseY) {
            int cx = bookLeft + IMG_WIDTH / 2;
            int y = bookTop + 24;

            Component title = Component.translatable("screen." + MainMod.MODID + ".stat_title");
            g.drawString(font, title, cx - font.width(title) / 2, y, 0x3F3F3F, false);
            y += 15;

            Component count = Component.translatable("screen." + MainMod.MODID + ".unlocked_count", unlockedCount, totalCount);
            g.drawString(font, count, cx - font.width(count) / 2, y, 0x3F3F3F, false);
            y += 28;

            int[] msArr = milestones();
            int completedMilestones = 0;
            for (int m : msArr) {
                if (unlockedCount >= m) completedMilestones++;
            }
            Component ms = Component.translatable("screen." + MainMod.MODID + ".milestone_count", completedMilestones, msArr.length);
            g.drawString(font, ms, cx - font.width(ms) / 2, y, 0x3F3F3F, false);
            y += 28;

            int lineY = y;
            int leftEdge = cx - 36;
            int leftPoint = cx - 24;
            int rightPoint = cx + 24;
            int rightEdge = cx + 36;

            int prev = 0;
            int next = -1;
            for (int m : milestones()) {
                if (unlockedCount < m) { next = m; break; }
                prev = m;
            }

            if (next == -1) {
                g.fill(leftEdge, lineY, rightEdge, lineY + 1, 0xFF333333);
                Component done = Component.translatable("screen." + MainMod.MODID + ".all_complete");
                g.drawString(font, done, cx - font.width(done) / 2, lineY + 12, 0x3F3F3F, false);
                return null;
            }

            int progress = (unlockedCount - prev) * 48 / (next - prev);
            int px = leftPoint + Math.min(progress, 48);

            g.fill(leftEdge, lineY, leftPoint, lineY + 1, 0xFFCCCCCC);
            g.fill(leftPoint, lineY, px, lineY + 1, 0xFF333333);
            g.fill(px, lineY, rightPoint, lineY + 1, 0xFFAAAAAA);
            g.fill(rightPoint, lineY, rightEdge, lineY + 1, 0xFFCCCCCC);

            Component prevL = Component.literal(String.valueOf(prev));
            g.drawString(font, prevL, leftPoint - font.width(prevL) / 2, lineY - 11, 0x3F3F3F, false);
            g.fill(leftPoint, lineY - 2, leftPoint + 1, lineY, 0xFF333333);

            Component nextL = Component.literal(String.valueOf(next));
            g.drawString(font, nextL, rightPoint - font.width(nextL) / 2, lineY - 11, 0x888888, false);
            g.fill(rightPoint, lineY - 2, rightPoint + 1, lineY, 0xFF888888);

            Component cur = Component.literal(String.valueOf(unlockedCount));
            g.drawString(font, cur, px - font.width(cur) / 2, lineY + 5, 0x3F3F3F, false);
            g.fill(px, lineY + 1, px + 1, lineY + 4, 0xFF333333);
            return null;
        }
    }

    private class EntityPage implements PageContent {
        private final List<EntryInfo> entries;

        EntityPage(List<EntryInfo> entries) {
            this.entries = entries;
        }

        @Override
        public Component render(GuiGraphics g, int bookLeft, int bookTop, int mouseX, int mouseY) {
            Component hovered = null;
            int cx = bookLeft + IMG_WIDTH / 2;
            int cy = bookTop + IMG_HEIGHT / 2;

            for (int i = 0; i < entries.size(); i++) {
                EntryInfo e = entries.get(i);
                int ix = bookLeft + TEXT_LEFT;
                int iy = cy + GRID_OFFSET_Y + i * ITEM_SPACING;

                String name = e.name.getString();
                int color = e.unlocked ? 0xFF000000 : 0xFF999999;
                g.drawString(font, "• " + name, ix, iy + 4, color, false);

                if (mouseX >= ix && mouseX < ix + 140 && mouseY >= iy && mouseY < iy + 16) {
                    hovered = e.name;
                }
            }
            return hovered;
        }
    }
}

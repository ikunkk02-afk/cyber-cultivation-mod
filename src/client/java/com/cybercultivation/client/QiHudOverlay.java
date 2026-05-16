package com.cybercultivation.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class QiHudOverlay {

    private static final int PANEL_WIDTH = 180;
    private static final int PANEL_HEIGHT = 138;
    private static final int BAR_WIDTH = 100;
    private static final int BAR_HEIGHT = 8;

    private static final int COLOR_PANEL_BG = 0xCC0D0D1A;
    private static final int COLOR_PANEL_INNER = 0x88182030;
    private static final int COLOR_BORDER_OUTER = 0xEED5A84C;
    private static final int COLOR_BORDER_INNER = 0x9954E6E4;
    private static final int COLOR_TITLE = 0xFFFFE8A3;
    private static final int COLOR_QI_LABEL = 0xFF8CEDEA;
    private static final int COLOR_QI_VALUE = 0xFFEAF7FF;
    private static final int COLOR_LABEL = 0xFF8CEDEA;
    private static final int COLOR_VALUE = 0xFFEAF7FF;
    private static final int COLOR_BAR_BG = 0xAA111827;
    private static final int COLOR_BAR_FILL_START = 0xFF31E6E0;
    private static final int COLOR_BAR_FILL_END = 0xFF247CFF;
    private static final int COLOR_DIM_BG = 0xCC000000;
    private static final int COLOR_EDIT_HIGHLIGHT = 0x3300BFFF;

    private static final HudClientConfig CONFIG = HudClientConfig.load();
    private static boolean dragging;
    private static double dragOffsetX;
    private static double dragOffsetY;

    public static void render(GuiGraphics graphics) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.options.hideGui) {
            return;
        }

        int qi = ClientQiData.getCurrentQi();
        int maxQi = ClientQiData.getMaxQi();
        if (maxQi <= 0) {
            return;
        }

        boolean editMode = HudEditController.isEditMode();

        if (editMode) {
            graphics.fill(0, 0, graphics.guiWidth(), graphics.guiHeight(), COLOR_DIM_BG);
        }

        handleEditInput(client, graphics);

        float scale = CONFIG.getHudScale();
        graphics.pose().pushPose();
        graphics.pose().translate(CONFIG.getHudX(), CONFIG.getHudY(), 0.0F);
        graphics.pose().scale(scale, scale, 1.0F);

        drawPanel(graphics, client, qi, maxQi);

        if (editMode) {
            drawEditOverlay(graphics);
        }

        graphics.pose().popPose();
    }

    public static void stopDragging() {
        dragging = false;
    }

    public static void saveConfig() {
        stopDragging();
        CONFIG.save();
    }

    private static void drawPanel(GuiGraphics graphics, Minecraft client, int qi, int maxQi) {
        drawFrame(graphics);

        String title = "✦ 修仙面板 ✦";
        int titleW = client.font.width(Component.literal(title));
        graphics.drawString(client.font, title, (PANEL_WIDTH - titleW) / 2, 10, COLOR_TITLE, false);

        graphics.fill(12, 23, PANEL_WIDTH - 12, 24, 0x88DBB96A);

        graphics.drawString(client.font, "灵力", 16, 32, COLOR_QI_LABEL, false);
        String qiText = qi + " / " + maxQi;
        int qiTextW = client.font.width(Component.literal(qiText));
        graphics.drawString(client.font, qiText, PANEL_WIDTH - 16 - qiTextW, 32, COLOR_QI_VALUE, false);

        int barX = 16;
        int barY = 44;
        drawQiBar(graphics, barX, barY, qi, maxQi);

        graphics.fill(12, 57, PANEL_WIDTH - 12, 58, 0x66DBB96A);

        int row1Y = 66;
        drawPair(graphics, client, "路线", ClientQiData.getPathDisplayName(),
                "五行", ClientQiData.getElementDisplayName(), row1Y);

        int row2Y = 80;
        drawPair(graphics, client, "主科", ClientQiData.getMainDisciplineDisplayName(),
                "副科", ClientQiData.getSubDisciplinesDisplayName(), row2Y);
        int row3Y = 94;
        int statusColor = ClientQiData.isFlyingSword() ? 0xFF54E6E4 : COLOR_VALUE;
        graphics.drawString(client.font, "\u72b6\u6001\uff1a", 16, row3Y, COLOR_LABEL, false);
        graphics.drawString(client.font, ClientQiData.getFlyingSwordStatusDisplayName(), 58, row3Y, statusColor, false);
        if (ClientQiData.isFlyingSword()) {
            int row4Y = 108;
            graphics.drawString(client.font, "\u5fa1\u5251\u6cd5\u5251\uff1a", 16, row4Y, COLOR_LABEL, false);
            graphics.drawString(client.font, ClientQiData.getFlyingSwordItemDisplayName(), 82, row4Y, COLOR_VALUE, false);
        }
        if (ClientQiData.isInHerbalRealm()) {
            int realmY = ClientQiData.isFlyingSword() ? 122 : 108;
            graphics.drawString(client.font, "\u79d8\u5883\u5269\u4f59\u65f6\u95f4\uff1a", 16, realmY, COLOR_LABEL, false);
            graphics.drawString(client.font, ClientQiData.getHerbalRealmTimeDisplayName(), 100, realmY, 0xFF9EFFD2, false);
        }
    }

    private static void drawFrame(GuiGraphics graphics) {
        graphics.fill(0, 0, PANEL_WIDTH, PANEL_HEIGHT, COLOR_PANEL_BG);
        graphics.fill(3, 3, PANEL_WIDTH - 3, PANEL_HEIGHT - 3, COLOR_PANEL_INNER);

        graphics.fill(0, 0, PANEL_WIDTH, 1, COLOR_BORDER_OUTER);
        graphics.fill(0, PANEL_HEIGHT - 1, PANEL_WIDTH, PANEL_HEIGHT, COLOR_BORDER_OUTER);
        graphics.fill(0, 0, 1, PANEL_HEIGHT, COLOR_BORDER_OUTER);
        graphics.fill(PANEL_WIDTH - 1, 0, PANEL_WIDTH, PANEL_HEIGHT, COLOR_BORDER_OUTER);

        graphics.fill(2, 2, PANEL_WIDTH - 2, 3, COLOR_BORDER_INNER);
        graphics.fill(2, PANEL_HEIGHT - 3, PANEL_WIDTH - 2, PANEL_HEIGHT - 2, COLOR_BORDER_INNER);
        graphics.fill(2, 2, 3, PANEL_HEIGHT - 2, COLOR_BORDER_INNER);
        graphics.fill(PANEL_WIDTH - 3, 2, PANEL_WIDTH - 2, PANEL_HEIGHT - 2, COLOR_BORDER_INNER);
    }

    private static void drawQiBar(GuiGraphics graphics, int x, int y, int qi, int maxQi) {
        graphics.fill(x, y, x + BAR_WIDTH, y + BAR_HEIGHT, COLOR_BAR_BG);

        float ratio = Math.max(0.0F, Math.min(1.0F, (float) qi / (float) maxQi));
        int fillW = (int) (ratio * BAR_WIDTH);
        if (fillW > 0) {
            int mid = Math.max(1, fillW / 2);
            graphics.fill(x, y, x + mid, y + BAR_HEIGHT, COLOR_BAR_FILL_START);
            graphics.fill(x + mid, y, x + fillW, y + BAR_HEIGHT, COLOR_BAR_FILL_END);
            graphics.fill(x + 1, y + 1, x + Math.max(1, fillW - 1), y + 2, 0xAAE9FFFF);
        }

        graphics.fill(x - 1, y - 1, x + BAR_WIDTH + 1, y, 0xAA54E6E4);
        graphics.fill(x - 1, y + BAR_HEIGHT, x + BAR_WIDTH + 1, y + BAR_HEIGHT + 1, 0xAA54E6E4);
        graphics.fill(x - 1, y, x, y + BAR_HEIGHT, 0xAA54E6E4);
        graphics.fill(x + BAR_WIDTH, y, x + BAR_WIDTH + 1, y + BAR_HEIGHT, 0xAA54E6E4);
    }

    private static void drawPair(GuiGraphics graphics, Minecraft client,
                                 String label1, String value1,
                                 String label2, String value2, int y) {
        graphics.drawString(client.font, label1 + "：", 16, y, COLOR_LABEL, false);
        int color1 = isUnset(value1) ? 0xFF8E8E98 : COLOR_VALUE;
        graphics.drawString(client.font, value1, 52, y, color1, false);

        int col2X = (PANEL_WIDTH + 16) / 2;
        graphics.drawString(client.font, label2 + "：", col2X, y, COLOR_LABEL, false);
        int color2 = isUnset(value2) ? 0xFF8E8E98 : COLOR_VALUE;
        graphics.drawString(client.font, value2, col2X + 36, y, color2, false);
    }

    private static void drawEditOverlay(GuiGraphics graphics) {
        graphics.fill(-2, -2, PANEL_WIDTH + 2, -1, 0xFF00BFFF);
        graphics.fill(-2, PANEL_HEIGHT + 1, PANEL_WIDTH + 2, PANEL_HEIGHT + 2, 0xFF00BFFF);
        graphics.fill(-2, -2, -1, PANEL_HEIGHT + 2, 0xFF00BFFF);
        graphics.fill(PANEL_WIDTH + 1, -2, PANEL_WIDTH + 2, PANEL_HEIGHT + 2, 0xFF00BFFF);
    }

    private static boolean isUnset(String value) {
        return "未选择".equals(value) || "无".equals(value) || "未觉醒".equals(value);
    }

    private static void handleEditInput(Minecraft client, GuiGraphics graphics) {
        if (!HudEditController.isEditMode()) {
            stopDragging();
            return;
        }

        double scrollDelta = HudEditController.consumeScrollDelta();
        if (scrollDelta != 0.0) {
            CONFIG.setHudScale(CONFIG.getHudScale() + (float) scrollDelta * 0.05F);
        }

        double mouseX = scaledMouseX(client, graphics);
        double mouseY = scaledMouseY(client, graphics);
        boolean mouseDown = GLFW.glfwGetMouseButton(client.getWindow().getWindow(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;

        if (!mouseDown) {
            stopDragging();
            return;
        }

        if (!dragging && isMouseInside(mouseX, mouseY)) {
            dragging = true;
            dragOffsetX = mouseX - CONFIG.getHudX();
            dragOffsetY = mouseY - CONFIG.getHudY();
        }

        if (dragging) {
            CONFIG.setHudX((int) Math.round(mouseX - dragOffsetX));
            CONFIG.setHudY((int) Math.round(mouseY - dragOffsetY));
        }
    }

    private static boolean isMouseInside(double mouseX, double mouseY) {
        double w = PANEL_WIDTH * CONFIG.getHudScale();
        double h = PANEL_HEIGHT * CONFIG.getHudScale();
        return mouseX >= CONFIG.getHudX()
                && mouseY >= CONFIG.getHudY()
                && mouseX <= CONFIG.getHudX() + w
                && mouseY <= CONFIG.getHudY() + h;
    }

    private static double scaledMouseX(Minecraft client, GuiGraphics graphics) {
        return client.mouseHandler.xpos() * graphics.guiWidth() / client.getWindow().getScreenWidth();
    }

    private static double scaledMouseY(Minecraft client, GuiGraphics graphics) {
        return client.mouseHandler.ypos() * graphics.guiHeight() / client.getWindow().getScreenHeight();
    }
}

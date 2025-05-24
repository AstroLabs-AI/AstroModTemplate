package com.astrolabs.arcanecodex.client.gui;

import com.astrolabs.arcanecodex.ArcaneCodex;
import com.astrolabs.arcanecodex.common.blockentities.RealityCompilerBlockEntity;
import com.astrolabs.arcanecodex.common.network.ModNetworking;
import com.astrolabs.arcanecodex.common.network.packets.ExecuteRPLPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class RealityCompilerScreen extends AbstractContainerScreen<RealityCompilerMenu> {
    
    private static final ResourceLocation TEXTURE = new ResourceLocation(ArcaneCodex.MOD_ID, "textures/gui/reality_compiler.png");
    
    private MultiLineEditBox codeEditor;
    private Button executeButton;
    private Button clearButton;
    private List<Component> outputLog = new ArrayList<>();
    private int outputScroll = 0;
    
    public RealityCompilerScreen(RealityCompilerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 256;
        this.imageHeight = 200;
    }
    
    @Override
    protected void init() {
        super.init();
        
        // Code editor
        this.codeEditor = new MultiLineEditBox(this.font, 
            leftPos + 8, topPos + 20, 160, 100, 
            Component.literal("Enter RPL code..."));
        this.codeEditor.setMaxLength(2000);
        this.codeEditor.setValue(String.join("\n", menu.getBlockEntity().getCodeLines()));
        this.addRenderableWidget(this.codeEditor);
        
        // Execute button
        this.executeButton = Button.builder(
            Component.literal("Execute").withStyle(style -> style.withColor(0x00FF00)),
            button -> this.executeCode()
        ).bounds(leftPos + 8, topPos + 125, 50, 20).build();
        this.addRenderableWidget(this.executeButton);
        
        // Clear button
        this.clearButton = Button.builder(
            Component.literal("Clear"),
            button -> this.codeEditor.setValue("")
        ).bounds(leftPos + 62, topPos + 125, 40, 20).build();
        this.addRenderableWidget(this.clearButton);
        
        // Help button
        Button helpButton = Button.builder(
            Component.literal("?"),
            button -> this.showHelp()
        ).bounds(leftPos + 148, topPos + 125, 20, 20).build();
        this.addRenderableWidget(helpButton);
    }
    
    private void executeCode() {
        String code = this.codeEditor.getValue();
        List<String> lines = List.of(code.split("\n"));
        
        // Send to server
        ModNetworking.sendToServer(new ExecuteRPLPacket(menu.getBlockEntity().getBlockPos(), lines));
        
        // Update local display
        outputLog.add(Component.literal("> Executing...").withStyle(style -> style.withColor(0x00FFFF)));
    }
    
    private void showHelp() {
        outputLog.clear();
        outputLog.add(Component.literal("=== RPL Command Reference ===").withStyle(style -> style.withColor(0xFFFF00)));
        outputLog.add(Component.literal(""));
        outputLog.add(Component.literal("reality.manifest({type: \"gravitational_anomaly\", strength: 9.8})"));
        outputLog.add(Component.literal("  Creates gravity field at target location"));
        outputLog.add(Component.literal(""));
        outputLog.add(Component.literal("player.augment({phase: \"ethereal\", duration: 200})"));
        outputLog.add(Component.literal("  Phase shift the player temporarily"));
        outputLog.add(Component.literal(""));
        outputLog.add(Component.literal("quantum.measure({radius: 10})"));
        outputLog.add(Component.literal("  Collapse quantum superposition in area"));
        outputLog.add(Component.literal(""));
        outputLog.add(Component.literal("time.dilate({factor: 0.5, duration: 100})"));
        outputLog.add(Component.literal("  Slow time in the area"));
        outputLog.add(Component.literal(""));
        outputLog.add(Component.literal("energy.cascade({type: \"resonance\", power: 1000})"));
        outputLog.add(Component.literal("  Create an energy cascade effect"));
    }
    
    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        
        // Render output log
        int outputY = topPos + 20;
        graphics.fill(leftPos + 174, outputY, leftPos + 248, topPos + 145, 0xDD000000);
        
        // Draw output text
        int textY = outputY + 2;
        int maxLines = 11;
        int startLine = Math.max(0, outputLog.size() - maxLines - outputScroll);
        int endLine = Math.min(outputLog.size(), startLine + maxLines);
        
        for (int i = startLine; i < endLine; i++) {
            Component line = outputLog.get(i);
            graphics.drawString(font, line, leftPos + 176, textY, 0xFFFFFF, false);
            textY += 10;
        }
        
        // Energy display
        if (menu.getBlockEntity() != null) {
            long energy = menu.getBlockEntity().getEnergyStorage()
                .getEnergyStored(com.astrolabs.arcanecodex.api.IQuantumEnergy.EnergyType.QUANTUM_FOAM);
            graphics.drawString(font, "Energy: " + energy, leftPos + 8, topPos + 150, 0x00FF00, false);
        }
    }
    
    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, 8, 6, 0x00FFFF, false);
        graphics.drawString(this.font, "Output:", 174, 10, 0x00FFFF, false);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_TAB && this.codeEditor.isFocused()) {
            this.codeEditor.insertText("  ");
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        // Check if mouse is over output area
        if (mouseX >= leftPos + 174 && mouseX <= leftPos + 248 && 
            mouseY >= topPos + 20 && mouseY <= topPos + 145) {
            outputScroll = Math.max(0, outputScroll + (int)delta);
            outputScroll = Math.min(outputScroll, Math.max(0, outputLog.size() - 11));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }
    
    public void addOutput(Component message) {
        outputLog.add(message);
        // Auto-scroll to bottom
        outputScroll = 0;
    }
}
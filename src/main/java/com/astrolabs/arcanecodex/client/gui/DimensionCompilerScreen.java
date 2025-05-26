package com.astrolabs.arcanecodex.client.gui;

import com.astrolabs.arcanecodex.ArcaneCodex;
import com.astrolabs.arcanecodex.common.dimensions.DimensionCodeParser;
import com.astrolabs.arcanecodex.common.dimensions.DimensionValidator;
import com.astrolabs.arcanecodex.common.dimensions.properties.DimensionProperties;
import com.astrolabs.arcanecodex.common.network.ModNetworking;
import com.astrolabs.arcanecodex.common.network.packets.CompileDimensionPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class DimensionCompilerScreen extends AbstractContainerScreen<DimensionCompilerMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(ArcaneCodex.MOD_ID, "textures/gui/dimension_compiler.png");
    
    private MultiLineEditBox codeEditor;
    private Button compileButton;
    private Button validateButton;
    private Button helpButton;
    
    private List<Component> messages = new ArrayList<>();
    private boolean showHelp = false;
    
    public DimensionCompilerScreen(DimensionCompilerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 256;
        this.imageHeight = 200;
    }
    
    @Override
    protected void init() {
        super.init();
        
        // Code editor
        this.codeEditor = new MultiLineEditBox(this.font, 
            this.leftPos + 8, this.topPos + 20, 160, 120, 
            Component.literal("Enter dimension code..."), Component.literal("Dimension Code"));
        this.codeEditor.setCharacterLimit(2000);
        this.codeEditor.setValue(getDefaultCode());
        this.addWidget(this.codeEditor);
        
        // Compile button
        this.compileButton = this.addRenderableWidget(Button.builder(
            Component.literal("Compile"),
            button -> compileDimension()
        ).bounds(this.leftPos + 8, this.topPos + 145, 50, 20).build());
        
        // Validate button
        this.validateButton = this.addRenderableWidget(Button.builder(
            Component.literal("Validate"),
            button -> validateCode()
        ).bounds(this.leftPos + 62, this.topPos + 145, 50, 20).build());
        
        // Help button
        this.helpButton = this.addRenderableWidget(Button.builder(
            Component.literal("Help"),
            button -> showHelp = !showHelp
        ).bounds(this.leftPos + 116, this.topPos + 145, 50, 20).build());
        
        updateButtonStates();
    }
    
    private String getDefaultCode() {
        return """
            // Dimension Code Example
            NAME: My Custom Dimension
            SEED: 12345
            GRAVITY: 0.8
            TIME_FLOW: 1.0
            TERRAIN_TYPE: ISLANDS
            SKY_COLOR: 0x00FFFF
            FOG_COLOR: 0x80FFFF
            ENERGY_DENSITY: 2.0
            INSTABILITY: 0.2
            
            BIOME {name=quantum_forest, type=forest, temperature=0.7, humidity=0.6}
            
            RULE {NO_WEATHER: true}
            RULE {MOB_SPAWN_RATE: 0.5}
            """;
    }
    
    private void compileDimension() {
        String code = codeEditor.getValue();
        
        // Parse the code
        DimensionCodeParser.ParseResult parseResult = DimensionCodeParser.parse(code);
        
        if (!parseResult.success) {
            messages.clear();
            messages.add(Component.literal("§cParsing failed:"));
            messages.addAll(parseResult.errors);
            return;
        }
        
        // Validate the dimension
        DimensionValidator.ValidationResult validationResult = DimensionValidator.validate(parseResult.properties);
        
        if (!validationResult.valid) {
            messages.clear();
            messages.add(Component.literal("§cValidation failed:"));
            messages.addAll(validationResult.errors);
            return;
        }
        
        // Check energy cost
        if (menu.blockEntity.getEnergyStored() < validationResult.energyCost) {
            messages.clear();
            messages.add(Component.literal("§cInsufficient energy!"));
            messages.add(Component.literal("Required: " + validationResult.energyCost + " QE"));
            messages.add(Component.literal("Available: " + menu.blockEntity.getEnergyStored() + " QE"));
            return;
        }
        
        // Send compilation request to server
        ModNetworking.sendToServer(new CompileDimensionPacket(menu.blockEntity.getBlockPos(), code));
        
        messages.clear();
        messages.add(Component.literal("§aCompiling dimension..."));
        messages.add(Component.literal("Complexity: " + validationResult.complexityScore));
        messages.add(Component.literal("Energy cost: " + validationResult.energyCost + " QE"));
        
        if (!validationResult.warnings.isEmpty()) {
            messages.add(Component.literal("§eWarnings:"));
            messages.addAll(validationResult.warnings);
        }
    }
    
    private void validateCode() {
        String code = codeEditor.getValue();
        DimensionCodeParser.ParseResult parseResult = DimensionCodeParser.parse(code);
        
        messages.clear();
        
        if (!parseResult.success) {
            messages.add(Component.literal("§cParsing errors:"));
            messages.addAll(parseResult.errors);
        } else {
            DimensionValidator.ValidationResult validationResult = DimensionValidator.validate(parseResult.properties);
            
            if (validationResult.valid) {
                messages.add(Component.literal("§aCode is valid!"));
                messages.add(Component.literal("Complexity: " + validationResult.complexityScore));
                messages.add(Component.literal("Energy cost: " + validationResult.energyCost + " QE"));
            } else {
                messages.add(Component.literal("§cValidation errors:"));
                messages.addAll(validationResult.errors);
            }
            
            if (!validationResult.warnings.isEmpty()) {
                messages.add(Component.literal("§eWarnings:"));
                messages.addAll(validationResult.warnings);
            }
        }
    }
    
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        
        // Render code editor
        this.codeEditor.render(graphics, mouseX, mouseY, partialTick);
        
        // Render messages
        int messageY = this.topPos + 20;
        for (Component message : messages) {
            graphics.drawString(this.font, message, this.leftPos + 175, messageY, 0xFFFFFF);
            messageY += 10;
        }
        
        // Render help overlay
        if (showHelp) {
            renderHelp(graphics);
        }
        
        this.renderTooltip(graphics, mouseX, mouseY);
    }
    
    private void renderHelp(GuiGraphics graphics) {
        int x = this.leftPos + 10;
        int y = this.topPos + 10;
        int width = this.imageWidth - 20;
        int height = this.imageHeight - 20;
        
        // Dark background
        graphics.fill(x, y, x + width, y + height, 0xDD000000);
        
        // Help text
        List<Component> helpText = List.of(
            Component.literal("§e=== Dimension Code Syntax ==="),
            Component.literal(""),
            Component.literal("§bProperties:"),
            Component.literal("NAME: <dimension name>"),
            Component.literal("SEED: <number>"),
            Component.literal("GRAVITY: <-2.0 to 5.0>"),
            Component.literal("TIME_FLOW: <0.01 to 10.0>"),
            Component.literal("TERRAIN_TYPE: <type>"),
            Component.literal("SKY_COLOR: <hex color>"),
            Component.literal("ENERGY_DENSITY: <0 to 10>"),
            Component.literal("INSTABILITY: <0 to 1>"),
            Component.literal(""),
            Component.literal("§bTerrain Types:"),
            Component.literal("NORMAL, FLAT, ISLANDS, VOID"),
            Component.literal("FRACTAL, CRYSTALLINE, LIQUID"),
            Component.literal("INVERTED, GRID, QUANTUM"),
            Component.literal(""),
            Component.literal("§bBiomes:"),
            Component.literal("BIOME {name=..., type=..., ...}"),
            Component.literal(""),
            Component.literal("§bRules:"),
            Component.literal("RULE {NO_MOBS: true/false}"),
            Component.literal("RULE {ALWAYS_DAY: true/false}")
        );
        
        int helpY = y + 5;
        for (Component line : helpText) {
            graphics.drawString(this.font, line, x + 5, helpY, 0xFFFFFF);
            helpY += 9;
        }
    }
    
    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        
        // Render energy bar
        int energy = menu.blockEntity.getEnergyStored();
        int maxEnergy = menu.blockEntity.getMaxEnergyStored();
        int energyHeight = energy * 52 / maxEnergy;
        
        graphics.fill(this.leftPos + 180, this.topPos + 168 - energyHeight, 
                     this.leftPos + 188, this.topPos + 168, 0xFF00FFFF);
        
        // Render stability bar
        float stability = menu.blockEntity.getStability();
        int stabilityHeight = (int)(stability * 52);
        
        graphics.fill(this.leftPos + 195, this.topPos + 168 - stabilityHeight,
                     this.leftPos + 203, this.topPos + 168, 0xFF00FF00);
        
        // Render compilation progress
        if (menu.blockEntity.getCompilationTime() > 0) {
            int progress = menu.blockEntity.getCompilationProgress();
            int total = menu.blockEntity.getCompilationTime();
            int progressWidth = progress * 160 / total;
            
            graphics.fill(this.leftPos + 8, this.topPos + 170,
                         this.leftPos + 8 + progressWidth, this.topPos + 175, 0xFFFFFF00);
        }
    }
    
    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, 8, 6, 0x404040, false);
        graphics.drawString(this.font, "Energy", 175, 6, 0x404040, false);
        graphics.drawString(this.font, "Stability", 190, 6, 0x404040, false);
    }
    
    private void updateButtonStates() {
        boolean hasCode = codeEditor != null && !codeEditor.getValue().trim().isEmpty();
        boolean isCompiling = menu.blockEntity.getCompilationTime() > 0;
        
        if (compileButton != null) {
            compileButton.active = hasCode && !isCompiling && menu.blockEntity.isMultiblockFormed();
        }
        if (validateButton != null) {
            validateButton.active = hasCode;
        }
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.codeEditor.keyPressed(keyCode, scanCode, modifiers)) {
            updateButtonStates();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.codeEditor.charTyped(codePoint, modifiers)) {
            updateButtonStates();
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.codeEditor.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
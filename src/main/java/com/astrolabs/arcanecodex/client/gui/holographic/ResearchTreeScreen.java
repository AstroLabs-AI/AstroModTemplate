package com.astrolabs.arcanecodex.client.gui.holographic;

import com.astrolabs.arcanecodex.ArcaneCodex;
import com.astrolabs.arcanecodex.api.IConsciousness;
import com.astrolabs.arcanecodex.common.capabilities.ModCapabilities;
import com.astrolabs.arcanecodex.common.research.ResearchNode;
import com.astrolabs.arcanecodex.common.research.ResearchTree;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

public class ResearchTreeScreen extends Screen {
    
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(ArcaneCodex.MOD_ID, "textures/gui/research_background.png");
    private static final ResourceLocation NODE_TEXTURE = new ResourceLocation(ArcaneCodex.MOD_ID, "textures/gui/research_node.png");
    
    private final Player player;
    private IConsciousness consciousness;
    
    // Camera controls
    private float cameraX = 0;
    private float cameraY = 0;
    private float cameraZ = -5;
    private float rotationX = 0;
    private float rotationY = 0;
    private float zoom = 1.0f;
    
    // Mouse tracking
    private boolean dragging = false;
    private double lastMouseX;
    private double lastMouseY;
    
    // Animation
    private float animationTicks = 0;
    private ResearchNode selectedNode = null;
    private ResearchNode hoveredNode = null;
    
    public ResearchTreeScreen(Player player) {
        super(Component.literal("Research Tree"));
        this.player = player;
    }
    
    @Override
    protected void init() {
        super.init();
        
        // Get player consciousness data
        player.getCapability(ModCapabilities.CONSCIOUSNESS).ifPresent(c -> {
            this.consciousness = c;
        });
    }
    
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        // Update animation
        animationTicks += partialTicks;
        
        // Dark background
        graphics.fill(0, 0, width, height, 0xDD000000);
        
        // Setup 3D rendering
        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        
        // Move to center of screen
        poseStack.translate(width / 2.0f, height / 2.0f, 100);
        
        // Apply camera transformations
        poseStack.scale(50 * zoom, -50 * zoom, 50 * zoom);
        poseStack.mulPose(Axis.XP.rotationDegrees(rotationX));
        poseStack.mulPose(Axis.YP.rotationDegrees(rotationY));
        poseStack.translate(cameraX, cameraY, cameraZ);
        
        // Enable depth testing
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        
        // Render synaptic links
        renderSynapticLinks(poseStack);
        
        // Render research nodes
        renderResearchNodes(poseStack, mouseX, mouseY);
        
        // Render particles
        renderHolographicParticles(poseStack);
        
        RenderSystem.disableDepthTest();
        poseStack.popPose();
        
        // Render 2D overlay
        renderOverlay(graphics, mouseX, mouseY);
        
        super.render(graphics, mouseX, mouseY, partialTicks);
    }
    
    private void renderSynapticLinks(PoseStack poseStack) {
        if (consciousness == null) return;
        
        List<ResearchTree.SynapticLink> links = ResearchTree.getSynapticLinks(consciousness.getUnlockedResearch());
        
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR);
        
        for (ResearchTree.SynapticLink link : links) {
            float alpha = link.active ? 0.8f : 0.3f;
            float pulse = link.active ? (float)(Math.sin(animationTicks * 0.1) * 0.2 + 0.8) : 1.0f;
            
            // Draw glowing line
            Vector3f from = new Vector3f(link.from.getX(), link.from.getY(), link.from.getZ());
            Vector3f to = new Vector3f(link.to.getX(), link.to.getY(), link.to.getZ());
            
            Matrix4f matrix = poseStack.last().pose();
            buffer.vertex(matrix, from.x, from.y, from.z)
                .color(0.0f, 1.0f * pulse, 1.0f * pulse, alpha)
                .endVertex();
            buffer.vertex(matrix, to.x, to.y, to.z)
                .color(0.0f, 1.0f * pulse, 1.0f * pulse, alpha)
                .endVertex();
        }
        
        BufferUploader.drawWithShader(buffer.end());
    }
    
    private void renderResearchNodes(PoseStack poseStack, int mouseX, int mouseY) {
        if (consciousness == null) return;
        
        List<ResourceLocation> unlocked = consciousness.getUnlockedResearch();
        hoveredNode = null;
        
        for (ResearchNode node : ResearchTree.getAllNodes()) {
            boolean isUnlocked = unlocked.contains(node.getId());
            boolean canUnlock = node.canUnlock(unlocked, consciousness.getConsciousnessLevel());
            
            // Calculate node position
            float x = node.getX();
            float y = node.getY();
            float z = node.getZ();
            
            // Check if mouse is over this node
            Vector3f screenPos = worldToScreen(poseStack, x, y, z);
            float dx = mouseX - screenPos.x;
            float dy = mouseY - screenPos.y;
            float distance = (float)Math.sqrt(dx * dx + dy * dy);
            
            if (distance < 30) {
                hoveredNode = node;
            }
            
            // Render node
            renderNode(poseStack, node, x, y, z, isUnlocked, canUnlock, node == hoveredNode);
        }
    }
    
    private void renderNode(PoseStack poseStack, ResearchNode node, float x, float y, float z, 
                           boolean unlocked, boolean available, boolean hovered) {
        poseStack.pushPose();
        poseStack.translate(x, y, z);
        
        // Face camera
        poseStack.mulPose(Axis.YP.rotationDegrees(-rotationY));
        poseStack.mulPose(Axis.XP.rotationDegrees(-rotationX));
        
        // Scale based on state
        float scale = hovered ? 0.15f : 0.1f;
        if (node == selectedNode) {
            scale *= 1.2f;
        }
        poseStack.scale(scale, scale, scale);
        
        // Determine color
        float r, g, b, a;
        if (unlocked) {
            r = 0.0f; g = 1.0f; b = 1.0f; a = 1.0f;
        } else if (available) {
            float pulse = (float)(Math.sin(animationTicks * 0.05) * 0.3 + 0.7);
            r = 0.5f * pulse; g = 0.5f * pulse; b = 1.0f; a = 0.8f;
        } else {
            r = 0.3f; g = 0.3f; b = 0.3f; a = 0.5f;
        }
        
        // Render holographic node
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(r, g, b, a);
        
        // Draw icosahedron or other 3D shape
        renderHolographicShape(poseStack);
        
        poseStack.popPose();
    }
    
    private void renderHolographicShape(PoseStack poseStack) {
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        
        Matrix4f matrix = poseStack.last().pose();
        float size = 1.0f;
        
        // Simple cube for now
        // Front face
        buffer.vertex(matrix, -size, -size, size).color(1f, 1f, 1f, 0.8f).endVertex();
        buffer.vertex(matrix, size, -size, size).color(1f, 1f, 1f, 0.8f).endVertex();
        buffer.vertex(matrix, -size, size, size).color(1f, 1f, 1f, 0.8f).endVertex();
        buffer.vertex(matrix, size, size, size).color(1f, 1f, 1f, 0.8f).endVertex();
        
        BufferUploader.drawWithShader(buffer.end());
    }
    
    private void renderHolographicParticles(PoseStack poseStack) {
        // Add floating particles for atmosphere
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        
        for (int i = 0; i < 50; i++) {
            float time = animationTicks * 0.02f + i * 0.5f;
            float x = (float)(Math.sin(time + i) * 3);
            float y = (float)(Math.cos(time * 0.7f + i * 2) * 2);
            float z = (float)(Math.sin(time * 0.5f + i * 3) * 3);
            
            float alpha = (float)(Math.sin(time * 2) * 0.3 + 0.5);
            
            Matrix4f matrix = poseStack.last().pose();
            float size = 0.02f;
            
            buffer.vertex(matrix, x - size, y - size, z).color(0f, 1f, 1f, alpha).endVertex();
            buffer.vertex(matrix, x + size, y - size, z).color(0f, 1f, 1f, alpha).endVertex();
            buffer.vertex(matrix, x + size, y + size, z).color(0f, 1f, 1f, alpha).endVertex();
            buffer.vertex(matrix, x - size, y + size, z).color(0f, 1f, 1f, alpha).endVertex();
        }
        
        BufferUploader.drawWithShader(buffer.end());
    }
    
    private void renderOverlay(GuiGraphics graphics, int mouseX, int mouseY) {
        // Title
        graphics.drawCenteredString(font, "Neural Research Network", width / 2, 10, 0x00FFFF);
        
        // Consciousness level
        if (consciousness != null) {
            graphics.drawString(font, "Consciousness Level: " + consciousness.getConsciousnessLevel(), 
                10, height - 20, 0x00FFFF);
            graphics.drawString(font, "Neural Charge: " + consciousness.getNeuralCharge() + "/" + consciousness.getMaxNeuralCharge(), 
                10, height - 10, 0x00FFFF);
        }
        
        // Hovered node info
        if (hoveredNode != null) {
            List<Component> tooltip = List.of(
                hoveredNode.getName(),
                hoveredNode.getDescription(),
                Component.literal("Tier " + hoveredNode.getTier()).withStyle(style -> style.withColor(0xFFFF00)),
                Component.literal("Cost: " + hoveredNode.getCost() + " Neural Charge").withStyle(style -> style.withColor(0x00FF00))
            );
            
            graphics.renderComponentTooltip(font, tooltip, mouseX, mouseY);
        }
        
        // Controls hint
        graphics.drawString(font, "Drag to rotate | Scroll to zoom | Click to select", 
            10, 10, 0x808080);
    }
    
    private Vector3f worldToScreen(PoseStack poseStack, float x, float y, float z) {
        Matrix4f matrix = poseStack.last().pose();
        Vector3f pos = new Vector3f(x, y, z);
        pos.mulPosition(matrix);
        
        // Project to screen
        float screenX = width / 2.0f + pos.x * 50 * zoom;
        float screenY = height / 2.0f - pos.y * 50 * zoom;
        
        return new Vector3f(screenX, screenY, pos.z);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            dragging = true;
            lastMouseX = mouseX;
            lastMouseY = mouseY;
            
            if (hoveredNode != null && consciousness != null) {
                selectedNode = hoveredNode;
                
                // Try to unlock research
                if (!consciousness.getUnlockedResearch().contains(hoveredNode.getId()) &&
                    hoveredNode.canUnlock(consciousness.getUnlockedResearch(), consciousness.getConsciousnessLevel())) {
                    
                    if (consciousness.getNeuralCharge() >= hoveredNode.getCost()) {
                        // Send unlock packet to server
                        // TODO: Implement network packet
                        player.sendSystemMessage(Component.literal("Research unlock requested: " + hoveredNode.getName().getString()));
                    } else {
                        player.sendSystemMessage(Component.literal("Insufficient Neural Charge!").withStyle(style -> style.withColor(0xFF0000)));
                    }
                }
            }
            
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            dragging = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (dragging) {
            rotationY += dragX * 0.5f;
            rotationX += dragY * 0.5f;
            rotationX = Mth.clamp(rotationX, -90, 90);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        zoom += delta * 0.1f;
        zoom = Mth.clamp(zoom, 0.5f, 3.0f);
        return true;
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
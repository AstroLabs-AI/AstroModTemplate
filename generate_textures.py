#!/usr/bin/env python3

import os
import numpy as np
from PIL import Image, ImageDraw, ImageFilter, ImageEnhance
import colorsys
import random

# Base directory for textures
TEXTURE_BASE = "src/main/resources/assets/arcanecodex/textures"

# Color palettes for different item types
PALETTES = {
    "quantum": [(0, 255, 255), (0, 128, 255), (128, 0, 255), (255, 255, 255)],  # Cyan/Blue/Purple
    "neural": [(255, 0, 128), (255, 128, 255), (128, 0, 255), (255, 255, 255)],  # Pink/Purple
    "temporal": [(255, 128, 0), (255, 255, 0), (255, 200, 128), (255, 255, 255)],  # Orange/Yellow
    "reality": [(0, 255, 128), (0, 255, 255), (128, 255, 255), (255, 255, 255)],  # Green/Cyan
    "dimension": [(128, 0, 255), (255, 0, 255), (255, 128, 255), (255, 255, 255)],  # Purple/Magenta
    "augment": [(64, 255, 128), (128, 255, 255), (192, 192, 255), (255, 255, 255)],  # Tech colors
    "component": [(192, 192, 192), (255, 255, 255), (128, 255, 255), (64, 192, 255)],  # Metallic
}

def add_glow(img, color, intensity=0.5):
    """Add a glowing effect to an image"""
    glow = Image.new('RGBA', img.size, (0, 0, 0, 0))
    draw = ImageDraw.Draw(glow)
    
    # Create glow layers
    for i in range(3, 0, -1):
        alpha = int(255 * intensity * (i / 3))
        glow_color = color + (alpha,)
        glow_layer = img.filter(ImageFilter.GaussianBlur(radius=i))
        
        # Blend with color
        colored = Image.new('RGBA', img.size, glow_color)
        glow = Image.alpha_composite(glow, Image.blend(glow_layer, colored, 0.5))
    
    return Image.alpha_composite(glow, img)

def create_circuit_pattern(size, color, density=0.3):
    """Create a circuit-like pattern"""
    img = Image.new('RGBA', size, (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Grid lines
    for i in range(0, size[0], 4):
        if random.random() < density:
            draw.line([(i, 0), (i, size[1])], fill=color + (64,), width=1)
    for i in range(0, size[1], 4):
        if random.random() < density:
            draw.line([(0, i), (size[0], i)], fill=color + (64,), width=1)
    
    # Nodes
    for x in range(2, size[0]-2, 4):
        for y in range(2, size[1]-2, 4):
            if random.random() < density/2:
                draw.ellipse([x-1, y-1, x+1, y+1], fill=color + (255,))
    
    return img

def create_energy_swirl(size, colors):
    """Create an energy swirl pattern"""
    img = Image.new('RGBA', size, (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    center = (size[0]//2, size[1]//2)
    for i in range(20):
        angle = (i / 20) * 2 * np.pi
        radius = (size[0]//3) * (0.5 + 0.5 * np.sin(angle * 3))
        x = int(center[0] + radius * np.cos(angle))
        y = int(center[1] + radius * np.sin(angle))
        
        color = random.choice(colors)
        alpha = 128 + int(127 * np.sin(angle * 5))
        draw.ellipse([x-2, y-2, x+2, y+2], fill=color + (alpha,))
    
    return img.filter(ImageFilter.GaussianBlur(radius=1))

def create_crystal_texture(size, colors):
    """Create a crystalline texture"""
    img = Image.new('RGBA', size, (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Background gradient
    for y in range(size[1]):
        ratio = y / size[1]
        color = tuple(int(c * (0.3 + 0.7 * ratio)) for c in colors[0])
        draw.line([(0, y), (size[0], y)], fill=color + (255,))
    
    # Crystal facets
    points = []
    for i in range(6):
        angle = (i / 6) * 2 * np.pi
        x = size[0]//2 + int(size[0]//3 * np.cos(angle))
        y = size[1]//2 + int(size[1]//3 * np.sin(angle))
        points.append((x, y))
    
    draw.polygon(points, fill=colors[1] + (200,), outline=colors[2] + (255,))
    
    # Inner shine
    inner_points = [(int(p[0]*0.7 + size[0]*0.15), int(p[1]*0.7 + size[1]*0.15)) for p in points[:3]]
    draw.polygon(inner_points, fill=colors[3] + (128,))
    
    return img

def create_block_texture(name, palette_key):
    """Create a block texture with advanced patterns"""
    img = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    colors = PALETTES.get(palette_key, PALETTES["component"])
    
    # Base color with gradient
    for y in range(16):
        shade = 0.7 + 0.3 * (y / 16)
        color = tuple(int(c * shade) for c in colors[0])
        draw.line([(0, y), (16, y)], fill=color + (255,))
    
    # Add patterns based on block type
    if "quantum" in name or "neural" in name:
        circuit = create_circuit_pattern((16, 16), colors[1], 0.5)
        img = Image.alpha_composite(img, circuit)
    
    if "compiler" in name or "interface" in name:
        # Tech interface pattern
        draw.rectangle([2, 2, 14, 14], outline=colors[2] + (255,))
        draw.rectangle([4, 4, 12, 12], outline=colors[3] + (128,))
        for i in range(6, 11, 2):
            draw.point((i, 8), fill=colors[3] + (255,))
    
    if "conduit" in name:
        # Energy flow pattern
        for i in range(0, 16, 3):
            alpha = 128 + int(127 * np.sin(i * 0.5))
            draw.line([(i, 0), (i, 16)], fill=colors[2] + (alpha,), width=2)
    
    if "stabilizer" in name:
        # Stabilizing rings
        draw.ellipse([3, 3, 13, 13], outline=colors[2] + (255,), width=2)
        draw.ellipse([5, 5, 11, 11], outline=colors[3] + (192,), width=1)
    
    # Edge highlights
    draw.line([(0, 0), (15, 0)], fill=colors[3] + (128,))
    draw.line([(0, 0), (0, 15)], fill=colors[3] + (128,))
    draw.line([(15, 1), (15, 15)], fill=(0, 0, 0, 128))
    draw.line([(1, 15), (15, 15)], fill=(0, 0, 0, 128))
    
    # Add subtle glow
    if "active" in name or "compiler" in name:
        img = add_glow(img, colors[1], 0.3)
    
    return img

def create_item_texture(name, palette_key):
    """Create an item texture with advanced effects"""
    img = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    colors = PALETTES.get(palette_key, PALETTES["component"])
    
    if "augment" in name:
        # Hexagonal augment shape
        points = []
        for i in range(6):
            angle = (i / 6) * 2 * np.pi - np.pi/6
            x = 8 + int(6 * np.cos(angle))
            y = 8 + int(6 * np.sin(angle))
            points.append((x, y))
        
        # Outer frame
        draw.polygon(points, fill=colors[0] + (255,), outline=colors[2] + (255,))
        
        # Inner tech pattern
        inner_points = [(int(p[0]*0.6 + 8*0.4), int(p[1]*0.6 + 8*0.4)) for p in points]
        draw.polygon(inner_points, fill=colors[1] + (200,))
        
        # Center gem
        draw.ellipse([6, 6, 10, 10], fill=colors[3] + (255,))
        draw.ellipse([7, 7, 9, 9], fill=(255, 255, 255, 200))
    
    elif "scanner" in name or "multitool" in name:
        # Tool shape
        draw.rectangle([4, 2, 12, 14], fill=colors[0] + (255,), outline=colors[2] + (255,))
        draw.rectangle([6, 1, 10, 3], fill=colors[1] + (255,))  # Handle
        
        # Screen/display
        draw.rectangle([5, 5, 11, 10], fill=(0, 0, 0, 255))
        for i in range(6, 10):
            color = random.choice(colors)
            draw.line([(6, i), (10, i)], fill=color + (192,))
    
    elif "core" in name or "matrix" in name:
        # Crystalline core
        img = create_crystal_texture((16, 16), colors)
    
    elif "memory" in name:
        # Data chip design
        draw.rectangle([3, 4, 13, 12], fill=colors[0] + (255,), outline=colors[2] + (255,))
        
        # Circuit traces
        for y in range(5, 12, 2):
            draw.line([(4, y), (12, y)], fill=colors[1] + (192,))
        
        # Contact points
        for x in range(5, 12, 3):
            draw.rectangle([x, 12, x+1, 14], fill=colors[3] + (255,))
    
    elif "codex" in name:
        # Book shape
        draw.polygon([(3, 2), (13, 2), (13, 14), (3, 14), (3, 2)], fill=colors[0] + (255,))
        draw.line([(3, 2), (3, 14)], fill=colors[2] + (255,), width=2)  # Spine
        
        # Pages
        for x in range(5, 12, 2):
            draw.line([(x, 4), (x, 12)], fill=(255, 255, 255, 128))
        
        # Symbol
        draw.ellipse([7, 6, 11, 10], outline=colors[3] + (255,))
    
    else:
        # Generic item
        draw.ellipse([3, 3, 13, 13], fill=colors[0] + (255,), outline=colors[2] + (255,))
        swirl = create_energy_swirl((16, 16), colors[1:])
        img = Image.alpha_composite(img, swirl)
    
    # Add shine effect
    if "quantum" in name or "neural" in name or "augment" in name:
        img = add_glow(img, colors[1], 0.2)
    
    return img

def create_particle_texture(name):
    """Create particle textures"""
    img = Image.new('RGBA', (8, 8), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    if "quantum" in name:
        # Quantum particle - star burst
        draw.line([(4, 0), (4, 8)], fill=(0, 255, 255, 255))
        draw.line([(0, 4), (8, 4)], fill=(0, 255, 255, 255))
        draw.line([(1, 1), (7, 7)], fill=(0, 192, 255, 192))
        draw.line([(7, 1), (1, 7)], fill=(0, 192, 255, 192))
        draw.point((4, 4), fill=(255, 255, 255, 255))
    
    elif "neural" in name:
        # Neural spark - electric
        points = [(2, 1), (6, 2), (3, 4), (5, 6), (2, 7)]
        for i in range(len(points)-1):
            draw.line([points[i], points[i+1]], fill=(255, 128, 255, 255))
        draw.ellipse([3, 3, 5, 5], fill=(255, 255, 255, 255))
    
    elif "holographic" in name:
        # Holographic - grid
        for x in range(0, 8, 2):
            for y in range(0, 8, 2):
                alpha = 128 + int(127 * ((x + y) / 14))
                draw.point((x, y), fill=(128, 255, 255, alpha))
    
    return img

def create_gui_texture(name, size=(256, 256)):
    """Create GUI background textures"""
    img = Image.new('RGBA', size, (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Dark techno background
    for y in range(size[1]):
        gray = 20 + int(10 * np.sin(y * 0.1))
        draw.line([(0, y), (size[0], y)], fill=(gray, gray, gray + 5, 255))
    
    # Tech border
    border_color = (0, 128, 192, 255)
    draw.rectangle([0, 0, size[0]-1, size[1]-1], outline=border_color, width=3)
    draw.rectangle([3, 3, size[0]-4, size[1]-4], outline=border_color[:3] + (128,), width=1)
    
    # Corner details
    for corner in [(0, 0), (size[0]-16, 0), (0, size[1]-16), (size[0]-16, size[1]-16)]:
        x, y = corner
        draw.rectangle([x+1, y+1, x+15, y+15], outline=(0, 255, 255, 128))
        draw.line([(x+8, y+3), (x+8, y+13)], fill=(0, 255, 255, 255))
        draw.line([(x+3, y+8), (x+13, y+8)], fill=(0, 255, 255, 255))
    
    # Inventory slots (if applicable)
    if "compiler" in name:
        # Code editor area
        draw.rectangle([10, 30, size[0]-10, size[1]-60], fill=(0, 0, 0, 200))
        
        # Line numbers
        for i in range(10):
            y = 35 + i * 15
            draw.text((15, y), str(i+1), fill=(0, 128, 128, 255))
    
    return img

def main():
    """Generate all textures"""
    os.makedirs(f"{TEXTURE_BASE}/block", exist_ok=True)
    os.makedirs(f"{TEXTURE_BASE}/item", exist_ok=True)
    os.makedirs(f"{TEXTURE_BASE}/particle", exist_ok=True)
    os.makedirs(f"{TEXTURE_BASE}/gui", exist_ok=True)
    
    # Block textures
    blocks = {
        "neural_interface": "neural",
        "quantum_harvester": "quantum",
        "quantum_harvester_active": "quantum",
        "quantum_conduit": "quantum",
        "augmentation_table": "augment",
        "reality_compiler": "reality",
        "temporal_stabilizer": "temporal",
        "dimension_compiler_core": "dimension",
        "dimension_frame": "dimension",
        "dimension_stabilizer": "dimension",
        "dimensional_rift": "dimension",
    }
    
    for name, palette in blocks.items():
        texture = create_block_texture(name, palette)
        texture.save(f"{TEXTURE_BASE}/block/{name}.png")
        print(f"Created block texture: {name}")
    
    # Item textures
    items = {
        "quantum_scanner": "quantum",
        "nano_multitool": "component",
        "quantum_entangler": "quantum",
        "probability_manipulator": "reality",
        "dimension_stabilizer": "dimension",
        "quantum_core": "quantum",
        "neural_matrix": "neural",
        "memory_fragment": "neural",
        "rpl_codex": "reality",
        "cortex_processor": "augment",
        "optic_enhancer": "augment",
        "dermal_plating": "augment",
        "neural_link": "augment",
        "phase_shift": "augment",
        "quantum_core_augment": "augment",
        "temporal_sync": "augment",
        "neural_resonator": "augment",
        "synaptic_booster": "augment",
        "quantum_tunneler": "augment",
        "gravity_anchor": "augment",
        "reactive_shield": "augment",
    }
    
    for name, palette in items.items():
        texture = create_item_texture(name, palette)
        texture.save(f"{TEXTURE_BASE}/item/{name}.png")
        print(f"Created item texture: {name}")
    
    # Particle textures
    particles = ["quantum_particle", "neural_spark", "holographic_particle"]
    for name in particles:
        texture = create_particle_texture(name)
        texture = texture.resize((16, 16), Image.Resampling.NEAREST)  # Scale up for visibility
        texture.save(f"{TEXTURE_BASE}/particle/{name}.png")
        print(f"Created particle texture: {name}")
    
    # GUI textures (if not already exists)
    if not os.path.exists(f"{TEXTURE_BASE}/gui/dimension_compiler.png"):
        gui_texture = create_gui_texture("dimension_compiler")
        gui_texture.save(f"{TEXTURE_BASE}/gui/dimension_compiler.png")
        print("Created GUI texture: dimension_compiler")
    
    print("\nAll textures generated successfully!")
    print("The textures have a cohesive cyber-magic aesthetic with:")
    print("- Glowing effects for energy items")
    print("- Circuit patterns for tech blocks")
    print("- Crystalline designs for cores")
    print("- Hexagonal shapes for augments")
    print("- Consistent color palettes per system")

if __name__ == "__main__":
    main()
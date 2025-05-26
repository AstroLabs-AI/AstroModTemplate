#!/usr/bin/env python3
"""
Generate placeholder 16x16 PNG textures for ArcaneCodex mod
"""

import os
from PIL import Image, ImageDraw
import random

# Base path for textures
TEXTURE_BASE = "src/main/resources/assets/arcanecodex/textures"

# Define all required textures based on model files and registries
TEXTURES = {
    "block": [
        # From model files
        "quantum_harvester_top",
        "quantum_harvester_top_active", 
        "quantum_harvester_side",
        "quantum_harvester_side_active",
        "machine_bottom",
        "dimension_compiler_core",
        "dimension_frame",
        "dimension_stabilizer",
        
        # From block registry
        "neural_interface",
        "neural_interface_active",
        "quantum_conduit",
        "augmentation_table",
        "augmentation_table_active",
        "reality_compiler",
        "reality_compiler_active",
        "temporal_stabilizer",
        "temporal_stabilizer_active",
        "dimensional_rift"
    ],
    "item": [
        # Tools
        "quantum_scanner",
        "nano_multitool",
        "quantum_entangler",
        "probability_manipulator",
        "dimension_stabilizer",
        
        # Components
        "memory_fragment",
        "quantum_core",
        "neural_matrix",
        
        # Augments
        "cortex_processor",
        "optic_enhancer",
        "neural_link",
        "temporal_sync",
        "quantum_core_augment",
        "phase_shift",
        "dermal_plating",
        "synaptic_booster",
        "reactive_shield",
        "neural_resonator",
        "gravity_anchor",
        "quantum_tunneler",
        
        # RPL Codex
        "rpl_codex_basics",
        "rpl_codex_quantum",
        "rpl_codex_temporal"
    ],
    "particle": [
        "quantum_energy",
        "neural_spark",
        "holographic",
        "reality_glitch"
    ]
}

# Color schemes for different texture types
COLOR_SCHEMES = {
    "quantum": [(0, 150, 255), (0, 200, 255), (100, 220, 255)],  # Blue/cyan
    "neural": [(255, 150, 0), (255, 200, 50), (255, 220, 100)],  # Orange/yellow
    "dimension": [(150, 0, 255), (200, 50, 255), (220, 100, 255)],  # Purple
    "temporal": [(0, 255, 150), (50, 255, 200), (100, 255, 220)],  # Green/cyan
    "reality": [(255, 0, 150), (255, 50, 200), (255, 100, 220)],  # Pink/magenta
    "augment": [(200, 200, 200), (150, 150, 150), (100, 100, 100)],  # Gray
    "machine": [(100, 100, 100), (150, 150, 150), (200, 200, 200)],  # Gray gradient
    "rpl": [(255, 255, 0), (255, 200, 0), (255, 150, 0)],  # Yellow/gold
    "default": [(128, 128, 128), (192, 192, 192), (64, 64, 64)]  # Default gray
}

def get_color_scheme(texture_name):
    """Determine color scheme based on texture name"""
    name_lower = texture_name.lower()
    
    if "quantum" in name_lower:
        return COLOR_SCHEMES["quantum"]
    elif "neural" in name_lower:
        return COLOR_SCHEMES["neural"]
    elif "dimension" in name_lower:
        return COLOR_SCHEMES["dimension"]
    elif "temporal" in name_lower:
        return COLOR_SCHEMES["temporal"]
    elif "reality" in name_lower:
        return COLOR_SCHEMES["reality"]
    elif any(aug in name_lower for aug in ["processor", "enhancer", "link", "sync", "shift", "plating", "booster", "shield", "resonator", "anchor", "tunneler"]):
        return COLOR_SCHEMES["augment"]
    elif any(machine in name_lower for machine in ["harvester", "interface", "conduit", "table", "compiler", "stabilizer"]):
        return COLOR_SCHEMES["machine"]
    elif "rpl" in name_lower:
        return COLOR_SCHEMES["rpl"]
    else:
        return COLOR_SCHEMES["default"]

def create_placeholder_texture(name, category):
    """Create a 16x16 placeholder texture with appropriate styling"""
    img = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    colors = get_color_scheme(name)
    
    # Different patterns based on category
    if category == "block":
        # Fill with base color
        draw.rectangle([0, 0, 15, 15], fill=colors[0])
        # Add border
        draw.rectangle([0, 0, 15, 15], outline=colors[2], width=1)
        # Add some detail in center
        draw.rectangle([6, 6, 9, 9], fill=colors[1])
        
    elif category == "item":
        # Create a simple icon pattern
        draw.rectangle([4, 4, 11, 11], fill=colors[0])
        draw.rectangle([5, 5, 10, 10], fill=colors[1])
        draw.point((7, 7), fill=colors[2])
        draw.point((8, 8), fill=colors[2])
        
    elif category == "particle":
        # Create a particle pattern (scattered pixels)
        for _ in range(20):
            x = random.randint(2, 13)
            y = random.randint(2, 13)
            color = random.choice(colors)
            # Add transparency variation for particles
            color_with_alpha = color + (random.randint(100, 255),)
            draw.point((x, y), fill=color_with_alpha)
    
    return img

def main():
    """Generate all placeholder textures"""
    print("Generating placeholder textures for ArcaneCodex...")
    
    # Create directories
    for category in TEXTURES:
        dir_path = os.path.join(TEXTURE_BASE, category)
        os.makedirs(dir_path, exist_ok=True)
        print(f"\nCreating {category} textures in {dir_path}:")
        
        # Generate textures for this category
        for texture_name in TEXTURES[category]:
            texture_path = os.path.join(dir_path, f"{texture_name}.png")
            
            # Create the texture
            img = create_placeholder_texture(texture_name, category)
            img.save(texture_path)
            print(f"  ✓ {texture_name}.png")
    
    # Also create the GUI texture that exists
    gui_dir = os.path.join(TEXTURE_BASE, "gui")
    os.makedirs(gui_dir, exist_ok=True)
    
    # The dimension_compiler.png is a GUI texture (256x256)
    gui_img = Image.new('RGBA', (256, 256), (40, 40, 40, 255))
    gui_draw = ImageDraw.Draw(gui_img)
    
    # Add some basic GUI elements
    # Title area
    gui_draw.rectangle([8, 8, 248, 30], fill=(60, 60, 60))
    # Main area
    gui_draw.rectangle([8, 35, 248, 200], fill=(50, 50, 50))
    # Button area
    gui_draw.rectangle([90, 210, 166, 230], fill=(80, 80, 80))
    
    gui_img.save(os.path.join(gui_dir, "dimension_compiler.png"))
    print(f"\n✓ Created GUI texture: dimension_compiler.png")
    
    print("\nAll placeholder textures generated successfully!")
    print("\nNote: These are basic placeholders. You'll want to replace them with proper textures.")

if __name__ == "__main__":
    # Check if PIL/Pillow is installed
    try:
        from PIL import Image, ImageDraw
    except ImportError:
        print("Error: Pillow library is required. Install it with: pip install Pillow")
        exit(1)
    
    main()
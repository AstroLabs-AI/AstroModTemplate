#!/usr/bin/env python3

import os
from PIL import Image
import json
import glob

# Base directory for textures
TEXTURE_BASE = "src/main/resources/assets/arcanecodex/textures"

# Colors for different types
COLORS = {
    "block": (128, 128, 255),     # Blue for blocks
    "item": (128, 255, 128),      # Green for items  
    "particle": (255, 128, 128),   # Red for particles
    "gui": (255, 255, 128),        # Yellow for GUI
}

def create_placeholder_texture(path, color=(128, 128, 128), size=(16, 16)):
    """Create a simple colored placeholder texture."""
    os.makedirs(os.path.dirname(path), exist_ok=True)
    
    img = Image.new('RGBA', size, color + (255,))
    
    # Add a simple pattern to make it more visible
    for i in range(0, size[0], 4):
        for j in range(0, size[1], 4):
            if (i + j) % 8 == 0:
                img.putpixel((i, j), (255, 255, 255, 255))
    
    img.save(path)
    print(f"Created: {path}")

def extract_textures_from_json(json_file):
    """Extract texture references from JSON model files."""
    textures = []
    try:
        with open(json_file, 'r') as f:
            data = json.load(f)
            if 'textures' in data:
                for key, value in data['textures'].items():
                    if value.startswith('arcanecodex:'):
                        texture_path = value.replace('arcanecodex:', '') + '.png'
                        textures.append(texture_path)
    except:
        pass
    return textures

# Collect all texture references
all_textures = set()

# From model files
for json_file in glob.glob('src/main/resources/assets/arcanecodex/models/**/*.json', recursive=True):
    all_textures.update(extract_textures_from_json(json_file))

# Add known textures that might not be in models
known_textures = [
    # Blocks
    "block/neural_interface",
    "block/quantum_harvester", 
    "block/quantum_harvester_active",
    "block/quantum_conduit",
    "block/augmentation_table",
    "block/reality_compiler",
    "block/temporal_stabilizer",
    "block/dimension_compiler_core",
    "block/dimension_frame",
    "block/dimension_stabilizer",
    "block/dimensional_rift",
    
    # Items
    "item/quantum_scanner",
    "item/nano_multitool",
    "item/quantum_entangler",
    "item/probability_manipulator",
    "item/dimension_stabilizer",
    "item/quantum_core",
    "item/neural_matrix",
    "item/memory_fragment",
    "item/rpl_codex",
    
    # Augments
    "item/cortex_processor",
    "item/optic_enhancer",
    "item/dermal_plating",
    "item/neural_link",
    "item/phase_shift",
    "item/quantum_core_augment",
    "item/temporal_sync",
    "item/neural_resonator",
    "item/synaptic_booster",
    "item/quantum_tunneler",
    "item/gravity_anchor",
    "item/reactive_shield",
    
    # Particles
    "particle/quantum_particle",
    "particle/neural_spark",
    "particle/holographic_particle",
]

all_textures.update(known_textures)

# Create textures
for texture in all_textures:
    texture_path = os.path.join(TEXTURE_BASE, texture + '.png')
    
    # Skip if already exists
    if os.path.exists(texture_path):
        continue
    
    # Determine color based on type
    if texture.startswith('block/'):
        color = COLORS['block']
    elif texture.startswith('item/'):
        color = COLORS['item']
    elif texture.startswith('particle/'):
        color = COLORS['particle']
    elif texture.startswith('gui/'):
        color = COLORS['gui']
    else:
        color = (128, 128, 128)
    
    create_placeholder_texture(texture_path, color)

print(f"\nTotal textures created: {len(all_textures)}")
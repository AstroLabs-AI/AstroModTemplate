#!/usr/bin/env python3
"""
Generate simple placeholder 16x16 PNG textures for ArcaneCodex mod
This version uses no external dependencies - creates minimal valid PNG files
"""

import os
import struct
import zlib

# Base path for textures
TEXTURE_BASE = "src/main/resources/assets/arcanecodex/textures"

# Define all required textures
TEXTURES = {
    "block": [
        "quantum_harvester_top",
        "quantum_harvester_top_active", 
        "quantum_harvester_side",
        "quantum_harvester_side_active",
        "machine_bottom",
        "dimension_compiler_core",
        "dimension_frame",
        "dimension_stabilizer",
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
        "quantum_scanner",
        "nano_multitool",
        "quantum_entangler",
        "probability_manipulator",
        "dimension_stabilizer",
        "memory_fragment",
        "quantum_core",
        "neural_matrix",
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

def create_simple_png(width, height, color):
    """Create a simple solid color PNG file"""
    
    def png_pack(data):
        return struct.pack('>I', len(data)) + data + struct.pack('>I', zlib.crc32(data))
    
    # PNG header
    png_data = b'\x89PNG\r\n\x1a\n'
    
    # IHDR chunk
    ihdr_data = struct.pack('>IIBBBBB', width, height, 8, 2, 0, 0, 0)
    ihdr = b'IHDR' + ihdr_data
    png_data += png_pack(ihdr)
    
    # IDAT chunk (image data)
    raw_data = b''
    for y in range(height):
        raw_data += b'\x00'  # filter type
        for x in range(width):
            raw_data += struct.pack('BBB', color[0], color[1], color[2])
    
    compressed = zlib.compress(raw_data)
    idat = b'IDAT' + compressed
    png_data += png_pack(idat)
    
    # IEND chunk
    iend = b'IEND'
    png_data += png_pack(iend)
    
    return png_data

def get_color_for_texture(name):
    """Get a color based on texture name"""
    name_lower = name.lower()
    
    if "quantum" in name_lower:
        return (0, 150, 255)  # Blue
    elif "neural" in name_lower:
        return (255, 150, 0)  # Orange
    elif "dimension" in name_lower:
        return (150, 0, 255)  # Purple
    elif "temporal" in name_lower:
        return (0, 255, 150)  # Green
    elif "reality" in name_lower:
        return (255, 0, 150)  # Pink
    elif "active" in name_lower:
        return (255, 255, 100)  # Bright yellow for active states
    elif "machine" in name_lower or "interface" in name_lower:
        return (128, 128, 128)  # Gray
    else:
        return (100, 100, 100)  # Dark gray default

def main():
    """Generate all placeholder textures"""
    print("Generating simple placeholder textures for ArcaneCodex...")
    
    # Create directories and generate textures
    for category, texture_list in TEXTURES.items():
        dir_path = os.path.join(TEXTURE_BASE, category)
        os.makedirs(dir_path, exist_ok=True)
        print(f"\nCreating {category} textures in {dir_path}:")
        
        for texture_name in texture_list:
            texture_path = os.path.join(dir_path, f"{texture_name}.png")
            
            # Get color for this texture
            color = get_color_for_texture(texture_name)
            
            # Create 16x16 PNG
            png_data = create_simple_png(16, 16, color)
            
            # Write to file
            with open(texture_path, 'wb') as f:
                f.write(png_data)
            
            print(f"  ✓ {texture_name}.png (RGB: {color})")
    
    # Create GUI texture directory
    gui_dir = os.path.join(TEXTURE_BASE, "gui")
    os.makedirs(gui_dir, exist_ok=True)
    
    # Create a simple 256x256 GUI texture
    gui_png = create_simple_png(256, 256, (50, 50, 50))
    with open(os.path.join(gui_dir, "dimension_compiler.png"), 'wb') as f:
        f.write(gui_png)
    print(f"\n✓ Created GUI texture: dimension_compiler.png (256x256)")
    
    print("\nAll placeholder textures generated successfully!")
    print("\nNote: These are very basic solid-color placeholders.")
    print("You'll want to replace them with proper textures for the final mod.")

if __name__ == "__main__":
    main()
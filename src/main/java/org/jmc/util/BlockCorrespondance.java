package org.jmc.util;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Paul on 22/04/2016.
 */
public class BlockCorrespondance {
	public static class Block{
		public final String tint;
		public final String fileName;
		public final String mtlName;
		public final boolean isSquare;

		public Block(String fileName, String mtlName, String tint, boolean isSquare) {
			this.fileName = fileName;
			this.mtlName = mtlName;
			this.tint = tint;
			this.isSquare = isSquare;

		}

		public Block(String fileName, String mtlName, String tint) {
			this(fileName, mtlName, tint, true);
		}

		public Block(String fileName, String mtlName) {
			this(fileName, mtlName, "", true);
		}

		public boolean equals(Block block){
			if(block.fileName.equals(this.fileName)){
				return true;
			}
			return false;
		}
	}

	private static Set<Block> blocks = new HashSet<>();
	static {
		/********** Block **********/
		blocks.add(new Block("command_side.png", "command_block.png"));
		blocks.add(new Block("beacon_beam.png", "beacon.png"));
		blocks.add(new Block("soulsand.png", "soul_sand.png"));
		blocks.add(new Block("sandstone_side.png", "sandstone_normal.png"));
		blocks.add(new Block("sandstone_side_carved.png", "sandstone_carved.png"));
		blocks.add(new Block("sandstone_side_smooth.png", "sandstone_smooth.png"));
		blocks.add(new Block("lapis_lazuli_ore.png", "lapis_ore.png"));
		blocks.add(new Block("lapis_lazuli_block.png", "lapis_block.png"));
		blocks.add(new Block("slime.png", "slime.png"));
		blocks.add(new Block("gold_block.png", "gold_block.png"));
		blocks.add(new Block("glowstone.png", "glowstone.png"));

		blocks.add(new Block("quartz_side.png", "quartz_block_side.png"));
		blocks.add(new Block("quartz_top.png", "quartz_block_top.png"));
		blocks.add(new Block("nether_quartz_ore.png", "quartz_ore.png"));
		blocks.add(new Block("quartz_bottom.png", "quartz_block_bottom.png"));
		blocks.add(new Block("quartz_side_lines.png", "quartz_block_lines.png"));
		blocks.add(new Block("quartz_side_chiseled.png", "quartz_block_chiseled.png"));
		blocks.add(new Block("quartz_top_chiseled.png", "quartz_block_chiseled_top.png"));
		blocks.add(new Block("endportal_frame_side.png", "endframe_side.png"));
		blocks.add(new Block("endportal_frame_top.png", "endframe_top.png"));
		blocks.add(new Block("spawner.png", "mob_spawner.png"));

		/********** Clay **********/
		blocks.add(new Block("stained_clay_magenta.png", "hardened_clay_stained_magenta.png"));
		blocks.add(new Block("stained_clay_yellow.png", "hardened_clay_stained_yellow.png"));
		blocks.add(new Block("stained_clay_orange.png", "hardened_clay_stained_orange.png"));
		blocks.add(new Block("stained_clay_silver.png", "hardened_clay_stained_silver.png"));
		blocks.add(new Block("stained_clay_white.png", "hardened_clay_stained_white.png"));
		blocks.add(new Block("stained_clay_red.png", "hardened_clay_stained_red.png"));
		blocks.add(new Block("stained_clay_pink.png", "hardened_clay_stained_pink.png"));
		blocks.add(new Block("stained_clay_purple.png", "hardened_clay_stained_purple.png"));
		blocks.add(new Block("stained_clay_black.png", "hardened_clay_stained_black.png"));
		blocks.add(new Block("stained_clay_blue.png", "hardened_clay_stained_blue.png"));
		blocks.add(new Block("stained_clay_brown.png", "hardened_clay_stained_brown.png"));
		blocks.add(new Block("stained_clay_cyan.png", "hardened_clay_stained_cyan.png"));
		blocks.add(new Block("stained_clay_gray.png", "clay.png"));
		blocks.add(new Block("stained_clay_green.png", "hardened_clay_stained_green.png"));
		blocks.add(new Block("stained_clay_light_gray.png", "hardened_clay_stained_silver.png"));
		blocks.add(new Block("stained_clay_light_blue.png", "hardened_clay_stained_light_blue.png"));
		blocks.add(new Block("stained_clay_lime.png", "hardened_clay_stained_lime.png"));
		blocks.add(new Block("stained_clay_dark_gray.png", "hardened_clay_stained_gray.png"));

		/********** Log Wood **********/
		blocks.add(new Block("log_oak_side.png", "log_oak.png"));
		blocks.add(new Block("log_acacia_side.png", "log_acacia.png"));
		blocks.add(new Block("log_jungle_side.png", "log_jungle.png"));
		blocks.add(new Block("log_spruce_side.png", "log_spruce.png"));
		blocks.add(new Block("log_birch_side.png", "log_birch.png"));
		blocks.add(new Block("log_big_oak_side.png", "log_big_oak.png"));


		/********** wood plank **********/
		blocks.add(new Block("plank_oak.png", "planks_oak.png"));
		blocks.add(new Block("plank_acacia.png", "planks_acacia.png"));
		blocks.add(new Block("plank_jungle.png", "planks_jungle.png"));
		blocks.add(new Block("plank_spruce.png", "planks_spruce.png"));
		blocks.add(new Block("plank_birch.png", "planks_birch.png"));
		blocks.add(new Block("plank_big_oak.png", "planks_big_oak.png"));

		/********** Stone **********/
		blocks.add(new Block("stone_brick_mossy.png", "stonebrick_mossy.png"));
		blocks.add(new Block("stone_brick.png", "stonebrick.png"));
		blocks.add(new Block("stone_brick_circle.png", "stonebrick_carved.png"));
		blocks.add(new Block("stone_brick_cracked.png", "stonebrick_cracked.png"));
		blocks.add(new Block("endstone.png", "end_stone.png"));

		/********** carpet **********/
		blocks.add(new Block("carpet_black.png", "wool_colored_black.png"));
		blocks.add(new Block("carpet_blue.png", "wool_colored_blue.png"));
		blocks.add(new Block("carpet_brown.png", "wool_colored_brown.png"));
		blocks.add(new Block("carpet_cyan.png", "wool_colored_cyan.png"));
		blocks.add(new Block("carpet_dark_gray.png", "wool_colored_gray.png"));
		blocks.add(new Block("carpet_green.png", "wool_colored_green.png"));
		blocks.add(new Block("carpet_light_blue.png", "wool_colored_light_blue.png"));
		blocks.add(new Block("carpet_lime.png", "wool_colored_lime.png"));
		blocks.add(new Block("carpet_magenta.png", "wool_colored_magenta.png"));
		blocks.add(new Block("carpet_orange.png", "wool_colored_orange.png"));
		blocks.add(new Block("carpet_pink.png", "wool_colored_pink.png"));
		blocks.add(new Block("carpet_purple.png", "wool_colored_purple.png"));
		blocks.add(new Block("carpet_red.png", "wool_colored_red.png"));
		blocks.add(new Block("carpet_light_gray.png", "wool_colored_silver.png"));
		blocks.add(new Block("carpet_white.png", "wool_colored_white.png"));
		blocks.add(new Block("carpet_yellow.png", "wool_colored_yellow.png"));


		/********** Wool **********/
		blocks.add(new Block("wool_white.png", "wool_colored_white.png"));
		blocks.add(new Block("wool_yellow.png", "wool_colored_yellow.png"));
		blocks.add(new Block("wool_purple.png", "wool_colored_purple.png"));
		blocks.add(new Block("wool_red.png", "wool_colored_red.png"));
		blocks.add(new Block("wool_silver.png", "wool_colored_silver.png"));
		blocks.add(new Block("wool_orange.png", "wool_colored_orange.png"));
		blocks.add(new Block("wool_pink.png", "wool_colored_pink.png"));
		blocks.add(new Block("wool_light_blue.png", "wool_colored_light_blue.png"));
		blocks.add(new Block("wool_lime.png", "wool_colored_lime.png"));
		blocks.add(new Block("wool_magenta.png", "wool_colored_magenta.png"));
		blocks.add(new Block("wool_gray.png", "wool_colored_gray.png"));
		blocks.add(new Block("wool_green.png", "wool_colored_green.png"));
		blocks.add(new Block("wool_blue.png", "wool_colored_blue.png"));
		blocks.add(new Block("wool_brown.png", "wool_colored_brown.png"));
		blocks.add(new Block("wool_cyan.png", "wool_colored_cyan.png"));
		blocks.add(new Block("wool_black.png", "wool_colored_black.png"));
		blocks.add(new Block("wool_light_gray.png", "wool_colored_silver.png"));
		blocks.add(new Block("wool_dark_gray.png", "wool_colored_gray.png"));

		/********** Glass  **********/
		blocks.add(new Block("glass_dark_gray.png", "glass_gray.png"));
		blocks.add(new Block("glass_light_gray.png", "glass_silver.png"));

		blocks.add(new Block("glass_pane_side_cyan.png", "glass_pane_top_cyan.png"));
		blocks.add(new Block("glass_pane_side_orange.png", "glass_pane_top_orange.png"));
		blocks.add(new Block("glass_pane_side_green.png", "glass_pane_top_green.png"));
		blocks.add(new Block("glass_pane_side_black.png", "glass_pane_top_black.png"));
		blocks.add(new Block("glass_pane_side_white.png", "glass_pane_top_white.png"));
		blocks.add(new Block("glass_pane_side_blue.png", "glass_pane_top_blue.png"));
		blocks.add(new Block("glass_pane_side_lime.png", "glass_pane_top_lime.png"));
		blocks.add(new Block("glass_pane_side_red.png", "glass_pane_top_red.png"));
		blocks.add(new Block("glass_pane_side.png", "glass_pane_top.png"));
		blocks.add(new Block("glass_pane_side_magenta.png", "glass_pane_top_magenta.png"));
		blocks.add(new Block("glass_pane_side_pink.png", "glass_pane_top_pink.png"));
		blocks.add(new Block("glass_pane_side_light_gray.png", "glass_pane_top_silver.png"));
		blocks.add(new Block("glass_pane_side_purple.png", "glass_pane_top_purple.png"));
		blocks.add(new Block("glass_pane_side_brown.png", "glass_pane_top_brown.png"));
		blocks.add(new Block("glass_pane_side_light_blue.png", "glass_pane_top_light_blue.png"));
		blocks.add(new Block("glass_pane_side_dark_gray.png", "glass_pane_top_gray.png"));
		blocks.add(new Block("glass_pane_side_yellow.png", "glass_pane_top_yellow.png"));

		/********** Carpets  **********/
		blocks.add(new Block("carpet_black.png", "wool_colored_black.png"));
		blocks.add(new Block("carpet_blue.png", "wool_colored_blue.png"));
		blocks.add(new Block("carpet_brown.png", "wool_colored_brown.png"));
		blocks.add(new Block("carpet_cyan.png", "wool_colored_cyan.png"));
		blocks.add(new Block("carpet_dark_gray.png", "wool_colored_gray.png"));
		blocks.add(new Block("carpet_green.png", "wool_colored_green.png"));
		blocks.add(new Block("carpet_light_blue.png", "wool_colored_light_blue.png"));
		blocks.add(new Block("carpet_lime.png", "wool_colored_lime.png"));
		blocks.add(new Block("carpet_magenta.png", "wool_colored_magenta.png"));
		blocks.add(new Block("carpet_orange.png", "wool_colored_orange.png"));
		blocks.add(new Block("carpet_pink.png", "wool_colored_pink.png"));
		blocks.add(new Block("carpet_purple.png", "wool_colored_purple.png"));
		blocks.add(new Block("carpet_red.png", "wool_colored_red.png"));
		blocks.add(new Block("carpet_light_gray.png", "wool_colored_silver.png"));
		blocks.add(new Block("carpet_white.png", "wool_colored_white.png"));
		blocks.add(new Block("carpet_yellow.png", "wool_colored_yellow.png"));

		/********* Grass *********/
		blocks.add(new Block("dirt_grass_side.png", "grass_side.png"));
		blocks.add(new Block("dirt_grass_top.png", "grass_top.png", "A2FF65"));
		blocks.add(new Block("dirt_grass_top-desert.png", "grass_top.png", "D1FEB3"));
		blocks.add(new Block("dirt_grass_top-forest.png", "grass_top.png", "72B247"));
		blocks.add(new Block("dirt_mycelium_top.png", "mycelium_top.png"));
		blocks.add(new Block("dirt_mycelium_side.png", "mycelium_side.png"));
		blocks.add(new Block("dirt_snow_side.png", "grass_side_snowed.png"));


		/********** Door **********/
		blocks.add(new Block("door_wood_top.png", "door_wood_upper.png"));
		blocks.add(new Block("door_wood_bottom.png", "door_wood_lower.png"));
		blocks.add(new Block("door_iron_top.png", "door_iron_upper.png"));
		blocks.add(new Block("door_iron_bottom.png", "door_iron_lower.png"));
		blocks.add(new Block("hatch.png", "trapdoor.png"));
		blocks.add(new Block("tripwire.png", "trip_wire.png"));
		blocks.add(new Block("tripwire_hook.png", "trip_wire_source.png"));


		/********** Plants **********/
		blocks.add(new Block("mushroom_red_cap.png", "mushroom_block_skin_red.png"));
		blocks.add(new Block("pumpkin_front_lit.png", "pumpkin_face_on.png"));
		blocks.add(new Block("pumpkin_stem_1.png", "pumpkin_stem_disconnected.png", "DBC21A"));
		blocks.add(new Block("pumpkin_stem_2.png", "pumpkin_stem_connected.png", "DBC21A"));
		blocks.add(new Block("pumpkin_front.png", "pumpkin_face_off.png"));
		blocks.add(new Block("tall_grass.png", "tallgrass.png", "47B418"));
		blocks.add(new Block("double_plant_grass_bottom.png", "double_plant_grass_bottom.png", "47B418"));
		blocks.add(new Block("double_plant_grass_top.png", "double_plant_grass_top.png", "47B418"));
		blocks.add(new Block("fern.png", "fern.png", "47B418"));
		blocks.add(new Block("double_plant_fern_bottom.png", "double_plant_fern_bottom.png", "47B418"));
		blocks.add(new Block("double_plant_fern_top.png", "double_plant_fern_top.png", "47B418"));
		blocks.add(new Block("vines.png", "vine.png", "47B418"));
		blocks.add(new Block("lilypad.png", "waterlily.png", "47B418"));
		blocks.add(new Block("crops_1.png", "wheat_stage_0.png"));
		blocks.add(new Block("crops_2.png", "wheat_stage_1.png"));
		blocks.add(new Block("crops_3.png", "wheat_stage_2.png"));
		blocks.add(new Block("crops_4.png", "wheat_stage_3.png"));
		blocks.add(new Block("crops_5.png", "wheat_stage_4.png"));
		blocks.add(new Block("crops_6.png", "wheat_stage_5.png"));
		blocks.add(new Block("crops_7.png", "wheat_stage_6.png"));
		blocks.add(new Block("crops_8.png", "wheat_stage_7.png"));
		blocks.add(new Block("netherwart_1.png", "nether_wart_stage_0.png"));
		blocks.add(new Block("netherwart_2.png", "nether_wart_stage_1.png"));
		blocks.add(new Block("netherwart_3.png", "nether_wart_stage_2.png"));
		blocks.add(new Block("carrot_crop_1.png", "carrots_stage_0.png"));
		blocks.add(new Block("carrot_crop_2.png", "carrots_stage_1.png"));
		blocks.add(new Block("carrot_crop_3.png", "carrots_stage_2.png"));
		blocks.add(new Block("carrot_crop_4.png", "carrots_stage_3.png"));
		blocks.add(new Block("cocoa_plant_1.png", "cocoa_stage_0.png"));
		blocks.add(new Block("cocoa_plant_2.png", "cocoa_stage_1.png"));
		blocks.add(new Block("cocoa_plant_3.png", "cocoa_stage_2.png"));
		blocks.add(new Block("flowerpot.png", "flower_pot.png"));
		blocks.add(new Block("melon_stem_2.png", "melon_stem_connected.png", "DBC21A"));
		blocks.add(new Block("melon_stem_1.png", "melon_stem_disconnected.png", "DBC21A"));
		blocks.add(new Block("flower_red.png", "flower_rose.png"));
		blocks.add(new Block("flower_yellow.png", "flower_dandelion.png"));
		blocks.add(new Block("sugarcane.png", "reeds.png", "A9DC70"));
		blocks.add(new Block("potato_crop_1.png", "potatoes_stage_0.png"));
		blocks.add(new Block("potato_crop_2.png", "potatoes_stage_1.png"));
		blocks.add(new Block("potato_crop_3.png", "potatoes_stage_2.png"));
		blocks.add(new Block("potato_crop_4.png", "potatoes_stage_3.png"));

		blocks.add(new Block("mushroom_brown_cap.png", "mushroom_block_skin_brown.png"));
		blocks.add(new Block("mushroom_inside.png", "mushroom_block_inside.png"));
		blocks.add(new Block("mushroom_stem.png", "mushroom_block_skin_stem.png"));

		blocks.add(new Block("dead_shrub.png", "deadbush.png"));


		/********** Object **********/
		blocks.add(new Block("torch.png", "torch_on.png"));
		blocks.add(new Block("anvil_side.png", "anvil_base.png"));
		blocks.add(new Block("anvil_top_1.png", "anvil_top_damaged_0.png"));
		blocks.add(new Block("anvil_top_2.png", "anvil_top_damaged_1.png"));
		blocks.add(new Block("anvil_top_3.png", "anvil_top_damaged_2.png"));
		blocks.add(new Block("eye_of_ender.png", "endframe_eye.png"));
		blocks.add(new Block("endportal_frame_side.png", "endframe_side.png"));
		blocks.add(new Block("endportal_frame_top.png", "endframe_top.png"));
		blocks.add(new Block("bed_head_front.png", "bed_head_end.png"));
		blocks.add(new Block("bed_head_side.png", "bed_head_side.png"));
		blocks.add(new Block("bed_head_top.png", "bed_head_top.png"));
		blocks.add(new Block("bed_foot_front.png", "bed_head_end.png"));
		blocks.add(new Block("bed_foot_side.png", "bed_feet_side.png"));
		blocks.add(new Block("bed_foot_top.png", "bed_feet_top.png"));

		blocks.add(new Block("chest_trapped.png", "trapped.png"));
		blocks.add(new Block("largechest_trapped.png", "trapped_double.png"));
		blocks.add(new Block("chest.png", "normal.png"));
		blocks.add(new Block("largechest.png", "normal_double.png"));
		blocks.add(new Block("enderchest.png", "ender.png"));
		blocks.add(new Block("enchant_table_side.png", "enchanting_table_side.png"));
		blocks.add(new Block("enchant_table_bottom.png", "obsidian.png"));
		blocks.add(new Block("enchant_table_top.png", "enchanting_table_top.png"));
		blocks.add(new Block("furnace_front.png", "furnace_side.png"));
		blocks.add(new Block("furnace_lit_front.png", "furnace_front_on.png"));
		blocks.add(new Block("furnace_front.png", "furnace_front_off.png"));

		blocks.add(new Block("cobweb.png", "web.png"));
		blocks.add(new Block("workbench_top.png", "crafting_table_front.png"));
		blocks.add(new Block("workbench_back.png", "crafting_table_side.png"));
		blocks.add(new Block("workbench_front.png", "crafting_table_top.png"));
		blocks.add(new Block("cauldron_feet.png", "cauldron_bottom.png"));
		blocks.add(new Block("cauldron_inside.png", "cauldron_inner.png"));
		blocks.add(new Block("cauldron_top.png", "cauldron_top.png"));
		blocks.add(new Block("enderdragon_egg.png", "dragon_egg.png"));
		blocks.add(new Block("cake_inside.png", "cake_inner.png"));

		/********** RedStone **********/
		blocks.add(new Block("redstone_dust_off.png", "redstone_dust_cross.png", "700000"));
		blocks.add(new Block("redstone_dust_on.png", "redstone_dust_cross.png", "FD0101"));
		blocks.add(new Block("redstone_repeater_off.png", "repeater_off.png"));
		blocks.add(new Block("redstone_repeater_on.png", "repeater_on.png"));
		blocks.add(new Block("redstone_comparator_off.png", "comparator_off.png"));
		blocks.add(new Block("redstone_comparator_on.png", "comparator_on.png"));
		blocks.add(new Block("redstone_wire_on.png", "redstone_dust_line.png", "FD0101"));
		blocks.add(new Block("redstone_wire_off.png", "redstone_dust_line.png", "700000"));
		blocks.add(new Block("rails_powered_on.png", "rail_golden_powered.png"));
		blocks.add(new Block("rails_powered_off.png", "rail_golden.png"));
		blocks.add(new Block("rails_curved.png", "rail_normal_turned.png"));
		blocks.add(new Block("rails_detector_off.png", "rail_detector.png"));
		blocks.add(new Block("rails_detector_on.png", "rail_detector_powered.png"));
		blocks.add(new Block("rails_activator_off.png", "rail_activator.png"));
		blocks.add(new Block("rails_activator_on.png", "rail_activator_powered.png"));
		blocks.add(new Block("rails.png", "rail_normal.png"));

		blocks.add(new Block("piston_arm.png", "piston_top_normal.png"));
		blocks.add(new Block("piston_arm_sticky.png", "piston_top_sticky.png"));
		blocks.add(new Block("piston_top.png", "piston_inner.png"));
		blocks.add(new Block("daylight_sensor_side.png", "daylight_detector_side.png"));
		blocks.add(new Block("daylight_sensor_inv_top.png", "daylight_detector_inverted_top.png"));
		blocks.add(new Block("daylight_sensor_top.png", "daylight_detector_top.png"));
		blocks.add(new Block("dropper_front.png", "dropper_front_horizontal.png"));
		blocks.add(new Block("dispenser_front.png", "dispenser_front_horizontal.png"));


		/********** other **********/
		blocks.add(new Block("water.png", "water_still.png"));
		blocks.add(new Block("water_flowing.png", "water_flow.png"));
		blocks.add(new Block("lava.png", "lava_still.png"));
		blocks.add(new Block("lava_flowing.png", "lava_flow.png"));
		blocks.add(new Block("fire.png", "fire_layer_0.png"));

		/********** banner **********/
		blocks.add(new Block("banner_base.png", "base.png"));
		blocks.add(new Block("banner_pattern_base.png", "base.png"));
		blocks.add(new Block("banner_pattern_bo.png", "border.png"));
		blocks.add(new Block("banner_pattern_bri.png", "bricks.png"));
		blocks.add(new Block("banner_pattern_mc.png", "circle.png"));
		blocks.add(new Block("banner_pattern_cre.png", "creeper.png"));
		blocks.add(new Block("banner_pattern_cr.png", "cross.png"));
		blocks.add(new Block("banner_pattern_cbo.png", "curly_border.png"));
		blocks.add(new Block("banner_pattern_ld.png", "diagonal_left.png"));
		blocks.add(new Block("banner_pattern_rd.png", "diagonal_right.png"));
		blocks.add(new Block("banner_pattern_lud.png", "diagonal_up_left.png"));
		blocks.add(new Block("banner_pattern_rud.png", "diagonal_up_right.png"));
		blocks.add(new Block("banner_pattern_flo.png", "flower.png"));
		blocks.add(new Block("banner_pattern_gra.png", "gradient.png"));
		blocks.add(new Block("banner_pattern_gru.png", "gradient_up.png"));
		blocks.add(new Block("banner_pattern_hh.png", "half_horizontal.png"));
		blocks.add(new Block("banner_pattern_hhb.png", "half_horizontal_bottom.png"));
		blocks.add(new Block("banner_pattern_vh.png", "half_vertical.png"));
		blocks.add(new Block("banner_pattern_vhr.png", "half_vertical_right.png"));
		blocks.add(new Block("banner_pattern_moj.png", "mojang.png"));
		blocks.add(new Block("banner_pattern_mr.png", "rhombus.png"));
		blocks.add(new Block("banner_pattern_sku.png", "skull.png"));
		blocks.add(new Block("banner_pattern_ss.png", "small_stripes.png"));
		blocks.add(new Block("banner_pattern_bl.png", "square_bottom_left.png"));
		blocks.add(new Block("banner_pattern_br.png", "square_bottom_right.png"));
		blocks.add(new Block("banner_pattern_tl.png", "square_top_left.png"));
		blocks.add(new Block("banner_pattern_tr.png", "square_top_right.png"));
		blocks.add(new Block("banner_pattern_sc.png", "straight_cross.png"));
		blocks.add(new Block("banner_pattern_bs.png", "stripe_bottom.png"));
		blocks.add(new Block("banner_pattern_cs.png", "stripe_center.png"));
		blocks.add(new Block("banner_pattern_dls.png", "stripe_downleft.png"));
		blocks.add(new Block("banner_pattern_drs.png", "stripe_downright.png"));
		blocks.add(new Block("banner_pattern_ls.png", "stripe_left.png"));
		blocks.add(new Block("banner_pattern_ms.png", "stripe_middle.png"));
		blocks.add(new Block("banner_pattern_rs.png", "stripe_right.png"));
		blocks.add(new Block("banner_pattern_ts.png", "stripe_top.png"));
		blocks.add(new Block("banner_pattern_bts.png", "triangles_bottom.png"));
		blocks.add(new Block("banner_pattern_tts.png", "triangles_top.png"));
		blocks.add(new Block("banner_pattern_bt.png", "triangle_bottom.png"));
		blocks.add(new Block("banner_pattern_tt.png", "triangle_top.png"));


		/******** Armor Stand ********/

		blocks.add(new Block("mob_char.png", "steve.png"));
		blocks.add(new Block("mob_creeper.png", "creeper.png"));
		blocks.add(new Block("mob_skeleton.png", "skeleton.png"));
		blocks.add(new Block("mob_skeleton_wither.png", "wither_skeleton.png"));
		blocks.add(new Block("mob_zombie.png", "zombie.png"));
		blocks.add(new Block("mob_pig.png", "pig.png"));
		blocks.add(new Block("mob_chicken.png", "chicken.png"));
		blocks.add(new Block("mob_cow.png", "cow.png"));
		blocks.add(new Block("mob_sheep.png", "sheep.png"));
		blocks.add(new Block("mob_squid.png", "squid.png"));
		blocks.add(new Block("armor_stand.png", "wood.png"));
		blocks.add(new Block("armor_golden_helmet.png", "gold_layer_1.png"));
		blocks.add(new Block("armor_golden_chest.png", "gold_layer_1.png"));
		blocks.add(new Block("armor_chainmail_helmet.png", "chainmail_layer_1.png"));
		blocks.add(new Block("armor_chainmail_chest.png", "chainmail_layer_1.png"));
		blocks.add(new Block("armor_diamond_helmet.png", "diamond_layer_1.png"));
		blocks.add(new Block("armor_diamond_chest.png", "diamond_layer_1.png"));
		blocks.add(new Block("armor_iron_helmet.png", "iron_layer_1.png"));
		blocks.add(new Block("armor_iron_chest.png", "iron_layer_1.png"));
		blocks.add(new Block("armor_leather_helmet.png", "leather_layer_1.png"));
		blocks.add(new Block("armor_leather_chest.png", "leather_layer_1.png"));
		blocks.add(new Block("armor_leather_helmet_overlay.png", "leather_layer_1_overlay.png"));
		blocks.add(new Block("armor_leather_chest_overlay.png", "leather_layer_1_overlay.png"));
		blocks.add(new Block("armor_golden_legs.png", "gold_layer_2.png"));
		blocks.add(new Block("armor_golden_feet.png", "gold_layer_2.png"));
		blocks.add(new Block("armor_chainmail_legs.png", "chainmail_layer_2.png"));
		blocks.add(new Block("armor_chainmail_feet.png", "chainmail_layer_2.png"));
		blocks.add(new Block("armor_diamond_legs.png", "diamond_layer_2.png"));
		blocks.add(new Block("armor_diamond_feet.png", "diamond_layer_2.png"));
		blocks.add(new Block("armor_iron_legs.png", "iron_layer_2.png"));
		blocks.add(new Block("armor_iron_feet.png", "iron_layer_2.png"));
		blocks.add(new Block("armor_leather_legs.png", "leather_layer_2.png"));
		blocks.add(new Block("armor_leather_feet.png", "leather_layer_2.png"));
		blocks.add(new Block("armor_leather_legs_overlay.png", "leather_layer_2_overlay.png"));
		blocks.add(new Block("armor_leather_feet_overlay.png", "leather_layer_2_overlay.png"));

		/******* Leaves ********/
		blocks.add(new Block("leaves_acacia.png", "leaves_acacia.png",  "60F020"));
		blocks.add(new Block("leaves_big_oak.png", "leaves_big_oak.png", "60F020"));
		blocks.add(new Block("leaves_birch.png" , "leaves_birch.png", "A9DC70"));
		blocks.add(new Block("leaves_jungle.png", "leaves_jungle.png",  "60F020"));
		blocks.add(new Block("leaves_oak.png", "leaves_oak.png", "60F020"));
		blocks.add(new Block("leaves_spruce.png", "leaves_spruce.png",  "80CA80"));
	}


	public static Set<Block> getBlocks() {
		return blocks;
	}
	public static Block get(String name) {
		for (Block b : blocks) {
			if (b.fileName.equals(name))
				return b;
		}
		return null;
	}
}

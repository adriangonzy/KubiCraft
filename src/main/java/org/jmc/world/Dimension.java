package org.jmc.world;

/**
 * @author adrian.
 */
public enum Dimension {

	OVERWORLD("Overworld", 0),
	NETHER("Nether", -1),
	END("The End", 1);

	private String name;
	private int index;

	Dimension(String name, int index) {
		this.name = name;
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public String getName() {
		return name;
	}

	public static Dimension getByIndex(int index) {
		switch (index) {
			case -1:
				return Dimension.NETHER;
			case 1:
				return Dimension.END;
			default:
				return Dimension.OVERWORLD;
		}
	}
}

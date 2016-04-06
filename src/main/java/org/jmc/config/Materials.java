package org.jmc.config;

import org.jmc.util.Log;
import org.jmc.util.Resources;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * This class reads the default materials file (default.mtl).
 * Currently the only needed information is the material color.
 */
public class Materials
{

	public static final String CONFIG_FILE = "/conf/default.mtl";

	private static HashMap<String, Color> mtlColors;

	private static void readConfig(HashMap<String, Color> mtlColors) throws Exception
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(Resources.safeLoad(CONFIG_FILE)));
		try
		{
			String currMtl = null;
			Pattern rxNewmtl = Pattern.compile("^\\s*newmtl\\s+(.*?)\\s*$");
			Pattern rxKd = Pattern.compile("^\\s*Kd\\s+([0-9.]+)\\s+([0-9.]+)\\s+([0-9.]+)\\s*$");

			String line;
			while ((line = reader.readLine()) != null)
			{
				Matcher mNewmtl = rxNewmtl.matcher(line);
				Matcher mKd = rxKd.matcher(line);
				if (mNewmtl.matches())
				{
					currMtl = mNewmtl.group(1);
				}
				else if (mKd.matches() && currMtl != null)
				{
					float r = Float.parseFloat(mKd.group(1));
					float g = Float.parseFloat(mKd.group(2));
					float b = Float.parseFloat(mKd.group(3));

					mtlColors.put(currMtl.toLowerCase(), new Color(r,g,b,1));
				}
			}
		}
		finally
		{
			reader.close();
		}
	}


	/**
	 * Reads the configuration file.
	 * Must be called once at the start of the program.
	 * 
	 * @throws Exception if reading the configuration failed. In this case the program should abort.
	 */
	public static void initialize() throws Exception
	{
		// create the colors table
		Log.info("Reading materials file...");

		mtlColors = new HashMap<String, Color>();
		readConfig(mtlColors);

		Log.info("Loaded " + mtlColors.size() + " materials.");
	}


	/**
	 * Gets the diffuse color defined for a material.
	 * If the material name is not found, returns a default color.
	 * 
	 * @param mtlName Material name
	 * @return Material color
	 */
	public static Color getColor(String mtlName)
	{
		Color c = mtlColors.get(mtlName.toLowerCase());
		return c != null ? c : new Color(0,0,0);
	}


}

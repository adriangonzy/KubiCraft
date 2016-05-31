package org.jmc.export;

import org.jmc.Options;
import org.jmc.util.BlocksMap;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import static org.jmc.util.Resources.*;

/**
 * Created by Paul on 25/04/2016.
 */
public class TextureExporter {
	private final String TEX_FOLDER = "conf/textures/tex";
	private final int TEXTURE_SIZE = 256;
	public final String PATH_TO_CUSTOM_TEXTURE = Paths.get(Options.outputDir, Options.exportsFolder, "texture-packs").toFile().getAbsolutePath();

	private final String texturePath;
	private final ProgressCallback progress;
	private float blockScale;

	public TextureExporter(String texturePath, ProgressCallback progress) {
		this.texturePath = texturePath;
		this.progress = progress;
	}

	public Iterator<Map.Entry<String, InputStream>> loadTextures() throws IOException {
		Map<String, String> textures;
		if (texturePath != null && texturePath.length() > 0) {
			textures = loadCustomTextures(texturePath);
		} else {
			textures = loadTextureFromJar();
		}

		final Iterator<Map.Entry<String, String>> iterator = textures.entrySet().iterator();

		return new Iterator<Map.Entry<String, InputStream>>() {
			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public Map.Entry<String, InputStream> next() {
				Map.Entry<String, String> entry = iterator.next();
				return new AbstractMap.SimpleEntry<>(entry.getKey(), loadFile(entry.getValue()));
			}

			@Override
			public void remove() {
				iterator.remove();
			}
		};
	}


	private Map<String, String> loadCustomTextures(String path) throws IOException {
		Path fullPath = Paths.get(path, "assets", "minecraft", "textures");
		//Get textures
		Map<String, String> defaultTextures = loadTextureFromJar();
		Map<String, String> customTextures = listAllCustomTextures(fullPath.toString());

		String[] segments = path.split(Pattern.quote(File.separator));
		File customTexturesFolder = Paths.get(PATH_TO_CUSTOM_TEXTURE, segments[segments.length - 1]).toFile();
		customTexturesFolder.mkdirs();
		int count = 0;

		/* Compute the default texture block image scale */
		computeBlockScale(customTextures);

		/** Replace default image **/
		for (String name : new ArrayList<>(defaultTextures.keySet())) {
			BlocksMap.Block block = BlocksMap.get(name);

			/* We need to transform only custom texture */
			if (customTextures.get(block.mtlName) != null) {
				defaultTextures.remove(name);
				String outTexPath = Paths.get(customTexturesFolder.getAbsolutePath(), block.fileName).toString();
				defaultTextures.put(name, transformTexture(block, customTextures.get(block.mtlName), outTexPath));
			} else {
				System.out.println("Missing texture: " + block.mtlName);
			}

			//Update progress bar
			if (progress != null) {
				float progValue = (float) count / (float) defaultTextures.size();
				progress.setProgress(progValue);
				count++;
			}
		}
		return defaultTextures;
	}

	private Map<String, String> loadTextureFromJar() throws IOException {
		final Map<String, String> textures = new HashMap<>();
		final File jarFile = new File(TextureExporter.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		if (jarFile.isFile()) {
			final JarFile jar = new JarFile(jarFile);
			final Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
			while(entries.hasMoreElements()) {
				String test = entries.nextElement().getName();
				if (test.startsWith(TEX_FOLDER) && !test.equals(TEX_FOLDER + "/")) { //filter according to the path
					String[] segments = test.split("/");
					textures.put(segments[segments.length - 1], "/" + test);
				}
			}
			jar.close();
		} else {
			BufferedReader in=new BufferedReader(new InputStreamReader(load("/" + TEX_FOLDER)));
			String texName;
			while((texName=in.readLine()) != null) {
				textures.put(texName, "/" + TEX_FOLDER + "/" + texName);
			}
		}
		return textures;
	}

	private Map<String, String> listAllCustomTextures(String path){
		Map<String, String> textures= new HashMap<>();

		File repertory = new File(path);
		if (!repertory.exists()) {
			return textures;
		}
		for (File file : repertory.listFiles()){
			if (!file.isDirectory()) {
				if (file.getName().contains(".png")) {
					textures.put(file.getName(), file.getAbsolutePath());
				}
			} else {
				if ((file.getPath().contains("blocks")
						|| file.getPath().contains("entity")
						|| file.getPath().contains("models")
						|| file.getPath().contains("particle"))
						&& !file.getPath().contains("slime")) {
					Map<String, String> subtextures = listAllCustomTextures(file.getAbsolutePath());
					if (subtextures != null) {
						textures.putAll(subtextures);
					}

				}
			}
		}
		return textures;
	}

	private void computeBlockScale(Map<String, String> textures) {
		String tmpPath = textures.get("stone.png");
		if (tmpPath == null) {
			tmpPath = textures.get("dirt.png");
		}
		BufferedImage src;
		try {
			src = ImageIO.read(new File(tmpPath));
			blockScale = src.getWidth() > src.getHeight() ? src.getHeight() : src.getWidth();
			blockScale = TEXTURE_SIZE / blockScale;
			if (blockScale < 1.0f)
				blockScale = 1.0f;
		} catch (IOException e) {
			System.out.println("Default scale can't be compute");
			blockScale = 0;
		}
	}

	private String transformTexture(BlocksMap.Block block, String srcTexPath, String outTexPath){

		//Entities aren't square !
		if (block.isSquare && !srcTexPath.contains("entity")) {
			srcTexPath = squareImage(srcTexPath, outTexPath);
		}
		if (!block.tint.equals("")){
			srcTexPath = tintImage(new Color(Integer.parseInt(block.tint, 16)), srcTexPath, outTexPath);
		}

		return resizeImage(srcTexPath, outTexPath);
	}

	private String squareImage(String srcPath, String outPath) {
		try {
			BufferedImage src = ImageIO.read(new File(srcPath));
			int size;

			if (src.getHeight() == src.getWidth()) {
				return srcPath;
			}else if (src.getWidth() > src.getHeight()){
				size = src.getHeight();
			} else {
				size = src.getWidth();
			}
			BufferedImage resized = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);

			resized.getGraphics().drawImage(src, 0, 0, null);
			ImageIO.write(resized, "png", new File(outPath));
			return outPath;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return srcPath;
	}
	private String resizeImage(String srcPath, String outPath) {
		try {
			BufferedImage src = ImageIO.read(new File(srcPath));

			int newWidth;
			int newHeight;
			int minSize = src.getWidth() > src.getHeight() ? src.getHeight() : src.getWidth();

			float scale = blockScale;
			if (blockScale == 0) {
				scale = TEXTURE_SIZE / (float) minSize;
			}

			newWidth = (int) (scale * src.getWidth());
			newHeight = (int) (scale * src.getHeight());

			BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

			Graphics2D g2 = resized.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g2.drawImage(src, 0, 0, newWidth, newHeight, null);
			g2.dispose();

			ImageIO.write(resized, "png", new File(outPath));
			return outPath;
		} catch (IOException e) {
			e.printStackTrace();
			return srcPath;
		}
	}
	private String tintImage(Color tint, String srcPath, String outPath) {
		try {
			BufferedImage src = ImageIO.read(new File(srcPath));

			int w = src.getWidth();
			int h = src.getHeight();

			// convert color model to discrete int for later byte operations
			if (src.getColorModel() instanceof IndexColorModel) {
				IndexColorModel colorModel = (IndexColorModel) src.getColorModel();
				src = colorModel.convertToIntDiscrete(src.getRaster(), true);
			}

			int nbCanal = src.getColorModel().getNumComponents();
			int[] buffer = new int[w * h * nbCanal];

			WritableRaster raster = src.getRaster();
			raster.getPixels(0, 0, w, h, buffer);

			int c;
			int r = tint.getRed();
			int g = tint.getGreen();
			int b = tint.getBlue();

			for (int i = 0; i < w * h; i++) {
				c = (buffer[nbCanal * i] * r) >> 8;
				if (c > 255)
					c = 255;
				buffer[nbCanal * i] = c;

				c = (buffer[nbCanal * i + 1] * g) >> 8;
				if (c > 255)
					c = 255;
				buffer[nbCanal * i + 1] = c;

				c = (buffer[nbCanal * i + 2] * b) >> 8;
				if (c > 255)
					c = 255;
				buffer[nbCanal * i + 2] = c;
			}

			raster.setPixels(0, 0, w, h, buffer);
			ImageIO.write(src, "png", new File(outPath));

			return outPath;
		} catch (IOException e) {
			System.out.println(srcPath);
			System.out.println(outPath);
			System.out.println(tint);
			e.printStackTrace();
			return srcPath;
		}
	}
}
